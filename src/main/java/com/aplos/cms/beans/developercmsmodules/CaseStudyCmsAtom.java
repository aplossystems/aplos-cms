package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.interfaces.BasicCmsContentAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="CASE_STUDY_ATOM")
public class CaseStudyCmsAtom extends ConfigurableDeveloperCmsAtom implements BasicCmsContentAtom {
	private static final long serialVersionUID = 1956091887950233732L;

	public CaseStudyCmsAtom() {
		super();
	}

	@Override
	public String getName() {
		return "Case study";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		CaseStudyCmsAtom copiedAtom = new CaseStudyCmsAtom();
		return copiedAtom;
	}
}
