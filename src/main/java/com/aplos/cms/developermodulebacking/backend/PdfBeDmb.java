package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.UploadedFile;

import com.aplos.cms.beans.developercmsmodules.PdfModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class PdfBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 8260958143825652352L;
	private UploadedFile pdfUploadedFile;

	public void setPdfUploadedFile(UploadedFile pdfUploadedFile) {
		this.pdfUploadedFile = pdfUploadedFile;
	}

	public UploadedFile getPdfUploadedFile() {
		return pdfUploadedFile;
	}

	public void uploadFileAndSaveModule() {
		PdfModule pdfModule = JSFUtil.getBeanFromScope(PdfModule.class);
		pdfModule.saveDetails();
//		if ( CommonUtil.isFileUploaded(pdfUploadedFile) ) {
//			if ( pdfUploadedFile.getFileName().endsWith("pdf")) {
//				pdfModule.setFileName( CommonUtil.uploadFile( pdfUploadedFile, CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath() + "/", pdfModule.getId() ) );
//				pdfModule.aqlSaveDetails();
//			}
//			else {
//				JSFUtil.addMessage("The file you have selected is not a PDF file.", FacesMessage.SEVERITY_ERROR);
//			}
//		}
	}
}
