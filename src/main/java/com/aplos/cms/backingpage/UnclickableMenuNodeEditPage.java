package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.backingpage.pages.CmsPageRevisionListPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageFolder;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=MenuNode.class)
public class UnclickableMenuNodeEditPage extends EditPage {
	private static final long serialVersionUID = -5921317382622498602L;
	private boolean isUsingMappingPath = false;

	public UnclickableMenuNodeEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener(this) {
			/**
			 *
			 */
			private static final long serialVersionUID = 8972022509089800705L;

			@Override
			public void actionPerformed(boolean redirect) {
				if (saveAction()) {
					JSFUtil.redirect(CmsPageRevisionListPage.class);
				}
			}
		});
		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener(this) {
			/**
			 *
			 */
			private static final long serialVersionUID = 8394949475331011406L;

			@Override
			public void actionPerformed(boolean redirect) {
				if (saveAction()) {
					JSFUtil.redirect(CmsPageRevisionListPage.class);
				}
			}
		});
	}
	
	@Override
	public boolean responsePageLoad() {
		boolean continueLoad = super.responsePageLoad();
		MenuNode menuNode = JSFUtil.getBeanFromScope(MenuNode.class);
		if( menuNode.getCmsPage() == null || menuNode.getCmsPage().getCachedMappingPath() == null ) {
			setUsingMappingPath( false );
		} else {
			setUsingMappingPath( true );
		}
		return continueLoad;
	}
	
	public boolean isUsingMappingPath() {
		return isUsingMappingPath;
	}
	
	public void setUsingMappingPath( boolean isUsingMappingPath ) {
		this.isUsingMappingPath = isUsingMappingPath;
	}

	public boolean saveAction() {
		MenuNode menuNode = JSFUtil.getBeanFromScope(MenuNode.class);
		CmsPage cmsPage = menuNode.getCmsPage();
		boolean wasUsingMappingPath = menuNode.getCmsPage() != null && menuNode.getCmsPage().getCachedMappingPath() != null;

		if( menuNode.isNew() ) {
			menuNode.saveDetails();
			MenuNode loadedUnusedMenu = ((CmsWebsite)Website.getCurrentWebsiteFromTabSession()).getUnusedMenu(); 
			loadedUnusedMenu.addChild(menuNode);
			loadedUnusedMenu.saveDetails();
//			HibernateUtil.getCurrentSession().flush();
		}
		
		
		if( wasUsingMappingPath || isUsingMappingPath ) {
			if( cmsPage == null ) {
				cmsPage = new CmsPageFolder();
				menuNode.setCmsPage(cmsPage);
			} 
			if( isUsingMappingPath() ) {
				cmsPage.setMapping( menuNode.getName() );
			} else if( !isUsingMappingPath() ) {
				cmsPage.setMapping( null );
			}
			cmsPage.setName(menuNode.getName());
			cmsPage.saveDetails();
			cmsPage.updateCachedMappingPath();
			cmsPage.saveDetails();
			
			menuNode.saveDetails();
			menuNode.updateChildrensCachedMappingPath(true);
		}
		return true;
	}

}
