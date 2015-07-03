package com.aplos.cms.module;

import com.aplos.cms.beans.AplosCmsUser;
import com.aplos.cms.beans.Article;
import com.aplos.cms.beans.ArticleComment;
import com.aplos.cms.beans.BackgroundImageMapping;
import com.aplos.cms.beans.BlogEntry;
import com.aplos.cms.beans.CaseStudy;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.cms.beans.CompetitionPrize;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.cms.beans.ContactFormSubmission;
import com.aplos.cms.beans.ContentBox;
import com.aplos.cms.beans.Exhibition;
import com.aplos.cms.beans.GalleryImage;
import com.aplos.cms.beans.HostedVideo;
import com.aplos.cms.beans.JobOffer;
import com.aplos.cms.beans.Mailout;
import com.aplos.cms.beans.MailoutTemplate;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.PositionedGeneratorMenuItem;
import com.aplos.cms.beans.Publication;
import com.aplos.cms.beans.Testimonial;
import com.aplos.cms.beans.Timetable;
import com.aplos.cms.beans.bookings.BandBBooking;
import com.aplos.cms.beans.bookings.BandBRoom;
import com.aplos.cms.beans.bookings.BandBRoomType;
import com.aplos.cms.beans.bookings.RoomRates;
import com.aplos.cms.beans.customforms.CheckboxElement;
import com.aplos.cms.beans.customforms.DropDownElement;
import com.aplos.cms.beans.customforms.FileUploadElement;
import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormElement;
import com.aplos.cms.beans.customforms.FormRecord;
import com.aplos.cms.beans.customforms.InputTextElement;
import com.aplos.cms.beans.customforms.InputTextareaElement;
import com.aplos.cms.beans.customforms.ListBoxElement;
import com.aplos.cms.beans.customforms.MultipleChoiceElement;
import com.aplos.cms.beans.customforms.RadioElement;
import com.aplos.cms.beans.developercmsmodules.ArticleCmsAtom;
import com.aplos.cms.beans.developercmsmodules.BlogModule;
import com.aplos.cms.beans.developercmsmodules.CaseStudyCmsAtom;
import com.aplos.cms.beans.developercmsmodules.CompetitionModule;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ContactPageCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ContentBoxModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ExhibitionCmsAtom;
import com.aplos.cms.beans.developercmsmodules.FlashModule;
import com.aplos.cms.beans.developercmsmodules.GalleryCategory;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GoogleMapModule;
import com.aplos.cms.beans.developercmsmodules.HostedVideoCmsAtom;
import com.aplos.cms.beans.developercmsmodules.JobOfferCmsAtom;
import com.aplos.cms.beans.developercmsmodules.PdfModule;
import com.aplos.cms.beans.developercmsmodules.TestimonialsModule;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsAtomPassThrough;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageAlias;
import com.aplos.cms.beans.pages.CmsPageFolder;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.beans.pages.CmsPageLink;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.JavascriptResource;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.cms.templates.emailtemplates.ArticleCommentAddedEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionConsolationEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionNonClaimantEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionWinnerEmail;
import com.aplos.cms.templates.emailtemplates.ContactEmail;
import com.aplos.cms.utils.CmsUserLevelUtil;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.ModuleDbConfig;
import com.aplos.common.persistence.PersistentApplication;

public class CmsModuleDbConfig extends ModuleDbConfig {
	
	private boolean isCustomFormsUsed = false;
	private boolean isCompetitionUsed = false;
	private boolean isBookingUsed = false;

	public CmsModuleDbConfig( AplosModuleImpl aplosModule ) {
		super(aplosModule);
	}

//	@Override
//	public void includeAllTables() {
//		setCustomFormsUsed( true );
//	}

