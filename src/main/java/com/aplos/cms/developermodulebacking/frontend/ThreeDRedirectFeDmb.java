package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class ThreeDRedirectFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 2458077897700229633L;
	
	public String getRedirectionUrl() {
		String threeDRedirectUrl = CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway().getThreeDRedirectUrl();
		if( JSFUtil.isLocalHost() ) {
			return JSFUtil.getContextPath() + threeDRedirectUrl;
		} else {
			return threeDRedirectUrl;
		}
				
	}

}



