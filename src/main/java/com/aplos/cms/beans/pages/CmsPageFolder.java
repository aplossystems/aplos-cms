package com.aplos.cms.beans.pages;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;


@Entity
@Cache
@DiscriminatorValue("CmsPageFolder")
public class CmsPageFolder extends CmsPage {
	private static final long serialVersionUID = 9131178929829977014L;

	public CmsPageFolder() {}
}
