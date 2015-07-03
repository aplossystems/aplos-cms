package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GoogleMapModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class GoogleMapFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -562081635910362272L;

	//im doing this as i dont know if it will stay module or change to atom
	//this way if we change it in the future we dont have to go through all
	//sites / budget site views to change the el
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

}
