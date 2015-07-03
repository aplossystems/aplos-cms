package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.ArticleCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@ViewScoped
public class NewsBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5847775562741108256L;
	private ArticleCmsAtom articleCmsAtom;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setArticleCmsAtom((ArticleCmsAtom) developerCmsAtom);
		return true;
	}

	public ArticleCmsAtom getArticleCmsAtom() {
		return articleCmsAtom;
	}

	public void setArticleCmsAtom(ArticleCmsAtom articleCmsAtom) {
		this.articleCmsAtom = articleCmsAtom;
	}

}
