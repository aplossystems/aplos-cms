package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;

@Entity
@ManagedBean
@SessionScoped
public class Competition extends AplosBean {
	private static final long serialVersionUID = -2426307577228524977L;
	private Date endDate = null;
	private Date announceDate = null;
	private String competitionName;
	@Column(columnDefinition="LONGTEXT")
	private String competitionDescription = "";
	@CollectionOfElements
	@Column(columnDefinition="LONGTEXT")
	private List<String> termsAndConditionsList = new ArrayList<String>();
	private boolean winningEmailsSent = false;
	private boolean consulationEmailsSent = false;
	private boolean nonClaimantEmailsSent = false;

	@OneToMany( mappedBy = "competition" )
	@Cascade({CascadeType.ALL})
	private List<CompetitionPrize> prizeList = new ArrayList<CompetitionPrize>();

	private int maxEntriesPerUser = 1;

	public Competition() {
		super();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 28);
		endDate = cal.getTime();
		cal.add(Calendar.DATE, 2);
		announceDate = cal.getTime();
		//add them to the list so they can be removed if not wanted, rather than adding them at 'runtime'
		termsAndConditionsList.add("If asked, we will provide the name of the winner. You agree that, should you win, you are happy for us to disclose this information.");
		termsAndConditionsList.add("We reserve the right to cancel any prize and ban you from participating in any of our competitons if we reasonably believe you have broken the terms of the competition or have given false information.");
		termsAndConditionsList.add("There is no cash alternative and no purchase is necessary.");
		termsAndConditionsList.add("Prizes are subject to change without notice.");
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getPrizeList(), fullInitialisation);
//		Hibernate.initialize(getTermsAndConditionsList());
//	}

	@Override
	public String getDisplayName() {
		return getCompetitionName();
	}

	public String getWinnerDlItems() {
		StringBuffer winnerText = new StringBuffer("<dl class='aplos-winner'>");
		for (CompetitionPrize prize : getPrizeList()) {
			winnerText.append("<dt>");
			winnerText.append(prize.getPrizeName());
			winnerText.append("</dt><dd>");
			if (prize.isWinnersSelected()) {
				winnerText.append(prize.getWinnersUnorderedList());
			} else {
				winnerText.append("Not Selected");
			}
			winnerText.append("</dd>");
		}
		winnerText.append("</dl>");
		return winnerText.toString();
	}

	public boolean isClosed() {
		return !isNew() && endDate != null && endDate.compareTo(new Date()) < 1;
	}

	public boolean isWinnersSelected() {
		//we might never have enough entries to draw all winenrs, so we need to be able to close early
		//which means the best we can check for is any single winner
		if (getPrizeList() == null || getPrizeList().size() < 1) {
			return false;
		}
		for (CompetitionPrize prize : getPrizeList()) {
			if (prize.isWinnersSelected()) {
				return true;
			}
		}
		return false;
	}

	public void setCompetitionDescription(String competitionDescription) {
		this.competitionDescription = competitionDescription;
	}

	public String getCompetitionDescription() {
		return competitionDescription;
	}

	public void setAnnounceDate(Date announceDate) {
		this.announceDate = announceDate;
	}

	public Date getAnnounceDate() {
		return announceDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setTermsAndConditionsList(List<String> termsAndConditionsList) {
		this.termsAndConditionsList = termsAndConditionsList;
	}

	public List<String> getTermsAndConditionsList() {
		return termsAndConditionsList;
	}

	public void addTermOrCondion(String newTerm) {
		if (getTermsAndConditionsList() == null) {
			setTermsAndConditionsList(new ArrayList<String>());
		}
		if (!getTermsAndConditionsList().contains(newTerm)) {
			getTermsAndConditionsList().add(newTerm);
		}
	}

	public void setMaxEntriesPerUser(int maxEntriesPerUser) {
		this.maxEntriesPerUser = maxEntriesPerUser;
	}

	public int getMaxEntriesPerUser() {
		return maxEntriesPerUser;
	}

	public String getTermsAndConditionsHtmlListItems() {
		StringBuffer buff = new StringBuffer();
		for (String tandc : termsAndConditionsList) {
			buff.append("<li>");
			buff.append(tandc);
			buff.append("</li>");
		}

		if (maxEntriesPerUser > 0) {
			buff.append("<li>There is a limit of ");
			String[] numberText = {
			    "one","two","three","four","five","six","seven","eight","nine","ten",
			    "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen",
			    "eighteen","nineteen","twenty","twenty one","twenty two","twenty three",
			    "twenty four","twenty five"
			};
			String number=null;
			if (numberText.length >= maxEntriesPerUser-2) {
				number = numberText[maxEntriesPerUser-1];
			} else {
				number = String.valueOf(maxEntriesPerUser);
			}
			buff.append(number);
			buff.append(" entry per person. Any entries over this limit will be excluded from our records.</li>");
		}
		return buff.toString();
	}


	public void setCompetitionName(String competitionName) {
		this.competitionName = competitionName;
	}

	public String getCompetitionName() {
		return competitionName;
	}

	public void setPrizeList(List<CompetitionPrize> prizeList) {
		this.prizeList = prizeList;
	}

	public List<CompetitionPrize> getPrizeList() {
		return prizeList;
	}

	@SuppressWarnings("unchecked")
	public List<CompetitionPrize> getSortedPrizeList() {
		return (List<CompetitionPrize>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) getPrizeList() );
	}

	public void setWinningEmailsSent(boolean winningEmailsSent) {
		this.winningEmailsSent = winningEmailsSent;
	}

	public boolean isWinningEmailsSent() {
		return winningEmailsSent;
	}

	public Boolean getConsulationEmailsSent() {
		return consulationEmailsSent;
	}

	public void setConsulationEmailsSent(Boolean consulationEmailsSent) {
		this.consulationEmailsSent = consulationEmailsSent;
	}

	public boolean isNonClaimantEmailsSent() {
		return nonClaimantEmailsSent;
	}

	public void setNonClaimantEmailsSent(boolean nonClaimantEmailsSent) {
		this.nonClaimantEmailsSent = nonClaimantEmailsSent;
	}

}






