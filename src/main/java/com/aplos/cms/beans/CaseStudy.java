package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;

@Entity
public class CaseStudy extends BasicCmsContent {
	private static final long serialVersionUID = -7527749395221429692L;

	private int views;
	@CollectionOfElements
	private List<String> keywords = new ArrayList<String>();
	private String websiteUrl;
	private String location;

	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return CmsWorkingDirectory.CASE_STUDY_IMAGE_DIR;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
