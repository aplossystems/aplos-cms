package com.aplos.cms.beans;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.annotations.BeanScope;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;

@Entity
@BeanScope(scope=JsfScope.TAB_SESSION)
public class Article extends BasicCmsContent {
	private static final long serialVersionUID = 979019643480651445L;
	
	public Article() {
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return CmsWorkingDirectory.ARTICLE_IMAGE_DIR;
	}
}
