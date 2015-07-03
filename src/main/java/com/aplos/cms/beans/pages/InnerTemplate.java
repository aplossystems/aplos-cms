package com.aplos.cms.beans.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aplos.cms.PlaceholderContentWrapper;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;

@Entity
@Cache
@DynamicMetaValueKey(oldKey={"INNER_TEMPLATE"})
public class InnerTemplate extends TemplateContent implements CmsTemplate {
	private static final long serialVersionUID = -716732834538627446L;

	private String name;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="innerTemplate_cmsAtomPassThrough")
	@Cache
	@Cascade({CascadeType.ALL})
	private List<CmsAtomPassThrough> cmsAtomPassThroughList = new ArrayList<CmsAtomPassThrough>();

	public InnerTemplate() {}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseMap(getPlaceholderContentMap(), fullInitialisation);
//		HibernateUtil.initialiseList(getCmsAtomPassThroughList(), fullInitialisation);
//	}

	@Override
	public String getElPath() {
		return "cmsPageRevision.cmsTemplate";
	}

	public List<PlaceholderContentWrapper> getPlaceholderContentWrapperList() {
		return getPlaceholderContentWrapperList(false);
	}

//	@Override
//	public void addXhtmlContentToPageContentHolder( CmsPageContentHolder cmsPageContentHolder, String content ) {
//		cmsPageContentHolder.addInnerTemplateContent( getId(), content );
//	}

	@Override
	public List<String> getTemplateEditableCphNameList(boolean toLowerCase) {
		return getCmsTemplate().getEditableCphNameList(false, toLowerCase);
	}

