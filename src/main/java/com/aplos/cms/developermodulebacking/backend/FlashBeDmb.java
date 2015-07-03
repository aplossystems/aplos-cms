package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.FlashModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class FlashBeDmb extends DeveloperModuleBacking  {

	private static final long serialVersionUID = 6637005957100104736L;

	public void uploadFileAndSaveModule() {
		FlashModule flashModule = JSFUtil.getBeanFromScope(FlashModule.class);
		flashModule.saveDetails();
//		if( CommonUtil.isFileUploaded(uploadedFlashFile) ) {
//			if ( uploadedFlashFile.getFileName().endsWith("swf") || uploadedFlashFile.getFileName().endsWith("flv") ) {
//				flashModule.setFileName( CommonUtil.uploadFile( uploadedFlashFile, CmsWorkingDirectory.FLASH_FILE_DIR.getDirectoryPath() + "/", flashModule.getId(), "flashFile" ) );
//
//				if ( uploadedDefaultImageBufferedImageFile != null ) {
//					if ( uploadedDefaultImageBufferedImageFile.getName().endsWith("jpg") || uploadedDefaultImageBufferedImageFile.getName().endsWith("png") || uploadedDefaultImageBufferedImageFile.getName().endsWith("gif") || uploadedDefaultImageBufferedImageFile.getName().endsWith("jpeg") ) {
//						flashModule.setDefaultImageName( ImageUtil.saveImageToFile( uploadedDefaultImageBufferedImageFile, CmsWorkingDirectory.FLASH_FILE_DIR.getDirectoryPath(), flashModule.getId(), "defaultImage", 1000, -1 ) );
//						flashModule.aqlSaveDetails();
//					} else {
//						JSFUtil.addMessage("The default image you have selected is not an image file.", FacesMessage.SEVERITY_ERROR);
//					}
//				}
//
//			} else {
//				JSFUtil.addMessage("The file you have selected is not a Flash file.", FacesMessage.SEVERITY_ERROR);
//			}
//		}
	}

}
