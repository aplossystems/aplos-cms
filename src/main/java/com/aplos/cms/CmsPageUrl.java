package com.aplos.cms;

import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.AplosUrl;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.SslProtocolEnum;


public class CmsPageUrl extends AplosUrl {

    public CmsPageUrl() {
    	super();
    }

    public CmsPageUrl( CmsPageRevision cmsPageRevision, Website site ) {
    	this( cmsPageRevision.getCmsPage() );
    	setHost( site );
    	/*
    	 * This is already added by the getFullMapping
    	 */
    	addContextPath(false);
    }

    public CmsPageUrl( CmsPageRevision cmsPageRevision ) {
    	this( cmsPageRevision.getCmsPage() );
    }
    
    public CmsPageUrl( CmsPageRevision cmsPageRevision, boolean isExternalUrl ) {
    	this( cmsPageRevision.getCmsPage(), isExternalUrl );
    }

    public CmsPageUrl( CmsPageRevision cmsPageRevision, String queryParameters ) {
    	this( cmsPageRevision.getCmsPage(), queryParameters );
    }

    public CmsPageUrl( CmsPage cmsPage, Website site ) {
    	this( cmsPage, true );
    	setHost( site );
    	/*
    	 * This is already added by the getFullMapping
    	 */
    	addContextPath(false);
    }

    public CmsPageUrl( CmsPage cmsPage, String queryParameters ) {
    	this( cmsPage );
    	this.addQueryParameters(queryParameters);
    }

    public CmsPageUrl( CmsPage cmsPage ) {
    	this( cmsPage.getFullMapping(true), cmsPage.getSslProtocolEnum(), null );
    }
    
    public CmsPageUrl( CmsPage cmsPage, boolean isExternalUrl ) {
    	this( cmsPage.getFullMapping(true), cmsPage.getSslProtocolEnum(), null, isExternalUrl );
    }

    public CmsPageUrl( CmsPage cmsPage, SslProtocolEnum sslProtocolEnum, String queryParameters ) {
    	this( cmsPage, sslProtocolEnum );
    	this.addQueryParameters(queryParameters);
    }

    public CmsPageUrl( CmsPage cmsPage, SslProtocolEnum sslProtocolEnum ) {
    	this(cmsPage.getFullMapping(true), sslProtocolEnum, null);
    }

    /**
     * @see for preferred usage see {@link #CmsPageUrl(CmsPage, String)}
     */
    public CmsPageUrl( String url, String queryParameters ) {
    	this(url, SslProtocolEnum.DONT_CHANGE, queryParameters);
    }

    /**
     * @see for preferred usage see {@link #CmsPageUrl(CmsPage, boolean, String)}
     */
    public CmsPageUrl( String url, SslProtocolEnum sslProtocolEnum, String queryParameters ) {
    	this( url, sslProtocolEnum, queryParameters, false );
    }
    
    public CmsPageUrl( String url, SslProtocolEnum sslProtocolEnum, String queryParameters, boolean isExternalUrl ) {
    	super(url, false, sslProtocolEnum, queryParameters);
    	String path = getPath();
    	if (!path.endsWith(".aplos")) {
    		super.setPath(path + ".aplos");
    	}
    	if( getHost() == null && isExternalUrl ) {
    		setHost( Website.getCurrentWebsiteFromTabSession() );
    	}
    	/*
    	 * This is already added by the getFullMapping
    	 */
    	addContextPath(false);
    	// It only does something when it's true so don't add it to the URL otherwise
    	if( isExternalUrl ) {
    		addQueryParameter("sessionLogin", isExternalUrl);
    	}
    }

}

