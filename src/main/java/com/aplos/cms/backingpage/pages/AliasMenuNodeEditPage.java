package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageAlias;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsPageAlias.class)
public class AliasMenuNodeEditPage extends EditPage {

	private static final long serialVersionUID = -899064810689206261L;


	public void goToOriginal() {
		CmsPageAlias cmsPageAlias = JSFUtil.getBeanFromScope(CmsPageAlias.class);
		if (cmsPageAlias != null) {
			cmsPageAlias.getCmsPage().getLatestRevision().redirectToEditPage();
			
		}
	}

	public void deleteShortcut() {
		CmsPageAlias cmsPageAlias = JSFUtil.getBeanFromScope(CmsPageAlias.class);
		if (cmsPageAlias != null) {
			BeanDao menuNodeDao = new BeanDao( MenuNode.class );
			menuNodeDao.addWhereCriteria( "cmsPage.id = " + cmsPageAlias.getId() );
			MenuNode menuNode = (MenuNode) menuNodeDao.getFirstBeanResult();
			if (menuNode.getParent() != null && menuNode.getParent().getChildren() != null) {
				menuNode.getParent().getChildren().remove( menuNode );
				menuNode.getParent().saveDetails();
			}
//			HibernateUtil.startNewTransaction();
			menuNode.hardDelete();
			cmsPageAlias.hardDelete();
		}
		JSFUtil.redirect( CmsPageRevisionListPage.class );
	}

}
