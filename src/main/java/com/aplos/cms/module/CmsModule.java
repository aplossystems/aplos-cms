package com.aplos.cms.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.aplos.cms.CmsMenuCreator;
import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.annotations.DmbOverride;
import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.backingpage.pages.CmsPageRevisionListPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.ArticleCmsAtom;
import com.aplos.cms.beans.developercmsmodules.BlogModule;
import com.aplos.cms.beans.developercmsmodules.CaseStudyCmsAtom;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.CompetitionModule;
import com.aplos.cms.beans.developercmsmodules.ContactPageCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ContentBoxModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ExhibitionCmsAtom;
import com.aplos.cms.beans.developercmsmodules.FlashModule;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GoogleMapModule;
import com.aplos.cms.beans.developercmsmodules.HostedVideoCmsAtom;
import com.aplos.cms.beans.developercmsmodules.JobOfferCmsAtom;
import com.aplos.cms.beans.developercmsmodules.PdfModule;
import com.aplos.cms.beans.developercmsmodules.TestimonialsModule;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.JavascriptResource;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.cms.utils.CmsUserLevelUtil;
import com.aplos.cms.utils.CmsUtil;
import com.aplos.common.AplosRequestContext;
import com.aplos.common.AplosUrl;
import com.aplos.common.BackingPageUrl;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.EmailTemplate;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.AplosModule;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.module.DatabaseLoader;
import com.aplos.common.module.ModuleConfiguration;
import com.aplos.common.module.ModuleDbConfig;
import com.aplos.common.module.ModuleUpgrader;
import com.aplos.common.persistence.PersistentClass;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.UserLevelUtil;

public class CmsModule extends AplosModuleImpl {
	private List<EmailTemplate> availableEmailTemplateList;
	private static HashMap<String, String> modulebindingOverrideMap = new HashMap<String, String>();
	private List<MailRecipientFinder> availableMailRecipientFinderList = new ArrayList<MailRecipientFinder>();
	private List<Class<? extends GeneratorMenuItem>> availableGeneratorItemClassList = new ArrayList<Class<? extends GeneratorMenuItem>>();
	private static CmsMenuCreator cmsMenuCreator = new CmsMenuCreator();
	private Logger logger = Logger.getLogger(getClass());
	
	
	public CmsModule() {
	}
	
	@Override
	public DatabaseLoader createDatabaseLoader() {
		return new CmsDatabaseLoader(this);
	}
	
	@Override
	public Boolean determineIsFrontEnd(String requestUrl) {
		if( JSFUtil.getCurrentBackingPage() instanceof ContentPage ) {
			return true;
		}
		return null;
	}
	
	@Override
	public List<String> getRestrictedMediaPaths() {
		List<String> restrictedPaths = new ArrayList<String>();
		for (CmsWorkingDirectory awd : CmsWorkingDirectory.values()) {
			if (awd.isRestricted()) {
				restrictedPaths.add(awd.getDirectoryPath(false));
			}
		}
		if (restrictedPaths.size() > 0) {
			return restrictedPaths;
		} else {
			return null;
		}
	}
	
	@Override
	public Boolean rewriteUrl( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		String path = request.getRequestURL().toString();

		Pattern pattern = Pattern.compile( "/cms(/)?$" );
		Matcher matcher = pattern.matcher( path );
		if( matcher.find() ) {
			SystemUser currentUser = JSFUtil.getLoggedInUser(request.getSession());
			if( currentUser != null && currentUser.isLoggedIn() ) {
				BackingPageUrl backingPageUrl = new BackingPageUrl( CmsPageRevisionListPage.class );
				backingPageUrl.addContextPath( false );
				backingPageUrl.addQueryParameter( AplosScopedBindings.ADD_WEBSITE_FOR_ACCESS, "true" );
				JSFUtil.getAplosRequestContext(request).setRedirectionUrl( backingPageUrl.toString() );
			} else {
				AplosUrl aplosUrl = new AplosUrl(ApplicationUtil.getAplosContextListener().getLoginPageUrl(path,request,true,false));
				JSFUtil.getAplosRequestContext(request).setRedirectionUrl( aplosUrl.toString() );
			}
			return true;
		}
		
		if( path.endsWith( "/sitemap.xml" ) ) {
			AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext(request); 
			aplosRequestContext.setDynamicViewEl( "'sitemap.jsf'" );
	    	return true;
		}

		/*
		 *  Need to be careful here with the .aplos$, don't remove the $ otherwise it will 
		 *  catch URLs for www.aplossystems.co.uk, tom2.aplossystems.co.uk etc.  
		 */
		pattern = Pattern.compile( "(?!.*(^/media/))(.*/$|.*\\.aplos$|.*/;jsessionid=.*)" );
		matcher = pattern.matcher( path );
		if( matcher.find() ) {
			JSFUtil.getAplosRequestContext(request).setDynamicViewEl( "pageDispatcher.viewOrEdit" );
			return true;
		}

		return null;
	}
	
