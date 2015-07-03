package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.developercmsmodules.CompetitionModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;

@ManagedBean
@ViewScoped
public class CompetitionBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1135186358977304753L;

	public CompetitionBeDmb() {}

	public SelectItem[] getCompetitionSelectItems() {
		return Competition.getSelectItemBeans( new BeanDao( Competition.class ).setIsReturningActiveBeans(true).getAll() );
	}

	public CompetitionModule getCompetitionModule() {
		return (CompetitionModule) getDeveloperCmsAtom();
	}

}
