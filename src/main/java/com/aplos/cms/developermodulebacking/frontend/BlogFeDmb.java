package com.aplos.cms.developermodulebacking.frontend;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.appconstants.CmsAppConstants;
import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.beans.BlogEntry;
import com.aplos.cms.beans.developercmsmodules.BlogModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.AplosUrl;
import com.aplos.common.AplosUrl.Protocol;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class BlogFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -3972521936219421655L;

	private BlogModule blogAtom;

	private List<BlogEntry> entries = null;
	private final String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private final int[] month_days = { 31,28,31,30,31,30,31,31,30,31,30,31 };
	private BlogEntry selectedBlogEntry = null;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setBlogAtom((BlogModule) developerCmsAtom);
		
		String blogListKeyStr = JSFUtil.getRequestParameter(CmsAppConstants.BLOG_ENTRY_ID);
		if( !CommonUtil.isNullOrEmpty( blogListKeyStr ) ) {
			Long id = null;
			try {
				id = Long.parseLong(blogListKeyStr);
				setSelectedBlogEntry((BlogEntry) new BeanDao( BlogEntry.class ).get( id ));
				if( getSelectedBlogEntry() != null ) {
					List<BlogEntry> blogEntries = new ArrayList<BlogEntry>();
					blogEntries.add( getSelectedBlogEntry() );
					setEntries(blogEntries);
				}
			} catch (NumberFormatException nfe) {
				JSFUtil.addMessage( "The article could not be found.");
			}
			if( getSelectedBlogEntry() != null ) {
				CmsPageRevision cmsPageRevision = ((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision();
				String blogEntryUrl = getBlogEntryUrl( getSelectedBlogEntry() );
				cmsPageRevision.setTitleOverride( getSelectedBlogEntry().getTitle() );
				cmsPageRevision.setCanocialPathOverride( blogEntryUrl );
				
				cmsPageRevision.getCmsPage().setFacebookTypeOverride( "article" );
				cmsPageRevision.getCmsPage().setFacebookUrlOverride( blogEntryUrl );
				cmsPageRevision.getCmsPage().setFacebookTitleOverride( getSelectedBlogEntry().getTitleForGsd() );
				cmsPageRevision.getCmsPage().setFacebookImageOverride( getSelectedBlogEntry().getImageDetails().getExternalFileUrl() );
				cmsPageRevision.getCmsPage().setFacebookDescriptionOverride( getSelectedBlogEntry().getShortDescriptionForGsd() );
			}
		} else { 
			blogListKeyStr = JSFUtil.getRequestParameter(CmsAppConstants.BLOG_ARCHIVE);
			if( blogListKeyStr != null ) {
				try {
					blogListKeyStr = URLDecoder.decode( blogListKeyStr, "UTF8" );
				} catch( UnsupportedEncodingException useEx ) {
					ApplicationUtil.handleError(useEx);
				}
			}
			
			CmsPageRevision blogCpr = CmsConfiguration.getCmsConfiguration().getBlogCpr();
			CmsPageUrl blogPageUrl = new CmsPageUrl( blogCpr, Website.getCurrentWebsiteFromTabSession() );
			if( blogCpr != null && blogCpr.getCmsPage().getSslProtocolEnum() != null ) {
				blogPageUrl.setScheme(blogCpr.getCmsPage().getSslProtocolEnum());
			} else {
				blogPageUrl.setScheme(Protocol.HTTP);
			}
			
			if( "latest".equals( blogListKeyStr ) ) {
				loadLatestPostList(blogPageUrl);
			} else if( !CommonUtil.isNullOrEmpty( blogListKeyStr ) ) {
				fetchArchive( blogListKeyStr );
				blogPageUrl.addQueryParameter( CmsAppConstants.BLOG_ARCHIVE, blogListKeyStr );
				((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setTitleOverride( "Blogs - " + blogListKeyStr );	
				((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setCanocialPathOverride( blogPageUrl.toString() );
			}  else if( getBlogEntryList() == null ) {
				loadLatestPostList(blogPageUrl);
			}
		}
		return true;
	}
	
	public void loadLatestPostList( CmsPageUrl blogPageUrl ) {
		getLatestPostList();
		((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setTitleOverride( "Latest blogs" );	
		((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setCanocialPathOverride( blogPageUrl.toString() );
	}

	public void setBlogAtom(BlogModule blogAtom) {
		this.blogAtom = blogAtom;
	}

	public BlogModule getBlogAtom() {
		return blogAtom;
	}

	public List<BlogEntry> getBlogEntryList() {
//		if (isShowingSingleBlogEntry()) {
//			Long id = Long.parseLong(JSFUtil.getRequestParameter("CmsAppConstants.BLOG_ENTRY_ID"));
//			ArrayList<BlogEntry> entriesCopyList = new ArrayList<BlogEntry>();
//			for (BlogEntry entry : getEntries()) {
//				if (entry.getId().equals(id)) {
//					entriesCopyList.add(entry);
//					return entriesCopyList;
//				}
//			}
//		}
		return getEntries();
	}
	
	public List<BlogEntry> getRecentPostsList() {
		@SuppressWarnings("unchecked")
		BeanDao blogEntriesDao = new BeanDao( BlogEntry.class );
		blogEntriesDao.addWhereCriteria( "bean.showOnWebsiteDate <= NOW()" );
		blogEntriesDao.setMaxResults( 10 );
		return blogEntriesDao.getAll();
	}

	public String getFeedUrl() {
		return CmsWorkingDirectory.RSS_FEEDS + blogAtom.getRssFeedMapping() + ".xml";
	}

	public void getLatestPostList() {
		@SuppressWarnings("unchecked")
		BeanDao blogEntriesDao = new BeanDao( BlogEntry.class );
		blogEntriesDao.addWhereCriteria( "bean.showOnWebsiteDate <= NOW()" );
		blogEntriesDao.setMaxResults( 10 );
		List<BlogEntry> dbEntries =  blogEntriesDao.getAll();
		setEntries(dbEntries);
	}

	public String getLatestPostListUrl() {
		CmsPageUrl baseBlogUrl = getBaseBlogUrl();
		baseBlogUrl.addQueryParameter( CmsAppConstants.BLOG_ARCHIVE, "latest" );
		return baseBlogUrl.toString();
	}

	public String getArchivedPostListUrl() {
		String archiveStr = (String) JSFUtil.getFromRequest( "archive" );
		CmsPageUrl baseBlogUrl = getBaseBlogUrl();
		baseBlogUrl.addQueryParameter( CmsAppConstants.BLOG_ARCHIVE, archiveStr );
		return baseBlogUrl.toString();
	}
	
	public CmsPageUrl getBaseBlogUrl() {
		ContentPage contentPage = (ContentPage) JSFUtil.getFromRequest( AplosScopedBindings.BACKING_PAGE );
		CmsPageUrl cmsPageUrl = new CmsPageUrl( contentPage.getCmsPageRevision().getCmsPage(), Website.getCurrentWebsiteFromTabSession() );
		return cmsPageUrl;
	}

	public ArrayList<String> getArchiveMonthList() {
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	    BeanDao beanDao = new BeanDao( BlogEntry.class );
	    beanDao.setSelectCriteria( "bean.dateCreated" );
	    beanDao.addWhereCriteria( "dateCreated < '" + sdf.format(cal.getTimeInMillis()) + "-01'" );
	    beanDao.addWhereCriteria( "showOnWebsiteDate <= NOW()" );
	    beanDao.setGroupBy( "Year(dateCreated), Month(dateCreated)" );
	    beanDao.setOrderBy( "Year(dateCreated) DESC, Month(dateCreated) DESC" );
	    
		@SuppressWarnings("unchecked")
		List<Date> resultList = beanDao.getBeanResults();
		ArrayList<String> archiveList = new ArrayList<String>();
		sdf = new SimpleDateFormat("MMMM yyyy");
		for( int i = 0, n = resultList.size(); i < n; i++ ) {
			archiveList.add( sdf.format( resultList.get(i) ) );
		}
		for (int i=0; i < archiveList.size(); i++) {
			archiveList.set(i,sdf.format(resultList.get(i)));
		}
		return archiveList;
	}
	
	public String getBlogEntryUrl() {
		return getBlogEntryUrl( (BlogEntry) JSFUtil.getBeanFromRequest( "entry" ) );
	}
	
	public String getBlogEntryUrl( BlogEntry blogEntry ) {
		CmsPageRevision blogCpr = CmsConfiguration.getCmsConfiguration().getBlogCpr();
		return getBlogEntryUrl(blogEntry, blogCpr);
	}
	
	public static String getBlogEntryUrl( BlogEntry blogEntry, CmsPageRevision blogCpr ) {
		StringBuffer urlStrBuf = new StringBuffer( "/blog/" );
		String safeBlogTitle = CommonUtil.getStringOrEmpty( blogEntry.getTitle() );
		safeBlogTitle = safeBlogTitle.replace( " ", "-" );
		safeBlogTitle = FormatUtil.stripToAllowedCharacters( safeBlogTitle, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-" );
		urlStrBuf.append( safeBlogTitle.toLowerCase() );
		urlStrBuf.append( ".aplos" );
		AplosUrl aplosUrl = new AplosUrl( urlStrBuf.toString(), false );
		aplosUrl.setHost( Website.getCurrentWebsiteFromTabSession() ); 
		if( blogCpr != null && blogCpr.getCmsPage().getSslProtocolEnum() != null ) {
			aplosUrl.setScheme(blogCpr.getCmsPage().getSslProtocolEnum());
		} else {
			aplosUrl.setScheme(Protocol.HTTP);
		}
		aplosUrl.addQueryParameter( CmsAppConstants.BLOG_ENTRY_ID, blogEntry.getId() );
		return aplosUrl.toString();
	}
	
	public String getBlogEntryContent( int maxChars ) {
		if( getSelectedBlogEntry() != null ) {
			return getSelectedBlogEntry().getContent();
		} else {
			BlogEntry blogEntry = JSFUtil.getBeanFromRequest( "entry" );
			return blogEntry.getMaxContent( maxChars );
		}
	}

	public int monthNumberFromName(String monthName) {
		for (int i=0; i<12; i++) {
			if (months[i].equals(monthName)) {
				if (i < 10) {
					return i;
				} else {
					return i;
				}
			}
		}
		return 1;
	}

	public void fetchArchive( String archiveName ) {
		String archiveNameParts[] = archiveName.split( " " );
		int month = monthNumberFromName(archiveNameParts[0]);
		String minDateStr = archiveNameParts[1] + "-" + (((month+1)<10)?"0":"") + (month+1) + "-01";
		String maxDateStr = archiveNameParts[1] + "-" + (((month+1)<10)?"0":"") + (month+1) + "-" + month_days[month];
		BeanDao blogEntryDao = new BeanDao( BlogEntry.class );
		blogEntryDao.addWhereCriteria( "bean.dateCreated <= '" + maxDateStr + "' AND bean.dateCreated >= '" + minDateStr + "'" );
		blogEntryDao.addWhereCriteria( "bean.showOnWebsiteDate <= NOW()" );
		blogEntryDao.setOrderBy( "bean.dateCreated DESC" );
		blogEntryDao.setMaxResults(10);
		@SuppressWarnings("unchecked")
		List<BlogEntry> dbEntries = blogEntryDao.getAll();
		setEntries( dbEntries );
	}

	public String getEntryDateString() {
		Date created = ((BlogEntry)JSFUtil.getRequest().getAttribute("entry")).getDateCreated();
		if (created == null) {
			return "No Date Specified";
		}
		//return created.getDate() + " " + months[created.getMonth()] + " " + (created.getYear()+1900);
		return FormatUtil.formatDate(created);
	}

	public boolean isShowingSingleBlogEntry() {
		if( getSelectedBlogEntry() != null ) {
			return true;
		}
		return false;
	}

	public BlogEntry getSelectedBlogEntry() {
		return selectedBlogEntry;
	}

	public void setSelectedBlogEntry(BlogEntry selectedBlogEntry) {
		this.selectedBlogEntry = selectedBlogEntry;
	}

	public List<BlogEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<BlogEntry> entries) {
		this.entries = entries;
	}

}
