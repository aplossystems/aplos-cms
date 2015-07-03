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
import com.aplos.common.utils.JSFUtil;

@Entity
@DynamicMetaValueKey(oldKey={"PDF_MODULE","PDF_MDL"})
public class PdfModule extends ConfigurableDeveloperCmsAtom implements FileDetailsOwnerInter {
	private static final long serialVersionUID = 6165705465601958635L;

//	private String fileName;
	private Integer width;
	private Integer height;
	
	@ManyToOne
	private FileDetails fileDetails;

	@Transient
	private PdfModuleFdoh pdfModuleFdoh = new PdfModuleFdoh(this);

	@Override
	public String getName() {
		return "PDF Display";
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return pdfModuleFdoh;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, PdfImageKey.values(), currentUser);
	}
	
	@Override
	public DeveloperCmsAtom getCopy() {
		PdfModule pdfModule = new PdfModule();
		pdfModule.setFileDetails(fileDetails);
		return pdfModule;
	}

	public String getFullFileUrl() {
		if (fileDetails == null || fileDetails.getFilename().equals("")) {
			return "";
		} else {
			try {
				String url = "/media/" + "?" + AplosAppConstants.FILE_NAME + "="
					+ CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath(false)
					+ URLEncoder.encode(fileDetails.getFilename(), "UTF-8") + "&type=misc";

				return JSFUtil.getRequest().getContextPath() + url;
			} catch (UnsupportedEncodingException usee) {
				return "";
			}
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

	//#################
	
	public enum PdfImageKey {
		PDF;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class PdfModuleFdoh extends SaveableFileDetailsOwnerHelper {
		public PdfModuleFdoh( PdfModule pdfModule ) {
			super( pdfModule );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (PdfImageKey.PDF.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (PdfImageKey.PDF.name().equals(fileDetailsKey)) {
				PdfModule.this.setFileDetails(fileDetails);		
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( PdfImageKey.PDF.name().equals( fileDetailsKey ) ) {
				return PdfModule.this.getFileDetails();
			}
			return null;
		}
	}
	
}
