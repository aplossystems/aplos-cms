package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.BlogModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class BlogBeDmb extends DeveloperModuleBacking {

	/**
	 *
	 */
	private static final long serialVersionUID = -7681215198583110294L;
	private BlogModule blogModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setBlogAtom((BlogModule) developerCmsAtom);
		return true;
	}

	public void setBlogAtom(BlogModule blogModule) {
		this.blogModule = blogModule;
	}

	public BlogModule getBlogAtom() {
		return blogModule;
	}

	public String getFeedPath() {
		String domain = JSFUtil.getServerUrl();
		if (!domain.toLowerCase().startsWith("http")) {
			domain = "http://" + domain;
		}
		if (domain.endsWith("/")) {
			domain = domain.substring(0, domain.length() - 1);
		}
		domain += JSFUtil.getContextPath() + "/";
		domain += CmsWorkingDirectory.RSS_FEEDS.getDirectoryPath(false);
		return domain;
	}
}