	@Override
	public void addAnnotatedClass(PersistentApplication persistentApplication) {
    	persistentApplication.addPersistentClass( AplosCmsUser.class, true );
    	persistentApplication.addPersistentClass( BlogEntry.class, true );
    	persistentApplication.addPersistentClass( CmsAtomPassThrough.class, true );
    	persistentApplication.addPersistentClass( CmsConfiguration.class, true );
    	persistentApplication.addPersistentClass( CmsPage.class, true );
    	persistentApplication.addPersistentClass( CmsPageRevision.class, true );
    	persistentApplication.addPersistentClass( CmsPageAlias.class, true );
    	persistentApplication.addPersistentClass( CmsPageLink.class, true );
    	persistentApplication.addPersistentClass( CmsPageFolder.class, true );
    	persistentApplication.addPersistentClass( CmsPageGenerator.class, true );
    	persistentApplication.addPersistentClass( CmsPlaceholderContent.class, true );
    	persistentApplication.addPersistentClass( ConfigurableDeveloperCmsAtom.class, true );
    	persistentApplication.addPersistentClass( CssResource.class, true );
    	persistentApplication.addPersistentClass( DeveloperCmsAtom.class, true );
    	persistentApplication.addPersistentClass( GalleryImage.class, true );
    	persistentApplication.addPersistentClass( GoogleMapModule.class, true );
    	persistentApplication.addPersistentClass( InnerTemplate.class, true );
    	persistentApplication.addPersistentClass( MenuNode.class, true );
    	persistentApplication.addPersistentClass( Publication.class, true );
    	persistentApplication.addPersistentClass( CmsWebsite.class, true );
    	persistentApplication.addPersistentClass( Timetable.class, true );
    	persistentApplication.addPersistentClass( TopLevelTemplate.class, true );
    	persistentApplication.addPersistentClass( UnconfigurableDeveloperCmsAtom.class, true );
    	persistentApplication.addPersistentClass( UserCmsModule.class, true );
    	persistentApplication.addPersistentClass( CodeSnippet.class, true );
    	persistentApplication.addPersistentClass( HostedVideo.class, true );
    	persistentApplication.addPersistentClass( BlogModule.class, true );
		persistentApplication.addPersistentClass( GalleryImage.class, true );
		persistentApplication.addPersistentClass( GalleryModule.class, true );
		persistentApplication.addPersistentClass( ContentBox.class, true );
		persistentApplication.addPersistentClass( ContentBoxModule.class, true );
		persistentApplication.addPersistentClass( Mailout.class, true );
		persistentApplication.addPersistentClass( MailoutTemplate.class, true );
		persistentApplication.addPersistentClass( PdfModule.class, true );
		persistentApplication.addPersistentClass( FlashModule.class, true );
		persistentApplication.addPersistentClass( ContactEmail.class, true );
		persistentApplication.addPersistentClass( MailRecipientFinder.class, true );
		persistentApplication.addPersistentClass( GalleryCategory.class, true );
		persistentApplication.addPersistentClass( JobOfferCmsAtom.class, true );
		persistentApplication.addPersistentClass( ExhibitionCmsAtom.class, true );
		persistentApplication.addPersistentClass( CmsUserLevelUtil.class, true );
    	persistentApplication.addPersistentClass( PositionedGeneratorMenuItem.class, true );
    	persistentApplication.addPersistentClass( GeneratorMenuCmsAtom.class, true );
    	persistentApplication.addPersistentClass( HostedVideoCmsAtom.class, true );
    	persistentApplication.addPersistentClass( ContactPageCmsAtom.class, true );
    	persistentApplication.addPersistentClass( ContactFormSubmission.class, true );
    	persistentApplication.addPersistentClass( Article.class, true );
    	persistentApplication.addPersistentClass( ArticleComment.class, true );
    	persistentApplication.addPersistentClass( ArticleCmsAtom.class, true );
    	persistentApplication.addPersistentClass( CaseStudy.class, true );
    	persistentApplication.addPersistentClass( CaseStudyCmsAtom.class, true );
		persistentApplication.addPersistentClass( Testimonial.class, true );
		persistentApplication.addPersistentClass( TestimonialsModule.class, true );

		persistentApplication.addPersistentClass( JavascriptResource.class, true );
		persistentApplication.addPersistentClass( JobOffer.class, false );
		persistentApplication.addPersistentClass( Exhibition.class, false );

		persistentApplication.addPersistentClass( BackgroundImageMapping.class, true );
		

    	persistentApplication.addPersistentClass( ArticleCommentAddedEmail.class, true );
    	
    	// interfaces

    	persistentApplication.addPersistentClass( CmsTemplate.class );
    	persistentApplication.addPersistentClass( GeneratorMenuItem.class );
		

    	persistentApplication.addPersistentClass( CheckboxElement.class );
    	persistentApplication.addPersistentClass( DropDownElement.class );
    	persistentApplication.addPersistentClass( FileUploadElement.class );
    	persistentApplication.addPersistentClass( Form.class );
    	persistentApplication.addPersistentClass( FormRecord.class );
    	persistentApplication.addPersistentClass( FormElement.class );
    	persistentApplication.addPersistentClass( InputTextElement.class );
    	persistentApplication.addPersistentClass( InputTextareaElement.class );
    	persistentApplication.addPersistentClass( ListBoxElement.class );
    	persistentApplication.addPersistentClass( MultipleChoiceElement.class );
    	persistentApplication.addPersistentClass( RadioElement.class );
    	
    	if( isCustomFormsUsed() ) {
        	persistentApplication.includePersistentClass( CheckboxElement.class );
        	persistentApplication.includePersistentClass( DropDownElement.class );
        	persistentApplication.includePersistentClass( FileUploadElement.class );
        	persistentApplication.includePersistentClass( Form.class );
        	persistentApplication.includePersistentClass( FormRecord.class );
        	persistentApplication.includePersistentClass( FormElement.class );
        	persistentApplication.includePersistentClass( InputTextElement.class );
        	persistentApplication.includePersistentClass( InputTextareaElement.class );
        	persistentApplication.includePersistentClass( ListBoxElement.class );
        	persistentApplication.includePersistentClass( MultipleChoiceElement.class );
        	persistentApplication.includePersistentClass( RadioElement.class );
    	}

    	persistentApplication.addPersistentClass( CompetitionModule.class );
		persistentApplication.addPersistentClass( CompetitionWinnerEmail.class );
		persistentApplication.addPersistentClass( CompetitionConsolationEmail.class );
		persistentApplication.addPersistentClass( CompetitionNonClaimantEmail.class );
		persistentApplication.addPersistentClass( CompetitionEntry.class );
		persistentApplication.addPersistentClass( Competition.class );
		persistentApplication.addPersistentClass( CompetitionPrize.class );
		persistentApplication.addPersistentClass( CompetitionPrizeWinner.class );

    	if( isCompetitionUsed() ) {
        	persistentApplication.includePersistentClass( CompetitionModule.class );
    		persistentApplication.includePersistentClass( CompetitionWinnerEmail.class );
    		persistentApplication.includePersistentClass( CompetitionConsolationEmail.class );
    		persistentApplication.includePersistentClass( CompetitionNonClaimantEmail.class );
    		persistentApplication.includePersistentClass( CompetitionEntry.class );
    		persistentApplication.includePersistentClass( Competition.class );
    		persistentApplication.includePersistentClass( CompetitionPrize.class );
    		persistentApplication.includePersistentClass( CompetitionPrizeWinner.class );
    	}

    	if( isBookingUsed() ) {
	    	persistentApplication.addPersistentClass( BandBRoom.class, true );
	    	persistentApplication.addPersistentClass( BandBBooking.class, true );
	    	persistentApplication.addPersistentClass( RoomRates.class, true );
	    	persistentApplication.addPersistentClass( BandBRoomType.class, true );
    	}

	}

	public boolean isCustomFormsUsed() {
		return isCustomFormsUsed;
	}

	public void setCustomFormsUsed(boolean isCustomFormsUsed) {
		this.isCustomFormsUsed = isCustomFormsUsed;
	}

	public boolean isCompetitionUsed() {
		return isCompetitionUsed;
	}

	public void setCompetitionUsed(boolean isCompetitionUsed) {
		this.isCompetitionUsed = isCompetitionUsed;
	}

	public boolean isBookingUsed() {
		return isBookingUsed;
	}

	public void setBookingUsed(boolean isBookingUsed) {
		this.isBookingUsed = isBookingUsed;
	}
}
