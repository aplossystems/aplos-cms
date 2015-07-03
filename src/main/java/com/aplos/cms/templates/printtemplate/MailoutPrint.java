package com.aplos.cms.templates.printtemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.Mailout;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@PluralDisplayName(name="mailout print templates")
public class MailoutPrint extends PrintTemplate {
	private static final long serialVersionUID = -8142872786240865190L;
	private Mailout mailout;
	
	@Override
	public String getName() {
		return "Mailout print";
	}
	
	@Override
	public void initialise(Map<String, String[]> params ) {

		Long mailoutId = Long.valueOf( params.get( "mailoutId" )[ 0 ] );
		setMailout( (Mailout) new BeanDao( Mailout.class ).get( mailoutId ) );
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return CmsWorkingDirectory.MAILOUT_PDFS;
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe = new JDynamiTe();
			dynamiTe.setInput(new BufferedReader(new StringReader( "<html><body>" +
					getParserTemplateStr(mailout) + "</body></html>")));

			MailRecipient mailRecipient = null;
			if( mailout.getMailRecipientFinders().size() > 0 ) {
				for( MailRecipientFinder mailRecipientFinder : mailout.getMailRecipientFinders() ) {
					List<MailRecipient> mailRecipientList = mailRecipientFinder.getMailRecipientsAsList();
					if( mailRecipientList.size() > 0 ) {
						mailRecipient = mailRecipientList.get( 0 );
						break;
					}
				}
			} else {
				return "";
			}

			if( mailRecipient != null ) {
				dynamiTe.setVariable("MAILOUT_CONTENT", CommonUtil
						.getStringOrEmpty(mailout.getContent()));

				if (mailRecipient.getMailRecipientAddress() != null) {
					dynamiTe.setVariable("MAIL_RECIPIENT_ADDRESS",CommonUtil
							.getStringOrEmpty(mailRecipient
							.getMailRecipientAddress().toHtmlFull("")));
					dynamiTe.setVariable("MAIL_RECIPIENT_PHONE", CommonUtil
							.getStringOrEmpty(mailRecipient
							.getMailRecipientAddress().getPhone()));
					dynamiTe.setVariable("MAIL_RECIPIENT_FAX", CommonUtil
							.getStringOrEmpty(mailRecipient
									.getMailRecipientAddress().getFax()));
				}
				dynamiTe.setVariable("MAIL_RECIPIENT_EMAIL", mailRecipient
						.getMailRecipientAddress().getEmailAddress());
				dynamiTe.setVariable("MAIL_RECIPIENT_NAME", CommonUtil
						.getStringOrEmpty(mailRecipient
						.getMailRecipientAddress().getContactFirstName()));

				dynamiTe.setVariable("COMPANY_ADDRESS", mailout.getCompanyDetails().getAddress()
						.join(", ", true));

				dynamiTe.setVariable("COMPANY_CONTACT_DETAILS",
						getCompanyContactDetails(mailout.getCompanyDetails()));

				dynamiTe.setVariable("COMPANY_LEGAL_DETAILS",
						getCompanyLegalDetails(mailout.getCompanyDetails()));

				dynamiTe.parse();

				try {
					return dynamiTe.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCompanyLegalDetails(CompanyDetails companyDetails) {
		List<String> companyLegalDetails = new ArrayList<String>();
		if (companyDetails.getRegNo() != null
				&& companyDetails.getRegNo() != "") {
			companyLegalDetails.add("Reg#: " + companyDetails.getRegNo());
		}
//		if (companyDetails.getDirectorsFullName() != null
//				&& companyDetails.getDirectorsFullName() != "") {
//			companyLegalDetails.add("Director: "
//					+ companyDetails.getDirectorsFirstName());
//		}
		if (companyDetails.getDirector() != null
				&& companyDetails.getDirector() != "") {
			companyLegalDetails.add("&#160;&#160;&#160;&#160;" + "Director: "
					+ companyDetails.getDirector());
		}
		if (companyDetails.getVatNo() != null
				&& companyDetails.getVatNo() != "") {
			companyLegalDetails.add("&#160;&#160;&#160;&#160;" + "VAT#: " + companyDetails.getVatNo());
		}

		return StringUtils.join(companyLegalDetails.toArray(), " ");
	}

	public static String getCompanyContactDetails(CompanyDetails companyDetails) {
		List<String> companyContactDetails = new ArrayList<String>();
		if (companyDetails.getAddress().getPhone() != null
				&& companyDetails.getAddress().getPhone() != "") {
			companyContactDetails.add("T. "
					+ companyDetails.getAddress().getPhone());
		}
		if (companyDetails.getAddress().getFax() != null
				&& companyDetails.getAddress().getFax() != "") {
			companyContactDetails.add("&#160;&#160;&#160;&#160;" + "F. "
					+ companyDetails.getAddress().getFax());
		}
		if (companyDetails.getAddress().getEmailAddress() != null
				&& companyDetails.getAddress().getEmailAddress() != "") {
			companyContactDetails.add("&#160;&#160;&#160;&#160;" + companyDetails.getAddress()
					.getEmailAddress());
		}
		if (companyDetails.getWeb() != null && companyDetails.getWeb() != "") {
			companyContactDetails.add("&#160;&#160;&#160;&#160;" + companyDetails.getWeb());
		}

		return StringUtils.join(companyContactDetails.toArray(), " ");
	}

	public static String getParserTemplateStr(Mailout mailout) {
		StringBuffer mailoutContent = new StringBuffer();
		mailoutContent.append(mailout.getMailoutTemplate().getMailoutHeader());
		mailoutContent.append(mailout.getMailoutTemplate().getMailoutFooter());
		return mailoutContent.toString();
	}

	public static String getServletUrl( AplosContextListener aplosContextListener, Long mailoutId, boolean createPDF ) {
		AplosUrl aplosUrl = getBaseTemplateUrl(MailoutPrint.class);

		aplosUrl.addQueryParameter( "mailoutId", mailoutId );
		aplosUrl.addQueryParameter( AplosAppConstants.CREATE_PDF, createPDF );

		JSFUtil.redirect( aplosUrl, true );

		return null;
	}

	public Mailout getMailout() {
		return mailout;
	}

	public void setMailout(Mailout mailout) {
		this.mailout = mailout;
	}
}
