package com.aplos.cms.beans.customforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;

@Entity
public class Form extends AplosBean {

	private static final long serialVersionUID = -3887960323607272728L;
	private String name;
	int count = 0;

	@Column(columnDefinition="LONGTEXT")
	private String description;

	@OneToMany
	@Cascade({CascadeType.ALL})
	@JoinColumn(name="form_id")
	@IndexColumn(name="position")
	private List<FormElement> elements = new ArrayList<FormElement>();

	@OneToMany
	@Cascade({CascadeType.ALL})
	@JoinColumn(name="form_id")
	@IndexColumn(name="position")
	private List<FormRecord> records = new ArrayList<FormRecord>();

	@CollectionOfElements
	private Set<String> emails = new HashSet<String>();

	@ManyToOne
	private CmsPageRevision submitPage;

	@Override
	public String getDisplayName() {
		return (name == null ? super.getDisplayName() : name);
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getElements(), fullInitialisation);
//	}

	public String getHtml(CmsPageRevision page, String contextRoot) {
		StringBuffer html = new StringBuffer();
		html.append( "<form method='post' enctype='multipart/form-data' action='" + contextRoot + "/form/submit.jsf' >");
		html.append( "<input type='hidden' name='form_id' value='" + getId() + "' />");
		html.append( "<input type='hidden' name='return' value='" + contextRoot + "/" + (submitPage==null ? page : submitPage).getCmsPage().getMapping() + "' />");

		html.append( "<table class='form'>");
		for (FormElement e : elements) {
			html.append( "<tr>");
			html.append( "<td class='formText'>");
			html.append( e.getName() );
			html.append( "</td><td class='formField'>");
			html.append( e.getHtml() );
			html.append( "</td></tr>");
		}
		html.append( "</table>" );

		html.append( "<br /><br /><input type='submit' value='Go!' /></form>" );
		return html.toString();
	}

	public void addElement() {
		FormElement element = new InputTextElement();
		element.setName( "New Field" + (count++ > 0 ? " " + count : "") );
		element.setForm( this );
		getElements().add( element );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public List<FormElement> getElements() {
		return elements;
	}

	public void setElements( List<FormElement> elements ) {
		this.elements = elements;
	}

	public void setRecords( List<FormRecord> records ) {
		this.records = records;
	}

	public List<FormRecord> getRecords() {
		return records;
	}

	public void setEmails( Set<String> emails ) {
		this.emails = emails;
	}

	public Set<String> getEmails() {
		return emails;
	}

	public void setSubmitPage( CmsPageRevision submitPage ) {
		this.submitPage = submitPage;
	}

	public CmsPageRevision getSubmitPage() {
		return submitPage;
	}

}
