package com.aplos.cms.beans;

import java.util.Date;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@PluralDisplayName(name="competition prize winners")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DynamicMetaValueKey(oldKey="COMPTN_PRIZE_WINNER")
public class CompetitionPrizeWinner extends AplosBean implements BulkEmailFinder, BulkSubscriberSource {
	private static final long serialVersionUID = -4576529661822807821L;

	@ManyToOne
	private CompetitionPrize competitionPrize;
	@ManyToOne
	private Subscriber subscriber;
	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address address;
	private boolean isClaimed = false;
	private boolean isSent = false;
	private String organisation; //used for various - school name in teletest competitions
	private String claimCode; //used for auth to stop people guessing the urls
	@Column(columnDefinition="LONGTEXT")
	private String notes;

	public CompetitionPrizeWinner() {
		//breaks hibernate doing this in constructor generateNewUniqueClaimCode();
	}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All competition prize winners";
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		if (isSent) {
			setClaimed(true);
		}
		if (address != null) {
			address.saveDetails();
		}
		super.saveBean(currentUser);
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public void generateNewUniqueClaimCode() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		String identifiers = "";
		if (competitionPrize != null) {
			identifiers += "P" + competitionPrize.getId();
			identifiers += "C" + competitionPrize.getCompetition().getId();
		} else if (competition != null) {
			identifiers += "C" + competition.getId();
		}
		if (subscriber != null) {
			identifiers += "S" + subscriber.getId();
		}

		String completeCode;
		while (true) {
			completeCode = identifiers + CommonUtil.md5(new Date());
			if (getCompetitionPrizeWinnerIdByCode(completeCode) == null){
				break;
			}
		}
		claimCode = completeCode;
	}

	public static CompetitionPrizeWinner getCompetitionPrizeWinnerIdByCode(String claimCode) {
		BeanDao aqlBeanDao = new BeanDao( CompetitionPrizeWinner.class );
		aqlBeanDao.addWhereCriteria( "bean.claimCode=:claimCode" );
		aqlBeanDao.setNamedParameter( "claimCode", claimCode);
		return aqlBeanDao.getFirstBeanResult();
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setClaimed(boolean isClaimed) {
		this.isClaimed = isClaimed;
	}

	public boolean isClaimed() {
		return isClaimed;
	}

	public void setClaimCode(String claimCode) {
		this.claimCode = claimCode;
	}

	public String getClaimCode() {
		return claimCode;
	}

	public void setCompetitionPrize(CompetitionPrize competitionPrize) {
		this.competitionPrize = competitionPrize;
	}

	public CompetitionPrize getCompetitionPrize() {
		return competitionPrize;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getOrganisation() {
		return organisation;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		return subscriber;
	}

	@Override
	public String getDisplayName() {
		return subscriber.getFullName() + ": " + competitionPrize.getPrizeName();
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return getDisplayName();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {

		BeanDao competitionPrizeWinnerDao = new BeanDao( CompetitionPrizeWinner.class );
		competitionPrizeWinnerDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> competitionPrizeWinners = null;
		if( searchString != null ) {
			competitionPrizeWinnerDao.addWhereCriteria( "competitionPrize.prizeName like :similarSearchText OR CONCAT(subscriber.firstName,' ',subscriber.surname) like :similarSearchText" );
			if( limit != null ) {
				competitionPrizeWinnerDao.setMaxResults(limit);
			}
			competitionPrizeWinnerDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			competitionPrizeWinners = (List<BulkEmailSource>) competitionPrizeWinnerDao.getAll();
		} else {
			competitionPrizeWinners = competitionPrizeWinnerDao.getAll();
		}
		return competitionPrizeWinners;
	}

	@Override
	public String getFirstName() {
		return subscriber.getFirstName();
	}

	@Override
	public String getSurname() {
		return subscriber.getSurname();
	}

	@Override
	public String getEmailAddress() {
		return subscriber.getEmailAddress();
	}

//	@Override
//	public AqlBeanDao getSourceBeanDao() {
//		return new AqlBeanDao(CompetitionPrizeWinner.class).setSelectCriteria("bean.subscriber");
//	}

	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.subscriber.firstName,' ',bean.subscriber.surname) LIKE :similarSearchText OR bean.subscriber.emailAddress LIKE :similarSearchText)";
	}
	
	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.subscriber.firstName ASC, bean.subscriber.surname ASC";
	}
}






