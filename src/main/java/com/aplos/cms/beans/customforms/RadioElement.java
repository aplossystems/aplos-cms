package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class RadioElement extends MultipleChoiceElement {

	private static final long serialVersionUID = -2461300484901796026L;

	@Override
	public String getHtml() {
		StringBuffer html = new StringBuffer();
		for (String o : getOptions()) {
			html.append( "<input type='radio' name='" + getName() + "' value='" + o + "'>" + o + "<br />" );
		}
		return html.toString();
	}

}
