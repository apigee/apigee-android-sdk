package com.apigee.sdk.apm.android.metrics;

import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

//import com.apigee.sdk.android.metrics.MPDatabaseHelper;
import com.apigee.sdk.apm.android.metrics.MPConfig;
import com.apigee.sdk.apm.android.metrics.MPDbAdapter;
import com.apigee.sdk.apm.android.model.ClientLog;
import com.apigee.sdk.apm.android.util.Base64Coder;

/**
 * 
 * This is a modified from MixPanel's android client to
 * 
 * SQLite database adapter for MPMetrics. This class is used from both the UI and
 * HTTP request threads, but maintains a single database connection. This is because
 * when performing concurrent writes from multiple database connections, some will
 * silently fail (save for a small message in logcat). Synchronize on each method,
 * so we don't close the connection when another thread is using it.
 *
 * @author anlu(Anlu Wang)
 *
 */
public class MPDbAdapter {
	private static final String LOGTAG = ClientLog.TAG_MONITORING_CLIENT;

	public static final String DATABASE_NAME = "apigee";
	public static final String EVENTS_TABLE = "events";
	public static final String PEOPLE_TABLE = "people";
	public static final String SESSION_TABLE = "sessions";
	private static final int DATABASE_VERSION = 4;

	public static final String KEY_DATA = "data";
	public static final String KEY_CREATED_AT = "created_at";
	public static final String SESSION_UUID = "uuid";
	public static final String SESSION_START_WALL_TIME = "session_start_wall_time";
	public static final String SESSION_LAST_UPDATE_TIME = "session_last_update_time";
	public static final String SESSION_CLOSED_FLAG = "session_closed_flag";

	private static final String CREATE_EVENTS_TABLE =
       "CREATE TABLE " + EVENTS_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_DATA + " STRING NOT NULL, " +
		KEY_CREATED_AT + " INTEGER NOT NULL);";
	private static final String CREATE_PEOPLE_TABLE =
       "CREATE TABLE " + PEOPLE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		KEY_DATA + " STRING NOT NULL, " +
		KEY_CREATED_AT + " INTEGER NOT NULL);";
	private static final String CREATE_SESSION_TABLE =
		       "CREATE TABLE " + SESSION_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_DATA + " STRING NOT NULL, " +
				KEY_CREATED_AT + " INTEGER NOT NULL," + 
				SESSION_LAST_UPDATE_TIME + " INTEGER NOT NULL," + 
				SESSION_CLOSED_FLAG + " INTEGER NOT NULL," +
				SESSION_UUID + " TEXT NOT NULL," +
				SESSION_START_WALL_TIME + " INTEGER NOT NULL);";
	
	private static final String EVENTS_TIME_INDEX =
		"CREATE INDEX IF NOT EXISTS time_idx ON " + EVENTS_TABLE +
		" (" + KEY_CREATED_AT + ");";
	private static final String PEOPLE_TIME_INDEX =
		"CREATE INDEX IF NOT EXISTS time_idx ON " + PEOPLE_TABLE +
		" (" + KEY_CREATED_AT + ");";
	//todo: Need to finish using the indexes
	//private static final String SESSION_TIME_INDEX =
	//	"CREATE INDEX IF NOT EXISTS time_idx ON " + SESSION_TABLE +
	//	" (" + KEY_CREATED_AT + ");";
	

	private MPDatabaseHelper mDb;
	
	private String sessionId;
	

	public String getSessionId() {
		return sessionId;
	}


