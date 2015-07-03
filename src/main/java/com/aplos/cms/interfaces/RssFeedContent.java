package com.aplos.cms.interfaces;

import java.util.Date;


public interface RssFeedContent {
	public String getFeedContentTitle();
	public String getFeedContentExcerpt();
	public String getFeedContentUrlRelative(); //optional (null)
	public Date getDateCreated();
}