	public static boolean dynamicRequests( HttpServletRequest request, List<String[]> redirects ) {
		Pattern pattern;
		Matcher matcher;
		String path = request.getServletPath();
		for( int i = 0, n = redirects.size(); i < n; i++ ) {
			pattern = Pattern.compile( redirects.get( i )[ 0 ], Pattern.CASE_INSENSITIVE );
			matcher = pattern.matcher( path );
			if( matcher.find() ) {
				AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext(request); 
				aplosRequestContext.setOriginalUrl( redirects.get( i )[ 1 ] );
				aplosRequestContext.setDynamicViewEl( "pageDispatcher.viewOrEdit" );
				return true;
			}
		}
		return false;
	}
	
	public CmsModuleDbConfig getCmsModuleDbConfig() {
		return (CmsModuleDbConfig) getModuleDbConfig();
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return CmsConfiguration.getCmsConfiguration();
	}

	public static CmsUserLevelUtil getUserLevelUtil() {
		return (CmsUserLevelUtil) CommonConfiguration.retrieveUserLevelUtil();
	}
	
	@Override
	public AplosWorkingDirectoryInter[] createWorkingDirectoryEnums() {
		CmsWorkingDirectory.createDirectories();
		return CmsWorkingDirectory.values();
	}

	@Override
	public UserLevelUtil createUserLevelUtil() {
		return new CmsUserLevelUtil();
	}

	@Override
	public String fireNewWebsiteAction() {
		AplosAbstractBean newBean = new BeanDao( CmsWebsite.class ).getNew();
		newBean.addToScope();

		return "websiteEdit";
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail, BulkEmailSource bulkEmailSource ) {
		if( "UNSUBSCRIBE_LINK".equals( variableKey ) && bulkEmailSource instanceof BulkSubscriberSource ) {
			CmsWebsite cmsWebsite = (CmsWebsite) aplosEmail.getParentWebsite();
			CmsPageUrl cmsPageUrl = new CmsPageUrl( cmsWebsite.getUnsubscribePage().getCmsPage(), cmsWebsite );
			cmsPageUrl.addQueryParameter( AplosScopedBindings.SUBSCRIBER, ((BulkSubscriberSource) bulkEmailSource).getMessageSourceId() );
			PersistentClass persistentClass = ApplicationUtil.getPersistentClass( bulkEmailSource.getClass() );
			cmsPageUrl.addQueryParameter( AplosScopedBindings.SUBSCRIBER_CLASS, persistentClass.getDbPersistentClass().getTableClass().getSimpleName() );
			return cmsPageUrl.toString();
		}
		return null;
	}

