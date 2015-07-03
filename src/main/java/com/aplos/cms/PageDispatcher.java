package com.aplos.cms;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.AplosRequestContext;
import com.aplos.common.AplosUrl;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.listeners.PageBindingPhaseListener;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean(eager=true)
@ApplicationScoped
public class PageDispatcher {
	public PageDispatcher() {

	}

	public String viewOrEdit() {
		String uri = JSFUtil.getAplosContextOriginalUrl();
		return viewOrEdit( uri );
	}

	public String viewOrEdit( String requestUrl ) {
		if( Website.getCurrentWebsiteFromTabSession() == null ) {
			Website.refreshWebsiteInSession();
		}
		String contextPath = JSFUtil.getContextPath();
		PageBindingPhaseListener.setNoCacheHeaders();
		String uri;
		AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext();
		if( aplosRequestContext != null && aplosRequestContext.getRedirectionUrl() != null ) {
			uri = aplosRequestContext.getRedirectionUrl();
			if( uri.indexOf( "?" ) > -1 ) {
				uri = uri.substring( 0, uri.indexOf( "?" ) );
			}
		} else {
			uri = requestUrl;
		}
		uri = uri.replaceAll( ".aplos", "" );
		if( uri.contains( ";jsession" ) ) {
			uri = uri.substring( 0, uri.indexOf( ";jsession" ) );
		}
		String mapping = uri;
		if( !CommonUtil.isNullOrEmpty( contextPath ) ) {
			mapping = mapping.replaceFirst( contextPath + "/", "" );
		}
		int firstMappingCharacterIndex = mapping.lastIndexOf('/');
		if( firstMappingCharacterIndex != -1 ) {
			mapping = mapping.substring(firstMappingCharacterIndex);
		}
		/*
		 * Mistakes are sometimes made with URLs especially because of the context path where the /
		 * is added twice, these mistakes should be ignored.  I can't see any application for them
		 * elsewhere.  We could just ignore the first one as this is where the mistake normally happens.
		 */
		String mappingPath = uri.replace( "//", "/" );
		if( mappingPath.startsWith( JSFUtil.getContextPath() + "/" ) || mappingPath.startsWith( JSFUtil.getContextPath(true) + "/" ) ) {
			mappingPath = mappingPath.substring( mappingPath.indexOf( JSFUtil.getContextPath() ) + JSFUtil.getContextPath().length() );
		}
		mappingPath = mappingPath.replace(mapping, "");
		//setup content page, to help us find the correct revision
		ContentPage contentPage = (ContentPage) PageBindingPhaseListener.resolveBackingPage( ContentPage.class );
		if (contentPage == null) {
			throw new RuntimeException("FATAL: Could not load Content Loader.");
		}
		SystemUser user = JSFUtil.getLoggedInUser();
		if ( user != null && user.isLoggedIn() ) {
			contentPage.setEditMode( true );
		} else {
			contentPage.setEditMode( false );
		}
		CmsPageRevision cmsPageRevision = getPageRevision( contentPage, mappingPath, mapping );
		if ( cmsPageRevision == null ) {
			return null;
		}
		contentPage.setCmsPageRevision( cmsPageRevision );
		String viewPathAbsolute = CmsWorkingDirectory.CMS_PAGE_REVISION_VIEW_FILES.getDirectoryPath(false) + cmsPageRevision.getId() + ".xhtml";
		cmsPageRevision.addToScope();
		AplosUrl aplosUrl = new CmsPageUrl( cmsPageRevision );
		aplosUrl.setCompletingResponseAfterRedirect( false );
		/*
		 * This was disabled otherwise the home page didn't load correctly in the local environment
		 * when the .aplos wasn't added to the end of the URL.
		 */
		aplosUrl.addContextPath( false );
		aplosUrl.addCurrentQueryString();
		if( JSFUtil.schemeRedirectIfRequired( aplosUrl, cmsPageRevision.getCmsPage().getSslProtocolEnum() ) ) {
			return null;
		} else {
			StringBuffer viewStrBuf = new StringBuffer( "/" );
			viewStrBuf.append( viewPathAbsolute.replace(".xhtml", ".jsf") );
			return viewStrBuf.toString();
		}
	}

	public CmsPageRevision getPageRevision(ContentPage contentPage, String mappingPath, String mapping) {

		boolean isDraft = false;
		if( JSFUtil.getLoggedInUser() != null && JSFUtil.getLoggedInUser().isLoggedIn() ) {
			isDraft = true;
		}

		CmsPageRevision cmsPageRevision = contentPage.determinePage( mappingPath, mapping, isDraft );


		if( cmsPageRevision != null ) {
			cmsPageRevision.addToScope();
		}

		return cmsPageRevision;
	}

}
