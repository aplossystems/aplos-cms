package com.aplos.cms.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionPrize;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.cms.module.CmsConfiguration.CmsBulkMessageFinderEnum;
import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
public class CompetitionNonClaimantEmail extends SourceGeneratedEmailTemplate<CompetitionPrizeWinner> {

	private static final long serialVersionUID = 727058876766443021L;

	public CompetitionNonClaimantEmail() {
	}
	
	public String getDefaultName() {
		return "Competition non claimants";
	}

	@Override
	public String getDefaultSubject() {
		return "{COMPANY_NAME} competititon";
	}
	
	@Override
	public BulkMessageSourceGroup getBulkMessageSourceGroup() {
		return Website.getCurrentWebsiteFromTabSession().getBulkMessageFinderMap().get( CmsBulkMessageFinderEnum.COMPETITION_PRIZE_WINNER );
	}
	
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "competitionNonClaimantEmail.html" );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			CompetitionPrizeWinner competitionPrizeWinner) {
		super.addContentJDynamiTeValues(jDynamiTe, competitionPrizeWinner);
		jDynamiTe.setVariable("RECIPIENT_NAME", WordUtils.capitalize(competitionPrizeWinner.getSubscriber().getFullName()));
		jDynamiTe.setVariable("COMPETITION_NAME", competitionPrizeWinner.getCompetitionPrize().getCompetition().getCompetitionName());
		jDynamiTe.setVariable("PRIZE_POSITION", FormatUtil.getTitledPosition( competitionPrizeWinner.getCompetitionPrize().getPositionIdx() + 1 ) + " prize" );
		if (competitionPrizeWinner.getCompetitionPrize().getPrizeCount() > 1) {
			jDynamiTe.setVariable("PRIZE_NAME", "1 of " + competitionPrizeWinner.getCompetitionPrize().getPrizeCount() + " " + competitionPrizeWinner.getCompetitionPrize().getPrizeName() + "s");
		} else {
			jDynamiTe.setVariable("PRIZE_NAME", "1 " + competitionPrizeWinner.getCompetitionPrize().getPrizeName());
		}

		Long cmsPageRevision_id = (Long) ApplicationUtil.getFirstResult("SELECT CmsPageRevision_id FROM cmspagerevision_cmsatomlist WHERE cmsAtom_id = (SELECT id FROM unconfigurabledevelopercmsatom WHERE cmsAtomIdStr='competitionClaim' LIMIT 1) LIMIT 1")[0];
		CmsPageRevision cmsPageRevision = null;
		if (cmsPageRevision_id != null) {
			cmsPageRevision = new BeanDao(CmsPageRevision.class).get(cmsPageRevision_id);
		}

		if (cmsPageRevision != null) {
			AplosUrl aplosUrl = CommonUtil.getExternalPageUrl( new CmsPageUrl( cmsPageRevision.getCmsPage() ) );
			aplosUrl.addQueryParameter( "competition", competitionPrizeWinner.getCompetitionPrize().getCompetition() );
			aplosUrl.addQueryParameter( "prize", competitionPrizeWinner.getCompetitionPrize() );
			aplosUrl.addQueryParameter( "claimCode", competitionPrizeWinner.getClaimCode() );
			jDynamiTe.setVariable("CLAIM_PRIZE_URL", aplosUrl.toString() );
		} else {
			jDynamiTe.setVariable("CLAIM_PRIZE_URL", JSFUtil.getServerUrl() + "?competition=" + competitionPrizeWinner.getCompetitionPrize().getCompetition().getId() + "&prize=" + competitionPrizeWinner.getCompetitionPrize().getId() + "&claimCode=" + competitionPrizeWinner.getClaimCode() );
			JSFUtil.addMessageForError("Could not locate the page used to claim competition prizes. Make sure you have a page with the required atom on.");
		}
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Competition competition = new Competition();
		competition.setAnnounceDate(new Date());
		competition.setCompetitionDescription("This is the test source competition");
		competition.setCompetitionName("Test Competition");
		CompetitionPrize competitionPrize = new CompetitionPrize();
		competitionPrize.setCompetition(competition);
		competitionPrize.setPrizeCount(1);
		competitionPrize.setPositionIdx(1);
		competitionPrize.setPrizeName("Test Prize");
		List<CompetitionPrize> prizeList = new ArrayList<CompetitionPrize>();
		prizeList.add(competitionPrize);
		competition.setPrizeList(prizeList);
		CompetitionPrizeWinner competitionPrizeWinner = new CompetitionPrizeWinner();
		competitionPrizeWinner.setAddress(adminUser.getAddress());
		competitionPrizeWinner.setClaimCode("T357C0DE");
		competitionPrizeWinner.setCompetitionPrize(competitionPrize);
		competitionPrizeWinner.setOrganisation("Test Organisation");
		competitionPrizeWinner.setSubscriber(new Subscriber(adminUser.getFirstName(), adminUser.getEmail()));
		List<CompetitionPrizeWinner> winners = new ArrayList<CompetitionPrizeWinner>();
		winners.add(competitionPrizeWinner);
		competitionPrize.setWinners(winners);
		return competitionPrizeWinner;
	}


	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return CmsEmailTemplateEnum.COMPETITION_NON_CLAIMANT;
	}
}
