package com.aplos.cms.beans.developercmsmodules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.servlets.MediaServlet.MediaFileType;
import com.aplos.common.utils.JSFUtil;

@Entity
@DynamicMetaValueKey(oldKey={"FLASH_MODULE","FLASH_MDL"})
public class FlashModule extends ConfigurableDeveloperCmsAtom implements FileDetailsOwnerInter {

	private static final long serialVersionUID = -9003329639071397996L;

//	private String fileName;
//	private String defaultImageName; //displayed when we cant load flash
	private Integer width;
	private Integer height;
	
	@ManyToOne
	private FileDetails fileDetails;
	@ManyToOne
	private FileDetails defaultDetails;

	@Transient
	private FlashModuleFdoh flashModuleFdoh = new FlashModuleFdoh(this);

	@Override
	public String getName() {
		return "Flash Display";
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return flashModuleFdoh;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		FlashModule flashModule = new FlashModule();
		flashModule.setFileDetails(fileDetails);
		flashModule.setDefaultDetails(defaultDetails);
		flashModule.setWidth(width);
		flashModule.setHeight(height);
		return flashModule;
	}

	public String getFullDefaultImageUrl() {
		return getFullDefaultImageUrl(true);
	}

	public String getFullDefaultImageUrl(boolean addContext) {
		if (defaultDetails == null || defaultDetails.getFilename().equals("")) {
			return "";
		} else {
			try {
				String url = "/media/?" + AplosAppConstants.FILE_NAME + "="
				+ CmsWorkingDirectory.FLASH_FILE_DIR.getDirectoryPath(false)
				+ URLEncoder.encode(defaultDetails.getFilename(), "UTF-8") + "&type=" + MediaFileType.IMAGE.name();
				if (addContext) {
					return JSFUtil.getRequest().getContextPath() + url;
				} else {
					return url;
				}
			} catch (UnsupportedEncodingException usee) {
				return "";
			}
		}
	}

	public String getFullFileUrl() {
		if (fileDetails == null || fileDetails.getFilename().equals("")) {
			return "";
		} else {
			return JSFUtil.getRequest().getContextPath() + "/media/?" + AplosAppConstants.FILE_NAME + "=" + getMovieUrl() + "&type=misc";
		}
	}

	public String getMovieUrl() {
		try {
			return CmsWorkingDirectory.FLASH_FILE_DIR.getDirectoryPath(false)
			+ URLEncoder.encode(fileDetails.getFilename(), "UTF-8");
		} catch (UnsupportedEncodingException usee) {
			return "";
		}
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getWidth() {
		return width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getHeight() {
		return height;
	}

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	public FileDetails getDefaultDetails() {
		return defaultDetails;
	}

	public void setDefaultDetails(FileDetails defaultDetails) {
		this.defaultDetails = defaultDetails;
	}
	
	@Override
	public boolean isValidForSave() {
		return this.isNew() && super.isValidForSave();
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, FlashImageKey.values(), currentUser);
	}
	
	public enum FlashImageKey {
		MOVIE,
		DEFAULT;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class FlashModuleFdoh extends SaveableFileDetailsOwnerHelper {
		public FlashModuleFdoh( FlashModule flashModule ) {
			super( flashModule );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (FlashImageKey.MOVIE.name().equals(fileDetailsKey) ||
					FlashImageKey.DEFAULT.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.FLASH_FILE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (FlashImageKey.MOVIE.name().equals(fileDetailsKey)) {
				FlashModule.this.setFileDetails(fileDetails);
			} else if (FlashImageKey.DEFAULT.name().equals(fileDetailsKey)) {
				setDefaultDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( FlashImageKey.MOVIE.name().equals( fileDetailsKey ) ) {
				return FlashModule.this.getFileDetails();
			} else if( FlashImageKey.DEFAULT.name().equals( fileDetailsKey ) ) {
				return getDefaultDetails();
			}
			return null;
		}
	}
	
}
