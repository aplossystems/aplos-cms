package com.aplos.cms.backingpage;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.appconstants.CmsAppConstants;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageAlias;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.enums.CmsRedirectFolder;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.AplosRequestContext;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.TrailDisplayName;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.annotations.SslProtocol;
import com.aplos.common.annotations.WindowId;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.backingpage.LoginPage;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.servlets.EditorUploadServlet;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@GlobalAccess
@SslProtocol(sslProtocolEnum=SslProtocolEnum.DONT_CHANGE)
@WindowId(required=false)
public class ContentPage extends BackingPage {
	private static final long serialVersionUID = 5114394727413536071L;
	private static Logger logger = Logger.getLogger( EditorUploadServlet.class );
	final private String contextRoot;
	private CmsPageRevision cmsPageRevision;
	private boolean editMode = false;

	public ContentPage() {
		contextRoot = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	}

	@Override
	public boolean responsePageLoad() {
		return genericPageLoad( false );
	}

	@Override
	public boolean requestPageLoad() {
		return genericPageLoad( true );
	}

	public String getSessionId() {
		return JSFUtil.getSessionTemp().getId();
	}

	@Override
	public TrailDisplayName determineTrailDisplayName() {
		if (cmsPageRevision != null && cmsPageRevision.getCmsPage() != null) {
			TrailDisplayName trailDisplayName = new TrailDisplayName(cmsPageRevision);
			return trailDisplayName;
		} else {
			return super.determineTrailDisplayName();
		}
	}
	
	@Override
	public void registerScan() {
		super.registerScan();
		if (cmsPageRevision != null) {
			if (cmsPageRevision.getCmsAtomList() != null ) {
				for (CmsAtom atom : cmsPageRevision.getCmsAtomList()) {
					if (atom instanceof DeveloperCmsAtom) {
						((DeveloperCmsAtom) atom).getFeDmb().registerScan( super.getScannedStr() );
					}
				}
			}
		}
	}

