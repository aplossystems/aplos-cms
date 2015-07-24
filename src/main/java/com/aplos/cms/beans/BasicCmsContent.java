package com.aplos.cms.beans;

import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@MappedSuperclass
public abstract class BasicCmsContent extends AplosBean implements FileDetailsOwnerInter {
	private String title;
	@Column(columnDefinition="LONGTEXT")
	private String shortDescription;
	@Column(columnDefinition="LONGTEXT")
	private String content;
	@ManyToOne
	private FileDetails imageDetails;
	private boolean isShowingInFrontEnd = true;

	@Transient
	private BasicContentFdoh basicContentFdoh = new BasicContentFdoh(this);

	
	public enum BasicContentFileKey {
		IMAGE;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, BasicContentFileKey.values(), currentUser);
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return basicContentFdoh;
	}
	
	public String getParsedContent() {
		return CommonUtil.includeContextRootInPaths( JSFUtil.getContextPath(), getContent());
	}
	
	public String getTitleForGsd() {
		String titleForGsd = CommonUtil.getStringOrEmpty( getTitle() );
		titleForGsd = titleForGsd.replace( "\"", "\\\"" );
		return titleForGsd;
	}
	
	public String getShortDescriptionForGsd() {
		String shortDescriptionForGsd = CommonUtil.getStringOrEmpty( getShortDescription() );
		shortDescriptionForGsd = shortDescriptionForGsd.replace( "\"", "\\\"" );
		return shortDescriptionForGsd;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public abstract AplosWorkingDirectoryInter getAplosWorkingDirectoryInter();

	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	public FileDetails getImageDetails() {
		return imageDetails;
	}
	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isShowingInFrontEnd() {
		return isShowingInFrontEnd;
	}

	public void setShowingInFrontEnd(boolean isShowingInFrontEnd) {
		this.isShowingInFrontEnd = isShowingInFrontEnd;
	}

	private class BasicContentFdoh extends SaveableFileDetailsOwnerHelper {
		public BasicContentFdoh( BasicCmsContent basicContent ) {
			super( basicContent );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BasicContentFileKey.IMAGE.name().equals(fileDetailsKey)) {
				return getAplosWorkingDirectoryInter().getDirectoryPath(includeServerWorkPath) + getAplosBean().getClass().getSimpleName() + "/";
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BasicContentFileKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( BasicContentFileKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}
}
