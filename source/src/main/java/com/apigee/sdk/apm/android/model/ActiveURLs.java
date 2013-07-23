package com.apigee.sdk.apm.android.model;

import java.io.Serializable;

public class ActiveURLs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;

	private Long count = 0L;

	private int rank;

	public ActiveURLs(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
