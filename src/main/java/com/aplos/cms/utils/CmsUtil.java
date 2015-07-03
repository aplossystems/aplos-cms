package com.aplos.cms.utils;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;

import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.interfaces.RssFeedContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

public class CmsUtil {

	public static boolean validateMapping( CmsPage cmsPage, String mapping ) {
		if( !validateMappingCharacters(mapping) ) {
			return false;
		}


		BeanDao cmsPageDao = new BeanDao( CmsPage.class )
		    .setSelectCriteria( "count(bean)" )
			.addWhereCriteria( "bean.mapping = :mapping" );
		
		cmsPageDao.addReverseJoinTable( MenuNode.class, "mn.cmsPage", "bean" );

		if( !cmsPage.isNew() ) {
			MenuNode menuNode = (MenuNode) new BeanDao( MenuNode.class ).addWhereCriteria( "cmsPage.id = " + cmsPage.getId() ).setIsReturningActiveBeans(null).getFirstBeanResult();
			/*
			 * menuNode might be null if the page is deleted.
			 */
			if( menuNode == null || menuNode.getParent() == null ) {
				cmsPageDao.addWhereCriteria( "mn.id IS NULL" );
			} else {
				cmsPageDao.addWhereCriteria( "mn.parent.id = " + menuNode.getParent().getId() );
			}
			cmsPageDao.addWhereCriteria( "bean.id != " + cmsPage.getId() );
		} else {
			cmsPageDao.addWhereCriteria( "mn.id IS NULL" );
		}
		if( cmsPage.getParentWebsite() != null ) {
			cmsPageDao.addWhereCriteria( "bean.parentWebsite.id = " + cmsPage.getParentWebsite().getId() );
		} else {
			cmsPageDao.addWhereCriteria( "bean.parentWebsite.id = " + Website.getCurrentWebsiteFromTabSession().getId() );
		}
		cmsPageDao.setIsReturningActiveBeans(true).setNamedParameter( "mapping", mapping );
		if( (Long) cmsPageDao.getFirstResult() > 0 ) {
			JSFUtil.addMessage("A page with this mapping already exists", FacesMessage.SEVERITY_ERROR );
			return false;
		}
		return true;
	}

	public static boolean validateMapping( String mapping, boolean addWarningMessage ) {
		return validateMapping( "", mapping, addWarningMessage );
	}

	@SuppressWarnings("unchecked")
	public static boolean validateMapping( String mappingPath, String mapping, boolean addWarningMessage ) {
		if( mappingPath == null ) {
			mappingPath = "";
		}
		if ( mappingPath.startsWith( "/" ) ) { //slashes get replaced so you end up with ormapping if you dont eat it
			mappingPath = mappingPath.substring(1);
		}
		if( !mappingPath.endsWith( "/" ) ) {
			mappingPath = mappingPath + "/";
		}
		if( !validateMappingCharacters(mapping) ) {
			return false;
		}

		BeanDao cmsPageDao = new BeanDao( CmsPage.class ).addWhereCriteria( "bean.mapping = :mapping" );
		cmsPageDao.setNamedParameter( "mapping", mapping );
		List<CmsPage> cmsPageList = cmsPageDao.getAll();
		if( cmsPageList.size() > 0 ) {
			for( int i = 0, n = cmsPageList.size(); i < n; i++ ) {
				if( mappingPath.equals( cmsPageList.get( i ).getCachedMappingPath() ) ) {
					if( addWarningMessage ) {
						JSFUtil.addMessage("A page with this mapping already exists", FacesMessage.SEVERITY_ERROR );
					}
					return false;
				}
			}
		}
		return true;
	}

	public static boolean validateMappingCharacters( String mapping ) {
		if (mapping.contains(" ") || mapping.contains("?") || mapping.contains("&") ) {
			JSFUtil.addMessage("Mapping cannot contain &, ? or space characters ", FacesMessage.SEVERITY_ERROR );
			return false;
		}
		return true;
	}

	public static CmsPageRevision createCmsPageRevision( String mapping, String name, MenuNode parentMenuNode,
			CmsTemplate cmsTemplate, SystemUser systemUser ) {
		CmsPageRevision cmsPageRevision = createCmsPageRevision(mapping, name, cmsTemplate, systemUser);
		addCmsPageToMenu( cmsPageRevision.getCmsPage(), parentMenuNode, systemUser );
		return cmsPageRevision;
	}

	public static CmsPageRevision createCmsPageRevision( String mapping, String name,
			CmsTemplate cmsTemplate, SystemUser systemUser ) {
		CmsPage cmsPage = new CmsPage();
		cmsPage.setMapping( mapping );
		cmsPage.setName( name );
		CmsPageRevision cmsPageRevision = new CmsPageRevision();
		cmsPageRevision.setCmsPage( cmsPage );
		cmsPage.saveDetails();

		cmsPageRevision.setTemplate( cmsTemplate, true, true );
		cmsPageRevision.saveDetails();
		return cmsPageRevision;
	}

