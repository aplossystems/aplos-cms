package com.aplos.cms.developermodulebacking.frontend;

import java.io.File;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.GalleryImage;
import com.aplos.cms.beans.developercmsmodules.GalleryCategory;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.utils.FileExportUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class GalleryFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -494621843743400890L;

	public List<GalleryImage> getCategorisedGalleryImages() {
		GalleryCategory galleryCategory = JSFUtil.getBeanFromRequest( "galleryCategory" );
		GalleryModule galleryModule = (GalleryModule) getDeveloperCmsAtom();
		return galleryModule.getCategorisedGalleryImages(galleryCategory);
	}

	public String downloadImage() {
		GalleryImage galleryImage = (GalleryImage) JSFUtil.getRequest().getAttribute("galleryImage");
		File file = new File(CmsWorkingDirectory.IMAGE_GALLERY_IMAGE_DIR.getDirectoryPath(true) + "galleryModule" + galleryImage.getGalleryModule().getId() + "/" + galleryImage.getImageDetails().getFilename() );
		FileExportUtil.fileDownload(file.getAbsolutePath(), null);
		return null;
	}
}
