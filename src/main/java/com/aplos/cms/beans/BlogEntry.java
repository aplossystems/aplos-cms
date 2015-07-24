package com.aplos.cms.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.developercmsmodules.BlogModule;
import com.aplos.cms.interfaces.RssFeedContent;
import com.aplos.cms.utils.CmsUtil;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ImageUtil;
import com.aplos.common.utils.XmlEntityUtil;

@Entity
@ManagedBean
@SessionScoped
@PluralDisplayName(name="blog entries")
@DynamicMetaValueKey(oldKey="BLOG_ENTRY")
public class BlogEntry extends AplosBean implements RssFeedContent, FileDetailsOwnerInter {
	private static final long serialVersionUID = 6895748606006614676L;

	private String title;
	@Column(columnDefinition="LONGTEXT")
	private String content;
	private Date showOnWebsiteDate = new Date();

	@ManyToOne
	private FileDetails imageDetails;

	@Transient
	private BlogEntryFdoh blogEntryFdoh = new BlogEntryFdoh(this);
	
	public enum BlogEntryImageKey {
		IMAGE;
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return blogEntryFdoh;
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		FileDetails.saveFileDetailsOwner(this, BlogEntryImageKey.values(), currentUser);
		BeanDao feedDao = new BeanDao(BlogModule.class);
		feedDao.setWhereCriteria("bean.generateRss=1");
		List<BlogModule> blogModules = feedDao.setIsReturningActiveBeans(true).getAll();
		BeanDao generatorDao = new BeanDao(this.getClass());
		generatorDao.setMaxResults(10);
		generatorDao.setOrderBy("bean.dateCreated DESC");
		for (BlogModule blogModule : blogModules) {
			//TODO: "/" is the location the full articles can be found at, ie blog, currently i have no way to get that from here without hard-coding it
			CmsUtil.generateRssFeed(generatorDao, blogModule.getRssFeedName(), blogModule.getRssFeedMapping(), "/", blogModule.getRssFeedDescription(), null);
		}
	}
	
	public String getTitleForGsd() {
		String titleForGsd = CommonUtil.getStringOrEmpty( getTitle() );
		titleForGsd = titleForGsd.replace( "\"", "\\\"" );
		return titleForGsd;
	}
	
	public String getShortDescriptionForGsd() {
		String shortDescriptionForGsd = CommonUtil.getStringOrEmpty( getMaxContent( 100 ) );
		shortDescriptionForGsd = shortDescriptionForGsd.replace( "\"", "\\\"" );
		return shortDescriptionForGsd;
	}
	
	public String getDateCreatedShortStr() {
		SimpleDateFormat shortDateFormat = new SimpleDateFormat( "d MMM" );
		return shortDateFormat.format( getDateCreated() );
	}

	public String getImageUrlWithoutContext() {
		return getFullImageUrl(false);
	}

	public String getFullImageUrl() {
		return getFullImageUrl(true);
	}

	private String getFullImageUrl(boolean addContext) {
		return ImageUtil.getFullFileUrl( imageDetails, addContext );
	}
	
	public String getMaxContent( int maxChars ) {
		if( maxChars > getContent().length() ) {
			return getContent();
		} else {
			return getContent().substring( 0, maxChars ) + "...";
		}
	}

	@Override
	public String getDisplayName() {
		return getTitle();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String getFeedContentTitle() {
		return getTitle();
	}

	@Override
	public String getFeedContentExcerpt() {
		return XmlEntityUtil.stripTags(getContent());
	}

	@Override
	public String getFeedContentUrlRelative() {
		return null; //use feed parent url
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}
	
	@Override
	public Date getDateCreated() {
		// TODO Auto-generated method stub
		return super.getDateCreated();
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	public Date getShowOnWebsiteDate() {
		return showOnWebsiteDate;
	}

	public void setShowOnWebsiteDate(Date showOnWebsiteDate) {
		this.showOnWebsiteDate = showOnWebsiteDate;
	}

	private class BlogEntryFdoh extends SaveableFileDetailsOwnerHelper {
		public BlogEntryFdoh( BlogEntry blogEntry ) {
			super( blogEntry );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BlogEntryImageKey.IMAGE.name().equals(fileDetailsKey)) {
				return CommonWorkingDirectory.LANGUAGE_ICONS.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BlogEntryImageKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);		
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( BlogEntryImageKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}
	
}
