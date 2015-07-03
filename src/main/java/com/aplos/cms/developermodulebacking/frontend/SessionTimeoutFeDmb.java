package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class SessionTimeoutFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -3179150207366579200L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		JSFUtil.getResponse().setStatus( 401 ); //http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
		return true;
	}

}