//	@Override
//	public void setTemplate(CmsTemplate cmsTemplate,boolean isForCmsPageRevision) {
//		super.setTemplate(cmsTemplate, isForCmsPageRevision);
//		if( ((TopLevelTemplate) getCmsTemplate()).getCprCphNameList().size() > 0 ) {
//
//		}
//	}

	@Override
	public void setTemplate(CmsTemplate cmsTemplate) {
		boolean hasTemplateChanged = cmsTemplate.hasTemplateChanged(this.getCmsTemplate());
		setTemplate( cmsTemplate, false, hasTemplateChanged );
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
	public void includeContent( String contextRoot, StringBuffer xhtmlBuffer, String cphName, String templateContent ) {
		xhtmlBuffer.append("<ui:define name='" + cphName + "'>");
		templateContent = CmsModule.parseCmsContentTags(templateContent);
		xhtmlBuffer.append(templateContent);
		xhtmlBuffer.append( "</ui:define>\n" );
	}

	@Override
	public List<String> getCphNameList(boolean toLowerCase) {
		Pattern pattern = Pattern.compile("ui:insert\\s*name=[\"'](.*?)[\"']");
		Set<String> matches = new HashSet<String>();

		for( PlaceholderContent phc : getPlaceholderContentMap().values() ) {
			if( phc.getContent() != null ) {
			Matcher m = pattern.matcher( phc.getContent() );
				while (m.find()) {
					String match = m.group(1);
					if (!match.startsWith( "_" )) {
						matches.add( m.group( 1 ) );
					}

				}
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

	@Override
	public boolean hasTemplateChanged(CmsTemplate dbTemplate) {
		InnerTemplate dbInnerTemplate = (InnerTemplate) dbTemplate;
		if (dbInnerTemplate==null) {
			return true;
		}
		//check against the values currently in memory
		List<String> theseCph = this.getEditableCphNameList(true, true);
		List<String> oldCph = dbInnerTemplate.getEditableCphNameList(true, true);

		List<CmsAtom> cmsAtomPassThroughList = new ArrayList<CmsAtom>( getCmsAtomPassedThroughMap().values() );
		List<CmsAtom> dbCmsAtomPassThroughList = new ArrayList<CmsAtom>( dbInnerTemplate.getCmsAtomPassedThroughMap().values() );
		if( cmsAtomPassThroughList.size() == dbCmsAtomPassThroughList.size() ) {
			for( int i = 0, n = cmsAtomPassThroughList.size(); i < n; i++ ) {
				if( !cmsAtomPassThroughList.get( i ).equals( dbCmsAtomPassThroughList.get( i ) ) ) {
					return false;
				}
			}
		} else {
			return false;
		}
		return !theseCph.equals(oldCph) || !this.getId().equals( dbTemplate.getId() );
	}

	@Override
	public List<String> getEditableCphNameList(boolean isForCmsPageRevision, boolean toLowerCase) {
		List<String> cphList = getCphNameList(toLowerCase);
		for( int i = cphList.size() - 1; i > -1; i-- ) {
			if( cphList.get( i ).contains( "uneditable" ) ) {
				cphList.remove( cphList.get( i ) );
			}
		}
		cphList.addAll( ((TopLevelTemplate) getCmsTemplate()).getCprCphNameList() );
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
		boolean wasNew = isNew();
		InnerTemplate dbTemplate = null;
		if( !wasNew ) {
			dbTemplate = new BeanDao( InnerTemplate.class ).get( getId() );
		}
//		this.getPlaceholderContentMap().get( "Template main Content" ).getContent();
		super.saveBean(currentUser);
		
	//  if !new or changed from db template
		if ( !wasNew && hasTemplateChanged(dbTemplate)) {
			updateCmsPageRevisionChildren();
		}

		if (this.isActive()) {
			writeViewToFile();
		}

	}
	
	@Override
	public String parseCmsAtoms(String viewAsString) {
		viewAsString = super.parseCmsAtoms(viewAsString);

		Pattern pattern = Pattern.compile("\\{_CAPT_([0-9]*?)_([0-9]*)\\}");
		Matcher matcher = pattern.matcher(viewAsString);
		while (matcher.find()) {
			Long cmsAtomPassThroughId = Long.parseLong( matcher.group(1) );

			CmsAtomPassThrough cmsAtomPassThrough = null;
			for( int i = 0, n = cmsAtomPassThroughList.size(); i < n; i++ ) {
				if( cmsAtomPassThroughList.get( i ).getId().equals( cmsAtomPassThroughId ) ) {
					cmsAtomPassThrough = cmsAtomPassThroughList.get( i );
				}
			}
			/*
			 * This was commented out on the 20 May 2013 as I couldn't find the viewId being used anywhere.
			 */
//			if( cmsAtomPassThrough != null && (matcher.group(2) == null || !matcher.group(2).equals( cmsAtomPassThrough.getViewId() )) ) {
//				cmsAtomPassThrough.setViewId( Integer.parseInt( matcher.group(2) ) );
//				cmsAtomPassThrough.aqlSaveDetails();	
//			}
			viewAsString = matcher.replaceFirst( "<ui:insert name=\"CAPT:" + cmsAtomPassThroughId + ":" + matcher.group(2) + "\" ></ui:insert>" );
			matcher = pattern.matcher(viewAsString);
		}
		return viewAsString;
	}

	public void writeViewToFile() {
		String viewAsString = generateXhtml();
		viewAsString = parseCmsAtoms( viewAsString );
		CmsModule.writeViewToFile( viewAsString, CmsWorkingDirectory.INNER_TEMPLATE_VIEW_FILES, getId(), "xhtml");
	}

	public void updateCmsPageRevisionChildren() {
		//  if different load all of the pages and run the script to add the corresponding CPH sections
		BeanDao aqlBeanDao = new BeanDao( CmsPageRevision.class );
		aqlBeanDao.addWhereCriteria( "bean.cmsTemplate.class = 'INNER_TEMPLATE' AND bean.cmsTemplate.id=" + this.getId() );
		List<CmsPageRevision> pageList = aqlBeanDao.getAll();
		for (CmsPageRevision page : pageList) {
			page.setTemplate(this,true,true);
			page.saveDetails();
		}
	}

	public void setCmsAtomPassThroughList(List<CmsAtomPassThrough> cmsAtomPassThroughList) {
		this.cmsAtomPassThroughList = cmsAtomPassThroughList;
	}

	@Override
	public List<CmsAtomPassThrough> getCmsAtomPassThroughList() {
		return cmsAtomPassThroughList;
	}

	@Override
	protected void appendXhtmlForTemplateContent( String contextRoot, StringBuffer xhtmlBuffer ) {
		xhtmlBuffer.append("\n<ui:define name=\"templateAtomHeadContent\">\n");
		for( int i = 0, n = getCmsAtomList().size(); i < n; i++ ) {
			if ( getCmsAtomList().get( i ) instanceof DeveloperCmsAtom ) {
				xhtmlBuffer.append(((DeveloperCmsAtom) getCmsAtomList().get( i )).addFrontEndHeadUrl());
			}
		}
		//TODO: check, do we need this or does cmspagerevision automatically get pass throughs from both levels of template?
//		for( int i = 0, n = getCmsAtomPassedThroughMap().size(); i < n; i++ ) {
//			if( getCmsAtomPassedThroughMap().get( i ) instanceof DeveloperCmsAtom ) {
//				xhtmlBuffer.append(((DeveloperCmsAtom) getCmsAtomPassedThroughMap().get( i )).addFrontEndHeadUrl());
//			}
//		}
		xhtmlBuffer.append( "</ui:define>\n" );
		super.appendXhtmlForTemplateContent( contextRoot, xhtmlBuffer );
	}
}
