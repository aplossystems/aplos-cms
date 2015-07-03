package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.backingpage.pages.CmsPageRevisionListPage;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=MenuNode.class)
public class GeneratorMenuNodeEditPage extends EditPage {

	private static final long serialVersionUID = -8723256258134711727L;

	public GeneratorMenuNodeEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener(this) {

			private static final long serialVersionUID = 7678679380476438147L;

			@Override
			public void actionPerformed(boolean redirect) {
				if (saveAction()) {
					MenuNode menuNode = JSFUtil.getBeanFromScope(MenuNode.class);
					if (menuNode != null && menuNode.getCmsPage() != null) {
						((CmsPageGenerator)menuNode.getCmsPage()).saveAtom();
					}
					JSFUtil.redirect(CmsPageRevisionListPage.class);
				}
			}
		});
		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener(this) {

			private static final long serialVersionUID = -7370014636139503342L;

			@Override
			public void actionPerformed(boolean redirect) {
				if (saveAction()) {
					MenuNode menuNode = JSFUtil.getBeanFromScope(MenuNode.class);
					if (menuNode != null && menuNode.getCmsPage() != null) {
						((CmsPageGenerator)menuNode.getCmsPage()).saveAtom();
					}
					JSFUtil.redirect(CmsPageRevisionListPage.class);
				}
			}
		});
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		MenuNode menuNode = JSFUtil.getBeanFromScope(MenuNode.class);
		if (menuNode != null && menuNode.getCmsPage() != null) {
			((CmsPageGenerator)menuNode.getCmsPage()).initAtomBackend();
		}
		return true;
	}

	public boolean saveAction() {
		MenuNode menuNode = (MenuNode) JSFUtil.getBeanFromScope(MenuNode.class);
		menuNode.saveDetails();
		return true;
	}

	public void deleteGenerator() {
		//TODO
//		CmsPageGenerator cmsPageGen = JSFUtil.getBeanFromScope(CmsPageGenerator.class);
//		if (cmsPageGen != null) {
//			AqlBeanDao menuNodeDao = new AqlBeanDao( MenuNode.class );
//			menuNodeDao.addWhereCriteria( "cmsPage.id = " + cmsPageGen.getId() );
//			MenuNode menuNode = (MenuNode) menuNodeDao.get();
//			if (menuNode.getParent() != null && menuNode.getParent().getChildren() != null) {
//				menuNode.getParent().getChildren().remove( menuNode );
//				menuNode.getParent().aqlSaveDetails();
//			}
//			HibernateUtil.startNewTransaction();
//			menuNode.hardDelete();
//			cmsPageGen.delete();
//		}
		JSFUtil.redirect( CmsPageRevisionListPage.class );
	}

}
