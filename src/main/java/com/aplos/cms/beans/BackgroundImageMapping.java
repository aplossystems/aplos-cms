package com.aplos.cms.beans;

import javax.faces.bean.ViewScoped;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.ImageUtil;

@Entity
@ViewScoped
@PluralDisplayName(name="background image mappings")
@DynamicMetaValueKey(oldKey="BACKGROUND_IMG")
public class BackgroundImageMapping extends AplosBean implements FileDetailsOwnerInter {

	private static final long serialVersionUID = 6814300071316366658L;
	private String name;
//	private String imageUrl;
	private	String mapping; // if the category part of a url contains this mapping we use our background
	private int priority = 1;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails backgroundDetails;

	@Transient
	private BackgroundImageMappingFdoh backgroundImageMappingFdoh = new BackgroundImageMappingFdoh(this);

	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return backgroundImageMappingFdoh;
	}
	
	@Override
	public void saveBean( SystemUser currentUser ) {
		FileDetails.saveFileDetailsOwner(this, BackgroundImageKey.values(), currentUser);
	}
	
//	public void hibernateInitialiseAfterCheck(boolean fullInitialisation) {
//		super.hibernateInitialiseAfterCheck(fullInitialisation);
//		HibernateUtil.initialise(getBackgroundDetails(), fullInitialisation);
//	};

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getFullImageUrl( boolean addContextPath ) {
		return ImageUtil.getFullFileUrl( backgroundDetails, addContextPath );
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public FileDetails getBackgroundDetails() {
		return backgroundDetails;
	}

	public void setBackgroundDetails(FileDetails backgroundDetails) {
		this.backgroundDetails = backgroundDetails;
	}

	public enum BackgroundImageKey {
		BACKGROUND;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class BackgroundImageMappingFdoh extends SaveableFileDetailsOwnerHelper {
		public BackgroundImageMappingFdoh( BackgroundImageMapping backgroundImageMapping ) {
			super( backgroundImageMapping );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BackgroundImageKey.BACKGROUND.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.BACKGROUND_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BackgroundImageKey.BACKGROUND.name().equals(fileDetailsKey)) {
				setBackgroundDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( BackgroundImageKey.BACKGROUND.name().equals( fileDetailsKey ) ) {
				return getBackgroundDetails();
			}
			return null;
		}
	}
}
