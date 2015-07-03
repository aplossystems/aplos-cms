package com.aplos.cms.beans.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@Cache
@DynamicMetaValueKey(oldKey={"TOP_LEVEL_TEMPLATE"})
public class TopLevelTemplate extends AplosSiteBean implements CmsTemplate {
	private static final long serialVersionUID = 2120251865244655913L;

	private String name;

	@Column(columnDefinition="LONGTEXT")
	private String head = "<!-- The reserved name \"_title\" will be filled by the page title automatically -->\n<title><ui:insert name='_title'>My Default Title</ui:insert></title>";

	@Column(columnDefinition="LONGTEXT")
	private String body = "<ui:insert name='Body'>Default body text</ui:insert>";

	@Column(columnDefinition="LONGTEXT")
	private String xmlNamespaceStr;

	@Transient
	List<String> headCPHList;  // Head content place holders
	@Transient
	List<String> bodyCPHList;  // Body content place holders

	@OneToMany
	@JoinTable(name="topLevelTemplate_cmsAtomPassThrough")
	@Cache
	private List<CmsAtomPassThrough> cmsAtomPassThroughList = new ArrayList<CmsAtomPassThrough>();


	public TopLevelTemplate() {
		
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getCmsAtomPassThroughList(), fullInitialisation);
//	}

	@Override
	public List<String> getCphNameList(boolean toLowerCase) {
		Pattern pattern = Pattern.compile("ui:insert\\s*name=[\"'](.*?)[\"']");
		Set<String> matches = new HashSet<String>();

		Matcher m = pattern.matcher( head );
		while (m.find()) {
			String match = m.group(1);
			if (!match.startsWith( "_" )) {
				matches.add( m.group( 1 ) );
			}

		}

		m = pattern.matcher( body );
		while (m.find()) {
			String match = m.group(1);
			if (!match.startsWith( "_" )) {
				matches.add( m.group( 1 ) );
			}
		}
		List<String> cphNameList = new ArrayList<String>(matches);
		if( toLowerCase ) {
			for( int i = 0, n = cphNameList.size(); i < n; i++ ) {
				cphNameList.set( i, cphNameList.get( i ).toLowerCase() );
			}
		}
		return cphNameList;
	}

	public List<String> getCprCphNameList() {
		List<String> cprCphList = getCphNameList(false);
		for( int i = cprCphList.size() - 1; i > -1; i-- ) {
			if( !cprCphList.get( i ).contains( "CPR_CPH" ) ) {
				cprCphList.remove( cprCphList.get( i ) );
			}
		}
		return cprCphList;
	}

