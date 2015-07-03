package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.ApplicationUtil;

@ManagedBean
@ViewScoped
public class SiteMapFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -7090061254734030693L;

	public SiteMapFeDmb() {}

	public String getSideMenuNodeIdStr() {
		try {
			CmsWebsite cmsWebsite = (CmsWebsite)CmsWebsite.getCurrentWebsiteFromTabSession();
			return String.valueOf(ApplicationUtil.getIdFromProxy(cmsWebsite.getSideMenu()));
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public String getMainMenuNodeIdStr() {
		try {
			CmsWebsite cmsWebsite = (CmsWebsite)CmsWebsite.getCurrentWebsiteFromTabSession();
			return String.valueOf(ApplicationUtil.getIdFromProxy(cmsWebsite.getMainMenu()));
		} catch (NullPointerException npe) {
			return null;
		}
	}

}



















