package com.aplos.cms.beans.pages;

import com.aplos.common.annotations.persistence.Entity;

@Entity
public class CmsPageLink extends CmsPage {
	private static final long serialVersionUID = -1647196392946624892L;
	
	private String linkUrl;
	
	public CmsPageLink() {
	}
	
	@Override
	public String getMapping() {
		return getLinkUrl();
	}

	public CmsPageLink( String linkUrl ) {
		this.linkUrl = linkUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
}
