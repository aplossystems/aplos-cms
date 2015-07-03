package com.aplos.cms.module;

import java.util.HashMap;
import java.util.List;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsPageRevision.PageStatus;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.cms.utils.CmsUserLevelUtil;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.module.DatabaseLoader;
import com.aplos.common.utils.CommonUtil;

public class CmsDatabaseLoader extends DatabaseLoader {
	private static CmsDatabaseLoader cmsDefaultsLoader;

	public CmsDatabaseLoader(AplosModuleImpl aplosModule) {
		super(aplosModule);
		CmsDatabaseLoader.setCmsDefaultsLoader(this);
	}

	@Override
	public void newTableAdded(Class<?> tableClass) {
		// TODO Auto-generated method stub
		
	}


	public void insertInitialUserLevelsAndUsers() {
		if (CommonConfiguration.retrieveUserLevelUtil().getAdminUserLevel() == null) {
			UserLevel basicUserLevel = new UserLevel();
			basicUserLevel.setName( "Basic" );
			basicUserLevel.setClearanceLevel(700);
			basicUserLevel.saveDetails();
			((CmsUserLevelUtil) CommonConfiguration.retrieveUserLevelUtil()).setBasicUserLevel( basicUserLevel );
		}
	}

	@Override
	public void loadTables() {
		CmsWebsite website = (CmsWebsite) Website.getWebsite();
		if (website != null) {
			SystemUser systemUser = CommonConfiguration.getCommonConfiguration().getDefaultAdminUser();
			createMenus( website, systemUser );
			TopLevelTemplate defaultTemplate = createMainTemplate( website, website.getMainMenu(), systemUser );
			TopLevelTemplate holdingTemplate = createHoldingTemplate( website, systemUser );
			createDefaultUserCmsModules( website, systemUser );
			createDefaultPages(website, defaultTemplate, holdingTemplate, systemUser );
		}
	}

	public static void createDefaultUserCmsModules( CmsWebsite website, SystemUser systemUser ) {
		UserCmsModule userCmsModule = new UserCmsModule();
		userCmsModule.setContent( "<p>Example Module Content</p>" );
		userCmsModule.setName( "Example Module" );
		userCmsModule.saveDetails( website, systemUser );
	}

	public static TopLevelTemplate createMainTemplate( CmsWebsite cmsWebsite, MenuNode mainMenu, SystemUser systemUser ) {
		TopLevelTemplate template = new TopLevelTemplate();
		template.setName( "Main" );
		template.setHead( 	"<!-- The reserved name \"_title\" will be filled by the page title automatically -->\n" +
							"<title><ui:insert name='_title'>My Default Title</ui:insert></title>\n" +
							"<style>\n" +
							"body { font: normal 10pt Verdana; padding: 50px; }\n" +
							"/* The generated menu will make use of these styles */\n" +
							"ul.nav { list-style: none; }	\n" +
							"ul.nav li { display: inline; margin-left: 11px; }\n" +
							"\n" +
							"/* Generated forms will make use of these styles */\n" +
							"table.form td:first-child {\n" +
							"	width: 150px;\n" +
							"	font-weight: bold;\n" +
							"	text-align: right;\n" +
							"	padding-right: 10px;\n" +
							"}\n" +
							"</style>\n"
						);
		template.setBody( 	"<div style='background-color: darkslategray; text-align: right; font: bold 24pt Garamond; padding: 50px; color: white;'>My Website</div>\n" +
							"{_MENU_" + mainMenu.getId() + "}\n" +
							"<hr />\n" +
							"<div style='width: 800px; margin: 0 auto;' >\n" +

							"<div style='padding: 20px; float: left; width: 500px; border-right: 2px solid #C9D9F9; '>\n" +
							"<ui:insert name=\"Main Content\" />\n" +
							"</div>\n" +

							"<div style='padding: 20px; float: left; width: 200px;border-right: 2px solid #C9D9F9;'>\n" +
							"<ui:insert name=\"Sidebar\" />\n" +
							"</div>\n" +

							"<br style='clear: both;' />\n" +
							"</div>\n" +

							"<hr />\n" +
							"<ui:insert name=\"Footer\" />\n"
						);
		template.setDeletable( false );
		template.saveDetails(cmsWebsite, systemUser);
		cmsWebsite.setHoldingTemplate( template );
		return template;
	}

