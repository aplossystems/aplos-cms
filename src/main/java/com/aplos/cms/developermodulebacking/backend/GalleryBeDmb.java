package com.aplos.cms.developermodulebacking.backend;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.GalleryImage;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GalleryCategory;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class GalleryBeDmb extends DeveloperModuleBacking {
	/**
	 *
	 */
	private static final long serialVersionUID = -8623658565659456579L;
	private String galleryCategoryName;
	private List<SelectItem> categorySelectItems;
	private GalleryImage newGalleryImage = new GalleryImage();
	private int previousIdx;
	private int newIdx;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( getNewGalleryImage() != null && getNewGalleryImage().getGalleryModule() == null) {
			getNewGalleryImage().setGalleryModule((GalleryModule)getDeveloperCmsAtom());
		}
		createCategorySelectItems();
		return true;
	}
	
	public void deleteImage() {
		GalleryImage deleteGalleryImage = (GalleryImage) JSFUtil.getBeanFromRequest("tableBean");
		deleteImage(deleteGalleryImage);
	}

	public void deleteImage( GalleryImage deleteGalleryImage ) {
		deleteGalleryImage.setGalleryModule(null);
		deleteGalleryImage.saveDetails();
		GalleryModule galleryModule = (GalleryModule)getDeveloperCmsAtom();
		galleryModule.getGalleryImages().remove(deleteGalleryImage);
		galleryModule.saveDetails();
	}
	
	public void postionUpdated() {
		GalleryModule galleryModule = (GalleryModule)getDeveloperCmsAtom();
		
		List<GalleryImage> list = galleryModule.getGalleryImages();
		GalleryImage galleryImage = list.remove( previousIdx );
		list.add( newIdx, galleryImage );
	}
	
	public int getPreviousIdx() {
		return previousIdx;
	}
	public void setPreviousIdx(int previousIdx) {
		this.previousIdx = previousIdx;
	}
	public int getNewIdx() {
		return newIdx;
	}
	public void setNewIdx(int newIdx) {
		this.newIdx = newIdx;
	}

	public void addGalleryCategory() {
		GalleryModule galleryModule = (GalleryModule) JSFUtil.getBeanFromScope(GalleryModule.class);
		GalleryCategory galleryCategory = new GalleryCategory( galleryCategoryName );
		galleryModule.getCategories().add( galleryCategory );
		galleryCategory.saveDetails();
		galleryModule.saveDetails();
	}

	public void removeGalleryCategory() {
		GalleryCategory galleryCategory = JSFUtil.getBeanFromRequest( "tableBean" );
		GalleryModule galleryModule = (GalleryModule) JSFUtil.getBeanFromScope(GalleryModule.class);
		galleryModule.getCategories().remove( galleryCategory );
		galleryModule.saveDetails();
		galleryCategory.setActive( false );
		galleryCategory.saveDetails();
	}


	public void createCategorySelectItems() {
		GalleryModule galleryModule = (GalleryModule) JSFUtil.getBeanFromScope(GalleryModule.class);
		categorySelectItems = Arrays.asList( AplosAbstractBean.getSelectItemBeansWithNotSelected( galleryModule.getCategories(), "Not Selected" ) );
	}

	public List<SelectItem> getCategorySelectItems() {
		return categorySelectItems;
	}

	public void addNewImage() {
		FileDetails fileDetails = getNewGalleryImage().getFileDetailsOwnerHelper().determineFileDetails( GalleryImage.GalleryImageFileDetailsKey.IMAGE.name(), null );
		if (getNewGalleryImage().getImageDetails() != null || (fileDetails != null && fileDetails.getUploadedFile() != null) ) {
			GalleryModule galleryModule = (GalleryModule) JSFUtil.getBeanFromScope(GalleryModule.class);
			getNewGalleryImage().setGalleryModule(galleryModule);
			getNewGalleryImage().saveDetails();
			galleryModule.getGalleryImages().add(getNewGalleryImage());
			galleryModule.saveDetails();
			setNewGalleryImage(new GalleryImage());
			getNewGalleryImage().setGalleryModule(galleryModule);
		}
		else {
			JSFUtil.addMessage("You have not selected any file.", FacesMessage.SEVERITY_ERROR);
		}
	}

	public void setGalleryCategoryName(String galleryCategoryName) {
		this.galleryCategoryName = galleryCategoryName;
	}

	public String getGalleryCategoryName() {
		return galleryCategoryName;
	}

	public GalleryImage getNewGalleryImage() {
		return newGalleryImage;
	}

	public void setNewGalleryImage(GalleryImage newGalleryImage) {
		this.newGalleryImage = newGalleryImage;
	}

}
