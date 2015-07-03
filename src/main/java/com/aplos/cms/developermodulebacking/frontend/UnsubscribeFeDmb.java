package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.UnsubscribeType;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.interfaces.SubscriberInter;
import com.aplos.common.persistence.PersistentClass;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class UnsubscribeFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 8488709676216675983L;
	private String unsubscribeReason;
	private SubscriberInter subscriber;

	public UnsubscribeFeDmb() {
		checkQueryParameters();
	}
	
	public void checkQueryParameters() {
		String subscriberIdStr = JSFUtil.getRequestParameter( AplosScopedBindings.SUBSCRIBER );
		if ( CommonUtil.isNullOrEmpty( subscriberIdStr ) ) {
			JSFUtil.addMessage("No subscriber has been specified. This is required", FacesMessage.SEVERITY_ERROR);
			return;
		} 

		String subscriberClassStr = JSFUtil.getRequestParameter( AplosScopedBindings.SUBSCRIBER_CLASS );
		if ( CommonUtil.isNullOrEmpty( subscriberClassStr ) ) {
			JSFUtil.addMessage("No subscriber class has been specified. This is required", FacesMessage.SEVERITY_ERROR);
			return;
		} 
		
		Long subscriberId = null;
		try {
			subscriberId = Long.parseLong( subscriberIdStr );
		} catch( NumberFormatException nfex ) {
			JSFUtil.addMessage("The subscriber identity provided was not recognised.", FacesMessage.SEVERITY_ERROR);
			return;
		}
		
		PersistentClass persistentClass = (PersistentClass) ApplicationUtil.getPersistentApplication().getPersistableTableBySqlNameMap().get( subscriberClassStr.toLowerCase() );
		//find the matching user
		@SuppressWarnings("unchecked")
		BulkSubscriberSource bulkSubscriberSource = (BulkSubscriberSource) new BeanDao( (Class<? extends AplosAbstractBean>) persistentClass.getTableClass() ).get( subscriberId );
		if (bulkSubscriberSource == null ) {
			JSFUtil.addMessage("The subscriber identity provided was not recognised.", FacesMessage.SEVERITY_ERROR);
		} else {
			setSubscriber( bulkSubscriberSource.getSourceSubscriber() );
		}
		
		if( JSFUtil.getRequestParameter( AplosScopedBindings.AUTO_UNSUBSCRIBE ) != null ) {
			unsubscribe();
		}
	}

	public void unsubscribe() {
		SubscriberInter saveableSubscriber = (SubscriberInter) getSubscriber().getSaveableBean();
		saveableSubscriber.setSubscribed(false);
		saveableSubscriber.setUnsubscribeType(UnsubscribeType.USER_REQUESTED);
		saveableSubscriber.setUnsubscribeReason( getUnsubscribeReason() );
		saveableSubscriber.saveDetails();
		
		AplosEmail aplosEmail = new AplosEmail( getSubscriber().getEmailAddress() + " has unsubscribed", "Their reason was: <br/>" + getSubscriber().getUnsubscribeReason() );
		aplosEmail.addToAddress( CommonUtil.getAdminUser().getEmailAddress() );
		aplosEmail.setFromAddress( CommonUtil.getAdminUser().getEmailAddress() );
		aplosEmail.sendAplosEmailToQueue();
		JSFUtil.addMessage("You have been unsubscribed from future newsletters. We're sorry to lose you.", FacesMessage.SEVERITY_INFO);
	}

	public void subscribe() {
		SubscriberInter saveableSubscriber = (SubscriberInter) getSubscriber().getSaveableBean();
		saveableSubscriber.setSubscribed(true);
		saveableSubscriber.setUnsubscribeType(null);
		saveableSubscriber.saveDetails();
		
		AplosEmail aplosEmail = new AplosEmail( saveableSubscriber.getEmailAddress() + " has subscribed", "" );
		aplosEmail.addToAddress( CommonUtil.getAdminUser().getEmailAddress() );
		aplosEmail.setFromAddress( CommonUtil.getAdminUser().getEmailAddress() );
		aplosEmail.sendAplosEmailToQueue();
		JSFUtil.addMessage("You have been subscribed for future newsletters, thank you for your loyalty.", FacesMessage.SEVERITY_INFO);
	}

	public String getUnsubscribeReason() {
		return unsubscribeReason;
	}

	public void setUnsubscribeReason(String unsubscribeReason) {
		this.unsubscribeReason = unsubscribeReason;
	}
	
	public boolean isShowingForm() {
		return getSubscriber() != null;
	}
	
	public boolean isSubscribed() {
		return getSubscriber().isSubscribed();
	}

	public SubscriberInter getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(SubscriberInter subscriber) {
		this.subscriber = subscriber;
	}
}



