	public static TopLevelTemplate createHoldingTemplate( CmsWebsite cmsWebsite, SystemUser systemUser ) {
		TopLevelTemplate template = new TopLevelTemplate();
		template.setName( "Holding Template" );
		template.setHead( 	"<title><ui:insert name='_title'>Holding Page</ui:insert></title>\n"

						);
		template.setBody( 	"<table style=\"margin:auto;padding-top:150px\">" +
							"<tr>" +
							"<td style=\"text-align:center;font-family: Verdana,Arial;font-size:14px;padding-top:20px\">" +
							"Website is currently under construction, please try again soon." +
							"</td>" +
							"</tr>" +
							"</table>"
						);
		template.saveDetails(cmsWebsite, systemUser);
		template.setDeletable( false );
		cmsWebsite.setHoldingTemplate( template );
		return template;
	}

	public static void createMenus( CmsWebsite cmsWebsite, SystemUser systemUser ) {

		// Create menus
		MenuNode rootMenu = new MenuNode();
		rootMenu.setName( "Root Menu" );

		MenuNode mainMenu = new MenuNode();
		mainMenu.setName( "Main Menu" );

		MenuNode sideMenu = new MenuNode();
		sideMenu.setName( "Side Menu" );

		MenuNode errorMenu = new MenuNode();
		errorMenu.setName( "Error Pages" );

		MenuNode unusedMenu = new MenuNode();
		unusedMenu.setName( "Unused Pages" );

		MenuNode hiddenPagesMenu = new MenuNode();
		hiddenPagesMenu.setName( "Hidden Pages" );
		
		mainMenu.saveDetails( cmsWebsite, systemUser );
		sideMenu.saveDetails( cmsWebsite, systemUser );
		unusedMenu.saveDetails( cmsWebsite, systemUser );
		hiddenPagesMenu.saveDetails( cmsWebsite, systemUser );
		errorMenu.saveDetails( cmsWebsite, systemUser );
		
		rootMenu.addChild( mainMenu );
		rootMenu.addChild( sideMenu );
		rootMenu.addChild( unusedMenu );
		rootMenu.addChild( hiddenPagesMenu );
		rootMenu.addChild( errorMenu );
		rootMenu.saveDetails( cmsWebsite, systemUser );

		cmsWebsite.setRootMenu(mainMenu);
		cmsWebsite.setMainMenu(mainMenu);
		cmsWebsite.setHiddenMenu(hiddenPagesMenu);
		cmsWebsite.setUnusedMenu(unusedMenu);
		cmsWebsite.setErrorMenu(errorMenu);
		cmsWebsite.setSideMenu(sideMenu);
	}

