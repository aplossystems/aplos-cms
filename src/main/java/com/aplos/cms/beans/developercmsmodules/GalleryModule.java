package com.aplos.cms.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.GalleryImage;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.SystemUser;

@Entity
@PluralDisplayName(name="image gallery modules")
@DynamicMetaValueKey(oldKey="GALLERY_MODULE")
public class GalleryModule extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = -7194467545955979258L;

	@OneToMany(mappedBy = "galleryModule")
	@Cascade({CascadeType.ALL})
	@IndexColumn(name="position")
	private List<GalleryImage> galleryImages = new ArrayList<GalleryImage>();
	private int numberOfColumns = 3;
	@OneToMany
	@Cascade(CascadeType.ALL)
	private List<GalleryCategory> categories = new ArrayList<GalleryCategory>();
	private boolean isOpeningInGallery;
	private boolean isShowingTitle = false;
	private boolean isIncludingCategories = false;
	private Integer maxImageWidth;
	private Integer maxImageHeight;
	
	static {
		CmsConfiguration cmsConfiguration = CmsConfiguration.getCmsConfiguration();
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "default" );
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "list" );
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "scroller" );
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "slideshow" );
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "touch_lightbox" );
		cmsConfiguration.addConfigurationCmsAtomView( GalleryModule.class, "scroller2" );
	}
	
	public GalleryModule() {
		setViewExtension("default");
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(galleryImages, fullInitialisation);
//		HibernateUtil.initialiseList(categories, fullInitialisation);
//	}
	
	public String getFullFileUrl( GalleryImage galleryImage ) {
		if( galleryImage != null && galleryImage.getImageDetails() != null ) {
			StringBuffer strBuf = new StringBuffer( galleryImage.getImageDetails().getFullFileUrl( true ) );
			if( getMaxImageWidth() != null ) {
				strBuf.append( "&maxWidth=" ).append( getMaxImageWidth() );
			}
			if( getMaxImageHeight() != null ) {
				strBuf.append( "&maxHeight=" ).append( getMaxImageHeight() );
			}
			return strBuf.toString();
		} else {
			return null;
		}
	}
	
	@Override
	public void saveBean(SystemUser currentUser) {
		// TODO Auto-generated method stub
		super.saveBean(currentUser);
	}

	@Override
	public String getName() {
		return "Image Gallery";
	}

	@Override
	public boolean initFrontend(boolean isRequestPageLoad) {
		super.initFrontend(isRequestPageLoad);
//		this.hibernateInitialise( true );
		this.addToScope();
		return true;
	}

	@Override
	public boolean initBackend() {
		super.initBackend();
//		this.hibernateInitialise( true );
		this.addToScope();
		return true;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		GalleryModule galleryModule = new GalleryModule();
		galleryModule.setGalleryImages(galleryImages);
		return galleryModule;
	}

	public void setGalleryImages(List<GalleryImage> galleryImages) {
		this.galleryImages = galleryImages;
	}

	public List<GalleryImage> getGalleryImages() {
		return galleryImages;
	}
	
	public String getImageContainerStyle() {
		if( getMaxImageHeight() != null ) {
			return "height:" + (getMaxImageHeight() + 10) + "px;";
		}
		return "";
	}

	public List<GalleryImage> getCategorisedGalleryImages( GalleryCategory galleryCategory ) {
		List<GalleryImage> categorisedGalleryImages = new ArrayList<GalleryImage>();
		if( galleryCategory == null ) {
			for( GalleryImage tempGalleryImage : getGalleryImages() ) {
				if( tempGalleryImage.getGalleryCategory() == null ) {
					categorisedGalleryImages.add( tempGalleryImage );
				}
			}
		} else {
			for( GalleryImage tempGalleryImage : getGalleryImages() ) {
				if( galleryCategory.equals( tempGalleryImage.getGalleryCategory() ) ) {
					categorisedGalleryImages.add( tempGalleryImage );
				}
			}
		}
		return categorisedGalleryImages;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setCategories(List<GalleryCategory> categories) {
		this.categories = categories;
	}

	public List<GalleryCategory> getCategories() {
		return categories;
	}

	public boolean isOpeningInGallery() {
		return isOpeningInGallery;
	}

	public void setOpeningInGallery(boolean isOpeningInGallery) {
		this.isOpeningInGallery = isOpeningInGallery;
	}

	public boolean isShowingTitle() {
		return isShowingTitle;
	}

	public void setShowingTitle(boolean isShowingTitle) {
		this.isShowingTitle = isShowingTitle;
	}

	public boolean isIncludingCategories() {
		return isIncludingCategories;
	}

	public void setIncludingCategories(boolean isIncludingCategories) {
		this.isIncludingCategories = isIncludingCategories;
	}

	public Integer getMaxImageWidth() {
		return maxImageWidth;
	}

	public void setMaxImageWidth(Integer maxImageWidth) {
		this.maxImageWidth = maxImageWidth;
	}

	public Integer getMaxImageHeight() {
		return maxImageHeight;
	}

	public void setMaxImageHeight(Integer maxImageHeight) {
		this.maxImageHeight = maxImageHeight;
	}

}
