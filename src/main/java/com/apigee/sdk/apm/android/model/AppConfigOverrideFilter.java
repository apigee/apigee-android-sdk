package com.apigee.sdk.apm.android.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

//import com.apigee.sdk.android.model.FILTER_TYPE;
import com.apigee.sdk.apm.android.model.App;

/**
 * 
 * @author prabhat
 * 
 */
public class AppConfigOverrideFilter implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 1L;

	public enum FILTER_TYPE {

		DEVICE_NUMBER, DEVICE_ID, DEVICE_MODEL, DEVICE_PLATFORM, NETWORK_TYPE, NETWORK_OPERATOR
	}

	private Long id;

	private App application;

	private String filterValue;

	protected FILTER_TYPE filterType;

	public AppConfigOverrideFilter() {
	}

	public AppConfigOverrideFilter(String filterValue, FILTER_TYPE filterType,
			App app) {
		this.filterValue = filterValue;
		this.filterType = filterType;
		this.application = app;
		app.addAppConfigOverrideFilters(this);
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FILTER_TYPE getFilterType() {
		return filterType;
	}

	public void setFilterType(FILTER_TYPE filterType) {
		this.filterType = filterType;
	}

	@JsonBackReference
	public App getApplication() {
		return application;
	}

	public void setApplication(
			App application) {
		this.application = application;
	}

	public String toString() {
		return "Filter for " + filterType + " is " + filterValue;
	}
}
