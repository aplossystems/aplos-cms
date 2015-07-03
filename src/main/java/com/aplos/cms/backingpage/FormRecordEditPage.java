package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormRecord;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=FormRecord.class)
public class FormRecordEditPage extends EditPage {
	private static final long serialVersionUID = -6494360071156850551L;
	private Form form;
	private int recordIndex;

	public void setFormById(String id) {
		long lid = Long.parseLong( id );
		form = (Form) new BeanDao( Form.class ).get( lid );
		form.addToScope();
	}

	/*
	 * The value submitted is an index starting from 1, so
	 * needs to be decremented.
	 */
	public void setRecordByIndex(String idx) {
		recordIndex = Integer.parseInt( idx );
		FormRecord record = form.getRecords().get( recordIndex - 1 );
		record.addToScope();
	}

}
