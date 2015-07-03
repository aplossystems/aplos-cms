package com.aplos.cms.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormElement;
import com.aplos.cms.beans.customforms.FormRecord;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.utils.CommonUtil;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class FormServlet extends HttpServlet {


	/**
	 *
	 */
	private static final long serialVersionUID = -2100774177543485455L;

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		/* The O'Reilly library will handle any file uploads for us, then
		 * process the string values afterwards. */
		MultipartRequest multi = new MultipartRequest( request, CommonWorkingDirectory.UPLOAD_DIR.getDirectoryPath(true), 10 * 1024 * 1024, "ISO-8859-1", new DefaultFileRenamePolicy() );
//		System.out.println(multi.getParameterNames());

		String form_id = multi.getParameter( "form_id" );
		if (form_id == null) { return; }

		/* Save new Record */
		Form form = (Form) new BeanDao(Form.class).get(Long.parseLong( form_id ));
		FormRecord record = new FormRecord();
		record.setForm( form );
		form.getRecords().add( record );

		Enumeration files = multi.getFileNames();
		while( files.hasMoreElements() ) {
			String name = (String)files.nextElement();
//			String filename = multi.getFilesystemName( name );
//			String originalFilename = multi.getOriginalFileName( name );
//			String type = multi.getContentType( name );
			File f = multi.getFile( name );
//			System.out.println( "name: " + name );
//			System.out.println( "filename: " + filename );
//			System.out.println( "originalFilename: " + originalFilename );
//			System.out.println( "type: " + type );
//			if( f != null ) {
//				System.out.println( "f.toString(): " + f.toString() );
//				System.out.println( "f.getName(): " + f.getName() );
//				System.out.println( "f.exists(): " + f.exists() );
//				System.out.println( "f.length(): " + f.length() );
//			}
//			System.out.println();

			FormElement element = (FormElement) new BeanDao(FormElement.class).addWhereCriteria( "form.id=" + form_id + " and name='" + name + "'" ).getFirstBeanResult();

			if (element == null) {
				System.err.println("Cannot find element matching '" + name + "'");
				continue;
			}

			if( f != null ) {
				record.getFields().put( element.getId(), f.getName() );
			}
			else {
				record.getFields().put( element.getId(), null );
			}
		}

		Enumeration params = multi.getParameterNames();
		while (params.hasMoreElements()) {
			String name = params.nextElement().toString();
			String value = "";
			String values[] = multi.getParameterValues( name );
			if (values.length == 1) {
				value = values[0];
			} else if (values.length > 1) {
				value = CommonUtil.join( Arrays.asList( values ), ", " );
			}

			// TODO Remove
//			System.out.println(name + ": " + value);

			if (name.contains( "////" )) {
				// Might have caught ourselves a stray checkbox value
				String elementName = name.split( "////" )[0];
				String optionName  = name.split( "////" )[1];

				FormElement element = (FormElement) new BeanDao(FormElement.class).addWhereCriteria( "form.id=" + form_id + " and name='" + elementName + "'" ).getFirstBeanResult();

				if (element != null) {
					// Append values
					String currentValues = record.getFields().get( element );
					if (currentValues != null) {
						currentValues += ", ";
					} else {
						currentValues = "";
					}
					record.getFields().put( element.getId(), currentValues + optionName );
					continue;
				}
			}

			FormElement element = (FormElement) new BeanDao(FormElement.class).addWhereCriteria( "form.id=" + form_id + " and name='" + name + "'" ).getFirstBeanResult();

			if (element == null) {
				System.err.println("Cannot find element matching '" + name + "'");
				continue;
			}

			String currentValues = record.getFields().get( element );
			if (currentValues != null) {
				currentValues += ", ";
			} else {
				currentValues = "";
			}
			record.getFields().put( element.getId(), currentValues + value );
		}

		record.saveDetails();
		form.saveDetails();

		/* Have to construct String here to wait for all checkbox values to collect */
		String recordString = "";
		for (FormElement e : form.getElements()) {
			recordString += "<b>" + e.getName() + "</b>: " + record.getFields().get( e ) + "<br />";
		}

		/* Send E-Mail Report */
		JDynamiTe template = new JDynamiTe();
		template.setInput( getClass().getResourceAsStream( "../resources/templates/newRecord.html" ) );
		template.setVariable( "DATE", new Date().toString() );
		template.setVariable( "RECORD", recordString );
		template.parse();

		for (String email : form.getEmails()) {
			try {
				AplosEmail aplosEmail = new AplosEmail( "New submission for '" + form.getName() + "'", template.toString() );
				aplosEmail.setFromAddress( CommonUtil.getAdminUser().getEmail() );
				aplosEmail.addToAddress( email );
				aplosEmail.sendAplosEmailToQueue();
			} catch (Exception e) {
				// TODO Some useful error handling

//				final String error = "Could not send confirmation E-Mail. Is the address valid?";
//				ErrorEmailSender.sendErrorEmail( ApplicationUtil.getAplosContextListener(), e );
//				FacesContext.getCurrentInstance().addMessage( null, new FacesMessage(FacesMessage.SEVERITY_ERROR, error, error) );
			}
		}

		if (multi.getParameter( "return" ) != null) {
			multi.getParameter( "return" );
			response.sendRedirect( multi.getParameter( "return" ) );
		}
	}

}