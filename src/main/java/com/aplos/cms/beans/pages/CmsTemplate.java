package com.aplos.cms.beans.pages;

import java.util.List;

public interface CmsTemplate {
	public Long getId();
	public boolean hasTemplateChanged(CmsTemplate cmsTemplate);
	public List<String> getCphNameList(boolean toLowerCase);
	public List<String> getEditableCphNameList( boolean isForCmsPageRevision, boolean toLowerCase );
	public boolean cmsAtomPassThroughListContains( Long cmsAtomPassThroughId );
	public List<CmsAtomPassThrough> getCmsAtomPassThroughList();
	public String generateXhtml();
	public void redirectToEditPage();
}
