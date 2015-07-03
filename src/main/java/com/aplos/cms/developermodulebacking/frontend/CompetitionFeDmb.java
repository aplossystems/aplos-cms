package com.aplos.cms.developermodulebacking.frontend;

import java.util.Date;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.cms.beans.CompetitionPrize;
import com.aplos.cms.beans.developercmsmodules.CompetitionModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CompetitionFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5854923031086058738L;

	private CompetitionModule competitionModule;
	private String entryName;
	private String entryEmailAddress;
	private String completeMessage=null;
	public CompetitionFeDmb() {}

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad( developerCmsAtom);
		competitionModule = (CompetitionModule) getDeveloperCmsAtom();
		return true;
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("aplos-competition-sumbit");
	}

	public String getPrizeOverviewText() {
		StringBuffer buff = new StringBuffer("<h4>");
		if (competitionModule == null ||
			competitionModule.getCompetition() == null ||
			competitionModule.getCompetition().getPrizeList() == null ||
			competitionModule.getCompetition().getPrizeList().size() < 1) {
			buff.append("No prizes defined");
			buff.append("</h4>");
		} else {
			List<CompetitionPrize> sortedPrizeList = competitionModule.getCompetition().getSortedPrizeList();
			for (int i=0; i < sortedPrizeList.size(); i++) {
				if (i > 0) {
					buff.append("<h5>");
				}
				CompetitionPrize competitionPrize = sortedPrizeList.get(i);
				if (competitionPrize.getPrizeCount() > 1) {
					buff.append("O");
					if (i == 1) {
						buff.append("r o");
					}
					buff.append("ne of ");
					buff.append(competitionPrize.getPrizeCount());
					buff.append(" ");
				} else if (i > 0) {
					buff.append("Or the ");
				}
				if (i > 0) {
					buff.append("runner up prize");
					if (competitionPrize.getPrizeCount() > 1) {
						buff.append("s");
					}
					if (competitionPrize.getPrizeCount() > 1) {
						buff.append(" of ");
					} else {
						buff.append(" ");
					}
				}
				buff.append(competitionPrize.getPrizeName());
				if (i ==0) {
					buff.append("</h4>");
				} else {
					buff.append("</h5>");
				}
			}
		}
		return buff.toString();
	}

	public void setCompetitionModule(CompetitionModule competitionModule) {
		this.competitionModule = competitionModule;
	}

	public CompetitionModule getCompetitionModule() {
		return competitionModule;
	}

	public boolean isCompetitionOpen() {
		return competitionModule.getCompetition().getEndDate().compareTo(new Date()) >= 0;
	}

	public String submitEntry() {

		if (CommonUtil.validateEmailAddressFormat(entryEmailAddress)) {
			//http://code.google.com/apis/recaptcha/docs/java.html
			String remoteAddr = JSFUtil.getRequest().getRemoteAddr();
		    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		    reCaptcha.setPrivateKey("6Ldmjb4SAAAAAJ088BY8WYglz4rcA1VM7f_3EoGG");
		    String challenge = JSFUtil.getRequest().getParameter("recaptcha_challenge_field");
	        String userresponse = JSFUtil.getRequest().getParameter("recaptcha_response_field");
		    ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, userresponse);
			if (reCaptchaResponse.isValid()) {
				Subscriber entrant = new Subscriber();
				entrant.setSubscriptionHook(SubscriptionHook.COMPETITION);
				entrant.setEmailAddress(entryEmailAddress);
				entrant.setFirstName(entryName);
				//make sure subscriber exists, if a previous exists the above information will be replaced
				entrant = (Subscriber)ApplicationUtil.saveNewOrLoadBeanByUniqueValue(entrant, "EmailAddress");
				Competition competition = competitionModule.getCompetition();
				CompetitionEntry entry = new CompetitionEntry();
				entry.setCompetition(competition);
				entry.setSubscriber(entrant);
				if (competition.getMaxEntriesPerUser() > 0) {
					BeanDao countDao = new BeanDao(CompetitionEntry.class);
					countDao.setWhereCriteria("bean.competition.id=" + competition.getId());
					countDao.addWhereCriteria("bean.subscriber.id=" + entrant.getId());
					int previousEntryCount = countDao.setIsReturningActiveBeans(true).getCountAll();
					if (previousEntryCount < competition.getMaxEntriesPerUser()) {
						entry.saveDetails();
						completeMessage = "Your entry has been registered, we will be in contact should you win!";
					} else {
						completeMessage = "Sorry, we couldn't register your entry - you are not allowed any more entries.";
					}
				} else {
					entry.saveDetails(); //we dont care about existing entries here
					completeMessage = "Your entry has been registered, we will be in contact should you win!";
				}
			} else {
				completeMessage = "The words you entered in response to the reCaptcha challenge were incorrect.";
			}
		} else {
			completeMessage = "Please re-enter email address in a valid format.";
		}
		return null;
	}

	public String getEndDateString() {
		return FormatUtil.formatDate(competitionModule.getCompetition().getEndDate());
	}

	public String getAnnounceDateString() {
		return FormatUtil.formatDate(competitionModule.getCompetition().getAnnounceDate());
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryEmailAddress(String entryEmailAddress) {
		this.entryEmailAddress = entryEmailAddress;
	}

	public String getEntryEmailAddress() {
		return entryEmailAddress;
	}

	public void setCompleteMessage(String completeMessage) {
		this.completeMessage = completeMessage;
	}

	public String getCompleteMessage() {
		return completeMessage;
	}

}














