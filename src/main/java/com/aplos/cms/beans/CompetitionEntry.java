package com.aplos.cms.beans;

import java.util.List;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.utils.FormatUtil;

@Entity
@PluralDisplayName(name="competition entries")
@DynamicMetaValueKey(oldKey="COMPTN_ENTRY")
public class CompetitionEntry extends AplosBean implements BulkEmailFinder, BulkSubscriberSource {

	private static final long serialVersionUID = -2709217142512975909L;
	@ManyToOne
	private Subscriber subscriber;
	@ManyToOne
	private Competition competition;

	public CompetitionEntry() {}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All competition entrants";
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	public Competition getCompetition() {
		return competition;
	}

	public String getDateCreatedStr(){
		return FormatUtil.formatDate(super.getDateCreated());
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		return subscriber;
	}

	@Override
	public String getDisplayName() {
		String uniqueName = "";
		if (competition != null) {
			uniqueName += competition.getCompetitionName();
			if (subscriber != null) {
				uniqueName += ": ";
			}
		}
		if (subscriber != null) {
			uniqueName += subscriber.getFullName();
		}
		if (uniqueName.equals("")) {
			uniqueName = "Competition Entry";
		}
		return uniqueName;
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return getDisplayName();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {
		BeanDao competitionEntryDao = new BeanDao( CompetitionEntry.class );
		competitionEntryDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> competitionEntries = null;
		if( searchString != null ) {
			competitionEntryDao.addWhereCriteria( "competition.competitionName like :similarSearchText OR CONCAT(subscriber.firstName,' ',subscriber.surname) like :similarSearchText" );
			if( limit != null ) {
				competitionEntryDao.setMaxResults(limit);
			}
			competitionEntryDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			competitionEntries = (List<BulkEmailSource>) competitionEntryDao.getAll();
		} else {
			competitionEntries = competitionEntryDao.getAll();
		}
		return competitionEntries;
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

	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.subscriber.firstName,' ',bean.subscriber.surname) LIKE :similarSearchText OR bean.subscriber.emailAddress LIKE :similarSearchText)";
	}

	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.subscriber.firstName ASC, bean.subscriber.surname ASC";
	}
}






