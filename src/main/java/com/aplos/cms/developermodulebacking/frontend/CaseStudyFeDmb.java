package com.aplos.cms.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.appconstants.CmsAppConstants;
import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.beans.BasicCmsContent;
import com.aplos.cms.beans.CaseStudy;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsRedirectFolder;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.AplosUrl;
import com.aplos.common.AplosUrl.Protocol;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class CaseStudyFeDmb extends BasicCmsContentFeDmb {
	private static final long serialVersionUID = 6133841572488403194L;
	
	private Set<String> availableKeywords;
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);
		if( getSelectedBasicCmsContent() != null ) {
			CaseStudy caseStudy = (CaseStudy) getSelectedBasicCmsContent().getSaveableBean(); 
			caseStudy.setViews( caseStudy.getViews() + 1 );
			caseStudy.saveDetails();
		}
		if( getAvailableKeywords() == null ) {
			setAvailableKeywords(new HashSet<String>());
			for( BasicCmsContent tempBasicCmsContent : getBasicCmsContents() ) {
				List<String> caseStudyKeywords = ((CaseStudy) tempBasicCmsContent).getKeywords();
				for( String tempKeyword : caseStudyKeywords ) {
					getAvailableKeywords().add( tempKeyword.toLowerCase() );
				}
			}
		}
		return continueLoad;
	}
	
	@Override
	public String getBasicCmsContentPageTitle( BasicCmsContent basicCmsContent ) {
		CaseStudy caseStudy = (CaseStudy) basicCmsContent;
		String caseStudyTitle = CommonUtil.getStringOrEmpty( CommonUtil.getStringOrEmpty( caseStudy.getTitle() ) );
		if( !CommonUtil.isNullOrEmpty(caseStudy.getLocation()) ) {
			caseStudyTitle += ", " + caseStudy.getLocation();
		}
		return caseStudyTitle;
	}

	public void redirectToBasicCmsContentList() {
		JSFUtil.redirect( new CmsPageUrl( CmsConfiguration.getCmsConfiguration().getCaseStudyCpr().getCmsPage() ) );
	}
	
	@Override
	public CmsRedirectFolder getCmsRedirectFolder() {
		return CmsRedirectFolder.CASE_STUDIES;
	}
	
	public List<String> getAvailableKeywordList() {
		return new ArrayList<String>(getAvailableKeywords());
	}
	
	public String getKeywordStr() {
		CaseStudy caseStudy = (CaseStudy) JSFUtil.getBeanFromRequest( "basicCmsContent" );
		StringBuffer keywordStrBuf = new StringBuffer();
		for( int i = 0, n = caseStudy.getKeywords().size(); i < n; i++ ) {
			keywordStrBuf.append( caseStudy.getKeywords().get( i ).toLowerCase() ).append( " " );
		}
		return keywordStrBuf.toString();
	}

	@Override
	public Class<? extends BasicCmsContent> getBeanClass() {
		return CaseStudy.class;
	}

	public Set<String> getAvailableKeywords() {
		return availableKeywords;
	}

	public void setAvailableKeywords(Set<String> availableKeywords) {
		this.availableKeywords = availableKeywords;
	}
}
