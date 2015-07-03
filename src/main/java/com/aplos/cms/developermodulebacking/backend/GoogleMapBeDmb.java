package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GoogleMapModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.ApplicationUtil;

@ManagedBean
@ViewScoped
public class GoogleMapBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -4114068213238223238L;
	private GoogleMapModule googleMapAtom;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setGoogleMapAtom((GoogleMapModule) developerCmsAtom);
		return true;
	}

	public void setGoogleMapAtom(GoogleMapModule googleMapAtom) {
		this.googleMapAtom = googleMapAtom;
	}

	public GoogleMapModule getGoogleMapAtom() {
		return googleMapAtom;
	}

	public SelectItem[] getCountrySelectItemBeans() {
		SelectItem[] countryItems = ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
		SelectItem[] countryItemsWithNotSelected = new SelectItem[countryItems.length+1];
		countryItemsWithNotSelected[0] = new SelectItem(null,"Not Selected");
		for (int i=0; i < countryItems.length; i++) {
			countryItemsWithNotSelected[i+1] = countryItems[i];
		}
		return countryItemsWithNotSelected;
	}

}
