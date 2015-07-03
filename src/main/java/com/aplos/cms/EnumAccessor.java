package com.aplos.cms;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.enums.ContentPlaceholderType;

@ManagedBean
@SessionScoped
public class EnumAccessor {
	public ContentPlaceholderType getCphType() {
		if( ContentPlaceholderType.values().length > 0 ) {
			return ContentPlaceholderType.values()[ 0 ];
		} else {
			return null;
		}
	}
}
