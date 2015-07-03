package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.communication.MailRecipientFinder;

@Entity
public class Mailout extends AplosBean {
	private static final long serialVersionUID = 6713575534923856007L;

	@ManyToOne
	private MailoutTemplate mailoutTemplate;

	private String name;

	@ManyToOne
	private CompanyDetails companyDetails;

	@ManyToMany
	private List<MailRecipientFinder> mailRecipientFinders = new ArrayList<MailRecipientFinder>();

	@Column(columnDefinition="LONGTEXT")
	private String content;

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList( getMailRecipientFinders(), fullInitialisation );
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMailoutTemplate(MailoutTemplate mailoutTemplate) {
		this.mailoutTemplate = mailoutTemplate;
	}

	public MailoutTemplate getMailoutTemplate() {
		return mailoutTemplate;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setCompanyDetails(CompanyDetails companyDetails) {
		this.companyDetails = companyDetails;
	}

	public CompanyDetails getCompanyDetails() {
		return companyDetails;
	}

	public void setMailRecipientFinders(List<MailRecipientFinder> mailRecipientFinders) {
		this.mailRecipientFinders = mailRecipientFinders;
	}

	public List<MailRecipientFinder> getMailRecipientFinders() {
		return mailRecipientFinders;
	}

	//  This is a hibernate hack otherwise it complains about lazy initialisation
	public List<MailRecipientFinder> getMailRecipientFindersInNewList() {
		List<MailRecipientFinder> mailRecipientFinderList = new ArrayList<MailRecipientFinder>();
		mailRecipientFinderList.addAll( mailRecipientFinders );
		return mailRecipientFinderList;
	}

	public void setMailRecipientFindersInNewList(List<MailRecipientFinder> mailRecipientFinders) {
		setMailRecipientFinders( mailRecipientFinders );
	}
}
