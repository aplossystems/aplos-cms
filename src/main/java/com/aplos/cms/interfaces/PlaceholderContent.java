package com.aplos.cms.interfaces;

import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.common.beans.SystemUser;

public interface PlaceholderContent {
	public String getContent();
	public ContentPlaceholderType getCphType();
	public boolean saveDetails();
	public boolean saveDetails( SystemUser currentUser );
}
