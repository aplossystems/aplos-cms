package com.aplos.cms.beans.pages;

import java.io.File;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

@ManagedBean
@SessionScoped
@Entity
@PluralDisplayName(name="code snippets")
@Cache
public class CodeSnippet extends ConfigurableDeveloperCmsAtom implements PlaceholderContent {

	private static final long serialVersionUID = -2213709471345953905L;
	
	@ManyToOne
	private CmsPageRevision cmsPageRevision;

	@Column(columnDefinition="LONGTEXT")
	private String content = "<aplos:panel>Empty Code Snippet</aplos:panel>";
	
	//ConfigurableDeveloperCmsAtom covers up CmsAtom.name
	private String snippetName = null;

	@Column(columnDefinition="LONGTEXT")
	private String xmlNamespaceStr = "xmlns:aplos=\"http://www.aplossystems.co.uk/aplosComponents\"";

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public ContentPlaceholderType getCphType() {
		return ContentPlaceholderType.CODE_SNIPPET;
	}

	@Override
	public String getContent() {
		return content;
	}
	
	public String getExcerpt() {
		if (content.length() > 50) {
			return content.substring(0, 50);
		} else {
			return content;
		}
	}
	
	@Override
	public void saveBean( SystemUser systemUser ) {
		content = XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(content);
		super.saveBean( systemUser );
		if (this.isActive()) {
			writeViewToFile();

		}

	}
	
	public String generateXhtml( String contextRoot ) {
		StringBuffer xhtmlBuffer = new StringBuffer();
		xhtmlBuffer.append( "<ui:composition");
		xhtmlBuffer.append( " xmlns=\"http://www.w3.org/1999/xhtml\"");
		xhtmlBuffer.append( " xmlns:ui=\"http://java.sun.com/jsf/facelets\" " );
		if( getXmlNamespaceStr() != null ) {
			xhtmlBuffer.append( getXmlNamespaceStr() );
		}
//		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:aplos")) {
//			//need the aplos library for including menus invisibly
//			xhtmlBuffer.append( " xmlns:aplos=\"http://www.aplossystems.co.uk/aplosComponents\" " );
//		}
		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:h=")) {
			//need the h library for {_page_name} (becomes a h output text)
			xhtmlBuffer.append( " xmlns:h=\"http://java.sun.com/jsf/html\" " );
		}
		xhtmlBuffer.append( " >\n" );
		//for ease of debugging
		xhtmlBuffer.append( "<!-- Code Snippet: " + getName() + " -->\n" );
		xhtmlBuffer.append( "<!-- Last Generated: " + FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), new Date()) + " -->\n" );
		String processedContent = CommonUtil.includeContextRootInPaths( contextRoot, getContent() );
		xhtmlBuffer.append(processedContent);
		xhtmlBuffer.append( "</ui:composition>\n" );
		return xhtmlBuffer.toString();
	}

	/* Because the modules can be nested */
	public String getParsedContent(String contextRoot, CmsPageRevision cmsPageRevision) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return this.getContent();
	}

	@Override
	public String getAllInsertTexts() {
		return "{_SNIPPET_" + getId() + "}";
	}
	
	public File writeViewToFile() {
		String viewAsString = generateXhtml( JSFUtil.getContextPath() );
		viewAsString = getCmsPageRevision().parseCmsAtoms( viewAsString );
		return CmsModule.writeViewToFile( viewAsString, CmsWorkingDirectory.CODE_SNIPPET_VIEW_FILES, getId(), "xhtml" );
	}

	public void setXmlNamespaceStr(String xmlNamespaceStr) {
		this.xmlNamespaceStr = xmlNamespaceStr;
	}

	public String getXmlNamespaceStr() {
		return xmlNamespaceStr;
	}
	
	public CmsPageRevision getCmsPageRevision() {
		return cmsPageRevision;
	}

	public void setCmsPageRevision(CmsPageRevision cmsPageRevision) {
		this.cmsPageRevision = cmsPageRevision;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		return null;
	}

	@Override
	public String getName() {
		return snippetName;
	}
	
	@Override
	public void setName(String name) {
		setSnippetName(name);
	}

	public String getSnippetName() {
		return snippetName;
	}

	public void setSnippetName(String snippetName) {
		this.snippetName = snippetName;
	}
	
	@Override
	public void hardDelete() {
//		TODO: this is disabled temporarily because of a JSF issue
//		      template resolver caches results so when we remove this (and update the pages written view)
//		      the system doesnt pick it up and still tries to load the deleted view
//		if (!isNew()) {
//			String viewPath = CmsWorkingDirectory.CODE_SNIPPET_VIEW_FILES.getDirectoryPath() + getId() + ".xhtml";
//			File viewFile = new File(viewPath);
//			if (viewFile.exists()) {
//				viewFile.delete();
//			}
//		}
		super.hardDelete();
	}

}
