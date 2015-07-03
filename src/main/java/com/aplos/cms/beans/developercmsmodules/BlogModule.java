package com.aplos.cms.beans.developercmsmodules;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="BLOG_MODULE")
public class BlogModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -4700635805843319080L;

	private Boolean includeRecentPostsMenu=true;
	private Boolean includeRecentPostsTitle=true;
	private Boolean includeArchivedPostsMenu=true;
	private Boolean includeArchivedPostsTitle=true;
	private Boolean generateRss=true;
	private String rssFeedName="Blog RSS Feed";
	private String rssFeedMapping="blog-feed";
	private String rssFeedDescription="Keep up to date with our latest happenings as they are posted on our blog";

	public BlogModule() {
		super();
	}

	@Override
	public String getName() {
		return "Blog with Archive";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		BlogModule copiedAtom = new BlogModule();
		copiedAtom.setIncludeRecentPostsMenu(includeRecentPostsMenu);
		copiedAtom.setIncludeArchivedPostsMenu(includeArchivedPostsMenu);
		copiedAtom.setIncludeRecentPostsTitle(includeRecentPostsTitle);
		copiedAtom.setIncludeArchivedPostsTitle(includeArchivedPostsTitle);
		copiedAtom.setGenerateRss(generateRss);
		copiedAtom.setRssFeedMapping(rssFeedMapping);
		copiedAtom.setRssFeedName(rssFeedName);
		return copiedAtom;
	}

	public void setIncludeRecentPostsMenu(Boolean includeRecentPostsMenu) {
		this.includeRecentPostsMenu = includeRecentPostsMenu;
	}

	public Boolean getIncludeRecentPostsMenu() {
		return includeRecentPostsMenu;
	}

	public void setIncludeRecentPostsTitle(Boolean includeRecentPostsTitle) {
		this.includeRecentPostsTitle = includeRecentPostsTitle;
	}

	public Boolean getIncludeRecentPostsTitle() {
		return includeRecentPostsTitle;
	}

	public void setIncludeArchivedPostsTitle(Boolean includeArchivedPostsTitle) {
		this.includeArchivedPostsTitle = includeArchivedPostsTitle;
	}

	public Boolean getIncludeArchivedPostsTitle() {
		return includeArchivedPostsTitle;
	}

	public void setIncludeArchivedPostsMenu(Boolean includeArchivedPostsMenu) {
		this.includeArchivedPostsMenu = includeArchivedPostsMenu;
	}

	public Boolean getIncludeArchivedPostsMenu() {
		return includeArchivedPostsMenu;
	}

	public void setGenerateRss(Boolean generateRss) {
		this.generateRss = generateRss;
	}

	public Boolean getGenerateRss() {
		return generateRss;
	}

	public void setRssFeedName(String rssFeedName) {
		this.rssFeedName = rssFeedName;
	}

	public String getRssFeedName() {
		return rssFeedName;
	}

	public void setRssFeedMapping(String rssFeedMapping) {
		this.rssFeedMapping = rssFeedMapping;
	}

	public String getRssFeedMapping() {
		return rssFeedMapping;
	}

	public void setRssFeedDescription(String rssFeedDescription) {
		this.rssFeedDescription = rssFeedDescription;
	}

	public String getRssFeedDescription() {
		return rssFeedDescription;
	}

}
