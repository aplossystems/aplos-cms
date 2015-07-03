package com.aplos.cms.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.ContactFormSubmission;
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
import com.aplos.common.utils.CommonUtil;

@Entity
public class ContactEmail extends SourceGeneratedEmailTemplate<ContactFormSubmission> {
	private static final long serialVersionUID = 8643039154199484799L;

	public ContactEmail() {
	}
	
	public String getDefaultName() {
		return "Contact Email Template";
	}

	@Override
	public String getDefaultSubject() {
		return "New message from {SENDER_NAME}";
	}
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "contactEmail.html" );
	}
	
	@Override
	public BulkMessageSourceGroup getBulkMessageSourceGroup() {
		return Website.getCurrentWebsiteFromTabSession().getBulkMessageFinderMap().get( CmsBulkMessageFinderEnum.COMPETITION_PRIZE_WINNER );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		ContactFormSubmission contactFormSubmission = new ContactFormSubmission();
		Subscriber subscriber = new Subscriber();
		subscriber.setEmailAddress( adminUser.getEmail() );
		subscriber.setFirstName(adminUser.getFirstName());
		subscriber.setSurname(adminUser.getSurname());
		contactFormSubmission.setSubscriber(subscriber);
		contactFormSubmission.setSenderPhoneNumber(adminUser.getPhone());
		contactFormSubmission.setEmailContent("This is our test message");
		contactFormSubmission.setEmailSubject("Testing out our email template");
		return contactFormSubmission;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			ContactFormSubmission contactFormSubmission) {
		super.addSubjectJDynamiTeValues(jDynamiTe, contactFormSubmission);
		jDynamiTe.setVariable("SENDER_NAME", CommonUtil.getStringOrEmpty( contactFormSubmission.getSubscriber().getFirstName() ));
	}

	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			ContactFormSubmission contactFormSubmission) {
		super.addContentJDynamiTeValues(jDynamiTe, contactFormSubmission);
		jDynamiTe.setVariable("SENDER_NAME", CommonUtil.getStringOrEmpty(contactFormSubmission.getFirstName()));
		jDynamiTe.setVariable("SENDER_EMAIL_ADDRESS", CommonUtil.getStringOrEmpty(contactFormSubmission.getEmailAddress()));
		jDynamiTe.setVariable("SENDER_PHONE_NUMBER", CommonUtil.getStringOrEmpty( contactFormSubmission.getSenderPhoneNumber() ));
		jDynamiTe.setVariable("EMAIL_SUBJECT", CommonUtil.getStringOrEmpty(contactFormSubmission.getEmailSubject()));
		jDynamiTe.setVariable("EMAIL_CONTENT", CommonUtil.getStringOrEmpty(contactFormSubmission.getEmailContent()));
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return CmsEmailTemplateEnum.CONTACT;
	}

}
