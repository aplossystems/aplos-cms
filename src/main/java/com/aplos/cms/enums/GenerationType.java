package com.aplos.cms.enums;

import com.aplos.common.LabeledEnumInter;

public enum GenerationType implements LabeledEnumInter {
	NONE ("None"),
	GENERATOR ("Generator"),
	MANUAL_LIST ("Manual list");
	
	private String label;
	
	private GenerationType( String label ) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	};
}
