package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class CheckboxElement extends MultipleChoiceElement {

	private static final long serialVersionUID = -2461300484901796026L;

	@Override
	public String getHtml() {
		StringBuffer html = new StringBuffer();
		for (String o : getOptions()) {
			// Separate element and option by something we can split on in submission
			// handler, using '////'
			html.append( "<input type='checkbox' name='" + getName() + "////" + o + "' value='" + o + "'>" + o + "<br />" );
		}
		return html.toString();
	}

}
