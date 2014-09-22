package com.apigee.sdkexplorer;

import java.util.Comparator;

public class ArtistSearchServiceSortByName implements Comparator<ArtistSearchService>
{
	@Override
	public int compare(ArtistSearchService o1, ArtistSearchService o2) 
	{
	    return o1.name.compareToIgnoreCase(o2.name);
	}
}
