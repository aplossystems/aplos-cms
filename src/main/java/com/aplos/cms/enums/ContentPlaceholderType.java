package com.aplos.cms.enums;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.aplos.common.LabeledEnumInter;

public enum ContentPlaceholderType implements LabeledEnumInter {
	EDITOR("Editor"),
	CODE("Code"),
	CMS_ATOM("Cms Atom"),
	CODE_SNIPPET("Code Snippet");

	private ContentPlaceholderType(String label) { this.label = label; }

	private final String label;
	@Override
	public String getLabel() { return label; }
	public String getName() { return name();  }


	public static List<ContentPlaceholderType> getUserAvailableCphTypeList() {
		List<ContentPlaceholderType> cphTypeList = new ArrayList<ContentPlaceholderType>();
		cphTypeList.add( EDITOR );
		cphTypeList.add( CODE );

		return cphTypeList;
	}

	public List<SelectItem> getCphTypeSelectItems() {
		List<SelectItem> cphTypeSelectItems = new ArrayList<SelectItem>();
		for (ContentPlaceholderType cphType : ContentPlaceholderType.getUserAvailableCphTypeList()) {
			cphTypeSelectItems.add( new SelectItem(cphType, cphType.getLabel()) );
		}
		return cphTypeSelectItems;
	}
};
