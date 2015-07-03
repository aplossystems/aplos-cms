package com.aplos.cms.beans.developercmsmodules;

import com.aplos.common.annotations.persistence.Entity;

@Entity
public class UnsubscribeAtom extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = 5759586134628597516L;

	public UnsubscribeAtom() {
		super();
	}

	@Override
	public String getName() {
		return "Unsubscribe";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		return new UnsubscribeAtom();
	}

}