	public static void createDefaultPages( CmsWebsite cmsWebsite, CmsTemplate mainTemplate, CmsTemplate holdingTemplate, SystemUser systemUser ) {

		CmsPage homeCmsPage = new CmsPage();
		homeCmsPage.setName( "Home" );
		homeCmsPage.setMapping( "/" );
		homeCmsPage.setStatus( PageStatus.PUBLISHED );
		homeCmsPage.saveDetails( cmsWebsite, systemUser );

		CmsPageRevision home = new CmsPageRevision();
		home.setCmsPage( homeCmsPage );
		home.setTemplate( mainTemplate, true, true );
		home.addPlaceholderContent("Main Content", new CmsPlaceholderContent( "This is the body text." ) );
		home.addPlaceholderContent("Footer", new CmsPlaceholderContent( "This is the footer text." ));
		home.saveDetails( cmsWebsite, systemUser );
		MenuNode menuNode = new MenuNode(cmsWebsite, cmsWebsite.getMainMenu(), home.getCmsPage());
		menuNode.saveDetails( cmsWebsite, systemUser );
		cmsWebsite.getMainMenu().getChildren().add( menuNode );
		cmsWebsite.getMainMenu().saveDetails( cmsWebsite, systemUser );

		CmsPageRevision sessionTimeoutPage = createSessionTimeoutPage( cmsWebsite, systemUser, mainTemplate, cmsWebsite.getErrorMenu() );
		cmsWebsite.setSessionTimeoutPage(sessionTimeoutPage);

		CmsPageRevision issueReportedPage = createIssueReportedPage( cmsWebsite, systemUser, mainTemplate, cmsWebsite.getErrorMenu());
		cmsWebsite.setIssueReportedPage( issueReportedPage );

		CmsPageRevision holdingPage = createHoldingPage( cmsWebsite, systemUser, mainTemplate, cmsWebsite.getErrorMenu() );
		cmsWebsite.setHoldingPage( holdingPage );

		CmsPageRevision pageNotFoundPage = createPageNotFoundPage( cmsWebsite, systemUser, mainTemplate, cmsWebsite.getErrorMenu() );
		cmsWebsite.setPageNotFoundPage( pageNotFoundPage);

		CmsPageRevision unsubscribePage = createUnsubscribePage( cmsWebsite, systemUser, mainTemplate, cmsWebsite.getHiddenMenu() );
		cmsWebsite.setUnsubscribePage( unsubscribePage );

		cmsWebsite.saveDetails();
	}

	public static CmsPageRevision createHoldingPage( CmsWebsite cmsWebsite, SystemUser systemUser, CmsTemplate holdingTemplate, MenuNode menu ) {
		if( holdingTemplate != null && menu != null ) {
			CmsPage holdingCmsPage = new CmsPage();
			holdingCmsPage.setName( "Holding Page" );
			holdingCmsPage.setMapping( "holding-page" );
			holdingCmsPage.setStatus( PageStatus.PUBLISHED );
			holdingCmsPage.saveDetails( cmsWebsite, systemUser );
			holdingCmsPage.setDeletable( false );

			CmsPageRevision holdingPage = new CmsPageRevision();
			holdingPage.setCmsPage( holdingCmsPage );
			holdingPage.setTemplate( holdingTemplate, true, true );
			holdingPage.addPlaceholderContent("Main Content", new CmsPlaceholderContent( "This site is currently in development." ));
			holdingPage.saveDetails( cmsWebsite, systemUser );
			holdingPage.setDeletable( false );
			MenuNode holdingPageMenuNode = new MenuNode(cmsWebsite, menu, holdingPage.getCmsPage());
			holdingPageMenuNode.saveDetails();
			menu.addChild( holdingPageMenuNode );
			menu.saveDetails();
			return holdingPage;
		} else {
			return null;
		}
	}

	public static CmsPageRevision createPageNotFoundPage( CmsWebsite cmsWebsite, SystemUser systemUser, CmsTemplate mainTemplate, MenuNode menu ) {
		if( mainTemplate != null ) {
			CmsPage pageNotFoundCmsPage = new CmsPage();
			pageNotFoundCmsPage.setName( "Page Not Found" );
			pageNotFoundCmsPage.setMapping( "page-not-found" );
			pageNotFoundCmsPage.setStatus( PageStatus.PUBLISHED );
			pageNotFoundCmsPage.saveDetails( cmsWebsite, systemUser );
			pageNotFoundCmsPage.setDeletable( false );

			CmsPageRevision pageNotFoundPage = new CmsPageRevision();
			pageNotFoundPage.setCmsPage( pageNotFoundCmsPage );
			pageNotFoundPage.setTemplate( mainTemplate, true, true );
			pageNotFoundPage.addPlaceholderContent("Main Content", new CmsPlaceholderContent( "This page could not be found." ));
			pageNotFoundPage.saveDetails( cmsWebsite, systemUser );
			pageNotFoundPage.setDeletable( false );
			MenuNode pageNotFoundMenuNode = new MenuNode(cmsWebsite, menu, pageNotFoundPage.getCmsPage());
			pageNotFoundMenuNode.saveDetails();
			menu.getChildren().add( pageNotFoundMenuNode );
			menu.saveDetails();
			return pageNotFoundPage;
		} else {
			return null;
		}
	}

