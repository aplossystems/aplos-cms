package com.aplos.cms.backingpage.marketing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.Mailout;
import com.aplos.cms.beans.MailoutTemplate;
import com.aplos.cms.module.CmsModule;
import com.aplos.cms.templates.printtemplate.MailoutAddressPrint;
import com.aplos.cms.templates.printtemplate.MailoutPrint;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Mailout.class)
public class MailoutEditPage extends EditPage {
	private static final long serialVersionUID = -1511101389858814260L;
	private MailRecipientFinder mailRecipientFinder;

	public SelectItem[] getMailoutTemplateSelectItems() {
		return AplosBean.getSelectItemBeans( MailoutTemplate.class );
	}

	public String showMailout() {
		Set<MailRecipient> mailRecipients = mailRecipientFinder.getMailRecipients();
		Mailout mailout = resolveAssociatedBean();
		if( mailRecipients.size() > 0 ) {
			return MailoutPrint.getServletUrl( ApplicationUtil.getAplosContextListener(), mailout.getId(), false );
		} else {
			return null;
		}
	}

	public String showAddress() {
		Set<MailRecipient> mailRecipients = mailRecipientFinder.getMailRecipients();
		Mailout mailout = resolveAssociatedBean();
		if( mailRecipients.size() > 0 ) {
			return MailoutAddressPrint.getServletUrl( mailout.getId(), ApplicationUtil.getAplosContextListener(), false );
		} else {
			return null;
		}
	}

	public void goToMailoutTemplate() {
		Mailout mailout = resolveAssociatedBean();
		MailoutTemplate loadedMailoutTemplate = (MailoutTemplate) new BeanDao( MailoutTemplate.class ).get( mailout.getMailoutTemplate().getId() );
//		loadedMailoutTemplate.hibernateInitialise( true );
		loadedMailoutTemplate.addToScope( JsfScope.FLASH_VIEW );
		JSFUtil.redirect( MailoutTemplateEditPage.class );
	}

	public List<SelectItem> getMailRecipientFinderSelectItems() {
		AplosContextListener aplosContextListener = ApplicationUtil.getAplosContextListener();
		List<SelectItem> mailRecipientSelectItems = new ArrayList<SelectItem>();
		List<MailRecipientFinder> mailRecipientFinderList = ((CmsModule) aplosContextListener.getAplosModuleByClass(CmsModule.class)).getAvailableMailRecipientFinderList();
		//HibernateUtil.initialiseList(mailRecipientFinderList);
		for (MailRecipientFinder mailRecipientFinder : mailRecipientFinderList ) {
			mailRecipientSelectItems.add(new SelectItem(mailRecipientFinder, mailRecipientFinder.getName()));
		}
		return mailRecipientSelectItems;
	}

	public void setMailRecipientFinder(MailRecipientFinder mailRecipientFinder) {
		this.mailRecipientFinder = mailRecipientFinder;
	}

	public MailRecipientFinder getMailRecipientFinder() {
		return mailRecipientFinder;
	}
}
