package com.aplos.cms;

import java.io.Serializable;
import java.util.Map;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;

public class PlaceholderContentWrapper implements Serializable {

	private static final long serialVersionUID = -6407982067139545406L;
	private String cphName;
	private PlaceholderContent placeholderContent;
	private Map<String, CmsPlaceholderContent> phcMap;

	public PlaceholderContentWrapper() {}

	public String getDisplayName() {
		if( placeholderContent.getCphType().equals( ContentPlaceholderType.CMS_ATOM ) ) {
			return ((DeveloperCmsAtom) placeholderContent).getDisplayName();
		} else {
			return getCphName();
		}
	}

	public void setPlaceholderContent(PlaceholderContent placeholderContent) {
		this.placeholderContent = placeholderContent;
	}

	public PlaceholderContent getPlaceholderContent() {
		return placeholderContent;
	}

	public ContentPlaceholderType getCphType() {
		return placeholderContent.getCphType();
	}

	public void setCphType( ContentPlaceholderType cphType ) {
		if( getPhcMap().get( getCphName() ) instanceof CmsPlaceholderContent ) {
			getPhcMap().get( getCphName() ).setCphType(cphType);
		}
	}

	public void setCphName(String cphName) {
		this.cphName = cphName;
	}

	public String getCphName() {
		return cphName;
	}

	public void setPhcMap(Map<String, CmsPlaceholderContent> phcMap) {
		this.phcMap = phcMap;
	}

	public Map<String, CmsPlaceholderContent> getPhcMap() {
		return phcMap;
	}
}
