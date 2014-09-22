package com.apigee.sdkexplorer;

public class NameDescription {
	private String name;
	private String description;
	
	public NameDescription(String name,String description)
	{
		super();
		this.name = name;
		this.description = description;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
