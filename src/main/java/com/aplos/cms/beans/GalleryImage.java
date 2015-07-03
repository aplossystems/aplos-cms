package com.aplos.cms.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.developercmsmodules.GalleryCategory;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;

@Entity
@ManagedBean
@SessionScoped
@DynamicMetaValueKey(oldKey="GALLERY_IMAGE")
public class GalleryImage extends AplosBean implements FileDetailsOwnerInter {
	private static final long serialVersionUID = 4912667471262735413L;

	private String title;
	@Column(columnDefinition="LONGTEXT")
	private String description;

	@ManyToOne
	private FileDetails imageDetails;
	@ManyToOne
	private FileDetails thumbnailDetails;
	@ManyToOne
	private GalleryCategory galleryCategory;

	@ManyToOne
	private GalleryModule galleryModule;

	@Transient
	private GalleryImageFdoh galleryImageFdoh = new GalleryImageFdoh(this);
	
	public enum GalleryImageFileDetailsKey {
		IMAGE,
		THUMBNAIL;
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return galleryImageFdoh;
	}

	@Override
	public String getDisplayName() {
		if (getTitle() != null) {
			return getTitle();
		}
		else {
			return "Gallery Image " + getId();
		}
	}
	
	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, GalleryImageFileDetailsKey.values(), currentUser);
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	public String getImageDirectory() {
		return getImageDirectory(true);
	}

	public String getImageDirectory(boolean includeServerWorkPath) {
		StringBuffer dirBuf = new StringBuffer( CmsWorkingDirectory.IMAGE_GALLERY_IMAGE_DIR.getDirectoryPath(includeServerWorkPath) );
		if( galleryModule != null ) {
			dirBuf.append( "galleryModule" ).append( galleryModule.getId() );
		}

		if( !dirBuf.toString().endsWith( "/" ) ) {
			dirBuf.append( "/" );
		}
		return dirBuf.toString();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setGalleryModule(GalleryModule galleryModule) {
		this.galleryModule = galleryModule;
	}

	public GalleryModule getGalleryModule() {
		return galleryModule;
	}

	public void setGalleryCategory(GalleryCategory galleryCategory) {
		this.galleryCategory = galleryCategory;
	}

	public GalleryCategory getGalleryCategory() {
		return galleryCategory;
	}


	public FileDetails getImageDetails() {
		return imageDetails;
	}


	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}


	public FileDetails getThumbnailDetails() {
		return thumbnailDetails;
	}


	public void setThumbnailDetails(FileDetails thumbnailDetails) {
		this.thumbnailDetails = thumbnailDetails;
	}
	
	private class GalleryImageFdoh extends SaveableFileDetailsOwnerHelper {
		public GalleryImageFdoh( GalleryImage galleryImage ) {
			super( galleryImage );
		}
		
		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if( GalleryImageFileDetailsKey.IMAGE.name().equals( fileDetailsKey ) ) {
				setImageDetails( fileDetails );
			} else if( GalleryImageFileDetailsKey.THUMBNAIL.name().equals( fileDetailsKey ) ) {
				setThumbnailDetails( fileDetails );
			}
		}
		
		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( GalleryImageFileDetailsKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			} else if( GalleryImageFileDetailsKey.THUMBNAIL.name().equals( fileDetailsKey ) ) {
				return getThumbnailDetails();
			}
			return null;
		}
		
		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			return getImageDirectory(includeServerWorkPath);
		}
	}
}





