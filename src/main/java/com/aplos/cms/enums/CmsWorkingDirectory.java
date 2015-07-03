package com.aplos.cms.enums;

import com.aplos.common.beans.AplosWorkingDirectory;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.utils.CommonUtil;

public enum CmsWorkingDirectory implements AplosWorkingDirectoryInter{
    B_AND_B_ROOM_IMAGE_DIR ( "bandBroomImages/", false ),
    IMAGE_GALLERY_IMAGE_DIR ( "imageGalleryImages/", false ),
    BLOG_ENTRY_IMAGE_DIR ( "blogEntryImages/", false ),
    BLOG_ENTRY_EDITOR_IMAGE_DIR ( CommonWorkingDirectory.EDITOR_UPLOAD_DIR.getDirectoryPath(false) + "blogEntry/", false ), //TODO: check this editor stuff should be restricted
    CMS_PAGE_REVISION_EDITOR_IMAGE_DIR ( CommonWorkingDirectory.EDITOR_UPLOAD_DIR.getDirectoryPath(false) + "cmsPageRevision/", false ),
    INNER_TEMPLATE_EDITOR_IMAGE_DIR ( CommonWorkingDirectory.EDITOR_UPLOAD_DIR.getDirectoryPath(false) + "innerTemplate/", false ),
	CONTENTBOX_IMAGE_DIR ( "contentBoxImages/", false ),
	NEWSLETTER_FILE_DIR ( "newsletterFiles/", false ),
    EXHIBITION_IMAGE_DIR ( "exhibitionImages/", false ),
    BROCHURE_IMAGE_DIR ( "brochureImages/", false ),
    PDF_FILE_DIR ( "pdfFiles/", false ),
    FLASH_FILE_DIR ( "flashFiles/", false ),
    BACKGROUND_IMAGE_DIR ( "backgroundImages/", false ),
    CMS_GENERATED_VIEW_FILES ( "cmsGenerationViews/", false ),
    CMS_PAGE_REVISION_VIEW_FILES ( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "cmsPageRevisions/", false ),
    INNER_TEMPLATE_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "innerTemplates/", false ),
    TOP_TEMPLATE_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "topLevelTemplates/", false ),
    CMS_USER_MODULE_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "userModules/", false ),
    CSS_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "cssStylesheets/", false ),
    JS_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "jsScripts/", false ),
    RSS_FEEDS("rssFeeds/", false ),
    CODE_SNIPPET_VIEW_FILES( CMS_GENERATED_VIEW_FILES.getDirectoryPath(false) + "codeSnippets/", false ),
    MAILOUT_PDFS ("mailoutPdfs/", false ),
    ARTICLE_IMAGE_DIR ("articleImages/", false ),
    CASE_STUDY_IMAGE_DIR ("caseStudyImages/", false ),
    CONTACT_FORM_DIR ("contactFormFiles/", false );

	String directoryPath;
	boolean restricted;
	private AplosWorkingDirectory aplosWorkingDirectory;

	private CmsWorkingDirectory( String directoryPath, boolean restricted ) {
		this.directoryPath = directoryPath;
		this.restricted = restricted;
	}

	public String getDirectoryPath( boolean includeServerWorkPath ) {
		if( includeServerWorkPath ) {
			return CommonUtil.appendServerWorkPath( directoryPath );
		} else {
			return directoryPath;
		}
	}
	
	public static void createDirectories() {
		for( CmsWorkingDirectory cmsWorkingDirectory : CmsWorkingDirectory.values() ) {
			CommonUtil.createDirectory( cmsWorkingDirectory.getDirectoryPath(true) );
		}
	}
	
	public boolean isRestricted() {
		return restricted;
	}

	public AplosWorkingDirectory getAplosWorkingDirectory() {
		return aplosWorkingDirectory;
	}

	public void setAplosWorkingDirectory(AplosWorkingDirectory aplosWorkingDirectory) {
		this.aplosWorkingDirectory = aplosWorkingDirectory;
	}
	
}

