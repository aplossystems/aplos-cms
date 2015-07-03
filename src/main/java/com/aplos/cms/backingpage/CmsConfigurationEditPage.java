package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsConfiguration.class)
public class CmsConfigurationEditPage extends EditPage {
	private static final long serialVersionUID = 1389789109275401357L;

	public CmsConfigurationEditPage() {
		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener(this) {

			private static final long serialVersionUID = -3009308883156433380L;

			@Override
			public void actionPerformed(boolean redirect) {
				((CmsConfiguration) JSFUtil.getFromView( CmsConfiguration.class )).saveDetails();
//				HibernateUtil.startNewTransaction();
				JSFUtil.addMessage( ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ) );
				JSFUtil.redirect(getBackingPage().getBeanDao().getListPageClass());
			}
		});
		getBeanDao().setListPageClass( CmsConfigurationEditPage.class );
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		if( JSFUtil.getFromView( CmsConfiguration.class ) == null ) {
			JSFUtil.getViewMap().put( CommonUtil.getBinding( CmsConfiguration.class ), CmsConfiguration.getCmsConfiguration() );
		}
		return true;
	}
}
