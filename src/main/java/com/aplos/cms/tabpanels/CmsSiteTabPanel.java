package com.aplos.cms.tabpanels;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.backingpage.AtomAccessPage;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.SiteTabPanel;
import com.aplos.common.tabpanels.TabClass;

@ManagedBean
@SessionScoped
public class CmsSiteTabPanel extends SiteTabPanel {

	private static final long serialVersionUID = -8219183479082984803L;

	public CmsSiteTabPanel() { }

	public CmsSiteTabPanel(AplosContextListener contextLstn) {
		super();
		super.setName("Cms Site Selection Tab Panel");
	}
	
	public void addDefaultTabs(ArrayList<UserLevel> superUserPseudoList) {

		super.addDefaultTabs(superUserPseudoList);
		
		MenuTab atomsMenuTab = new MenuTab(superUserPseudoList, "Atom Availability", new TabClass(AtomAccessPage.class) );
		atomsMenuTab.setTabPanel(this);
		atomsMenuTab.setPackageName("cms");
		atomsMenuTab.setIsHiddenFromSuperuser(false);
		addMenuTab(atomsMenuTab);

	}

}













