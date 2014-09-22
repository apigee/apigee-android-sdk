package com.apigee.sdkexplorer;

import java.util.HashMap;

import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SectionedListViewAdapter implements ListAdapter
{
	private ListView listView;
	private ListViewDataSource dataSource;
	private int numberOfSections;
	private int numberSectionHeaders;
	private int numberRegularRows;
	private int numberViews;
	private HashMap<Integer,IndexPath> mapPosition;

	
    public SectionedListViewAdapter(ListView listView, ListViewDataSource dataSource)
    {
    	this.listView = listView;
    	this.dataSource = dataSource;
    	mapPosition = new HashMap<Integer,IndexPath>();
    	
    	if( dataSource != null )
    	{
    		numberOfSections = dataSource.numberOfSectionsInListView(listView);
    		numberSectionHeaders = 0;
    		numberRegularRows = 0;
    		int positionIndex = 0;
    		
    		for( int sectionIndex = 0; sectionIndex < numberOfSections; ++sectionIndex )
    		{
    			int numberRowsInSection = dataSource.listViewNumberOfRowsInSection(listView, sectionIndex);
    			numberRegularRows += numberRowsInSection;
    			
    			String sectionHeader = dataSource.listViewTitleForHeaderInSection(listView, sectionIndex);
    			if( sectionHeader != null )
    			{
    				mapPosition.put(Integer.valueOf(positionIndex), new IndexPath(sectionIndex,-1));
    				++numberSectionHeaders;
    				++positionIndex;
    			}
    			
    			for( int rowIndex = 0; rowIndex < numberRowsInSection; ++rowIndex )
    			{
    				mapPosition.put(Integer.valueOf(positionIndex), new IndexPath(sectionIndex,rowIndex));
    				++positionIndex;
    			}
    		}
    	}
    	else
    	{
    		numberOfSections = 0;
    		numberSectionHeaders = 0;
    		numberRegularRows = 0;
    	}
    	
    	numberViews = numberSectionHeaders + numberRegularRows;
    }

    @Override
	public boolean areAllItemsEnabled()
    {
    	return numberSectionHeaders == 0;
    }
    
    @Override
	public boolean isEnabled(int position)
    {
    	IndexPath indexPath = mapPosition.get(Integer.valueOf(position));
    	if( indexPath.row == -1 )
    	{
    		return false;
    	}
    	else
    	{
    		return true;
    	}
    }
    
    @Override
	public boolean isEmpty()
    {
    	return numberViews == 0;
    }
    
    @Override
	public boolean hasStableIds()
    {
    	return true;
    }
    
    @Override
	public int getCount()
    {
    	return numberViews;
    }

    @Override
	public int getViewTypeCount()
    {
    	if( numberSectionHeaders > 0 )
    	{
    		return 2;
    	}
    	else
    	{
    		return 1;
    	}
    }
    
    @Override
	public Object getItem(int position)
    {
    	return getView(position,null,null);
    }

    @Override
	public int getItemViewType(int position)
    {
    	return IGNORE_ITEM_VIEW_TYPE;
    }
    
    @Override
	public long getItemId(int position)
    {
        return position;
    }
    
    public View headerView(String title)
    {
    	LinearLayout ll = new LinearLayout(listView.getContext());
    	ll.setOrientation(LinearLayout.VERTICAL);

    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
    	     LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    	layoutParams.setMargins(10, 3, 0, 0);
    	
    	TextView tv = new TextView(listView.getContext());
    	tv.setText(title);
    	tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    	tv.setTypeface(null, Typeface.BOLD);
    	tv.setTextColor(0xFFFFFFFF);
    	ll.setBackgroundColor(0xFF818A93);
    	
    	ll.addView(tv, layoutParams);
    	
    	return ll;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup viewGroup)
    {
    	IndexPath indexPath = mapPosition.get(Integer.valueOf(position));
    	if( indexPath.row == -1 )
    	{
    		String headerTitle = dataSource.listViewTitleForHeaderInSection(listView, indexPath.section);
    		return headerView(headerTitle);
    	}
    	else
    	{
    		return dataSource.listViewCellForRowAtIndexPath(listView, convertView, indexPath);
    	}
    }
    
    @Override
	public void registerDataSetObserver(DataSetObserver observer)
    {
    }
    
    @Override
	public void unregisterDataSetObserver(DataSetObserver observer)
    {
    }
}
