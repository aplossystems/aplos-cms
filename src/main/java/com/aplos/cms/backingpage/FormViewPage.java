package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormRecord;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Form.class)
public class FormViewPage extends EditPage {
	private static final long serialVersionUID = -2052680959897354400L;

	public void createTestEntry() {
		Form form = (Form) resolveAssociatedBean();

		FormRecord entry = new FormRecord();
		entry.getFields().put( form.getElements().get( 0 ).getId(), "Dominic" );
		entry.getFields().put( form.getElements().get( 1 ).getId(), "dominic@life.co.uk" );
		entry.getFields().put( form.getElements().get( 2 ).getId(), "Student" );
		entry.setForm( form );
		entry.saveDetails();
		form.saveDetails();

	}

}
