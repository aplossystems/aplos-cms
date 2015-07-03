package com.aplos.cms.beans.customforms;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;


@Entity
public abstract class MultipleChoiceElement extends FormElement {

	/**
	 *
	 */
	private static final long serialVersionUID = 3278093821729612538L;
	@CollectionOfElements
	@IndexColumn(name="position")
	private List<String> options = new ArrayList<String>();

	public MultipleChoiceElement() {
		options.add( "Option One" );
	}

	public void setOptions( List<String> options ) {
		this.options = options;
	}

	public List<String> getOptions() {
		return options;
	}

}