	public void processDmbOverrides( Set<String> packages ) {
		for( String packageName : packages ) {
			try {
				if( !packageName.equals( "common" ) ) {
					List<Class<?>> uncastClassList = CommonUtil.getClasses("com/aplos/" + packageName + "/developermodulebacking/frontend", DeveloperModuleBacking.class, false);
					uncastClassList.addAll( CommonUtil.getClasses("com/aplos/" + packageName + "/developermodulebacking/backend", DeveloperModuleBacking.class, false) );
					for( int i = 0, n = uncastClassList.size(); i < n; i++ ) {
						DmbOverride dmbOverride = uncastClassList.get( i ).getAnnotation(DmbOverride.class);
						if( dmbOverride != null ) {
							addBindingOverride(dmbOverride.dmbClass(), (Class<? extends DeveloperModuleBacking>) uncastClassList.get( i ));
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void addBindingOverride(
			Class<? extends DeveloperModuleBacking> originalModuleBackingClass,
			Class<? extends DeveloperModuleBacking> overridingModuleBackingClass ) {
		String orignalBinding = CommonUtil.firstLetterToLowerCase( originalModuleBackingClass.getSimpleName() );
		String overrideBinding = CommonUtil.firstLetterToLowerCase( overridingModuleBackingClass.getSimpleName() );
		modulebindingOverrideMap.put( orignalBinding, overrideBinding );
	}

	public static DeveloperModuleBacking getDeveloperModuleBacking( Class<? extends DeveloperModuleBacking> developerModuleBackingClass, DeveloperCmsAtom developerCmsAtom ) {
		return getDeveloperModuleBacking( CommonUtil.getBinding( developerModuleBackingClass ), developerCmsAtom );
	}

	public static DeveloperModuleBacking getDeveloperModuleBacking( String binding, DeveloperCmsAtom developerCmsAtom ) {
		if( modulebindingOverrideMap.containsKey(binding) ) {
			binding = modulebindingOverrideMap.get(binding);
		}
		DeveloperModuleBacking developerModuleBacking = (DeveloperModuleBacking) JSFUtil.resolveVariable( binding );
		if( developerCmsAtom != null ) {
			if( developerModuleBacking != null ) {
				String atomSpecificBinding = developerModuleBacking.getClass().getSimpleName() + "_" + developerCmsAtom.getId();
				if( developerModuleBacking.getClass().getAnnotation( ViewScoped.class ) != null ) {
					developerModuleBacking = (DeveloperModuleBacking) JSFUtil.getFromView( atomSpecificBinding );
					if( developerModuleBacking == null ) {
						JSFUtil.addToView( binding, null );
						developerModuleBacking = (DeveloperModuleBacking) JSFUtil.resolveVariable( binding );
						JSFUtil.addToView( atomSpecificBinding, developerModuleBacking );
					}
				} else if( developerModuleBacking.getClass().getAnnotation( CustomScoped.class ) != null ) {
					developerModuleBacking = (DeveloperModuleBacking) JSFUtil.getFromTabSession( atomSpecificBinding );
					if( developerModuleBacking == null ) {
						JSFUtil.addToTabSession( binding, null );
						developerModuleBacking = (DeveloperModuleBacking) JSFUtil.resolveVariable( binding );
						JSFUtil.addToTabSession( atomSpecificBinding, developerModuleBacking );
					}
				}
			}
		}
		return developerModuleBacking;
	}

	@Override
	public void initModule() {
		List<Website> websiteList = ApplicationUtil.getAplosContextListener().getWebsiteList();
		Set<String> packages = new HashSet();
		for( Website tempWebsite : websiteList ) {
			if( tempWebsite instanceof CmsWebsite ) {
				CmsWebsite cmsWebsite = ((CmsWebsite) tempWebsite);
				if( getCmsModuleDbConfig().isCompetitionUsed() ) {
					cmsWebsite.addAvailableCmsAtom( new CompetitionModule() );
				}
				cmsWebsite.addAvailableCmsAtom( new BlogModule() );
				cmsWebsite.addAvailableCmsAtom( new GalleryModule() );
				cmsWebsite.addAvailableCmsAtom( new ContentBoxModule() );
				cmsWebsite.addAvailableCmsAtom( new GoogleMapModule() );
				cmsWebsite.addAvailableCmsAtom( new PdfModule() );
				cmsWebsite.addAvailableCmsAtom( new FlashModule() );
				cmsWebsite.addAvailableCmsAtom( new JobOfferCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new ExhibitionCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new GeneratorMenuCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new HostedVideoCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new ContactPageCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new ArticleCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new CaseStudyCmsAtom() );
				cmsWebsite.addAvailableCmsAtom( new TestimonialsModule() );
				cmsWebsite.addAllAvailableCmsAtoms( CmsConfiguration.getCmsConfiguration().getUnconfigurableDeveloperCmsAtomList() );
				packages.add( cmsWebsite.getPackageName() );
			}
		}
		
		List<AplosModule> aplosModuleList = ApplicationUtil.getAplosContextListener().getAplosModuleList();
		for( int i = 0, n = aplosModuleList.size(); i < n; i++ ) {
			packages.add( aplosModuleList.get( i ).getPackageName() );
		}
		processDmbOverrides(packages);
		super.initModule();
	}
	
	@Override
	public Boolean getFaceletEvent(String url) {
		if (url.contains("/cmsGenerationViews/" + CmsWorkingDirectory.CMS_USER_MODULE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
        	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + url.replaceFirst("/", "");
        	UserCmsModule userCmsModule = (UserCmsModule) new BeanDao(UserCmsModule.class).get(Long.parseLong(url.replace(".xhtml", "").substring(url.lastIndexOf("/")+1)));
			
			//the usermodule is already loaded above anyway so init its atoms like this
			//no need to make an sql call to see what it has
			for (CmsAtom atom : userCmsModule.getCmsAtomList()) {
				if (atom instanceof DeveloperCmsAtom) {
					atom.addToScope();
					((DeveloperCmsAtom)atom).initFrontend(false);
				}
			}
			return true;
        }
		return null;
	}

	@Override
	public URL checkFileLocations(String resourceName, String resourcesPath) {
		URL url = null;

		if (resourceName.startsWith("/" + CmsWorkingDirectory.INNER_TEMPLATE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
        	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
			File viewFile = new File(viewPathAbsolute);
			if (!viewFile.exists() || JSFUtil.isLocalHost()) {
				try {
					InnerTemplate innerTemplate = (InnerTemplate) new BeanDao(InnerTemplate.class).get(Long.parseLong(resourceName.replace(".xhtml", "").substring(resourceName.lastIndexOf("/")+1)));
					if (innerTemplate != null) {
						innerTemplate.writeViewToFile();
					}
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
			try {
				url = new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
        } else if (resourceName.startsWith("/" + CmsWorkingDirectory.TOP_TEMPLATE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
			String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
			File viewFile = new File(viewPathAbsolute);
			if (!viewFile.exists() || JSFUtil.isLocalHost()) {
				try {
					TopLevelTemplate topLevelTemplate = (TopLevelTemplate) new BeanDao(TopLevelTemplate.class).get(Long.parseLong(resourceName.replace(".xhtml", "").substring(resourceName.lastIndexOf("/")+1)));
					if (topLevelTemplate != null) {
						topLevelTemplate.writeViewToFile();
					}
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
			try {
				url = new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
        } else if (resourceName.startsWith("/" + CmsWorkingDirectory.CMS_USER_MODULE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
        	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
        	UserCmsModule userCmsModule = (UserCmsModule) new BeanDao(UserCmsModule.class).get(Long.parseLong(resourceName.replace(".xhtml", "").substring(resourceName.lastIndexOf("/")+1)));
			File viewFile = new File(viewPathAbsolute);
			if (!viewFile.exists() || JSFUtil.isLocalHost()) {
				try {
					if (userCmsModule != null) {
						userCmsModule.writeViewToFile();
					}
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
			try {
				url = new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
        } else if (resourceName.startsWith("/" + CmsWorkingDirectory.CODE_SNIPPET_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
        	
        	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
        	CodeSnippet codeSnippet = (CodeSnippet) new BeanDao(CodeSnippet.class).get(Long.parseLong(resourceName.replace(".xhtml", "").substring(resourceName.lastIndexOf("/")+1)));
			File viewFile = new File(viewPathAbsolute);
			if (!viewFile.exists() || JSFUtil.isLocalHost()) {
				if (codeSnippet != null) {
					codeSnippet.writeViewToFile();
				} else {
					return null;
				}
			}
			try {
				url = new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}

        } else if (resourceName.startsWith("/" + CmsWorkingDirectory.CMS_PAGE_REVISION_VIEW_FILES.getDirectoryPath(false) )) {
        	//old: if (path.equals("/frontend/content.xhtml")) {
        	CmsPageRevision cmsPageRevision = JSFUtil.getBeanFromScope(CmsPageRevision.class);
        	if (cmsPageRevision == null) {
        		try {
        			cmsPageRevision = (CmsPageRevision) new BeanDao(CmsPageRevision.class).get(Long.parseLong(resourceName.replace(".xhtml", "").replace(".jsf", "").substring(resourceName.lastIndexOf("/")+1)));
        		} catch (NumberFormatException ex) { /* hmmm, nothing here */ }
        	}
        	if (cmsPageRevision != null) {
				String viewPathAbsolute = CmsWorkingDirectory.CMS_PAGE_REVISION_VIEW_FILES.getDirectoryPath(true) + cmsPageRevision.getId() + ".xhtml";
				File viewFile = new File(viewPathAbsolute);
				if (!viewFile.exists() || JSFUtil.isLocalHost()) {
					cmsPageRevision.writeViewToFile();
				}
				try {
	        		url = new URL("file:/" + viewPathAbsolute);
				} catch (MalformedURLException mue) {
					mue.printStackTrace();
				}
        	}
        }
		if (url == null) {
			url = super.checkFileLocations(resourceName, resourcesPath);
	        if( url == null ) {
	        	File file = new File( CommonWorkingDirectory.UPLOAD_DIR.getDirectoryPath(true) + resourceName );
	        	if( file.exists() ) {
	        		try {
	        			url = file.toURI().toURL();
	        		} catch( MalformedURLException mUrlEx ) {
	        			mUrlEx.printStackTrace();
	        		}
	        	}
	        }
		}
        return url;
	}

	@Override
	public URL getResourceFilterUrl(String resourceName, String contextPath) {
		if (resourceName.startsWith("/" + CmsWorkingDirectory.CSS_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
         	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
				File viewFile = new File(viewPathAbsolute);
				if (!viewFile.exists()) {
					try {
						//HibernateUtil.startNewTransaction();
						CssResource cssResource = (CssResource) new BeanDao(CssResource.class).get(Long.parseLong(resourceName.replace(".css", "").substring(resourceName.lastIndexOf("/")+1)));
						if (cssResource != null || JSFUtil.isLocalHost()) {
							cssResource.writeViewToFile(contextPath);
						}
					} catch (NumberFormatException nfe) {
						/* be quiet */
					}
				}
				try {
					return new URL("file:/" + viewPathAbsolute);
				} catch (MalformedURLException mue) {
					mue.printStackTrace();
				}
        } else if (resourceName.startsWith("/" + CmsWorkingDirectory.JS_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),""))) {
         	String viewPathAbsolute = CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(true) + resourceName.replaceFirst("/", "");
			File viewFile = new File(viewPathAbsolute);
			if (!viewFile.exists()) {
				try {
					JavascriptResource javascriptResource = (JavascriptResource) new BeanDao(JavascriptResource.class).get(Long.parseLong(resourceName.replace(".js", "").substring(resourceName.lastIndexOf("/")+1)));
					if (javascriptResource != null || JSFUtil.isLocalHost()) {
						javascriptResource.writeViewToFile(contextPath);
					}
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
			try {
				return new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
        } else if (resourceName.contains(CmsWorkingDirectory.RSS_FEEDS.getDirectoryPath(false))) {
        	String viewPathAbsolute = CommonUtil.appendServerWorkPath( resourceName.replaceFirst("/", "") );
			try {
				return new URL("file:/" + viewPathAbsolute);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
        }
		return null;
	}

	@Override
	public void contextInitialisedFinishing(AplosContextListener aplosContextListener) {
		super.contextInitialisedFinishing(aplosContextListener);
	}


	@Override
	public void addImplicitPolymorphismEntries(AplosContextListener aplosContextListener) {
	}

	@Override
	public ModuleDbConfig createModuleDbConfig() {
		return new CmsModuleDbConfig( this );
	}

	@Override
	public ModuleUpgrader createModuleUpgrader() {
		return new CmsModuleUpgrader(this);
	}

	public void setAvailableEmailTemplateList(
			List<EmailTemplate> availableEmailTemplateList) {
		this.availableEmailTemplateList = availableEmailTemplateList;
	}

	public List<EmailTemplate> getAvailableEmailTemplateList() {
		return availableEmailTemplateList;
	}

	public void setAvailableMailRecipientFinderList(List<MailRecipientFinder> availableMailRecipientFinderList) {
		this.availableMailRecipientFinderList = availableMailRecipientFinderList;
	}

	public List<MailRecipientFinder> getAvailableMailRecipientFinderList() {
		return availableMailRecipientFinderList;
	}

	@Override
	public Currency updateSessionCurrency(HttpServletRequest request) {
		return null;
	}

	//methods to output the view files for cmsPageRevisions, templates and other resources

	public static File writeViewToFile(String viewContent, AplosWorkingDirectoryInter viewFileDirectory, Long viewId, String extension ) {
		return writeViewToFile(viewContent, viewFileDirectory, String.valueOf(viewId), extension );
	}

	/**
	 * This method is used to generate all cms views (pages/tempaltes/resources/etc.)
	 * It is also used by {@link CmsUtil#generateRssFeed(BeanDao, String, String, String, String, String)}
	 * @param viewContent
	 * @param viewFileDirectory
	 * @param viewId
	 * @param extension
	 */
	public static File writeViewToFile(String viewContent, AplosWorkingDirectoryInter viewFileDirectory, String viewId, String extension) {
		File viewFile = new File(viewFileDirectory.getDirectoryPath(true) + viewId + "." + extension);
		CommonUtil.writeStringToFile( CmsModule.parseCmsContentTags(viewContent), viewFile, true, true );
		if( extension.equals( "xhtml" ) ) {
			try {
				ApplicationUtil.getAplosFaceletCache().expireUrlFromCache(viewFile.toURI().toURL());
			} catch (MalformedURLException e) {
				ApplicationUtil.handleError(e, false);
			} catch (IOException e) {
				ApplicationUtil.handleError(e, false);
			}
		}
		return viewFile;
	}

	public static String parseCmsContentTags(String viewAsString) {
		String userModuleDirectory = CmsWorkingDirectory.CMS_USER_MODULE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),"");
		String codeSnippetDirectory = CmsWorkingDirectory.CODE_SNIPPET_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),"");
		String cssDirectory = CmsWorkingDirectory.CSS_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),"");
		String jsDirectory = CmsWorkingDirectory.JS_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false),"");
		viewAsString = viewAsString.replaceAll("\\{_USER_MODULE_([0-9]*?)\\}", "<ui:include src=\"/" + userModuleDirectory + "$1.xhtml\" />\n");
		viewAsString = viewAsString.replaceAll("\\{_SNIPPET_([0-9]*?)\\}", "<ui:include src=\"/" + codeSnippetDirectory + "$1.xhtml\" />\n");
		viewAsString = viewAsString.replaceAll("\\{_PAGE_NAME\\}", "<h:outputText value=\"#\\{cmsPageRevision.cmsPage.name\\}\" />\n");

		viewAsString = viewAsString.replaceAll("\\{_MENU_([0-9]*?)\\}", "<aplos:frontendCmsMenu menuNodeId=\"$1\" />\n");
		viewAsString = viewAsString.replaceAll("\\{_MENU_WITHOUT_DIVS_([0-9]*?)\\}", "<aplos:frontendCmsMenu menuNodeId=\"$1\" insertDivs=\"false\" />\n");
		
		Pattern pattern = Pattern.compile("\\{_CSS_([0-9]*?)\\}");
		Matcher m = pattern.matcher(viewAsString);
		Website website = Website.getCurrentWebsiteFromTabSession();
		while( m.find() ) {
			CssResource cssResource = (CssResource) new BeanDao( CssResource.class ).get(Long.parseLong(m.group(1)));
			String cssHref = "#{request.contextPath}/" + cssDirectory + cssResource.getId() + ".css?version=" + cssResource.getVersion();
			if( website.isDeferringStyle() ) {
				viewAsString = viewAsString.replace( m.group(0), "<script type=\"text/javascript\">aplosDeferStyle.add('" + cssHref + "');</script> \n" );
			} else {
				viewAsString = viewAsString.replace( m.group(0), "<link type=\"text/css\" href=\"" + cssHref + "\" rel=\"stylesheet\" />\n" );
			}
		}
		
		pattern = Pattern.compile("\\{_JS_([0-9]*?)\\}");
		m = pattern.matcher(viewAsString);
		while( m.find() ) {
			JavascriptResource javascriptResource = (JavascriptResource) new BeanDao( JavascriptResource.class ).get(Long.parseLong(m.group(1)));
			viewAsString = viewAsString.replace( m.group(0), "<script type=\"text/javascript\" src=\"#{request.contextPath}/" + jsDirectory + javascriptResource.getId() + ".js?version=" + javascriptResource.getVersion() + "\"></script>\n" );
		}

		return viewAsString;
	}

	public List<Class<? extends GeneratorMenuItem>> getAvailableGeneratorItemClassList() {
		return availableGeneratorItemClassList;
	}

	public void setAvailableGeneratorItemClassList(
			List<Class<? extends GeneratorMenuItem>> availableGeneratorItemClassList) {
		this.availableGeneratorItemClassList = availableGeneratorItemClassList;
	}

	public static CmsMenuCreator getCmsMenuCreator() {
		return cmsMenuCreator;
	}

	public static void setCmsMenuCreator(CmsMenuCreator cmsMenuCreator) {
		CmsModule.cmsMenuCreator = cmsMenuCreator;
	}
}
