package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped // This needs to be available in session due to c:forEach bug.
@AssociatedBean(beanClass=CodeSnippet.class)
public class CodeSnippetEditPage extends EditPage {

	private static final long serialVersionUID = 6273782705124644153L;

	public CodeSnippetEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener(this) {

			@Override
			public void actionPerformed(boolean redirect) {
				CodeSnippet codeSnippet = JSFUtil.getBeanFromScope( CodeSnippet.class );
				codeSnippet.saveDetails();
				super.actionPerformed(redirect);
			}
		});
		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener(this) {

			private static final long serialVersionUID = -4112453661943869689L;

			@Override
			public void actionPerformed(boolean redirect) {
				CodeSnippet codeSnippet = JSFUtil.getBeanFromScope( CodeSnippet.class );
				codeSnippet.saveDetails();
				JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
			}
		});
	}

//	public SelectItem[] getCmsPageRevisionSelectItemBeans() {
//		AqlBeanDao pageDao = new AqlBeanDao(CmsPageRevision.class);
//		pageDao.setIsReturningActiveBeans(true);
//		return AplosAbstractBean.getSelectItemBeansWithNotSelected(pageDao.getAll());
//	}
	
}