	public static CmsPageRevision createUnsubscribePage( CmsWebsite cmsWebsite, SystemUser systemUser, CmsTemplate mainTemplate, MenuNode menu ) {
		if( mainTemplate != null ) {
			//getUnconfigurableDeveloperCmsAtom
			CmsPage unsubscribeCmsPage = new CmsPage();
			unsubscribeCmsPage.setName( "Unsubscribe" );
			unsubscribeCmsPage.setMapping( "unsubscribe" );
			unsubscribeCmsPage.setStatus( PageStatus.PUBLISHED );
			unsubscribeCmsPage.saveDetails( cmsWebsite, systemUser );
			unsubscribeCmsPage.setDeletable( false );

			CmsPageRevision unsubscribePage = new CmsPageRevision();
			unsubscribePage.setCmsPage( unsubscribeCmsPage );
			unsubscribePage.setTemplate( mainTemplate, true, true );

			UnconfigurableDeveloperCmsAtom unsubscribeCmsAtom = CmsConfiguration.getCmsConfiguration().loadOrCreateUnconfigurationAtom( CmsConfiguration.CmsUnconfigurableAtomEnum.UNSUBSCRIBE );

			unsubscribePage.getCmsAtomList().add( unsubscribeCmsAtom );
			unsubscribePage.addPlaceholderContent("Main Content", new CmsPlaceholderContent( unsubscribeCmsAtom.getAllInsertTexts() ));
			unsubscribePage.saveDetails( cmsWebsite, systemUser );
			unsubscribePage.setDeletable( false );
			MenuNode unsubscribeMenuNode = new MenuNode(cmsWebsite, menu, unsubscribePage.getCmsPage());
			unsubscribeMenuNode.saveDetails();
			menu.getChildren().add( unsubscribeMenuNode );
			menu.saveDetails();
			return unsubscribePage;
		} else {
			return null;
		}
	}

	public static CmsPageRevision createIssueReportedPage( CmsWebsite cmsWebsite, SystemUser systemUser, CmsTemplate mainTemplate, MenuNode menu ) {
		if( mainTemplate != null ) {
			CmsPage issueReportedCmsPage = new CmsPage();
			issueReportedCmsPage.setName( "Issue Reported" );
			issueReportedCmsPage.setMapping( "issue-reported" );
			issueReportedCmsPage.setStatus( PageStatus.PUBLISHED );
			issueReportedCmsPage.saveDetails( cmsWebsite, systemUser );
			issueReportedCmsPage.setDeletable( false );

			CmsPageRevision issueReportedPage = new CmsPageRevision();
			issueReportedPage.setCmsPage( issueReportedCmsPage );
			issueReportedPage.setTemplate( mainTemplate, true, true );

			UnconfigurableDeveloperCmsAtom issueReportedUcCmsAtom = CmsConfiguration.getCmsConfiguration().loadOrCreateUnconfigurationAtom( CmsConfiguration.CmsUnconfigurableAtomEnum.ISSUE_REPORTED );
			//UnconfigurableDeveloperCmsAtom issueReportedUcCmsAtom = CmsConfiguration.getCmsConfiguration().getUcdCmsAtomMap().get( CmsConfiguration.CmsUnconfigurableAtomEnum.ISSUE_REPORTED );

			issueReportedPage.getCmsAtomList().add( issueReportedUcCmsAtom );
			issueReportedPage.addPlaceholderContent("Main Content", new CmsPlaceholderContent( issueReportedUcCmsAtom.getAllInsertTexts() ));
			issueReportedPage.saveDetails( cmsWebsite, systemUser );
			issueReportedPage.setDeletable( false );
			MenuNode issueReportedMenuNode = new MenuNode(cmsWebsite, menu, issueReportedPage.getCmsPage());
			issueReportedMenuNode.saveDetails();
			menu.getChildren().add( issueReportedMenuNode );
			menu.saveDetails();
			return issueReportedPage;
		} else {
			return null;
		}
	}

