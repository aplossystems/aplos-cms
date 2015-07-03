package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Form.class)
public class FormListPage extends ListPage {

	private static final long serialVersionUID = -6705915343518955025L;

	public void goToFormRecord() {
		Form form = (Form)JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN );
		form.redirectToEditPage();
	}

}
