package com.aplos.cms.beans.customforms;

import com.aplos.common.annotations.persistence.Entity;


@Entity
public class DropDownElement extends MultipleChoiceElement {
	private static final long serialVersionUID = -6508035721489759254L;

	@Override
	public String getHtml() {
		StringBuffer html = new StringBuffer("<select name='" + getName() + "' >\n");
		for (String o : getOptions()) {
			html.append( "<option>" + o + "</option>" );
		}
		html.append("</select><br />\n");
		return html.toString();
	}

}
