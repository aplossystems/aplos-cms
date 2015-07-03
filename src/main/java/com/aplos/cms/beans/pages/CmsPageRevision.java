package com.aplos.cms.beans.pages;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringEscapeUtils;

import com.aplos.cms.PlaceholderContentWrapper;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

@Entity
@PluralDisplayName(name="CMS page revisions")
@Cache
public class CmsPageRevision extends TemplateContent {
	private static final long serialVersionUID = -6664232745189920952L;

/*
 *  These scripts can be used to quickly find strings in a file and relate them to a cmsPageRevision
 * 	select cpr.* from cmspagerevision cpr LEFT OUTER JOIN cmspagerevision_cmsplaceholdercontent cpr_cpc ON cpr.id = cpr_cpc.templateContent_id WHERE cpr_cpc.cmsPlaceholderContent_id IN (select id from cmsplaceholdercontent where content LIKE '%MODULE:%')
 *  select it.* from innertemplate it LEFT OUTER JOIN innertemplate_cmsplaceholdercontent it_cpc ON it.id = it_cpc.templateContent_id WHERE it_cpc.cmsPlaceholderContent_id IN (select id from cmsplaceholdercontent where content LIKE '%MODULE:%')
 */
	
	private String title;

	@Column(columnDefinition="LONGTEXT")
	private String metaDescription;

	@Column(columnDefinition="LONGTEXT")
	private String metaKeywords;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private CmsPage cmsPage;
	
	@ManyToOne(fetch=FetchType.LAZY) 
	private CmsPageRevision deletedRedirect = null;

	private String cphName;

	private String backingPageBinding;
	