	public static CmsPageRevision createSessionTimeoutPage( CmsWebsite cmsWebsite, SystemUser systemUser, CmsTemplate mainTemplate, MenuNode menu ) {
		if( mainTemplate != null ) {
			CmsPage sessionTimeoutCmsPage = new CmsPage();
			sessionTimeoutCmsPage.setName( "Session Timeout" );
			sessionTimeoutCmsPage.setMapping( "session-timeout" );
			sessionTimeoutCmsPage.setStatus( PageStatus.PUBLISHED );
			sessionTimeoutCmsPage.saveDetails( cmsWebsite, systemUser );
			sessionTimeoutCmsPage.setDeletable( false );

			CmsPageRevision sessionTimeoutPage = new CmsPageRevision();
			sessionTimeoutPage.setCmsPage( sessionTimeoutCmsPage );
			sessionTimeoutPage.setTemplate( mainTemplate, true, true );

			String moduleName = CommonUtil.getModuleName( CmsConfiguration.CmsUnconfigurableAtomEnum.SESSION_TIMEOUT.getClass().getName() );
			String atomIdString = CmsConfiguration.CmsUnconfigurableAtomEnum.SESSION_TIMEOUT.getCmsAtomIdStr();
			String atomName = CmsConfiguration.CmsUnconfigurableAtomEnum.SESSION_TIMEOUT.getCmsAtomName();

			BeanDao unconfigurableAtomDao = new BeanDao( UnconfigurableDeveloperCmsAtom.class );
			unconfigurableAtomDao.setWhereCriteria("bean.cmsAtomIdStr='" + atomIdString + "'");
			unconfigurableAtomDao.setMaxResults(1);
			UnconfigurableDeveloperCmsAtom sessionTimeoutUcCmsAtom = unconfigurableAtomDao.getFirstBeanResult();
			if (sessionTimeoutUcCmsAtom == null) {
				sessionTimeoutUcCmsAtom = new UnconfigurableDeveloperCmsAtom( moduleName, atomIdString, atomName );
				sessionTimeoutUcCmsAtom.saveDetails();
			}
			//UnconfigurableDeveloperCmsAtom sessionTimeoutUcCmsAtom = CmsConfiguration.getCmsConfiguration().getUcdCmsAtomMap().get( CmsConfiguration.CmsUnconfigurableAtomEnum.SESSION_TIMEOUT );

			sessionTimeoutPage.getCmsAtomList().add( sessionTimeoutUcCmsAtom );
			String placeholderName = "Body";
			List<String> templateEditableCphNameList = sessionTimeoutPage.getTemplateEditableCphNameList(false); 
			if (templateEditableCphNameList.contains("Main Content")) {
				placeholderName = "Main Content";
			} else if (!templateEditableCphNameList.contains("Body")) {
				placeholderName = templateEditableCphNameList.get(0);
			}
			if (sessionTimeoutPage.getPlaceholderContentMap() == null) {
				sessionTimeoutPage.setPlaceholderContentMap(new HashMap<String, CmsPlaceholderContent>());
			}
			sessionTimeoutPage.addPlaceholderContent(placeholderName, new CmsPlaceholderContent( sessionTimeoutUcCmsAtom.getFirstInsertText() ));
			sessionTimeoutPage.saveDetails( cmsWebsite, systemUser );
			sessionTimeoutPage.setDeletable(false);
			MenuNode sessionTimeoutMenuNode = new MenuNode(cmsWebsite, menu, sessionTimeoutPage.getCmsPage());
			sessionTimeoutMenuNode.saveDetails();
			menu.getChildren().add( sessionTimeoutMenuNode );
			menu.saveDetails();
			return sessionTimeoutPage;
		} else {
			return null;
		}
	}

	public static void setCmsDefaultsLoader(CmsDatabaseLoader cmsDefaultsLoader) {
		CmsDatabaseLoader.cmsDefaultsLoader = cmsDefaultsLoader;
	}

	public static CmsDatabaseLoader getCmsDefaultsLoader() {
		return cmsDefaultsLoader;
	}
}
