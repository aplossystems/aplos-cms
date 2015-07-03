package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class ContactPageBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 8649005989060491710L;

}
