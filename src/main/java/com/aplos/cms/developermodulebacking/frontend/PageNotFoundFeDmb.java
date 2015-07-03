package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class PageNotFoundFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -3590973923316278275L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		JSFUtil.getResponse().setStatus( 404 );
		return true;
	}

}
