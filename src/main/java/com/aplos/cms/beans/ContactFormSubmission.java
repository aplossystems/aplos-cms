package com.aplos.cms.beans;

import java.util.List;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.InternationalNumber;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.SmsMessage;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSmsSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.interfaces.SubscriberInter;
import com.aplos.common.utils.CommonUtil;


@Entity
public class ContactFormSubmission extends AplosBean implements BulkEmailSource,BulkSmsSource, BulkEmailFinder, BulkSubscriberSource, FileDetailsOwnerInter {
	private static final long serialVersionUID = 2883140271086705043L;
	
	private String senderPhoneNumber;
	private String emailSubject;
	@Column(columnDefinition="LONGTEXT")
	private String emailContent;
	
	@ManyToOne
	@Cascade({CascadeType.ALL})
	private Subscriber subscriber;
	@ManyToOne
	private FileDetails attachment;
	private boolean isSmsSubscribed = true;

	@Transient
	private ContactFormFdoh contactFormFdoh = new ContactFormFdoh(this);
	
	public enum ContactFormFileKey {
		ATTACHMENT;
	}
	
	@Override
	public boolean determineIsSmsSubscribed(SmsMessage smsMessage) {
		return isSmsSubscribed();
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, ContactFormFileKey.values(), currentUser);
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return contactFormFdoh;
	}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All contact form submitters";
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return CommonUtil.getStringOrEmpty(getSubscriber().getFullName()) + " (" + CommonUtil.getStringOrEmpty(getSubscriber().getEmailAddress()) + ")";
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	@Override
	public SubscriberInter getSourceSubscriber() {
		return getSubscriber();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {

		BeanDao contactFormSubmissionDao = new BeanDao( ContactFormSubmission.class );
		contactFormSubmissionDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> contactFormSubmitters = null;
		if( searchString != null ) {
			contactFormSubmissionDao.addWhereCriteria( getFinderSearchCriteria() );
			if( limit != null ) {
				contactFormSubmissionDao.setMaxResults(limit);
			}
			contactFormSubmissionDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			contactFormSubmitters = (List<BulkEmailSource>) contactFormSubmissionDao.getAll();
		} else {
			contactFormSubmitters = contactFormSubmissionDao.getAll();
		}
		return contactFormSubmitters;
	}
	
	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.subscriber.firstName,' ',bean.subscriber.surname) LIKE :similarSearchText OR bean.subscriber.emailAddress LIKE :similarSearchText)";
	}
	
	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.subscriber.firstName ASC, bean.subscriber.surname ASC";
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	@Override
	public InternationalNumber getInternationalNumber() {
		return new InternationalNumber("44", senderPhoneNumber);
	}

	@Override
	public String getFirstName() {
		return getSubscriber().getFirstName();
	}

	@Override
	public String getSurname() {
		return getSubscriber().getSurname();
	}

	@Override
	public String getEmailAddress() {
		return getSubscriber().getEmailAddress();
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public FileDetails getAttachment() {
		return attachment;
	}

	public void setAttachment(FileDetails attachment) {
		this.attachment = attachment;
	}

	public boolean isSmsSubscribed() {
		return isSmsSubscribed;
	}

	public void setSmsSubscribed(boolean isSmsSubscribed) {
		this.isSmsSubscribed = isSmsSubscribed;
	}

	private class ContactFormFdoh extends SaveableFileDetailsOwnerHelper {
		public ContactFormFdoh( ContactFormSubmission contactFormSubmission ) {
			super( contactFormSubmission );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (ContactFormFileKey.ATTACHMENT.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.CONTACT_FORM_DIR.getDirectoryPath(includeServerWorkPath) + getAplosBean().getClass().getSimpleName() + "/";
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (ContactFormFileKey.ATTACHMENT.name().equals(fileDetailsKey)) {
				setAttachment(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( ContactFormFileKey.ATTACHMENT.name().equals( fileDetailsKey ) ) {
				return getAttachment();
			}
			return null;
		}
	}
}
