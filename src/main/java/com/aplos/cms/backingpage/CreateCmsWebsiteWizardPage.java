package com.aplos.cms.backingpage;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.backingpage.pages.CmsPageRevisionEditPage;
import com.aplos.cms.backingpage.pages.CmsPageRevisionListPage;
import com.aplos.cms.backingpage.pages.CssResourceEditPage;
import com.aplos.cms.backingpage.pages.CssResourceListPage;
import com.aplos.cms.backingpage.pages.InnerTemplateEditPage;
import com.aplos.cms.backingpage.pages.JavascriptResourceEditPage;
import com.aplos.cms.backingpage.pages.JavascriptResourceListPage;
import com.aplos.cms.backingpage.pages.TopLevelTemplateEditPage;
import com.aplos.cms.backingpage.pages.TopLevelTemplateListPage;
import com.aplos.cms.backingpage.pages.UserCmsModuleEditPage;
import com.aplos.cms.backingpage.pages.UserCmsModuleListPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.common.annotations.BackingPageOverride;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.backingpage.CreateWebsiteWizardPage;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.module.AplosModule;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.TabClass;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ApplicationUtil;

@ManagedBean
@ViewScoped
@GlobalAccess
@BackingPageOverride(backingPageClass=CreateWebsiteWizardPage.class)
public class CreateCmsWebsiteWizardPage extends CreateWebsiteWizardPage {
	private static final long serialVersionUID = 3972897225411517499L;

	private String googleAnalyticsId;
	private boolean isCmsProject = true;
	private boolean websiteLive = true;

	public CreateCmsWebsiteWizardPage() {
		super();
	}

	@Override
	public void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;
	}

	@Override
	public String getGoogleAnalyticsId() {
		return googleAnalyticsId;
	}

	@Override
	public void createWebsiteObject() {
		//dont create two websites by resubmitting if we just had an error the first time
		if (getCreatedWebsite() == null) {
			if (isCmsProject()) {
				setCreatedWebsite(new CmsWebsite());
			} else {
				setCreatedWebsite(new Website());
			}

			//set our variables
			getCreatedWebsite().setName(getName());
			getCreatedWebsite().setPrimaryHostName(getHostName());
			getCreatedWebsite().setPackageName(getPackageRoot());

			if (isCmsProject()) {
				//set extra CmsWebsite specific variables
				((CmsWebsite)getCreatedWebsite()).setLive(isWebsiteLive());
				((CmsWebsite)getCreatedWebsite()).setGoogleAnalyticsId(googleAnalyticsId);
			}

			getCreatedWebsite().saveDetails();
		}
	}

	@Override
	public TabPanel setupDynamicMenu(TabPanel mainDtp, List<UserLevel> viewableByList) {
		// setup the common stuff first
		mainDtp = super.setupDynamicMenu(mainDtp, viewableByList);
		//Setup our cms specific menus
		if (isCmsProject()) {

//			//i think this functionality is superceded by common config edit page and site structure
//			MenuTab siteTab = new MenuTab(viewableByList, "My Site", TabClass.get(CmsWebsiteEditPage.class));
//			siteTab.setTabPanel(settingsDtp);
//			settingsDtp.addMenuTab(siteTab);

			//setup pages tab
			TabPanel pagesDtp = new TabPanel(getCreatedWebsite(), "Cms Page Revision Tab Panel");
			pagesDtp.saveDetails();
			MenuTab pagesSubTab = new MenuTab(viewableByList, "Pages", TabClass.get(CmsPageRevisionListPage.class));
			pagesSubTab.addDefaultPageBinding(TabClass.get(CmsPageRevisionEditPage.class));
			pagesSubTab.setTabPanel(pagesDtp);
			pagesSubTab.saveDetails();
			pagesDtp.addMenuTab(pagesSubTab);
			pagesDtp.setDefaultTab(pagesSubTab);

			MenuTab cssTab = new MenuTab(viewableByList, "CSS", TabClass.get(CssResourceListPage.class));
			cssTab.addDefaultPageBinding(TabClass.get(CssResourceEditPage.class));
			cssTab.setTabPanel(pagesDtp);
			cssTab.saveDetails();
			pagesDtp.addMenuTab(cssTab);

			MenuTab modulesTab = new MenuTab(viewableByList, "Modules", TabClass.get(UserCmsModuleListPage.class));
			modulesTab.addDefaultPageBinding(TabClass.get(UserCmsModuleEditPage.class));
			modulesTab.setTabPanel(pagesDtp);
			modulesTab.saveDetails();
			pagesDtp.addMenuTab(modulesTab);

			MenuTab templatesTab = new MenuTab(viewableByList, "Templates", TabClass.get(TopLevelTemplateListPage.class));
			templatesTab.addDefaultPageBinding(TabClass.get(TopLevelTemplateEditPage.class));
			templatesTab.addDefaultPageBinding(TabClass.get(InnerTemplateEditPage.class));
			templatesTab.setTabPanel(pagesDtp);
			templatesTab.saveDetails();
			pagesDtp.addMenuTab(templatesTab);

			MenuTab javascriptTab = new MenuTab(viewableByList, "Javascript", TabClass.get(JavascriptResourceListPage.class));
			javascriptTab.addDefaultPageBinding(TabClass.get(JavascriptResourceEditPage.class));
			javascriptTab.setTabPanel(pagesDtp);
			javascriptTab.saveDetails();
			pagesDtp.addMenuTab(javascriptTab);

			//create tab to attach it to main tab panel
			MenuTab pagesTab = new MenuTab(viewableByList, "Pages", pagesDtp);
			pagesTab.setTabPanel(mainDtp);
			pagesTab.setSubTabPanel(pagesDtp);
			pagesTab.saveDetails();
			mainDtp.addMenuTab(pagesTab);
			mainDtp.setDefaultTab(pagesTab);
			mainDtp.saveDetails();

		}
		return mainDtp;
	}

	@Override
	public List<AplosModule> getModules() {
		List<AplosModule> modules = new ArrayList<AplosModule>();
		AplosModule mod = ApplicationUtil.getAplosContextListener().getAplosModuleByName("cms");
		if (mod != null) {
			modules.add(mod);
		}
		return modules;
	}

	public boolean isCmsProject() {
		return isCmsProject;
	}

	public void setCmsProject(boolean isCmsProject) {
		this.isCmsProject = isCmsProject;
	}

	public boolean isWebsiteLive() {
		return websiteLive;
	}

	public void setWebsiteLive(boolean websiteLive) {
		this.websiteLive = websiteLive;
	}
}

