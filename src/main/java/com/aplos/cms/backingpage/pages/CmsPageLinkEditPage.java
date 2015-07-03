package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageLink;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Website;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsPageLink.class)
public class CmsPageLinkEditPage extends EditPage {
	private static final long serialVersionUID = 1002148855000594823L;

	@Override
	public boolean saveBean() {
		CmsPageLink cmsPageLink = resolveAssociatedBean();
		boolean wasNew = cmsPageLink.isNew();
		boolean continueSave = super.saveBean();

		if( wasNew && continueSave ) {
			MenuNode linkNode = new MenuNode();
			linkNode.setParentWebsite(Website.getCurrentWebsiteFromTabSession());
			((CmsWebsite)Website.getCurrentWebsiteFromTabSession()).getUnusedMenu().addChild(linkNode);
			linkNode.setCmsPage(cmsPageLink);
			linkNode.saveDetails();
		}
		return continueSave;
	}
}
