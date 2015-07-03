package com.aplos.cms.beans;

import com.aplos.cms.beans.developercmsmodules.ContentBoxModule;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.LabeledEnumInter;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;

@Entity
@PluralDisplayName(name="content boxes")
@DynamicMetaValueKey(oldKey="CONTENT_BOX")
public class ContentBox extends AplosBean implements Comparable<ContentBox>, FileDetailsOwnerInter {
	private static final long serialVersionUID = -3046647178180052476L;

	private String name;
	@Column(columnDefinition="LONGTEXT")
	private String text;

//	private String imageUrl;
	
	@ManyToOne
	private FileDetails imageDetails;

	private String imageTitle;

	private ContentBoxLayout contentBoxLayout;

	@ManyToOne
	private ContentBoxModule contentBoxModule;

	private Integer positionIdx;

	private HeadingLevel headingLevel;

	@Transient
	private ContentBoxFdoh contentBoxFdoh = new ContentBoxFdoh(this);

	@Transient
	private String headingString;

	public enum ContentBoxLayout implements LabeledEnumInter {

		IMAGE_TO_THE_LEFT("Image to the left", "contentBoxImageToTheLeft"),
		IMAGE_TO_THE_RIGHT("Image to the right", "contentBoxImageToTheRight");

		private String name;
		private String cssClassName;

		private ContentBoxLayout(String name, String cssClassName) {
			this.name = name;
			this.cssClassName = cssClassName;
		}

		@Override
		public String getLabel() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setCssClassName(String cssClassName) {
			this.cssClassName = cssClassName;
		}

		public String getCssClassName() {
			return cssClassName;
		}
	}

	public enum HeadingLevel implements LabeledEnumInter {
		HEADING_1("Heading 1", 1),
		HEADING_2("Heading 2", 2),
		HEADING_3("Heading 3", 3);

		private String name;
		private int position;

		private HeadingLevel(String name, int position) {
			this.name = name;
		}
		@Override
		public String getLabel() {
			return name;
		}
		public int getPosition() {
			return position;
		}
		public int compare(HeadingLevel headingLevel) {
			if (position < headingLevel.getPosition()) {
				return -1;
			}
			else if (position > headingLevel.getPosition()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

	public ContentBox() {
		this.contentBoxLayout = ContentBoxLayout.IMAGE_TO_THE_RIGHT;
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return contentBoxFdoh;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, ContentBoxImageKey.values(), currentUser);
	}

	public String getFullImageUrl() {
		return getFullImageUrl( true, false );
	}

	public String getFullImageUrl( boolean addContextPath, boolean addRandom ) {
		if( imageDetails != null ) {
			return imageDetails.getFullFileUrl(addContextPath);
		} else {
			return null;
		}
	}

	public String getImageUrlMediaPath() {
		if( imageDetails != null ) {
			return imageDetails.getFullFileUrl(true);
		} else {
			return null;
		}
	}

	public String getText() {
		return text;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setContentBoxLayout(ContentBoxLayout contentBoxLayout) {
		this.contentBoxLayout = contentBoxLayout;
	}

	public ContentBoxLayout getContentBoxLayout() {
		return contentBoxLayout;
	}

	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	public Integer getPositionIdx() {
		return positionIdx;
	}

	public ContentBoxModule getContentBoxModule() {
		return contentBoxModule;
	}

	public void setContentBoxModule(ContentBoxModule contentBoxModule) {
		this.contentBoxModule = contentBoxModule;
	}

	@Override
	public int compareTo(ContentBox o) {
		if (positionIdx > o.positionIdx) {
			return 1;
		}
		else if (positionIdx < o.positionIdx) {
			return -1;
		}
		else {
			return 0;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setHeadingLevel(HeadingLevel headingLevel) {
		this.headingLevel = headingLevel;
	}

	public HeadingLevel getHeadingLevel() {
		return headingLevel;
	}

	public void setHeadingString(String headingString) {
		this.headingString = headingString;
	}

	public String getHeadingString() {
		return headingString;
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}
	
	public enum ContentBoxImageKey {
		IMAGE;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class ContentBoxFdoh extends SaveableFileDetailsOwnerHelper {
		public ContentBoxFdoh( ContentBox contentBox ) {
			super( contentBox );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (ContentBoxImageKey.IMAGE.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.CONTENTBOX_IMAGE_DIR.getDirectoryPath(includeServerWorkPath) + getAplosBean().getClass().getSimpleName() + "/";
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (ContentBoxImageKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( ContentBoxImageKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}
	
}
