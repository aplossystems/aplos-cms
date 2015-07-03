package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.beans.Competition;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;

@Entity
@DynamicMetaValueKey(oldKey="COMPETITION_MODULE")
public class CompetitionModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -7861872698586808031L;
	@ManyToOne
	private Competition competition;

	public CompetitionModule() {
		super();
	}

	@Override
	public String getName() {
		return "Competition Handler";
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	public Competition getCompetition() {
		return competition;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		CompetitionModule competitionModule = new CompetitionModule();
		competitionModule.setCompetition(competition);
		return competitionModule;
	}

}
