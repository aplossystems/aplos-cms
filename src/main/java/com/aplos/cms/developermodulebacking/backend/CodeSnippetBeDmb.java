package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class CodeSnippetBeDmb extends DeveloperModuleBacking {


	private static final long serialVersionUID = 2802642337499645124L;

	public CodeSnippetBeDmb() {}

	public CodeSnippet getSnippetById(Long id) {
		CmsPageRevision cmsPageRevsion = JSFUtil.getBeanFromScope(CmsPageRevision.class);
		if (cmsPageRevsion != null && cmsPageRevsion.getCodeSnippetList() != null) {
			for (CodeSnippet codeSnippet : cmsPageRevsion.getCodeSnippetList()) {
				if (codeSnippet.getId() != null && codeSnippet.getId().equals(id)) {
					return codeSnippet;
				}
			}
		}
		
		return null;
		
	}
	
	
}
