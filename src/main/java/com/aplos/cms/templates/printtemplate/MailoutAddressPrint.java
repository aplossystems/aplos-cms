package com.aplos.cms.templates.printtemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.Mailout;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.JSFUtil;

@PluralDisplayName(name="mailout addresss xml templates")
public class MailoutAddressPrint extends PrintTemplate {
	private static final long serialVersionUID = -3045617856456529684L;
	private Mailout mailout;
	
	@Override
	public String getName() {
		return "Mailout address print";
	}
	
	@Override
	public void initialise(Map<String, String[]> params ) {

		Long mailoutId = Long.valueOf( params.get( "mailoutId" )[ 0 ] );
		setMailout( (Mailout) new BeanDao( Mailout.class ).get( mailoutId ) );
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe = new JDynamiTe();

			MailRecipient mailRecipient = null;
			if( getMailout().getMailRecipientFinders().size() > 0 ) {
				for( MailRecipientFinder mailRecipientFinder : getMailout().getMailRecipientFinders() ) {
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
				dynamiTe.setInput(new BufferedReader(new StringReader( "<html><body>" +
						getParserTemplateStr(ApplicationUtil.getAplosContextListener()) + "</body></html>")));

				dynamiTe.setVariable("ADDRESS_1", CommonUtil
						.getStringOrEmpty(mailRecipient.getMailRecipientAddress().toHtmlFull("")));

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

	public static String getParserTemplateStr(AplosContextListener aplosContextListener ) {
		URL url = JSFUtil.checkFileLocations("mailoutAddressPrint.html", AplosAppConstants.RESOURCES_PRINT_TEMPLATE_DIR, true );
		if( url != null ) {
			try {
				String templateContent = new String(CommonUtil.readEntireFile( new File( url.toURI() ).getAbsolutePath() ));
				return templateContent;
			} catch (IOException e) {
				e.printStackTrace();
				ErrorEmailSender.sendErrorEmail( null,aplosContextListener, e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				ErrorEmailSender.sendErrorEmail( null,aplosContextListener, e);
			}
		}
		return null;
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return CmsWorkingDirectory.MAILOUT_PDFS;
	}

	public static String getServletUrl( Long mailoutId, AplosContextListener aplosContextListener, boolean createPDF ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( MailoutAddressPrint.class );

		aplosUrl.addQueryParameter( "mailoutId", String.valueOf( mailoutId ) );
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
