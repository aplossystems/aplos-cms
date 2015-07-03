package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.common.annotations.BackingPageOverride;
import com.aplos.common.backingpage.SiteStructurePage;
import com.aplos.common.tabpanels.MenuTab;

@ManagedBean
@SessionScoped //session for the tree memory
@BackingPageOverride(backingPageClass=SiteStructurePage.class)
public class DynamicCmsMenuListPage extends SiteStructurePage {

	private static final long serialVersionUID = 1700194001537620843L;

	public DynamicCmsMenuListPage() {
		super();
	}

	@Override
	protected String getRootType(MenuTab dynamicMenuTabNode) {
		if ( dynamicMenuTabNode.getWebsite() != null &&
				!(dynamicMenuTabNode.getWebsite() instanceof CmsWebsite) ) {
				return "simple_root";
			} else if ( dynamicMenuTabNode.getWebsite() != null &&
					!((CmsWebsite)dynamicMenuTabNode.getWebsite()).isLive() ) {
				return "inactive_root";
			} else {
				return "root";
			}
	}

}
