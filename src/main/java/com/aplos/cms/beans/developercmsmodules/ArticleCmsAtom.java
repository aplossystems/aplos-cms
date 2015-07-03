package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.interfaces.BasicCmsContentAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="ARTICLE_ATOM")
public class ArticleCmsAtom extends ConfigurableDeveloperCmsAtom implements BasicCmsContentAtom {
	private static final long serialVersionUID = 1956091887950233732L;

	public ArticleCmsAtom() {
		super();
	}

	@Override
	public String getName() {
		return "Article";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ArticleCmsAtom copiedAtom = new ArticleCmsAtom();
		return copiedAtom;
	}
}
