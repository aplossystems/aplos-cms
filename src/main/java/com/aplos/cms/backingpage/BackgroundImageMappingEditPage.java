package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.BackgroundImageMapping;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BackgroundImageMapping.class)
public class BackgroundImageMappingEditPage extends EditPage {
	private static final long serialVersionUID = 3860295855362709100L;

	@Override
	public void okBtnAction() {
		if (saveAction()) {
			JSFUtil.redirect(BackgroundImageMappingListPage.class);
			//return new BackingPageUrl(BackgroundImageMappingListPage.class);
		}
	}

	@Override
	public void applyBtnAction() {
		saveAction();
	}

	public boolean saveAction() {
		BackgroundImageMapping backgroundImageMapping = JSFUtil.getBeanFromScope(BackgroundImageMapping.class);
		backgroundImageMapping.saveDetails();
		JSFUtil.addMessage("Saved Successfully");
		return true;
	}
}