	@Override
	public boolean cmsAtomPassThroughListContains( Long cmsAtomPassThroughId ) {
		for( CmsAtomPassThrough cmsAtomPassThrough : cmsAtomPassThroughList ) {
			if( cmsAtomPassThrough.getId().equals( cmsAtomPassThroughId ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getEditableCphNameList( boolean isForCmsPageRevision, boolean toLowerCase ) {
		List<String> cphList = getCphNameList(toLowerCase);
		for( int i = cphList.size() - 1; i > -1; i-- ) {
			if( cphList.get( i ).contains( "uneditable" ) ) {
				cphList.remove( cphList.get( i ) );
			} else if( !isForCmsPageRevision && cphList.get( i ).contains( "cpr_cph" ) ) {
				cphList.remove( cphList.get( i ) );
			}
		}
		return cphList;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		//  get the template from the database (will have the old values)
		if( !isNew() ) {
			TopLevelTemplate dbTemplate = new BeanDao( TopLevelTemplate.class ).get( getId() );
			//  if !new or changed from db template
			if (dbTemplate == null || hasTemplateChanged(dbTemplate)) {
				//  if different load all of the pages and run the script to add the corresponding CPH sections
				BeanDao cmsPageRevisionDao = new BeanDao( CmsPageRevision.class );
				cmsPageRevisionDao.addWhereCriteria( "bean.cmsTemplate.class = 'TOP_LEVEL_TEMPLATE' AND bean.cmsTemplate.id=" + this.getId() );
				List<CmsPageRevision> pageList = cmsPageRevisionDao.getAll();
				for (CmsPageRevision page : pageList) {
					page.setTemplate(this,true,true);
					page.saveDetails();
				}
	
				BeanDao innerTemplateDao = new BeanDao( InnerTemplate.class );
				innerTemplateDao.addWhereCriteria( "bean.cmsTemplate.id=" + this.getId() );
				List<InnerTemplate> innerTemplateList = innerTemplateDao.getAll();
				boolean hasCprCphModulesChanged = hasCprCphModulesChanged(dbTemplate);
				//  Need to load it out of the session each time or hibernate throws
				//  a Transient Object wobbly.
				for (InnerTemplate innerTemplate : innerTemplateList) {
					InnerTemplate loadedInnerTemplate = (InnerTemplate) new BeanDao( InnerTemplate.class ).get( innerTemplate.getId() );
					loadedInnerTemplate.setTemplate(this,false,true);
					if( hasCprCphModulesChanged ) {
						loadedInnerTemplate.updateCmsPageRevisionChildren();
					}
					loadedInnerTemplate.saveDetails();
	//				HibernateUtil.startNewTransaction();
	//				session = HibernateUtil.getCurrentSession();
				}
			}
		}
		for (CmsAtomPassThrough cmsAtomPassThrough : cmsAtomPassThroughList ) {
			cmsAtomPassThrough.saveDetails( currentUser );
		}
		super.saveBean( currentUser );

		if (this.isActive()) {
			writeViewToFile();
		}

	}

	@Override
	public boolean hasTemplateChanged(CmsTemplate dbTemplate) {
		if (dbTemplate==null) {
			return true;
		}
		//check against the values currently in memory
		List<String> theseCph = this.getCphNameList(false);
		for (int i=0; i < theseCph.size(); i++) {
			if (!theseCph.get(i).contains("MODULE:")) {
				theseCph.remove(i);
			}
		}
		List<String> oldCph = dbTemplate.getCphNameList(false);
		for (int i=0; i < oldCph.size(); i++) {
			if (!oldCph.get(i).contains("MODULE:")) {
				oldCph.remove(i);
			}
		}
		return !theseCph.equals(oldCph) || !this.getId().equals( dbTemplate.getId() );
	}

	public boolean hasCprCphModulesChanged(CmsTemplate dbTemplate) {
		if (dbTemplate==null) {
			return true;
		}
		return !this.getCprCphNameList().equals(((TopLevelTemplate)dbTemplate).getCprCphNameList());
	}

	public void writeViewToFile() {
		CmsModule.writeViewToFile(generateXhtml(), CmsWorkingDirectory.TOP_TEMPLATE_VIEW_FILES, getId(), "xhtml" );
	}

	@Override
	public String generateXhtml() {
		StringBuffer xhtmlBuffer = new StringBuffer();
		xhtmlBuffer.append( "<ui:composition template=\"/templates/frontend.xhtml\"");
		xhtmlBuffer.append( " xmlns=\"http://www.w3.org/1999/xhtml\"");
		xhtmlBuffer.append( " xmlns:ui=\"http://java.sun.com/jsf/facelets\" " );
		if ( getXmlNamespaceStr() != null ) {
			xhtmlBuffer.append( getXmlNamespaceStr() );
		}
		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:aplos")) {
			//need the aplos library for including menus invisibly
			xhtmlBuffer.append( " xmlns:aplos=\"http://www.aplossystems.co.uk/aplosComponents\" " );
		}
		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:h=")) {
			//need the h library for {_page_name} (becomes a h output text)
			xhtmlBuffer.append( " xmlns:h=\"http://java.sun.com/jsf/html\" " );
		}
		xhtmlBuffer.append( " >\n" );
		//for ease of debugging
		
		xhtmlBuffer.append( "<ui:define name='__templateInsertHead'>\n");
		if( getParentWebsite() != null ) {
			String googleAnalyticsId = getParentWebsite().getGoogleAnalyticsId();
			if( !CommonUtil.isNullOrEmpty( googleAnalyticsId ) ) {
				Website.addGoogleAnalyticsCode( xhtmlBuffer, googleAnalyticsId );
			}
		}

		xhtmlBuffer.append("\n<ui:insert name=\"templateAtomHeadContent\" />\n");
		xhtmlBuffer.append("\n<ui:insert name=\"pageAtomHeadContent\" />\n");
		String contextPath = JSFUtil.getContextPath();
		String contextRootWithoutSlash = contextPath.replace( "\\", "" ).replace( "/", "" );


		String headContent = CommonUtil.includeContextRootInPaths( contextRootWithoutSlash, getHead() );
		//headContent = CmsModule.parseCmsContentTags(headContent);
		xhtmlBuffer.append(headContent);
		xhtmlBuffer.append( "\n</ui:define>\n" );
		xhtmlBuffer.append( "<ui:define name='__templateInsertBody'>\n");
		String bodyContent = CommonUtil.includeContextRootInPaths( contextRootWithoutSlash, getBody() );
		//bodyContent = CmsModule.parseCmsContentTags(bodyContent);
		xhtmlBuffer.append(bodyContent);
		xhtmlBuffer.append( "\n</ui:define>\n" );
		xhtmlBuffer.append( "</ui:composition>\n" );
		return xhtmlBuffer.toString();
	}

	public String getHead() {
		return head;
	}

	public void setHead( String head ) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody( String body ) {
		this.body = body;
	}

	public void setCmsAtomPassThroughList(List<CmsAtomPassThrough> cmsAtomPassThroughList) {
		this.cmsAtomPassThroughList = cmsAtomPassThroughList;
	}

	@Override
	public List<CmsAtomPassThrough> getCmsAtomPassThroughList() {
		return cmsAtomPassThroughList;
	}

	public void setXmlNamespaceStr(String xmlNamespaceStr) {
		this.xmlNamespaceStr = xmlNamespaceStr;
	}

	public String getXmlNamespaceStr() {
		return xmlNamespaceStr;
	}
}
