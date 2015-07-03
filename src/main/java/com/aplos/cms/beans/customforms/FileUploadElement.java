package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class FileUploadElement extends FormElement {

	private static final long serialVersionUID = 4879670239700482988L;

	@Override
	public String getHtml() {
		return "<input type='file'"
			+ " class='" + (isRequired() ? "required " : "")
			+ " ' name='" + getName() + "' />";
	}


}
