package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.aplos.cms.beans.ContactFormSubmission;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class ContactPageFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -7549918301222477273L;
	private static Logger logger = Logger.getLogger( ContactPageFeDmb.class );

	private ContactFormSubmission contactFormSubmission = new ContactFormSubmission();
	private String emailAddress;
	private String senderName;

	private boolean isMessageSent = false;
	private boolean isSendingDuplicateEmail = false;

	public ContactPageFeDmb() {

	}

	public void sendEmail( ) {
		logger.debug( "Entered send email method" );
		if (CommonUtil.validateEmailAddressFormat(emailAddress)) {
			Subscriber subscriber = Subscriber.getOrCreateSubscriber(emailAddress);
			if( subscriber.isNew() ) {
				String[] senderNameParts = getSenderName().split( " " );
				for( int i = 0, n = senderNameParts.length; i < n; i++ ) {
					if( i == 0 ) {
						subscriber.setFirstName( senderNameParts[ i ] );
					} else if( i == 1 ) {
						subscriber.setSurname( senderNameParts[ i ] );
					} else {
						subscriber.setSurname( subscriber.getSurname() + " " + senderNameParts[ i ] );
					}
				}
			}
			getContactFormSubmission().setSubscriber((Subscriber)subscriber.getSaveableBean());
			getContactFormSubmission().saveDetails();
			AplosEmail aplosEmail = new AplosEmail( CmsEmailTemplateEnum.CONTACT, getContactFormSubmission(), getContactFormSubmission() );
			if( getContactFormSubmission().getAttachment() != null ) {
				aplosEmail.addSaveableAttachment( getContactFormSubmission().getAttachment() );
			}
			aplosEmail.setToAddress( CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails().getEmailAddress() );
			if( isSendingDuplicateEmail() ) {
				aplosEmail.addCcAddress( getContactFormSubmission().getEmailAddress() );
			}
			if ( aplosEmail.sendAplosEmailToQueue() != null ) {
				setMessageSent( true );
				JSFUtil.addMessage( "Thank you, your message has been sent.", FacesMessage.SEVERITY_INFO );

				aplosEmail = new AplosEmail( CmsEmailTemplateEnum.CONTACT, getContactFormSubmission(), getContactFormSubmission() );
			} else {
				JSFUtil.addMessage("Sorry, your e-mail has not been sent.", FacesMessage.SEVERITY_WARN);
			}
		}
		else {
			JSFUtil.addMessage("Sorry, your e-mail has not been sent. Please check your email address is correct.", FacesMessage.SEVERITY_WARN);
		}
	}
	
	@Override
	public boolean isValidationRequired() {
		return BackingPage.validationRequired("aplosContactPagSendBtn") || super.isValidationRequired();
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmailAddress() {
		return emailAddress;
	}

	public void setSenderEmailAddress(String senderEmailAddress) {
		this.emailAddress = senderEmailAddress;
	}

	public String getEmailContent() {
		return getContactFormSubmission().getEmailContent();
	}

	public void setEmailContent(String emailContent) {
		getContactFormSubmission().setEmailContent(emailContent);
	}

	public void setEmailSubject(String emailSubject) {
		getContactFormSubmission().setEmailSubject(emailSubject);
	}

	public String getEmailSubject() {
		return getContactFormSubmission().getEmailSubject();
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		getContactFormSubmission().setSenderPhoneNumber(senderPhoneNumber);
	}

	public String getSenderPhoneNumber() {
		return getContactFormSubmission().getSenderPhoneNumber();
	}

	public void setMessageSent(boolean isMessageSent) {
		this.isMessageSent = isMessageSent;
	}

	public boolean isMessageSent() {
		return isMessageSent;
	}

	/**
	 * @return the contactFormSubmission
	 */
	public ContactFormSubmission getContactFormSubmission() {
		return contactFormSubmission;
	}

	/**
	 * @param contactFormSubmission the contactFormSubmission to set
	 */
	public void setContactFormSubmission(ContactFormSubmission contactFormSubmission) {
		this.contactFormSubmission = contactFormSubmission;
	}

	public boolean isSendingDuplicateEmail() {
		return isSendingDuplicateEmail;
	}

	public void setSendingDuplicateEmail(boolean isSendingDuplicateEmail) {
		this.isSendingDuplicateEmail = isSendingDuplicateEmail;
	}

}
