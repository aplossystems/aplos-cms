package com.aplos.cms.templates.xmltemplate;

import java.io.BufferedReader;
import java.io.IOException;
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
import com.aplos.cms.templates.printtemplate.MailoutAddressPrint;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.XmlEntityUtil;

@PluralDisplayName(name="mailout addresses xml templates")
public class MailoutAddressesXml extends PrintTemplate {
	private static final long serialVersionUID = 4655950378759384182L;
	private Mailout mailout;
	
	@Override
	public String getName() {
		return "Mailout addresses XML";
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
    		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document mailoutAddressesXml = docBuilder.newDocument();

    		Set<MailRecipient> mailRecipients = new HashSet<MailRecipient>();
    		for( MailRecipientFinder mailRecipientFinder : getMailout().getMailRecipientFinders() ) {
    			mailRecipients.addAll( mailRecipientFinder.getMailRecipients() );
			}

    		Element mailoutAddressesElement = mailoutAddressesXml
					.createElement("mailoutAddressTemplate");
    		mailoutAddressesXml.appendChild(mailoutAddressesElement);

    		Element templateElement = mailoutAddressesXml
					.createElement("template");
    		mailoutAddressesElement.appendChild(templateElement);

			JDynamiTe dynamiTe = new JDynamiTe();
			dynamiTe.setInput(new BufferedReader(new StringReader(
					MailoutAddressPrint.getParserTemplateStr(ApplicationUtil.getAplosContextListener()))));

			Enumeration dynamiTeKeys = dynamiTe.getVariableKeys();
			while( dynamiTeKeys.hasMoreElements() ) {
				String key = dynamiTeKeys.nextElement().toString();
				if( dynamiTe.getVariable( key ).equals( "" ) ) {
					dynamiTe.setVariable( key, "{" + key + "}" );
				}
			}

			dynamiTe.parse();
    		templateElement.setTextContent( dynamiTe.toString() );

			Element mailoutElement = mailoutAddressesXml.createElement( "mailoutAddresses" );
			mailoutAddressesElement.appendChild( mailoutElement );

			for( MailRecipient mailRecipient : mailRecipients ) {
				if( mailRecipient.getMailRecipientAddress() != null ) {
					String addressHtml = mailRecipient.getMailRecipientAddress().toHtmlFull( "" );
					if( !addressHtml.equals( "" ) ) {
						Element mailoutAddressElement = mailoutAddressesXml.createElement( "address" );
						mailoutElement.appendChild( mailoutAddressElement );
						mailoutAddressElement.setTextContent( XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode( addressHtml ) );
					}
				}
			}

	        return FormatUtil.stdConvertDocumentToString( mailoutAddressesXml, ApplicationUtil.getAplosContextListener() );
        } catch( ParserConfigurationException pcex ) {
        	ErrorEmailSender.sendErrorEmail( null, ApplicationUtil.getAplosContextListener(), pcex );
            return null;
        } catch( IOException ioex ) {
        	ErrorEmailSender.sendErrorEmail( null, ApplicationUtil.getAplosContextListener(), ioex );
            return null;
        }
	}

	public Mailout getMailout() {
		return mailout;
	}

	public void setMailout(Mailout mailout) {
		this.mailout = mailout;
	}
}
