package com.aplos.cms.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.developermodulebacking.frontend.BasicCmsContentFeDmb;
import com.aplos.cms.developermodulebacking.frontend.BlogFeDmb;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.enums.UnconfigurableAtomEnum;
import com.aplos.cms.interfaces.BasicCmsContentAtom;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.cms.module.CmsDatabaseLoader;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@Cache
public class CmsWebsite extends Website {
	private static final long serialVersionUID = 8332643873445501056L;
	private boolean live = false;
	private Integer cmsPageLimit = -1;

	@Transient
	private List<DeveloperCmsAtom> availableCmsAtomList = new ArrayList<DeveloperCmsAtom>();
	
	@ManyToAny( metaColumn = @Column( name = "cmsAtom_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "cmsAtom_id" ) )
	@Cache
	private List<CmsAtom> enabledCmsAtomList = new ArrayList<CmsAtom>();

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision holdingPage;

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision issueReportedPage;

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision sessionTimeoutPage;

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision pageNotFoundPage;

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision unsubscribePage;

	@ManyToOne(fetch=FetchType.LAZY)
	private TopLevelTemplate mainTemplate;

	@ManyToOne(fetch=FetchType.LAZY)
	private TopLevelTemplate holdingTemplate;

	private String defaultPageTitle;

	@Column(columnDefinition="LONGTEXT")
	private String defaultPageDescription;

	@Column(columnDefinition="LONGTEXT")
	private String defaultPageKeywords;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode rootMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode mainMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode hiddenMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode sideMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode unusedMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private MenuNode errorMenu;

	@OneToOne(fetch=FetchType.LAZY)
	private CssResource editorCss;
	

	public CmsWebsite() {
		
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		if( fullInitialisation ) {
//			HibernateUtil.initialise(getMainMenu(), false);
//			HibernateUtil.initialise(getHiddenMenu(), false);
//			HibernateUtil.initialise(getSideMenu(), false);
//			//You need to initialise this one so that adding new cms pages will work.
//			HibernateUtil.initialise(getUnusedMenu(), true);
//			HibernateUtil.initialise(getErrorMenu(), false);
//			HibernateUtil.initialise(getPageNotFoundPage(), fullInitialisation);
//			HibernateUtil.initialise(getHoldingPage(), fullInitialisation);
//			HibernateUtil.initialise(getIssueReportedPage(), fullInitialisation);
//			HibernateUtil.initialise(getSessionTimeoutPage(), fullInitialisation);
//			HibernateUtil.initialise(getMainTemplate(), fullInitialisation);
//			HibernateUtil.initialise(getHoldingTemplate(), fullInitialisation);
//		}
//	}
	
	public void generateSiteMap( File file ) {
		List<CmsPage> cmsPages = new BeanDao( CmsPage.class ).getAll();
		StringBuffer sitemapStrBuf = new StringBuffer();
		sitemapStrBuf.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
		sitemapStrBuf.append( "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" );
		CmsPageRevision tempCmsPageRevision;
		removeUnusedPages( cmsPages, getUnusedMenu().getChildren() );
		removeUnusedPages( cmsPages, getErrorMenu().getChildren() );
		for( int i = 0, n = cmsPages.size(); i < n; i++ ) {
			tempCmsPageRevision = cmsPages.get( i ).getLatestRevision();
			if( tempCmsPageRevision != null 
					&& cmsPages.get( i ).getClass().equals( CmsPage.class ) ) {
				sitemapStrBuf.append( "<url>" );
				sitemapStrBuf.append( "<loc>" );
				CmsPageUrl cmsPageUrl = new CmsPageUrl( cmsPages.get( i ) );
				cmsPageUrl.setHost( this );
				sitemapStrBuf.append( cmsPageUrl.toString() );
				sitemapStrBuf.append( "</loc>" );
				sitemapStrBuf.append( "<lastmod>" );
				sitemapStrBuf.append( FormatUtil.formatDateForDB( tempCmsPageRevision.getDateLastModified() ) );
				sitemapStrBuf.append( "</lastmod>" );
				sitemapStrBuf.append( "<priority>" );
				sitemapStrBuf.append( cmsPages.get( i ).getSiteMapPriority() );
				sitemapStrBuf.append( "</priority>" );
				sitemapStrBuf.append( "</url>" );
				
				for( CmsAtom tempCmsAtom : tempCmsPageRevision.getCmsAtomList() ) {
					if( tempCmsAtom instanceof BasicCmsContentAtom ) {
						addBasicCmsContentUrls(sitemapStrBuf, (BasicCmsContentFeDmb) ((BasicCmsContentAtom)tempCmsAtom).getFrontendModuleBacking(), tempCmsPageRevision);
					}
				}
			}
		}
		
		List<BlogEntry> blogEntryList = new BeanDao( BlogEntry.class ).getAll();
		for( int i = 0, n = blogEntryList.size(); i < n; i++ ) {
			sitemapStrBuf.append( "<url>" );
			sitemapStrBuf.append( "<loc>" );
			sitemapStrBuf.append( StringEscapeUtils.escapeXml( BlogFeDmb.getBlogEntryUrl(blogEntryList.get( i ), CmsConfiguration.getCmsConfiguration().getBlogCpr()) ) );
			sitemapStrBuf.append( "</loc>" );
			sitemapStrBuf.append( "<lastmod>" );
			sitemapStrBuf.append( FormatUtil.formatDateForDB( blogEntryList.get( i ).getDateLastModified() ) );
			sitemapStrBuf.append( "</lastmod>" );
			sitemapStrBuf.append( "</url>" );
		}
		
		sitemapStrBuf.append( "</urlset>" );

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write( sitemapStrBuf.toString() );
			writer.flush();
			writer.close();
		} catch( IOException ioex ) {
			ApplicationUtil.handleError(ioex);
		}
	}
	
	private void addBasicCmsContentUrls( StringBuffer sitemapStrBuf, BasicCmsContentFeDmb basicCmsContentFeDmb, CmsPageRevision cmsPageRevision ) {
		basicCmsContentFeDmb.createBasicCmsContents();
		List<BasicCmsContent> basicCmsContentList = basicCmsContentFeDmb.getBasicCmsContents();
		for( int i = 0, n = basicCmsContentList.size(); i < n; i++ ) {
			sitemapStrBuf.append( "<url>" );
			sitemapStrBuf.append( "<loc>" );
			sitemapStrBuf.append( StringEscapeUtils.escapeXml( basicCmsContentFeDmb.getBasicCmsContentLink(basicCmsContentList.get( i ), cmsPageRevision) ) );
			sitemapStrBuf.append( "</loc>" );
			sitemapStrBuf.append( "<lastmod>" );
			sitemapStrBuf.append( FormatUtil.formatDateForDB( basicCmsContentList.get( i ).getDateLastModified() ) );
			sitemapStrBuf.append( "</lastmod>" );
			sitemapStrBuf.append( "</url>" );
		}
	}
	
	private void removeUnusedPages( List<CmsPage> cmsPages, List<MenuNode> menuNodes ) {
		for( int i = 0, n = menuNodes.size(); i < n; i++ ) {
			cmsPages.remove( menuNodes.get( i ).getCmsPage() );
			if( menuNodes.get( i ).getChildren().size() > 0 ) {
				removeUnusedPages( cmsPages, menuNodes.get( i ).getChildren() );
			}
		}
	}

	public String getEditorCssUrl() {
		if( getEditorCss() != null ) {
			StringBuffer strBuf = new StringBuffer( "['" ).append( JSFUtil.getContextPath() );
			strBuf.append( "/cssStylesheets/" ).append( getEditorCss().getId() );
			strBuf.append( ".css']" );
			return strBuf.toString();
		} else {
			return null;
		}
	}

	public boolean isLive() {
		return live;
	}

	public void addAvailableCmsAtom( DeveloperCmsAtom cmsAtom ) {
		getAvailableCmsAtomList().add( cmsAtom );
	}

	public void addAllAvailableCmsAtoms( List<? extends DeveloperCmsAtom> cmsAtomList ) {
		getAvailableCmsAtomList().addAll( cmsAtomList );
	}

	@Override
	public void createDefaultWebsiteObjects( AplosContextListener aplosContextListener ) {
		super.createDefaultWebsiteObjects(aplosContextListener);
		SystemUser adminUser = CommonConfiguration.getCommonConfiguration().getDefaultAdminUser();
		
		CssResource cssResource = new CssResource();
		cssResource.setParentWebsite(this);
		cssResource.setName( "Main" );
		cssResource.saveDetails( this, adminUser );
		setEditorCss(cssResource);
		saveDetails();
		
		CmsDatabaseLoader.createMenus( this, adminUser );
		TopLevelTemplate mainTemplate = getOrCreateMainTemplate(adminUser);
		TopLevelTemplate holdingTemplate = CmsDatabaseLoader.createHoldingTemplate( this, adminUser );
		CmsDatabaseLoader.createDefaultUserCmsModules( this, adminUser );
		CmsDatabaseLoader.createDefaultPages(this, mainTemplate, holdingTemplate, adminUser );
	}

	public UnconfigurableDeveloperCmsAtom getAvailableUnconfigurableCmsAtom(UnconfigurableAtomEnum unconfigurableAtomEnum) {
		for (DeveloperCmsAtom atom : getAvailableCmsAtomList()) {
			if (atom instanceof UnconfigurableDeveloperCmsAtom &&
				atom.getCmsAtomIdStr().equals(unconfigurableAtomEnum.getCmsAtomIdStr())) {
				return (UnconfigurableDeveloperCmsAtom) atom;
			}
		}
		return null;
	}

	protected TopLevelTemplate getOrCreateMainTemplate(SystemUser adminUser) {
		if (getMainTemplate() == null) {
			BeanDao dao = new BeanDao(TopLevelTemplate.class);
			dao.setWhereCriteria("bean.name='Main' and bean.parentWebsite.id = " + getId() );
			dao.setMaxResults(1);
			this.setMainTemplate((TopLevelTemplate)dao.setIsReturningActiveBeans(true).getFirstBeanResult());
		}
		if (getMainTemplate() == null) {
			this.setMainTemplate(CmsDatabaseLoader.createMainTemplate( this, this.getMainMenu(), adminUser ));
		}
		return getMainTemplate();
	}

	public String appendMetaKeywords( String extraKeywords ) {
		StringBuffer fullKeywordsBuffer = new StringBuffer();
		if( extraKeywords != null ) {
			fullKeywordsBuffer.append( extraKeywords );
			if( !extraKeywords.trim().endsWith( "," ) && !getDefaultPageKeywords().trim().startsWith( "," ) ) {
				fullKeywordsBuffer.append( "," );
			}
		}
		fullKeywordsBuffer.append( getDefaultPageKeywords() );
		return fullKeywordsBuffer.toString();
	}

	@Override
	public String getSessionTimeoutUrl( String requestUrl ) {
		if( requestUrl.endsWith( "/" ) || requestUrl.contains( ".aplos" ) ) {
			if( getSessionTimeoutPage() != null ) {
				return getSessionTimeoutPage().getCmsPage().getFullMapping( true );
			}
		}
		return null;
	}

	@Override
	public String getPageNotFoundUrl( String requestUrl ) {
		if( requestUrl.endsWith( "/" ) || requestUrl.contains( ".aplos" ) ) {
			if( getSessionTimeoutPage() != null ) {
				return getPageNotFoundPage().getCmsPage().getFullMapping( true );
			}
		}
		return null;
	}

	public static CmsWebsite getCmsWebsite() {
		return (CmsWebsite)new BeanDao( CmsWebsite.class ).get( 1 );
	}

	public void setLive( boolean live ) {
		this.live = live;
	}

	public CmsPageRevision getHoldingPage() {
		return holdingPage;
	}

	public void setHoldingPage( CmsPageRevision holdingPage ) {
		this.holdingPage = holdingPage;
	}

	public void setPageNotFoundPage(CmsPageRevision pageNotFoundPage) {
		this.pageNotFoundPage = pageNotFoundPage;
	}

	public CmsPageRevision getPageNotFoundPage() {
		return pageNotFoundPage;
	}

	public void setDefaultPageDescription(String defaultPageDescription) {
		this.defaultPageDescription = defaultPageDescription;
	}

	public String getDefaultPageDescription() {
		return defaultPageDescription;
	}

	public void setDefaultPageKeywords(String defaultPageKeywords) {
		this.defaultPageKeywords = defaultPageKeywords;
	}

	public String getDefaultPageKeywords() {
		return defaultPageKeywords;
	}

	public void setMainMenu(MenuNode mainMenu) {
		this.mainMenu = mainMenu;
	}

	public MenuNode getMainMenu() {
		return mainMenu;
	}

	public void setHiddenMenu(MenuNode hiddenMenu) {
		this.hiddenMenu = hiddenMenu;
	}

	public MenuNode getHiddenMenu() {
		return hiddenMenu;
	}

	public void setSideMenu(MenuNode sideMenu) {
		this.sideMenu = sideMenu;
	}

	public MenuNode getSideMenu() {
		return sideMenu;
	}

	public void setUnusedMenu(MenuNode unusedMenu) {
		this.unusedMenu = unusedMenu;
	}

	public MenuNode getUnusedMenu() {
		return unusedMenu;
	}

	public void setErrorMenu(MenuNode errorMenu) {
		this.errorMenu = errorMenu;
	}

	public MenuNode getErrorMenu() {
		return errorMenu;
	}

	public void setAvailableCmsAtomList(List<DeveloperCmsAtom> availableCmsAtomList) {
		this.availableCmsAtomList = availableCmsAtomList;
	}

	public List<DeveloperCmsAtom> getAvailableCmsAtomList() {
		return availableCmsAtomList;
	}

	public void setCmsPageLimit(Integer cmsPageLimit) {
		this.cmsPageLimit = cmsPageLimit;
	}

	public Integer getCmsPageLimit() {
		return cmsPageLimit;
	}

	public void setIssueReportedPage(CmsPageRevision issueReportedPage) {
		this.issueReportedPage = issueReportedPage;
	}

	public CmsPageRevision getIssueReportedPage() {
		return issueReportedPage;
	}

	public void setSessionTimeoutPage(CmsPageRevision sessionTimeoutPage) {
		this.sessionTimeoutPage = sessionTimeoutPage;
	}

	public CmsPageRevision getSessionTimeoutPage() {
		return sessionTimeoutPage;
	}

	public void setMainTemplate(TopLevelTemplate mainTemplate) {
		this.mainTemplate = mainTemplate;
	}

	public TopLevelTemplate getMainTemplate() {
		return mainTemplate;
	}

	public void setHoldingTemplate(TopLevelTemplate holdingTemplate) {
		this.holdingTemplate = holdingTemplate;
	}

	public TopLevelTemplate getHoldingTemplate() {
		return holdingTemplate;
	}

	public String getDefaultPageTitle() {
		return defaultPageTitle;
	}

	public void setDefaultPageTitle(String defaultPageTitle) {
		this.defaultPageTitle = defaultPageTitle;
	}

	public List<CmsAtom> getEnabledCmsAtomList() {
		return enabledCmsAtomList;
	}

	public void setEnabledCmsAtomList(List<CmsAtom> enabledCmsAtomList) {
		this.enabledCmsAtomList = enabledCmsAtomList;
	}

	public CmsPageRevision getUnsubscribePage() {
		return unsubscribePage;
	}

	public void setUnsubscribePage(CmsPageRevision unsubscribePage) {
		this.unsubscribePage = unsubscribePage;
	}

	public CssResource getEditorCss() {
		return editorCss;
	}

	public void setEditorCss(CssResource editorCss) {
		this.editorCss = editorCss;
	}

	public MenuNode getRootMenu() {
		return rootMenu;
	}

	public void setRootMenu(MenuNode rootMenu) {
		this.rootMenu = rootMenu;
	}
}
