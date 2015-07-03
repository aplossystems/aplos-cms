package com.aplos.cms.templates.xmltemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.Mailout;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.templates.printtemplate.MailoutPrint;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.XmlEntityUtil;

@PluralDisplayName(name="mailout xml templates")
public class MailoutXml extends PrintTemplate {
	private static final long serialVersionUID = -7895460073245039416L;
	private Mailout mailout;
	
	@Override
	public String getName() {
		return "Mailout XML";
	}
	
	@Override
	public void initialise(Map<String, String[]> params) {

		Long mailoutId = Long.valueOf( params.get( "mailoutId" )[ 0 ] );
		setMailout( (Mailout) new BeanDao( Mailout.class ).get( mailoutId ) );
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return CmsWorkingDirectory.MAILOUT_PDFS;
	}

	public String getTemplateContent() {
        try {
    		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document mailoutXml = docBuilder.newDocument();

    		Set<MailRecipient> mailRecipients = new HashSet<MailRecipient>();
    		for( MailRecipientFinder mailRecipientFinder : getMailout().getMailRecipientFinders() ) {
    			mailRecipients.addAll( mailRecipientFinder.getMailRecipients() );
			}

			Element mailoutElement = mailoutXml.createElement( "mailout" );
			mailoutXml.appendChild( mailoutElement );
			createParserTemplateXml( getMailout(), mailoutXml, mailoutElement );
			for( MailRecipient mailRecipient : mailRecipients ) {
				createParserKeysXml( mailoutXml, mailoutElement, mailRecipient );
			}

	        return FormatUtil.stdConvertDocumentToString( mailoutXml, ApplicationUtil.getAplosContextListener() );
        } catch( ParserConfigurationException pcex ) {
        	ErrorEmailSender.sendErrorEmail( null, ApplicationUtil.getAplosContextListener(), pcex );
            return null;
        }
	}

	public static void createParserKeysXml(Document mailoutXml,
			Element mailoutElement, MailRecipient mailRecipient) {
		String mailRecipientAddress = mailRecipient.getMailRecipientAddress().toHtmlFull("");

		if( !mailRecipientAddress.equals("") ) {
			Element mailRecipientElement = mailoutXml
					.createElement("templateKeyValues");
			mailoutElement.appendChild(mailRecipientElement);

			if (mailRecipient.getMailRecipientAddress() != null) {
				mailRecipientElement.appendChild(
						mailoutXml.createElement("MAIL_RECIPIENT_ADDRESS"))
						.setTextContent(
								XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode( mailRecipient.getMailRecipientAddress().toHtmlFull(
										"")));
				mailRecipientElement.appendChild(
						mailoutXml.createElement("MAIL_RECIPIENT_PHONE"))
						.setTextContent(
								mailRecipient.getMailRecipientAddress().getPhone());
				mailRecipientElement.appendChild(
						mailoutXml.createElement("MAIL_RECIPIENT_FAX"))
						.setTextContent(
								CommonUtil.getStringOrEmpty(mailRecipient
										.getMailRecipientAddress().getFax()));
			}

			mailRecipientElement.appendChild(
					mailoutXml.createElement("MAIL_RECIPIENT_EMAIL"))
					.setTextContent(
							mailRecipient.getMailRecipientAddress()
									.getEmailAddress());
			mailRecipientElement.appendChild(
					mailoutXml.createElement("MAIL_RECIPIENT_NAME"))
					.setTextContent( XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode( mailRecipient.getMailRecipientAddress()
									.getContactFirstName() ));
		}
	}

	public static void createParserTemplateXml(Mailout mailout, Document mailoutXml,
			Element mailoutElement) {
		try {
			Element templateElement = mailoutXml
					.createElement("template");
			mailoutElement.appendChild(templateElement);

			JDynamiTe dynamiTe = new JDynamiTe();
			dynamiTe.setInput(new BufferedReader(new StringReader(
					MailoutPrint.getParserTemplateStr(mailout))));
			dynamiTe.setVariable("MAILOUT_CONTENT", CommonUtil
					.getStringOrEmpty(mailout.getContent()));

			dynamiTe.setVariable("COMPANY_ADDRESS", XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode( mailout.getCompanyDetails().getAddress()
					.join(", ") ) );

			dynamiTe.setVariable("COMPANY_CONTACT_DETAILS",
					MailoutPrint.getCompanyContactDetails(mailout.getCompanyDetails()));

			dynamiTe.setVariable("COMPANY_LEGAL_DETAILS",
					MailoutPrint.getCompanyLegalDetails(mailout.getCompanyDetails()));

			Enumeration dynamiTeKeys = dynamiTe.getVariableKeys();
			while( dynamiTeKeys.hasMoreElements() ) {
				String key = dynamiTeKeys.nextElement().toString();
				if( dynamiTe.getVariable( key ).equals( "" ) ) {
					dynamiTe.setVariable( key, "{" + key + "}" );
				}
			}

			dynamiTe.parse();

			templateElement.setTextContent( dynamiTe.toString() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Mailout getMailout() {
		return mailout;
	}

	public void setMailout(Mailout mailout) {
		this.mailout = mailout;
	}
}
