package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class InputTextareaElement extends FormElement {

	private static final long serialVersionUID = 214071908562564644L;

	@Override
	public String getHtml() {
		return "<textarea type='text' name='" + getName() + "'></textarea>";
	}

}