	public static void addCmsPageToMenu( CmsPage cmsPage, MenuNode parentMenuNode, SystemUser systemUser ) {
		MenuNode node = new MenuNode();
		node.saveDetails();
		node.setCmsPage( cmsPage );

		// Keep both sides of the relationship consistent
		node.setParent( parentMenuNode );
		parentMenuNode.getChildren().add( node );

		// The save will cascade to page without calling saveDetails
		// so we have a blank page with a blank mapping but with an
		// id, which is perfect - for now...
		node.saveDetails();
		parentMenuNode.saveDetails();
	}

	public static String getBackingPageUrl( Website website, Class<? extends BackingPage> backingPageClass ) {
		return getBackingPageUrl( website.getPackageName(), backingPageClass);
	}

	public static String getBackingPageUrl( String sitePackageName, Class<? extends BackingPage> backingPageClass ) {
		String viewName = CommonUtil.firstLetterToLowerCase( backingPageClass.getSimpleName() );
		String viewUrl = backingPageClass.getName().replace( backingPageClass.getSimpleName(), viewName );
		viewUrl = "/" + viewUrl.replaceFirst( "^(com\\.aplos\\.)(.*)(backingpage.)(.*)(Page)$", "$2$4" ).replace(".", "/");
		if( sitePackageName == null || viewUrl.startsWith( "/" + sitePackageName + "/" ) ) {
			return viewUrl;
		} else {
			return "/" + sitePackageName + viewUrl;
		}
	}

	public static boolean generateRssFeed(AplosBean aplosBean, String feedName, String feedMapping, String contentHomeUrlMapping, String feedDescription, String logoImageUrlRelative) {
		return generateRssFeed(new BeanDao(aplosBean.getClass()), feedName, feedMapping, contentHomeUrlMapping, feedDescription, logoImageUrlRelative);
	}

	/**
	 * @param BeanDao feedDao is a dao with criteria set to select objects to generate a feed using the RssFeedContent interface
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean generateRssFeed(BeanDao feedDao, String feedName, String feedMapping, String contentHomeUrlMapping, String feedDescription, String logoImageUrlRelative) {
		try {
			feedMapping = CommonUtil.makeSafeUrlMapping(feedMapping);
			List<RssFeedContent> rssFeedContents = feedDao.setIsReturningActiveBeans(true).getAll();
			StringBuffer feed = new StringBuffer("<?xml version=\"1.0\" ?>\n");
			feed.append("<rss version=\"2.0\">\n");
			feed.append("<channel>\n");
			feed.append("<lastGenerated>");
			feed.append(FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), new Date()));
			feed.append("</lastGenerated>\n");
			feed.append("<title>");
			feed.append(feedName);
			feed.append("</title>\n");
			feed.append("<link>");
			String domain = JSFUtil.getServerUrl();
			if (!domain.toLowerCase().startsWith("http")) {
				domain = "http://" + domain;
			}
			if (domain.endsWith("/")) {
				domain = domain.substring(0, domain.length() - 1);
			}
			domain += JSFUtil.getContextPath();
			feed.append(domain);
			if (!feedMapping.startsWith("/")) {
				feed.append("/");
			}
			feed.append(CmsWorkingDirectory.RSS_FEEDS.getDirectoryPath(false));
			feed.append(feedMapping);
			feed.append(".xml</link>\n");
			feed.append("<description>");
			if (feedDescription != null && !feedDescription.equals("")) {
				feed.append(feedDescription);
			} else {
				feed.append(feedName);
			}
			feed.append("</description>\n");
			//TODO: i dont think we have anywhere that doesnt pass in null for this at the moment
			if (logoImageUrlRelative != null && !logoImageUrlRelative.equals("")) {
				feed.append("<image>\n");
				feed.append("<url>");
				feed.append(domain);
				feed.append(logoImageUrlRelative);
				feed.append("</url>\n");
				feed.append("<link>");
				feed.append(domain);
				feed.append(contentHomeUrlMapping);
				feed.append("</link>\n");
				feed.append("</image>\n");
			}
			for (RssFeedContent content : rssFeedContents) {
				feed.append("<item>\n");
				feed.append("<title>");
				feed.append(content.getFeedContentTitle());
				feed.append("</title>\n");
				feed.append("<date>");
				feed.append(FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), content.getDateCreated()));
				feed.append("</date>\n");
				feed.append("<link>");
				feed.append(domain);
				if (content.getFeedContentUrlRelative() != null && !content.getFeedContentUrlRelative().equals("")) {
					feed.append(content.getFeedContentUrlRelative());
				} else {
					feed.append(contentHomeUrlMapping);
				}
				feed.append("</link>\n");
				feed.append("<description>\n");
				feed.append(XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(content.getFeedContentExcerpt()));
				feed.append("\n</description>\n");
				feed.append("</item>\n");
			}
			feed.append("</channel>\n");
			feed.append("</rss>");
			CmsModule.writeViewToFile(feed.toString(), CmsWorkingDirectory.RSS_FEEDS, feedMapping, "xml" );
			return true;
		} catch (Exception ex) { //TODO: make more specific to catch bean dao errors not npe's
			ex.printStackTrace();
			return false;
		}
	}

}
