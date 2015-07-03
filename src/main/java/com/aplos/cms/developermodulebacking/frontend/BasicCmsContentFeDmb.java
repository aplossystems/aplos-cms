package com.aplos.cms.developermodulebacking.frontend;

import java.util.Collections;
import java.util.List;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.appconstants.CmsAppConstants;
import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.beans.BasicCmsContent;
import com.aplos.cms.beans.CaseStudy;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsRedirectFolder;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.AplosRequestContext;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Website;
import com.aplos.common.comparators.DateCreatedComparator;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

public abstract class BasicCmsContentFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5254221973530134503L;

	private List<BasicCmsContent> basicCmsContents;
	private BasicCmsContent selectedBasicCmsContent = null;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);

		String contentIdStr = JSFUtil.getRequestParameter(getCmsRedirectFolder().getContentIdKey());
		if( !CommonUtil.isNullOrEmpty( contentIdStr ) ) {
			Long id = null;
			try {
				id = Long.parseLong(contentIdStr);
				setSelectedBasicCmsContent((BasicCmsContent) new BeanDao( getBeanClass() ).get( id ));
			} catch (NumberFormatException nfe) {
				JSFUtil.addMessage( "The content could not be found.");
			}
		}

		if( getBasicCmsContents() == null ) {		
			createBasicCmsContents();
		}
		
		if( getSelectedBasicCmsContent() != null ) {
			((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setTitleOverride( getBasicCmsContentPageTitle( getSelectedBasicCmsContent() ) );
			((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision().setCanocialPathOverride( getBasicCmsContentLink( getSelectedBasicCmsContent() ) );
		}
		
		return continueLoad;
	}
	
	public void createBasicCmsContents() {
		BeanDao basicCmsContentDao = new BeanDao( getBeanClass() );
		basicCmsContentDao.addWhereCriteria( "bean.isShowingInFrontEnd = true" );
		List sortedBasicCmsContentList = basicCmsContentDao.getAll();
		Collections.sort( sortedBasicCmsContentList, new DateCreatedComparator(false) );
		setBasicCmsContents( sortedBasicCmsContentList );
	}
	
	public String getBasicCmsContentPageTitle( BasicCmsContent basicCmsContent ) {
		return basicCmsContent.getTitle();
	}
	
	public abstract Class<? extends BasicCmsContent> getBeanClass();
	
	public CmsRedirectFolder getCmsRedirectFolder() {
		return CmsRedirectFolder.BASICS;
	}
	
	public String getBasicCmsContentLink( BasicCmsContent basicCmsContent ) {
		CmsPageRevision cmsPageRevision = ((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision();
		return getBasicCmsContentLink(basicCmsContent, cmsPageRevision);
	}
	
	public String getBasicCmsContentLink( BasicCmsContent basicCmsContent, CmsPageRevision cmsPageRevision ) {
		StringBuffer urlStrBuf = new StringBuffer( "/" ).append( getCmsRedirectFolder().getLabel() ).append( "/" );
		String safeBlogTitle = getBasicCmsContentPageTitle( basicCmsContent );
		safeBlogTitle = safeBlogTitle.replace( " ", "-" );
		safeBlogTitle = FormatUtil.stripToAllowedCharacters( safeBlogTitle, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-" );
		urlStrBuf.append( safeBlogTitle.toLowerCase() );
		urlStrBuf.append( ".aplos" );
		AplosUrl aplosUrl = new AplosUrl( urlStrBuf.toString(), false );
		aplosUrl.setHost( Website.getCurrentWebsiteFromTabSession() );
		aplosUrl.setScheme( cmsPageRevision.getCmsPage().getSslProtocolEnum() );
		aplosUrl.addQueryParameter( getCmsRedirectFolder().getContentIdKey(), basicCmsContent.getId() );
		aplosUrl.addQueryParameter( CmsAppConstants.CMS_PAGE_ID, cmsPageRevision.getCmsPage().getId() );
		return aplosUrl.toString();
	}

	public void redirectToBasicCmsContentList() {
		setSelectedBasicCmsContent(null);
		CmsPageRevision cmsPageRevision = ((ContentPage) JSFUtil.getCurrentBackingPage()).getCmsPageRevision();
		JSFUtil.redirect( new CmsPageUrl( cmsPageRevision.getCmsPage() ),false);
	}

	public void redirectToBasicCmsContent() {
		BasicCmsContent basicCmsContent = (BasicCmsContent) JSFUtil.getRequest().getAttribute("basicCmsContent");
		AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext();
		CmsPageUrl cmsPageUrl = new CmsPageUrl( aplosRequestContext.getOriginalUrl(),null);
		cmsPageUrl.addQueryParameter( getCmsRedirectFolder().getContentIdKey(), basicCmsContent);
		JSFUtil.redirect( cmsPageUrl,true);
	}

	public BasicCmsContent getSelectedBasicCmsContent() {
		return selectedBasicCmsContent;
	}

	public void setSelectedBasicCmsContent(BasicCmsContent selectedBasicCmsContent) {
		this.selectedBasicCmsContent = selectedBasicCmsContent;
	}

	public List<BasicCmsContent> getBasicCmsContents() {
		return basicCmsContents;
	}

	public void setBasicCmsContents(List<BasicCmsContent> basicCmsContents) {
		this.basicCmsContents = basicCmsContents;
	}
}
