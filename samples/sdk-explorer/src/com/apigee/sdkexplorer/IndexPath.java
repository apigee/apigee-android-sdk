package com.apigee.sdkexplorer;

public class IndexPath
{
	public int section;
	public int row;
	
	public IndexPath()
	{
		section = 0;
		row = 0;
	}
	
	public IndexPath(int section, int row)
	{
		this.section = section;
		this.row = row;
	}
}
