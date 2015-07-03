package com.aplos.cms.beans.customforms;

import java.util.HashMap;
import java.util.Map;

import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
public class FormRecord extends AplosBean {

	private static final long serialVersionUID = -6824426005241021414L;

	@ManyToOne
	@JoinColumn(name="form_id", insertable=false, updatable=false)
	private Form form;

	@CollectionOfElements
	private Map<Long, String> fields = new HashMap<Long, String>();

	@Column(columnDefinition="LONGTEXT")
	private String notes;

	public void setFields( Map<Long, String> fields ) {
		this.fields = fields;
	}

	public Map<Long, String> getFields() {
		return fields;
	}

	public void setForm( Form form ) {
		this.form = form;
	}

	public Form getForm() {
		return form;
	}

	public void setNotes( String notes ) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}
}