	@OneToMany(mappedBy="cmsPageRevision",fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	//@Cascade(value=CascadeType.ALL) -> MANUALLY SAVE -> We need to hit write view to file
	private List<CodeSnippet> codeSnippetList = new ArrayList<CodeSnippet>();

	private boolean draft = false;
	private boolean current = true;
	
	@Transient
	private boolean isRenderingForm = true;
	@Transient
	private String titleOverride;
	@Transient
	private String canocialPathOverride;

	public enum PageStatus implements LabeledEnumInter {
		UNPUBLISHED("Unpublished"), PUBLISHED("Published"), INACTIVE ("Inactive");

		private final String label;
		PageStatus(String label) { this.label = label; }
		@Override
		public String getLabel() { return label; }
		public List<SelectItem> getItems() {
			return CommonUtil.getEnumSelectItems(PageStatus.class, null);
		}
	};

	public CmsPageRevision() {}

	public CmsPageRevision( CmsPageRevision cmsPageRevision) {
		this.setTemplate(cmsPageRevision.getCmsTemplate(), true, true);

		Set<String> keySet = cmsPageRevision.getPlaceholderContentMap().keySet();
		Iterator<String> iterator = keySet.iterator();
		String key;
		while(iterator.hasNext()) {
			key = iterator.next();
			this.addPlaceholderContent( key, cmsPageRevision.getPlaceholderContentMap().get( key ).getCopy() );
		}

		List<CmsAtom> cmsAtoms = new ArrayList<CmsAtom>();
		for (int i=0 ; i<cmsPageRevision.getCmsAtomList().size() ; i++) {
			cmsAtoms.add(cmsPageRevision.getCmsAtomList().get(i).getCopy());
		}

		this.setCmsAtomList( cmsAtoms );

		Map<Long, CmsAtom> cmsAtomsPassedThrough = new HashMap<Long, CmsAtom>();
		Set<Long> keySet2 = cmsPageRevision.getCmsAtomPassedThroughMap().keySet();
		Iterator<Long> iterator2 = keySet2.iterator();
		while (iterator2.hasNext()) {
			Long myLong = iterator2.next();
			cmsAtomsPassedThrough.put( myLong, cmsPageRevision.getCmsAtomPassedThroughMap().get(myLong).getCopy() );
		}

		this.setCmsAtomPassedThroughMap( cmsAtomsPassedThrough );

		this.setXmlNamespaceStr(cmsPageRevision.getXmlNamespaceStr());
		this.setTitle( cmsPageRevision.getTitle() );
		this.setMetaDescription( cmsPageRevision.getMetaDescription() );
		this.setMetaKeywords( cmsPageRevision.getMetaKeywords() );

		CmsPage cmsPage = new CmsPage();

		BeanDao cmsPageDao = new BeanDao( CmsPage.class );
		cmsPageDao.setSelectCriteria("bean.mapping");
		cmsPageDao.addWhereCriteria( "bean.mapping like '" + cmsPageRevision.getCmsPage().getMapping() + "-copy%'" );
		List<String> mappings = cmsPageDao.getAll();
		int count = 0;
		String newMapping = cmsPageRevision.getCmsPage().getMapping() + "-copy";
		while (mappings.contains( newMapping )) {
			newMapping = (cmsPageRevision.getCmsPage().getMapping() + "-copy").concat( String.valueOf(++count) );
		}
		cmsPage.setMapping( newMapping );

		cmsPage.setName( cmsPageRevision.getCmsPage().getName() + "-copy" + (count == 0 ? "" : count) );
		cmsPage.setOwner( JSFUtil.getLoggedInUser() );
		cmsPage.setCachedMappingPath( cmsPageRevision.getCmsPage().getCachedMappingPath() );
		cmsPage.setAuthors(cmsPageRevision.getCmsPage().getAuthors());

		this.setCmsPage(cmsPage);
		this.setCphName( cmsPageRevision.getCphName() );
	}

	@Override
	public List<String> getTemplateEditableCphNameList(boolean toLowerCase) {
		return getCmsTemplate().getEditableCphNameList(true, toLowerCase);
	}

	public List<PlaceholderContentWrapper> getPlaceholderContentWrapperList() {
		return getPlaceholderContentWrapperList(true);
	}

	@Override
	public void setTemplate(CmsTemplate cmsTemplate) {
		boolean hasTemplateChanged = cmsTemplate.hasTemplateChanged(this.getCmsTemplate());
		setTemplate( cmsTemplate, true, hasTemplateChanged );
	}

	public CmsPageRevision getDraftCopy() {
		CmsPageRevision newCmsPageRevision = new CmsPageRevision();
		newCmsPageRevision.setTitle( title );
		newCmsPageRevision.setMetaDescription( metaDescription );
		newCmsPageRevision.setMetaKeywords( metaKeywords );
		newCmsPageRevision.setTemplate( getCmsTemplate(), true, true );
		newCmsPageRevision.setCmsPage( cmsPage );
		newCmsPageRevision.setCphName( cphName );
		new HashSet<SystemUser>();
		Map<String,CmsPlaceholderContent> newPlaceholderContentMap = new HashMap<String, CmsPlaceholderContent>();
		for( String phcKey : getPlaceholderContentMap().keySet() ) {
			newPlaceholderContentMap.put( phcKey.toLowerCase(), getPlaceholderContentMap().get( phcKey ).getCopy() );
		}
		newCmsPageRevision.setPlaceholderContentMap(newPlaceholderContentMap);
		ArrayList<CmsAtom> newCmsAtomList = new ArrayList<CmsAtom>();
		for( CmsAtom cmsAtom : getCmsAtomList() ) {
			newCmsAtomList.add( cmsAtom );
		}
		newCmsPageRevision.setCmsAtomList(newCmsAtomList);
		newCmsPageRevision.setBackingPageBinding(backingPageBinding);
		newCmsPageRevision.setDraft(isDraft());
		newCmsPageRevision.setCurrent(isCurrent());
		return newCmsPageRevision;
	}

	public ConfigurableDeveloperCmsAtom getFirstCmsAtomPassedThroughByClass( Class<? extends ConfigurableDeveloperCmsAtom> cmsAtomClass ) {
		for( CmsAtom cmsAtom : getCmsAtomPassedThroughMap().values() ) {
			if( ApplicationUtil.getClass( cmsAtom ) == cmsAtomClass ) {
				return (ConfigurableDeveloperCmsAtom) cmsAtom;
			}
		}
		return null;
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		if( fullInitialisation ) {
//			HibernateUtil.initialiseMap(getPlaceholderContentMap(), true);
//			HibernateUtil.initialiseList(getCodeSnippetList(), fullInitialisation);
//			for (String key : getPlaceholderContentMap().keySet()) {
//				HibernateUtil.initialise(getPlaceholderContentMap().get(key), fullInitialisation);
//			}
//			HibernateUtil.initialise(getDeletedRedirect(), fullInitialisation);
//		}
//		HibernateUtil.initialise(getCmsPage(), fullInitialisation);
//	}

	@Override
	public String getDisplayName() {
		return cmsPage.getName();
	}

	//get Selected CPH type - used to remember which tab we are looking at on an edit page
	public String getSelCPHType() {
		if( getPlaceholderContent( (String) JSFUtil.getRequest().getAttribute( "cphName" ) ) != null ) {
			return getPlaceholderContent( (String) JSFUtil.getRequest().getAttribute( "cphName" ) ).getCphType().getLabel();
		} else {
			return null;
		}
	}

	public void setSelCPHType( String cphType ) {
		//  TODO change the type of the placeholderContent
	}

	public void commit() {
		commit( JSFUtil.getLoggedInUser() );
	}

	public void commit( SystemUser adminUser ) {
		if( draft ) {
			BeanDao aqlBeanDao = new BeanDao( CmsPageRevision.class );
			aqlBeanDao.addWhereCriteria( "cmsPage.id = " + getCmsPage().getId() + " AND current = true" );
			CmsPageRevision prevCmsPageRevision = (CmsPageRevision) aqlBeanDao.getFirstBeanResult();

			for ( PlaceholderContent tempEntry : prevCmsPageRevision.getPlaceholderContentMap().values()) {
				if( tempEntry instanceof CmsPlaceholderContent ) {
					((CmsPlaceholderContent) tempEntry).hardDelete();
				}
			}
			prevCmsPageRevision.hardDelete();
		}
		draft = false;
		setCurrent(true);
		saveDetails( adminUser );
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		/* If this method throws errors that suggest the mapping is not unique
		 * when saving the UserCmsModules you will need to alter the tables
		 * page_usercmsmodule and page_draft_usercmsmodule - You must drop the
		 * UNIQUE index on the userCmsModulemMapping_id and add that same column
		 * to the primary key
		 */
		super.saveBean(currentUser);

		if (this.isActive()) {
			writeViewToFile();
		}

	}
	
	public UserCmsModule getUserModule( int userModuleId ) {
		UserCmsModule userCmsModule = new BeanDao( UserCmsModule.class ).get( userModuleId );
		return userCmsModule;
	}

	public File writeViewToFile() {
		String viewAsString = generateXhtml();
		viewAsString = parseCmsAtoms( viewAsString );
		return CmsModule.writeViewToFile(viewAsString, CmsWorkingDirectory.CMS_PAGE_REVISION_VIEW_FILES, getId(), "xhtml" );
	}

 	public void saveAsCopy() {
		setId( null );
		for ( PlaceholderContent singleEntry : getPlaceholderContentMap().values()) {
			if( singleEntry instanceof CmsPlaceholderContent ) {
				((CmsPlaceholderContent) singleEntry).setId( null );
				singleEntry.saveDetails();
			}
		}
		this.saveDetails();
	}
	
	public boolean isShowingCanonicalLink() {
		return !CommonUtil.isNullOrEmpty( getDeterminedCanonicalPath() );
	}

	public String getDeterminedCanonicalPath() {
		if( !CommonUtil.isNullOrEmpty(getCanocialPathOverride()) ) {
			return getCanocialPathOverride();
		}
		return getCmsPage().getCanonicalPath();
	}

	public String getDeterminedPageTitle() {
		CmsWebsite site = CmsWebsite.getCmsWebsite();
		if( !CommonUtil.isNullOrEmpty(getTitleOverride()) ) {
			return getTitleOverride();
		}
		String determinedTitle = title;
		if ( CommonUtil.isNullOrEmpty(determinedTitle) ) {
			determinedTitle = site.getDefaultPageTitle();
		}
		if (!CommonUtil.isNullOrEmpty(determinedTitle) ) {
			try {
				/*
				 * We need to make sure that the title is of the correct encoding, otherwise it will break when
				 * going to output.
				 */
				//determinedTitle = XmlEntityUtil.replaceCharactersWithEntities(determinedTitle);

				/* We actually do the *opposite* of what the comment above says now we have switched to
				 * file based views otherwise the title shows the entity itself (because we are now using el which escapes for us)
				 */
				determinedTitle = XmlEntityUtil.replaceEntitiesWith(determinedTitle, XmlEntityUtil.EncodingType.CHARACTER);
				return new String( determinedTitle.getBytes( "UTF-8" ) );
			} catch( UnsupportedEncodingException useEx ) {
				ApplicationUtil.getAplosContextListener().handleError( useEx );
			}
		}
		return null;
	}
	
	@Override
	public String getElPath() {
		return "cmsPageRevision";
	}

	public String getDeterminedPageDescription() {
		CmsWebsite site = CmsWebsite.getCmsWebsite();
		String metaDescription = getMetaDescription();
		if( metaDescription == null || metaDescription.equals( "" ) ) {
			metaDescription = site.getDefaultPageDescription();
		}
		if (metaDescription != null && !metaDescription.equals( "" )) {
			return StringEscapeUtils.escapeXml( metaDescription );
		}
		return null;
	}

	public String getDeterminedPageKeywords() {
		CmsWebsite site = CmsWebsite.getCmsWebsite();
		String metaKeywords = getMetaKeywords();
		if( metaKeywords == null || metaKeywords.equals( "" ) ) {
			metaKeywords = site.getDefaultPageKeywords();
		}
		if (metaKeywords != null && !metaKeywords.equals( "" )) {
			return StringEscapeUtils.escapeXml( metaKeywords );
		}
		return null;
	}

	@Override
	protected void appendXhtmlForTemplateContent( String contextRoot, StringBuffer xhtmlBuffer ) {
		//Im not sure if this is the perfect place for this, but it's the part of generateXhtml which is already overridden
		//Include head content for any atoms we want on this page
		xhtmlBuffer.append("\n<ui:define name=\"pageAtomHeadContent\">\n");
		for( int i = 0, n = getCmsAtomList().size(); i < n; i++ ) {
			if ( getCmsAtomList().get( i ) instanceof DeveloperCmsAtom ) {
				xhtmlBuffer.append(((DeveloperCmsAtom) getCmsAtomList().get( i )).addFrontEndHeadUrl());
			}
		}
		for( int i = 0, n = getCmsAtomPassedThroughMap().size(); i < n; i++ ) {
			if( getCmsAtomPassedThroughMap().get( i ) instanceof DeveloperCmsAtom ) {
				xhtmlBuffer.append(((DeveloperCmsAtom) getCmsAtomPassedThroughMap().get( i )).addFrontEndHeadUrl());
			}
		}
		xhtmlBuffer.append( "</ui:define>\n" );
		super.appendXhtmlForTemplateContent( contextRoot, xhtmlBuffer );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription( String metaDescription ) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords( String metaKeywords ) {
		this.metaKeywords = metaKeywords;
	}

	public void setDraft( boolean draft ) {
		this.draft = draft;
	}

	public boolean isDraft() {
		return draft;
	}

	public void setBackingPageBinding(String backingPageBinding) {
		this.backingPageBinding = backingPageBinding;
	}

	public String getBackingPageBinding() {
		return backingPageBinding;
	}

	public void setCmsPage(CmsPage cmsPage) {
		this.cmsPage = cmsPage;
	}

	public CmsPage getCmsPage() {
		return cmsPage;
	}

	public void setCphName(String cphName) {
		this.cphName = cphName;
	}

	public String getCphName() {
		return cphName;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public boolean isCurrent() {
		return current;
	}

	public List<CodeSnippet> getCodeSnippetList() {
		return codeSnippetList;
	}

	public void setCodeSnippetList(List<CodeSnippet> codeSnippetList) {
		this.codeSnippetList = codeSnippetList;
	}
	
	@Override
	public List<PlaceholderContentWrapper> getPlaceholderContentWrapperList( boolean isForCmsPageRevision ) {
		List<PlaceholderContentWrapper> phcWrapperList = super.getPlaceholderContentWrapperList(isForCmsPageRevision);
		for ( CodeSnippet codeSnippet : getCodeSnippetList() ) {
			PlaceholderContentWrapper tempPhcWrapper = new PlaceholderContentWrapper();
			tempPhcWrapper.setCphName( codeSnippet.getSnippetName() );
			tempPhcWrapper.setPhcMap(getPlaceholderContentMap());
			tempPhcWrapper.setPlaceholderContent(codeSnippet);
			phcWrapperList.add( tempPhcWrapper );
		}
		return phcWrapperList;
	}

	public CmsPageRevision getDeletedRedirect() {
		return deletedRedirect;
	}

	public void setDeletedRedirect(CmsPageRevision deletedRedirect) {
		this.deletedRedirect = deletedRedirect;
	}

	public boolean isRenderingForm() {
		return isRenderingForm;
	}

	public void setRenderingForm(boolean isRenderingForm) {
		this.isRenderingForm = isRenderingForm;
	}

	public String getTitleOverride() {
		return titleOverride;
	}

	public void setTitleOverride(String titleOverride) {
		this.titleOverride = titleOverride;
	}

	public String getCanocialPathOverride() {
		return canocialPathOverride;
	}

	public void setCanocialPathOverride(String canocialPathOverride) {
		this.canocialPathOverride = canocialPathOverride;
	}

}