	private static class MPDatabaseHelper extends SQLiteOpenHelper {
		MPDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_EVENTS_TABLE);
			db.execSQL(CREATE_PEOPLE_TABLE);
			db.execSQL(CREATE_SESSION_TABLE);
			db.execSQL(EVENTS_TIME_INDEX);
			db.execSQL(PEOPLE_TIME_INDEX);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    db.execSQL("DROP TABLE " + EVENTS_TABLE);
		    db.execSQL(CREATE_EVENTS_TABLE);
			db.execSQL(CREATE_PEOPLE_TABLE);
			db.execSQL(CREATE_SESSION_TABLE);
			db.execSQL(EVENTS_TIME_INDEX);
			db.execSQL(PEOPLE_TIME_INDEX);
		}
	}

	public MPDbAdapter(Context context, String token) {
		mDb = new MPDatabaseHelper(context);
		
	}

	/**
	 * Adds a JSON string representing an event with properties or a person record
	 * to the SQLiteDatabase.
	 * @param j the JSON to record
	 * @param table the table to insert into, either "events" or "people"
	 * @return the number of rows in the table, or -1 on failure
	 */
	public int addJSON(JSONObject j, String table) {
		synchronized (this) {
			if (MPConfig.DEBUG) { Log.d(LOGTAG, "addJSON " + table); }

			Cursor c = null;
			int count = -1;

			try {
				SQLiteDatabase db = mDb.getWritableDatabase();

				ContentValues cv = new ContentValues();
				cv.put(KEY_DATA, j.toString());
				cv.put(KEY_CREATED_AT, System.currentTimeMillis());
			    db.insert(table, null, cv);

			    c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
			    c.moveToFirst();
			    count = c.getInt(0);
			} catch (SQLiteException e) {
				Log.e(LOGTAG, "addJSON " + table, e);
			} finally {
			    mDb.close();
			    if (c != null) {
			    	c.close();
			    }
			}
			return count;
		}
	}
	
	public String openNewSession()
	{
		
		String table = SESSION_TABLE;
		
		synchronized(this)
		{
			
			SQLiteDatabase db = mDb.getWritableDatabase()
			;
			try {
				
				ContentValues cv = new ContentValues();
				cv.put(SESSION_UUID, UUID.randomUUID().toString());
				
				long currentTime = System.currentTimeMillis();
				cv.put(KEY_DATA,"");
				cv.put(SESSION_START_WALL_TIME, Long.valueOf(currentTime));
				cv.put(KEY_CREATED_AT, Long.valueOf(currentTime));
				cv.put(SESSION_LAST_UPDATE_TIME, Long.valueOf(currentTime));
				cv.put(SESSION_CLOSED_FLAG, 0);
				db.insert(table, null, cv);
				
				return cv.getAsString(SESSION_UUID);
				
			} catch (SQLiteException e) {
				Log.e(LOGTAG, "openNewSession " + table, e);
			} finally {
			    db.close();    
			}
		}
		
		return null;
	}
	
	
	public String openSession()
	{
		
		String table = SESSION_TABLE;
		
		synchronized (this) {
			try{
				//Closes sessions that expired.
				closeExpiredSessions();
				//Deletes all sessions that are over 2 days old
				cleanupEvents(System.currentTimeMillis() - MPConfig.DATA_EXPIRATION, SESSION_TABLE);
				//SQLiteDatabase db = mDb.getReadableDatabase();
				//db.qu
				final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(table);
				
				String [] projection = {SESSION_UUID,SESSION_START_WALL_TIME,SESSION_LAST_UPDATE_TIME};
				
				long currentTime = System.currentTimeMillis();
				long sessionExpiryTime = currentTime - MPConfig.SESSION_EXPIRATION;
				
				String selection = "SESSION_CLOSED_FLAG <> 1 " + " AND " + 
						SESSION_LAST_UPDATE_TIME + " >= " + sessionExpiryTime  ;
				//String selection = SESSION_LAST_UPDATE_TIME + " >= " + sessionExpiryTime  ;
				//String selection = "SESSION_CLOSED_FLAG <> 1 ";
				
				
				//qb.query(mDb, projection, selection, new String[] {}, null, null, null);
				
				final String [] selectionArgs = new String[] {};
				
				 final Cursor result = qb.query(mDb.getReadableDatabase(), projection, selection, selectionArgs, null, null, null);
				//final Cursor result = qb.query(mDb.getReadableDatabase(), projection, null, null, null, null, null);
				
				
				// new session needs to be created
				String uuid = "";
				if(result.getCount() == 0)
				{
					uuid = openNewSession();
				} else if (result.getCount() == 1)
				{
					result.moveToFirst();
					Log.i(LOGTAG, "Session UUID :" + result.getString(result.getColumnIndexOrThrow(SESSION_UUID)) + 
							" Session Start Time : " + result.getLong(result.getColumnIndexOrThrow(SESSION_START_WALL_TIME)) + 
							" Session Last Update Time : " + result.getLong(result.getColumnIndexOrThrow(SESSION_LAST_UPDATE_TIME)) + 
							" Session Expiry Time : " + sessionExpiryTime);
					
					uuid = result.getString(result.getColumnIndexOrThrow(SESSION_UUID));
				} else if (result.getCount() > 1)
				{
					Log.w(LOGTAG, "There were more than one open session detected. Old sessions should have been cleaned up. Num Session : " + result.getCount());
					result.moveToLast();
					uuid = result.getString(result.getColumnIndexOrThrow(SESSION_UUID));
				} else
				{
					Log.e(LOGTAG,"Error - no session ID was assigned. Num Results : " + result.getCount());
				}
				
				result.close();
				
				sessionId = uuid;
				
			} catch (SQLiteException e) {
				Log.e(LOGTAG, "openNewSession " + table, e);
			} finally {
			    mDb.close();    
			}
			
		}
		return sessionId;
	}
	
	public int closeExpiredSessions()
	{
		String table = MPDbAdapter.SESSION_TABLE;
		
		synchronized(this)
		{
			SQLiteDatabase db = mDb.getWritableDatabase();
			try {					
				ContentValues cv = new ContentValues();
				
				long currentTime = System.currentTimeMillis();
				long sessionExpiryTime = currentTime - MPConfig.SESSION_EXPIRATION;
				
				cv.put(SESSION_CLOSED_FLAG, 1);
				
				String whereClause = "SESSION_CLOSED_FLAG <> 1 " + " AND " + 
						SESSION_LAST_UPDATE_TIME + " < " + sessionExpiryTime  ;
				
				int rowsUpdated = db.update(table, cv, whereClause, null);
				
				if(rowsUpdated > 0)
				{
					Log.i(LOGTAG, "closed expired sessions. Num Sessions : " + rowsUpdated);
				} else
				{
					Log.d(LOGTAG, "No sessions closed.");
				}
				
				return rowsUpdated;
				
			} catch (SQLiteException e) {
				Log.e(LOGTAG, "closeOpenSession " + table, e);
				return 0;
			} finally {
			    db.close();    
			}
		}
	}
	
	
	public String closeOpenSession()
	{
		String closedSession = sessionId;
		
		if (sessionId != null)
		{
			String sessionToClose = sessionId;
			
			String table = SESSION_TABLE;
			
			synchronized(this)
			{
				SQLiteDatabase db = mDb.getWritableDatabase();
				try {					
					ContentValues cv = new ContentValues();
					long currentTime = System.currentTimeMillis();
					cv.put(SESSION_LAST_UPDATE_TIME, Long.valueOf(currentTime));
					cv.put(SESSION_CLOSED_FLAG, 1);
					
					String whereClause = SESSION_UUID + " LIKE '" + sessionToClose  +  "'";
					
					int rowsUpdated = db.update(table, cv, whereClause, null);
					
					if(rowsUpdated > 0)
					{
						sessionId = null;
					} else
					{
						Log.w(LOGTAG, "closeOpenSession did not manage to close any sessions. Assuming session is still open or closed by a different thread");
						return null;
					}
					
				} catch (SQLiteException e) {
					Log.e(LOGTAG, "closeOpenSession " + table, e);
				} finally {
				    db.close();    
				}
			}
		} else
		{
			Log.w(LOGTAG, "Attempting to close a closed session");
		}
		
		return closedSession;
	}
	
	
	/**
	 * updates the timestamp of the session
	 * @return boolean whether or not the session was actually updated. Session not updating can happen for a variety
	 * of reasons. It is possible that the session got accidently closed. Session updating can only happen to open 
	 * sessions
	 */
	public boolean updateSession()
	{
		if (sessionId != null)
		{
			String sessionToClose = sessionId;
			
			String table = SESSION_TABLE;
			
			synchronized(this)
			{
				SQLiteDatabase db = mDb.getWritableDatabase();
				try {					
					ContentValues cv = new ContentValues();
					long currentTime = System.currentTimeMillis();
					cv.put(SESSION_LAST_UPDATE_TIME, Long.valueOf(currentTime));
					
					
					String whereClause = SESSION_UUID + " LIKE '" + sessionToClose  +  "'" + " AND " + 
							SESSION_CLOSED_FLAG + " <> 1";
					
					int rowsUpdated = db.update(table, cv, whereClause, null);
					
					if(rowsUpdated > 0)
					{
						return true;
					} else
					{
						Log.w(LOGTAG, "updateSession did not manage to update any sessions. Assuming session was closed by a different thread");
						return false;
					}
					
				} catch (SQLiteException e) {
					Log.e(LOGTAG, "updateSession " + table, e);
					return false;
				} finally {
				    db.close();    
				}
			}
		} else
		{
			Log.w(LOGTAG, "No session was opened. Cannot update session");
			return false;
		}
	}
	
	public class SessionData
	{
		public Date sessionStartTime;
		public Date sessionEndTime;
		public String sessionUUID;
	}
	
	public SessionData getSessionData()
	{
		if (sessionId != null)
		{
			String table = SESSION_TABLE;
			synchronized(this)
			{
				
				final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(table);
				
				String [] projection = {SESSION_UUID,SESSION_START_WALL_TIME,SESSION_LAST_UPDATE_TIME};
				
				String selection = SESSION_UUID + " LIKE '" + this.sessionId  +  "'";
				
				final String [] selectionArgs = new String[] {};
				Cursor result = null;
				try {	
					result = qb.query(mDb.getReadableDatabase(), projection, selection, selectionArgs, null, null, null);
					if (result.getCount() > 0)
					{
						result.moveToLast();
						SessionData sd = new SessionData();
						
						sd.sessionUUID = result.getString(result.getColumnIndexOrThrow(SESSION_UUID));
						sd.sessionStartTime = new Date(result.getLong(result.getColumnIndexOrThrow(SESSION_START_WALL_TIME)));
						sd.sessionEndTime = new Date(result.getLong(result.getColumnIndexOrThrow(SESSION_LAST_UPDATE_TIME)));
						
						return sd;
					} else
					{
						Log.w(LOGTAG, "Attempting to get a session that does not exist");
					}
					result.close();
				} catch (SQLiteException e)
				{
					Log.e(LOGTAG, "Error retrieving session data for session UUID : " + sessionId);
				} finally {
					mDb.close();
					if (result != null)
					{
						result.close();
					}
				}
			}
		} else
		{
			Log.w(LOGTAG, "Attempting to get a session before session has been initialized");
		}
		return null;
	}
	

	/**
	 * Removes events with an _id <= last_id from table
	 * @param last_id the last id to delete
	 * @param table the table to remove events from, either "events" or "people"
	 */
	public void cleanupEvents(String last_id, String table) {
		synchronized (this) {
			if (MPConfig.DEBUG) { Log.d(LOGTAG, "cleanupEvents _id " + last_id + " from table " + table); }

			try {
				SQLiteDatabase db = mDb.getWritableDatabase();
			    db.delete(table, "_id <= " + last_id, null);
			} catch (SQLiteException e) {
				// If there's an exception, oh well, let the events persist
				Log.e(LOGTAG, "cleanupEvents " + table, e);
			} finally {
			    mDb.close();
			}
		}
	}

	/**
	 * Removes events before time.
	 * @param time the unix epoch in milliseconds to remove events before
	 * @param table the table to remove events from, either "events" or "people"
	 */
	public void cleanupEvents(long time, String table) {
		synchronized (this) {
			if (MPConfig.DEBUG) { Log.d(LOGTAG, "cleanupEvents time " + time + " from table " + table); }

			try {
				SQLiteDatabase db = mDb.getWritableDatabase();
			    db.delete(table, KEY_CREATED_AT + " <= " + time, null);
			} catch (SQLiteException e) {
				// If there's an exception, oh well, let the events persist
				Log.e(LOGTAG, "cleanupEvents " + table, e);
			} finally {
			    mDb.close();
			}
		}
	}

	/**
	 * Returns the data string to send to Mixpanel and the maximum ID of the row that
	 * we're sending, so we know what rows to delete when a track request was successful.
	 *
	 * @param table the table to read the JSON from, either "events" or "people"
	 * @return String array containing the maximum ID and the data string
	 * representing the events, or null if none could be successfully retrieved.
	 */
	public String[] generateDataString(String table) {
		synchronized (this) {
			Cursor c = null;
			String data = null;
			String last_id = null;

			try {
				SQLiteDatabase db = mDb.getReadableDatabase();
				c = db.rawQuery("SELECT * FROM " + table  +
		    		            " ORDER BY " + KEY_CREATED_AT + " ASC LIMIT 50", null);
				JSONArray arr = new JSONArray();

				while (c.moveToNext()) {
					if (c.isLast()) {
						last_id = c.getString(c.getColumnIndex("_id"));
					}
					try {
						JSONObject j = new JSONObject(c.getString(c.getColumnIndex(KEY_DATA)));
						arr.put(j);
					} catch (JSONException e) {
						// Ignore this object
					}
				}

				if (arr.length() > 0) {
					data = Base64Coder.encodeString(arr.toString());
				}
			} catch (SQLiteException e) {
				Log.e(LOGTAG, "generateDataString " + table, e);
			} finally {
				mDb.close();
				if (c != null) {
					c.close();
				}
			}

			if (last_id != null && data != null) {
				String[] ret = {last_id, data};
				return ret;
			}
			return null;
		}
	}
	
	
	public SQLiteDatabase getReadableDatabase()
	{
		return mDb.getReadableDatabase();
	}
	
	public SQLiteDatabase getWritableDatabase()
	{
		return mDb.getWritableDatabase();
	}
	
	//public Cursor getSession(String Session
	
}
