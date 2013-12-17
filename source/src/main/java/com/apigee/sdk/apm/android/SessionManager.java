package com.apigee.sdk.apm.android;

import java.util.Date;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;


public class SessionManager {
	private String sessionUUID;
	private Date sessionStartTime;
	private Date sessionLastActivityTime;
	private SessionTimeoutListener timeoutListener = null;
	private long sessionTimeoutMillis;
	private boolean isPaused;

    private Handler sessionTimeoutHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable sessionTimeoutCallback = new Runnable() {
        @Override
        public void run() {
            // session timed out
        	handleTimedOutSession();
        }
    };
    
    public SessionManager(long sessionTimeoutMillis) {
    	this.sessionTimeoutMillis = sessionTimeoutMillis;
    	this.isPaused = false;
    }
    
    public SessionManager(long sessionTimeoutMillis,SessionTimeoutListener timeoutListener) {
    	this.sessionTimeoutMillis = sessionTimeoutMillis;
    	this.timeoutListener = timeoutListener;
    	this.isPaused = false;
    }
    
    protected synchronized void handleTimedOutSession() {
    	if( ! isPaused ) {
    		if( timeoutListener != null ) {
    			timeoutListener.onSessionTimeout(sessionUUID, sessionStartTime, sessionLastActivityTime);
    		}
    		closeSession();
    	}
    }
    
	public synchronized String getSessionUUID() {
		return sessionUUID;
	}
	
	public synchronized Date getSessionStartTime() {
		return sessionStartTime;
	}
	
	public synchronized Date getSessionLastActivityTime() {
		return sessionLastActivityTime;
	}
	
	public synchronized void resetSessionTimeoutTimer() {
		sessionTimeoutHandler.removeCallbacks(sessionTimeoutCallback);
        sessionTimeoutHandler.postDelayed(sessionTimeoutCallback,sessionTimeoutMillis);
	}
	
	public synchronized boolean isSessionValid() {
		return( sessionUUID != null );
	}
	
	public synchronized boolean closeSession() {
		boolean closedASession = false;
		if( sessionUUID != null )
		{
			closedASession = true;
		}
		
		sessionUUID = null;
		sessionStartTime = null;
		sessionLastActivityTime = null;
		
		return closedASession;
	}
	
	public synchronized String openSession() {
		sessionUUID = UUID.randomUUID().toString();
		long currentTimeMillis = System.currentTimeMillis();
		sessionStartTime = new Date(currentTimeMillis);
		sessionLastActivityTime = null;
		resetSessionTimeoutTimer();
		return sessionUUID;
	}
	
	public synchronized void onUserInteraction() {
		if (this.isPaused) {
			this.isPaused = false;
			resetSessionTimeoutTimer();
		}
	}
	
	public synchronized void pause() {
		if (!this.isPaused) {
			this.isPaused = true;
		}
	}
	
	public synchronized void resume() {
		this.isPaused = false;
		resetSessionTimeoutTimer();
	}
	
	public synchronized boolean isPaused() {
		return this.isPaused;
	}

}
