package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class ListBoxElement extends MultipleChoiceElement {
	private static final long serialVersionUID = 2460406506571560553L;

	@Override
	public String getHtml() {
		StringBuffer html = new StringBuffer("<select multiple='multiple' name='" + getName() + "' >\n");
		for (String o : getOptions()) {
			html.append( "<option>" + o + "</option>" );
		}
		html.append("</select><br />\n");
		return html.toString();
	}

}