	public boolean genericPageLoad( boolean isRequestPageLoad ) {
		if ( getCmsPageRevision() != null && getCmsPageRevision().getBackingPageBinding() != null && !getCmsPageRevision().getBackingPageBinding().equals( "" ) ) {
			FacesContext context = FacesContext.getCurrentInstance();
			Object managedBean = context.getApplication().evaluateExpressionGet( context, "#{" + getCmsPageRevision().getBackingPageBinding() + "}", BackingPage.class );
			if ( managedBean != null ) {
				BackingPage backingPage = (BackingPage) managedBean;
				if( isRequestPageLoad ) {
					if( !backingPage.requestPageLoad() ) {
						return false;
					}
				} else {
					if( !backingPage.responsePageLoad() ) {
						return false;
					}
				}
				JSFUtil.getRequest().setAttribute( AplosScopedBindings.BACKING_PAGE, backingPage );
			} else {
				logger.error( "Content Backing Page not found" );
			}
		}
		if( !cmsPageRevision.initModules( true, isRequestPageLoad, true ) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public SslProtocolEnum getSslProtocolEnum() {
		if( cmsPageRevision != null ) {
			return cmsPageRevision.getCmsPage().getSslProtocolEnum();
		} else {
			return super.getSslProtocolEnum();
		}
	}

	@SuppressWarnings("unchecked")
	public CmsPageRevision determinePage( String mappingPath, String mapping, boolean isDraft ) {
		CmsPageRevision page = null;
		CmsWebsite site = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
		if (site != null) {
			SystemUser currentUser = JSFUtil.getLoggedInUser();
			if( !site.isLive() && !isPreview() && !editMode && !(currentUser != null && currentUser.isLoggedIn()) ) {
				page = site.getHoldingPage();
			} else {
				AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext();
				if( aplosRequestContext.getRequestParameterMap().containsKey( CmsAppConstants.BLOG_ENTRY_ID ) ) {
					if( CmsConfiguration.getCmsConfiguration().getBlogCpr() != null ) {
						return CmsConfiguration.getCmsConfiguration().getBlogCpr();
					}
				} else if( aplosRequestContext.getRequestParameterMap().containsKey( CmsAppConstants.CASE_STUDY_ID ) ) {
					if( CmsConfiguration.getCmsConfiguration().getCaseStudyCpr() != null ) {
						return CmsConfiguration.getCmsConfiguration().getCaseStudyCpr();
					}
				} else {
					for( CmsRedirectFolder tempCmsRedirectFolder : CmsRedirectFolder.values() ) {
						if( aplosRequestContext.getRequestParameterMap().containsKey( tempCmsRedirectFolder.getContentIdKey() ) &&
								aplosRequestContext.getRequestParameterMap().containsKey( CmsAppConstants.CMS_PAGE_ID ) ) {
							try {
								Long cmsPageId = Long.parseLong( (String) aplosRequestContext.getRequestParameterMap().get( CmsAppConstants.CMS_PAGE_ID ) );
								CmsPage cmsPage = new BeanDao( CmsPage.class ).get( cmsPageId ); 
								return cmsPage.getLatestRevision();
							} catch( NumberFormatException nfex ) {
								ApplicationUtil.handleError( nfex );
							}
						}
					}
				}
				if( mapping.length() > 1 ) {
					if( (mapping.endsWith("/") || mapping.endsWith( "\\" )) ) {
						mapping = mapping.substring(0, mapping.length()-1 );
					}
			    }

				BeanDao cmsPageRevisionDao = new SiteBeanDao(CmsPageRevision.class);
				cmsPageRevisionDao.setIsReturningActiveBeans(null);
				cmsPageRevisionDao.setSelectCriteria( "bean.id, bean.cmsPage.id, bean.cmsPage.mapping, bean.cmsPage.cachedMappingPath" );

				String mappingCriteria;
				if( mapping.length() == 1) {
					mappingCriteria = "(bean.cmsPage.mapping = :mapping ) ";
				} else {
					mappingCriteria = "(bean.cmsPage.mapping= :mapping or bean.cmsPage.mapping = :mappingWithoutFirstSlash) ";
				}
				
				cmsPageRevisionDao.addWhereCriteria( mappingCriteria );
				cmsPageRevisionDao.setReadOnly( true );
				
				cmsPageRevisionDao.setNamedParameter( "mapping", mapping );
				if( mapping.length() != 1) {
					cmsPageRevisionDao.setNamedParameter( "mappingWithoutFirstSlash", mapping.replaceFirst( "^/", "" ) );
				}
				List<CmsPageRevision> pages = cmsPageRevisionDao.getAll();
				if( pages.size() == 0 ) {
					BeanDao cmsPageAliasDao = new SiteBeanDao( CmsPageAlias.class );
					String cmsPageAliasCriteria;
					if( mapping.length() == 1) {
						cmsPageAliasCriteria = "(bean.mapping=:mapping) ";
					} else {
						cmsPageAliasCriteria = "(bean.mapping=:mapping or bean.mapping =:mappingWithoutFirstSlash) ";
					}
					cmsPageAliasDao.addWhereCriteria( cmsPageAliasCriteria );
					cmsPageAliasDao.setNamedParameter( "mapping", mapping );
					if( mapping.length() != 1) {
						cmsPageAliasDao.setNamedParameter( "mappingWithoutFirstSlash", mapping.replaceFirst( "^/", "" ) );
					}
					List<CmsPageAlias> cmsPageAliases = cmsPageAliasDao.getAll();
					if( cmsPageAliases.size() == 1 ) {
						page = cmsPageAliases.get( 0 ).getCmsPage().getLatestRevision();
					}
				} else if (pages.size() > 1) {
					page = getPageRevisionBasedOnParentUrl( pages, mappingPath, mapping );
				} else if (pages.size() == 1) {
					page = pages.get(0);
					/*
					 * Some hacking programs will look for directories which before would bring back the homepage.
					 * This checks to see if the page returned is the homepage and then makes sure there's no
					 * mapping path which is how a correct home page URL should be.
					 */
					if( page.getCmsPage().getCachedMappingPath().equals( "/" ) && !CommonUtil.isNullOrEmpty( mappingPath ) && !"/".equals(mappingPath) ) {
						page = site.getPageNotFoundPage();
						JSFUtil.getResponse().setStatus(404);
					}

				}

				if ( page == null && isDraft ) {  // if there's no draft get the current cmsPageRevision
					cmsPageRevisionDao.clearWhereCriteria();
					cmsPageRevisionDao.addWhereCriteria( mappingCriteria + " and current = true" );
					pages = cmsPageRevisionDao.getAll();
					page = getPageRevisionBasedOnParentUrl( pages, mappingPath, mapping );
				}

				//this outer if stops us getting stuck in an infinite loop
				if (page != null && !page.isActive() && page.getDeletedRedirect() != null) {
					while (!page.isActive()) {
						if (page.getDeletedRedirect() != null) {
							page = page.getDeletedRedirect();
						} else {
							break;
						}
					}
					JSFUtil.redirect( new CmsPageUrl(page) );
					return null;
				}
				
				if ( page == null ) {
					page = site.getPageNotFoundPage();
					JSFUtil.getResponse().setStatus(404);
					if( aplosRequestContext != null && aplosRequestContext.getPageRequest() != null ) {
						aplosRequestContext.getPageRequest().setStatus404(true);
					}
				}
					
//				if (page != null) {
					//otherwise initialising atoms like news dies
//					page.hibernateInitialise(true);
//				}
			}
		} else {
			//no site (new project)
			JSFUtil.redirect("common", LoginPage.class);
		}
		return page;
	}

	private CmsPageRevision getPageRevisionBasedOnParentUrl( List<CmsPageRevision> pages, String mappingPath, String mapping ) {
		String requestMapping = JSFUtil.getContextPath() + (mappingPath + mapping).toLowerCase();
		for (int i=0 ; i<pages.size() ; i++ ) {
			String pageMapping = pages.get(i).getCmsPage().getFullMapping( false ).toLowerCase();
			if (!pageMapping.startsWith("/")) {
				pageMapping = "/" + pageMapping;
			}
			if ( pageMapping.equals( requestMapping ) ) {
				BeanDao cmsPageRevisionDao = new BeanDao( CmsPageRevision.class );
				cmsPageRevisionDao.setReadOnly( true );
				return (CmsPageRevision) cmsPageRevisionDao.get( pages.get(i).getId() );
			}
		}
		return null;
	}

	public boolean isPreview() {
		return Boolean.parseBoolean( JSFUtil.getRequest().getParameter( "preview" ) )
					&& getCmsPageRevision().getCmsPage().isAuthor( JSFUtil.getLoggedInUser() );
	}

	public String wrapElementString(String name, String content) {
		return content;
	}

	public String getContextRoot() {
		return contextRoot;
	}

	public void setEditMode( boolean editMode ) {
		this.editMode = editMode;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setCmsPageRevision(CmsPageRevision cmsPageRevision) {
		this.cmsPageRevision = cmsPageRevision;
	}

	public CmsPageRevision getCmsPageRevision() {
		return cmsPageRevision;
	}

}
