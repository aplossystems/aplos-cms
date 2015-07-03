package com.aplos.cms.backingpage;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@GlobalAccess

public class AtomAccessPage extends ListPage {
	private static final long serialVersionUID = 1908802016640104500L;
	private List<CmsAtom> enabledAtoms = new ArrayList<CmsAtom>();
	private List<DeveloperCmsAtom> availableAtoms = new ArrayList<DeveloperCmsAtom>();
	private List<DeveloperCmsAtom> selectedEnabledAtoms = new ArrayList<DeveloperCmsAtom>();
	private List<DeveloperCmsAtom> selectedAvailableAtoms = new ArrayList<DeveloperCmsAtom>();

	public AtomAccessPage() {
		
		CmsWebsite cmsWebsite = (CmsWebsite) CmsWebsite.getCurrentWebsiteFromTabSession();
		enabledAtoms = cmsWebsite.getEnabledCmsAtomList();
		availableAtoms = cmsWebsite.getAvailableCmsAtomList();

	}

	public String enableAtoms() {
		int enabledCount=0;
		if (selectedAvailableAtoms.size() > 0) {
			CmsWebsite cmsWebsite = (CmsWebsite) CmsWebsite.getCurrentWebsiteFromTabSession();
			for (DeveloperCmsAtom cmsAtom : selectedAvailableAtoms) {
				availableAtoms.remove(cmsAtom);
				enabledAtoms.add(cmsAtom);
				cmsWebsite.getEnabledCmsAtomList().add(cmsAtom);
				cmsWebsite.saveDetails();
				enabledCount++;
			}
			JSFUtil.addMessage(enabledCount + " classes enabled");
		} else {
			JSFUtil.addMessageForError("No classes are selected to be enabled");
		}
		selectedAvailableAtoms = null;
		return null;
	}

	public String disableAtoms() {
		int disabledCount=0;
		if (selectedEnabledAtoms.size() > 0) {
			CmsWebsite cmsWebsite = (CmsWebsite) CmsWebsite.getCurrentWebsiteFromTabSession();
			for (DeveloperCmsAtom cmsAtom : selectedEnabledAtoms) {
				enabledAtoms.remove(cmsAtom);
				availableAtoms.add(cmsAtom);
				cmsWebsite.getEnabledCmsAtomList().remove(cmsAtom);
				cmsWebsite.saveDetails();
				disabledCount++;
			}
			JSFUtil.addMessageForError(disabledCount + " classes disabled.");
		} else {
			JSFUtil.addMessageForError("No classes are selected to be disabled");
		}
		selectedEnabledAtoms = null;
		return null;
	}

	public void setEnabledAtoms(List<CmsAtom> enabledAtoms) {
		this.enabledAtoms = enabledAtoms;
	}

	public List<CmsAtom> getEnabledAtoms() {
		return enabledAtoms;
	}

	public void setAvailableAtoms(List<DeveloperCmsAtom> availableAtoms) {
		this.availableAtoms = availableAtoms;
	}

	public List<DeveloperCmsAtom> getAvailableAtoms() {
		return availableAtoms;
	}

	public void setSelectedEnabledAtoms(List<DeveloperCmsAtom> selectedEnabledAtoms) {
		this.selectedEnabledAtoms = selectedEnabledAtoms;
	}

	public List<DeveloperCmsAtom> getSelectedEnabledAtoms() {
		return selectedEnabledAtoms;
	}

	public void setSelectedAvailableAtoms(List<DeveloperCmsAtom> selectedAvailableAtoms) {
		this.selectedAvailableAtoms = selectedAvailableAtoms;
	}

	public List<DeveloperCmsAtom> getSelectedAvailableAtoms() {
		return selectedAvailableAtoms;
	}

	public SelectItem[] getEnabledClassSelectItems() {
		return AplosBean.getSelectItemBeans(enabledAtoms);
	}

	public SelectItem[] getAvailableClassSelectItems() {
		return AplosBean.getSelectItemBeans(availableAtoms);
	}

}
