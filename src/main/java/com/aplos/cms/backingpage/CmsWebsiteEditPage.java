package com.aplos.cms.backingpage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringEscapeUtils;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.BasicCmsContent;
import com.aplos.cms.beans.BlogEntry;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.frontend.BasicCmsContentFeDmb;
import com.aplos.cms.developermodulebacking.frontend.BlogFeDmb;
import com.aplos.cms.interfaces.BasicCmsContentAtom;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.WebsiteEditPage;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
public class CmsWebsiteEditPage extends WebsiteEditPage {

	private static final long serialVersionUID = 1944302334785262609L;

	/*
	 * We need to set the AqlBeanDao to Website so that it applyBtnAction
	 * & okBtnAction work correctly.
	 */
	public CmsWebsiteEditPage() {
		super();
		getBeanDao().setListPageClass( CmsWebsiteListPage.class );
	}

	@Override
	public boolean responsePageLoad() {
		return super.responsePageLoad();
	}
	
	public void generateSiteMap() {
		CmsWebsite cmsWebsite = (CmsWebsite) resolveAssociatedBean();
		File file = new File( CommonWorkingDirectory.PROCESSED_RESOURCES_DIR.getDirectoryPath(true) + "sitemap.xml" );
		cmsWebsite.generateSiteMap( file );
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		Website website = (Website) resolveAssociatedBean();
		ApplicationUtil.getAplosContextListener().updateWebsite( website );
		JSFUtil.addToTabSession( AplosScopedBindings.CURRENT_WEBSITE, null );

		Website.refreshWebsiteInSession();
		Website.getCurrentWebsiteFromTabSession().addToScope( JsfScope.TAB_SESSION );
	}

	@Override
	public void okBtnAction() {
		super.okBtnAction();
		applyBtnAction();
	}
}
