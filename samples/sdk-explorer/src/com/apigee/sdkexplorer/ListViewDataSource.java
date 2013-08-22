package com.apigee.sdkexplorer;

import android.view.View;
import android.widget.ListView;

public interface ListViewDataSource
{
	public int numberOfSectionsInListView(ListView listView);
	public int listViewNumberOfRowsInSection(ListView listView, int section);
	public String listViewTitleForHeaderInSection(ListView listView, int section);
	public View listViewCellForRowAtIndexPath(ListView listView, View convertView, IndexPath indexPath);
}
