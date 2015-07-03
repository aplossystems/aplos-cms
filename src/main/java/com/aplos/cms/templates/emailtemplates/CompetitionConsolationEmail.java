package com.aplos.cms.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.WordUtils;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.cms.module.CmsConfiguration.CmsBulkMessageFinderEnum;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;

@Entity
public class CompetitionConsolationEmail extends SourceGeneratedEmailTemplate<CompetitionEntry> {

	private static final long serialVersionUID = 727058876766443021L;

	public CompetitionConsolationEmail() {
	}
	
	public String getDefaultName() {
		return "Competition consolation";
	}

	@Override
	public String getDefaultSubject() {
		return "{COMPANY_NAME} competition results";
	}
	
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "competitionConsolationEmail.html" );
	}
	
	@Override
	public BulkMessageSourceGroup getBulkMessageSourceGroup() {
		return Website.getCurrentWebsiteFromTabSession().getBulkMessageFinderMap().get( CmsBulkMessageFinderEnum.COMPETITION_ENTRY );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		CompetitionEntry competitionEntry = new CompetitionEntry();
		Competition competition = new Competition();
		competition.setAnnounceDate(new Date());
		competition.setCompetitionDescription("This is the test source competition");
		competition.setCompetitionName("Test Competition");
		competitionEntry.setCompetition(competition);
		competitionEntry.setSubscriber(new Subscriber(adminUser.getFirstName(), adminUser.getEmail()));
		return competitionEntry;
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			CompetitionEntry competitionEntry) {
		super.addContentJDynamiTeValues(jDynamiTe, competitionEntry);
		jDynamiTe.setVariable("RECIPIENT_NAME",  WordUtils.capitalize(competitionEntry.getSubscriber().getFullName()) );
		jDynamiTe.setVariable("COMPETITION_NAME", competitionEntry.getCompetition().getCompetitionName());
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return CmsEmailTemplateEnum.COMPETITION_CONSULATION;
	}

}
