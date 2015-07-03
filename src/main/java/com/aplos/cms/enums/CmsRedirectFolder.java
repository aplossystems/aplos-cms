package com.aplos.cms.enums;

import com.aplos.common.LabeledEnumInter;

public enum CmsRedirectFolder implements LabeledEnumInter {
	BASICS ( "basic", "content_id" ),
	ARTICLES ( "article", "content_id" ),
	CASE_STUDIES ( "portfolio", "caseStudyId" );
	
	private String label;
	private String contentIdKey;
	
	private CmsRedirectFolder( String label, String contentIdKey ) {
		this.label = label;
		this.contentIdKey = contentIdKey;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	public String getContentIdKey() {
		return contentIdKey;
	}
}
