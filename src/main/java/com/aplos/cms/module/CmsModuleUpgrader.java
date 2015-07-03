package com.aplos.cms.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aplos.cms.beans.BlogEntry;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.ContactFormSubmission;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.developercmsmodules.ContactPageCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GalleryModule;
import com.aplos.cms.beans.developercmsmodules.TestimonialsModule;
import com.aplos.cms.beans.pages.CmsAtomPassThrough;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.Website;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.CommonModuleUpgrader;
import com.aplos.common.module.ModuleUpgrader;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;

public class CmsModuleUpgrader extends ModuleUpgrader {

	public CmsModuleUpgrader( AplosModuleImpl aplosModule  ) {
		super(aplosModule, CmsConfiguration.class);
	}


	@Override
	protected void upgradeModule() {

		//don't use break, allow the rules to cascade
		switch (getMajorVersionNumber()) {
			case 1:

				switch (getMinorVersionNumber()) {
					case 8:
						switch (getPatchVersionNumber()) {
							case 23:
								upgradeTo1_8_24();
							case 24:
								upgradeTo1_8_25();
							case 25:
								upgradeTo1_8_26();
							case 26:
								upgradeTo1_8_27();
							case 27:
								upgradeTo1_8_28();
							case 28:
								upgradeTo1_8_29();
							case 29:
								upgradeTo1_8_30();
							case 30:
								upgradeTo1_8_31();
							case 31:
								upgradeTo1_8_32();
							case 32:
								upgradeTo1_8_33();
							case 33:
								upgradeTo1_8_34();
							case 34:
								upgradeTo1_8_35();
							default:
								upgradeTo1_9_0();
						}
					case 9:
						switch (getPatchVersionNumber()) {
							case 0:
								upgradeTo1_9_1();
							case 1:
								upgradeTo1_9_2();
							case 2:
								upgradeTo1_9_3();
							case 3:
								upgradeTo1_9_4();
							case 4:
								upgradeTo1_9_5();
							case 5:
								upgradeTo1_9_6();
							case 6:
								upgradeTo1_9_7();
							case 7:
								upgradeTo1_9_8();
							case 8:
								upgradeTo1_9_9();
							case 9:
								upgradeTo1_9_10();
							case 10:
								upgradeTo1_9_11();
							default:
								upgradeTo1_10_0();
						}
					case 10:
						switch (getPatchVersionNumber()) {
							case 0:
								upgradeTo1_10_1();
							case 1:
								upgradeTo1_10_2();
							case 2:
								upgradeTo1_10_3();
							case 3:
								upgradeTo1_10_4();
							case 4:
								upgradeTo1_10_5();
						}
				}
		}
	}
	
	private void upgradeTo1_10_5() {
		setDefault( CmsPage.class, "siteMapPriority", "0.5");
		setPatchVersionNumber(4);
	}
	
	private void upgradeTo1_10_4() {
		dropTable( "mediabank", true );
		dropTable( "gallerymodule_galleryimages", true );
		setPatchVersionNumber(4);
	}
	
	private void upgradeTo1_10_3() {
		dropTable( "gallerymodule_galleryimages", true );
		setPatchVersionNumber(3);
	}
	
	private void upgradeTo1_10_2() {
		ApplicationUtil.executeSql( "UPDATE cmspagerevision_placeholdercontentmap SET mapKey = lower(mapKey)" );
		ApplicationUtil.executeSql( "UPDATE innertemplate_placeholdercontentmap SET mapKey = lower(mapKey)" );
		setPatchVersionNumber(2);
	}
	
	private void upgradeTo1_10_1() {
		dropColumn( ContactPageCmsAtom.class, "selectedView" );
		dropColumn( ContactPageCmsAtom.class, "selectedGalleryView" );
		dropColumn( GalleryModule.class, "selectedGalleryView" );
		dropColumn( TestimonialsModule.class, "randomRotation" );
		setPatchVersionNumber(1);
	}

	private void upgradeTo1_10_0() {
		setMinorVersionNumber(10);
		setPatchVersionNumber(0);
	}

	private void upgradeTo1_9_11() {
		dropColumn( ContactFormSubmission.class, "senderEmailAddress" );
		dropColumn( ContactFormSubmission.class, "senderName" );
		
		setPatchVersionNumber(11);
	}

	private void upgradeTo1_9_10() {
		dropColumn( ContactPageCmsAtom.class, "selectedView" );
		dropColumn( ContactPageCmsAtom.class, "selectedGalleryView" );
		dropColumn( GalleryModule.class, "selectedGalleryView" );
		dropColumn( TestimonialsModule.class, "randomRotation" );
		
		setPatchVersionNumber(10);
	}

	private void upgradeTo1_9_9() {
		setDefault( ContactFormSubmission.class, "isSmsSubscribed", "true", false);
		
		setPatchVersionNumber(9);
	}

	private void upgradeTo1_9_8() {
		ApplicationUtil.executeSql( "UPDATE testimonialsModule SET viewExtension = 'pick one random' WHERE randomRotation = true OR randomRotation IS NULL" );
		ApplicationUtil.executeSql( "UPDATE testimonialsModule SET viewExtension = 'display all' WHERE randomRotation = false" );
		
		setPatchVersionNumber(8);
	}

	private void upgradeTo1_9_7() {
		try {
			ApplicationUtil.executeSql( "UPDATE contactPageCmsAtom SET viewExtension = 'vertical' WHERE selectedView = 0 OR selectedView IS NULL" );
			ApplicationUtil.executeSql( "UPDATE contactPageCmsAtom SET viewExtension = 'horizontal' WHERE selectedView = 1" );
		} catch( Exception ex ) {
			ApplicationUtil.executeSql( "UPDATE contactPageCmsAtom SET viewExtension = 'vertical' WHERE viewExtension IS NULL" );
		}
		
		try {
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'default' WHERE selectedGalleryView = 0 OR selectedGalleryView IS NULL" );
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'list' WHERE selectedGalleryView = 1" );
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'scroller' WHERE selectedGalleryView = 2" );
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'slideshow' WHERE selectedGalleryView = 3" );
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'touch lightbox' WHERE selectedGalleryView = 4" );
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'scroller2' WHERE selectedGalleryView = 5" );
		} catch( Exception ex ) {
			ApplicationUtil.executeSql( "UPDATE gallerymodule SET viewExtension = 'default' WHERE viewExtension IS NULL" );
		}
		setPatchVersionNumber(7);
	}

	private void upgradeTo1_9_6() {
		setDefault( BlogEntry.class, "showOnWebsiteDate", "'2010-01-01:00:00:00'" );
		setPatchVersionNumber(6);
	}
	
	private void upgradeTo1_9_5() {
		dropColumn( ContactFormSubmission.class, "senderName" );
		setPatchVersionNumber(5);
	}
	
	private void upgradeTo1_9_4() {
		dropColumn( ContactFormSubmission.class, "senderEmailAddress" );
		dropColumn( ContactFormSubmission.class, "senderName" );
		setPatchVersionNumber(4);
	}
	
	private void upgradeTo1_9_3() {
		setDefault( CmsConfiguration.class, "isShowingPageDeveloperOptions", "true");
		setPatchVersionNumber(3);
	}
	
	private void upgradeTo1_9_2() {
		List<Object[]> results = ApplicationUtil.getResults( "SELECT id, senderName, senderEmailAddress FROM ContactFormSubmission" );
		Map<Long, Object[]> resultsMap = new HashMap<Long, Object[]>();
		for( int i = 0, n = results.size(); i < n; i++ ) {
			resultsMap.put( (Long) results.get( i )[ 0 ], results.get( i ) );
		}
		List<ContactFormSubmission> contactFormSubmissions = new BeanDao( ContactFormSubmission.class ).getAll();
		Object[] tempResults;
		String[] tempNameParts;
		for( int i = 0, n = contactFormSubmissions.size(); i < n; i++ ) {
			tempResults = resultsMap.get( contactFormSubmissions.get( i ).getId() );
			Subscriber subscriber = Subscriber.getOrCreateSubscriber((String) tempResults[ 2 ]);
			contactFormSubmissions.get( i ).setSubscriber( subscriber );
			if( subscriber.isNew() ) {
				if( !CommonUtil.isNullOrEmpty( (String) tempResults[ 1 ] ) ) {
					tempNameParts = ((String) tempResults[ 1 ]).split( " " );
					for( int j = 0, p = tempNameParts.length; j < p; j++ ) {
						if( j == 0 ) {
							contactFormSubmissions.get( i ).getSubscriber().setFirstName( tempNameParts[ j ] );
						} else if( j == 1 ) {
							contactFormSubmissions.get( i ).getSubscriber().setSurname( tempNameParts[ j ] );
						} else {
							contactFormSubmissions.get( i ).getSubscriber().setSurname( contactFormSubmissions.get( i ).getSubscriber().getSurname() + " " + tempNameParts[ j ] );
						}
					}
				}
			}
			contactFormSubmissions.get( i ).saveDetails();
		}
		setPatchVersionNumber(2);
	}
	
	private void upgradeTo1_9_1() {
		ApplicationUtil.executeSql( "UPDATE ContactPageCmsAtom SET selectedView = 1, isDuplicateEmailShowing = false WHERE selectedView IS NULL" );
		setPatchVersionNumber(1);
	}
	
	private void upgradeTo1_9_0() {
		List<Website> websiteList = new BeanDao( Website.class ).getAll();
		CmsWebsite cmsWebsite;
		List<MenuNode> rootMenus = new ArrayList<MenuNode>();
		for( int i = 0, n = websiteList.size(); i < n; i++ ) {
			if( websiteList.get( i ) instanceof CmsWebsite ) {
				cmsWebsite = ((CmsWebsite) websiteList.get( i ));
				if( cmsWebsite.getRootMenu() == null ) {
					MenuNode rootMenu = new MenuNode();
					rootMenu.setName( "Root Menu" );
					
					rootMenu.addChild( cmsWebsite.getMainMenu() );
					rootMenu.addChild( cmsWebsite.getSideMenu() );
					rootMenu.addChild( cmsWebsite.getUnusedMenu() );
					rootMenu.addChild( cmsWebsite.getHiddenMenu() );
					rootMenu.addChild( cmsWebsite.getErrorMenu() );
					rootMenu.saveDetails( cmsWebsite );
					cmsWebsite.setRootMenu(rootMenu);
					cmsWebsite.saveDetails();
					rootMenus.add( rootMenu );
				}
			}
		}
		String rootMenuIdStr = CommonUtil.joinIds( rootMenus );
		ApplicationUtil.executeSql( "UPDATE MenuNode SET active = false WHERE parent_id IS NULL AND id NOT IN (" + rootMenuIdStr + ")" );
		setMinorVersionNumber(9);
		setPatchVersionNumber(0);
	}
	
	private void upgradeTo1_8_35() {
		dropColumn( CmsAtomPassThrough.class, "viewId" );
		setPatchVersionNumber(35);
	}
	
	private void upgradeTo1_8_34() {
		CommonModuleUpgrader.renameMetaValueKeys();
		setPatchVersionNumber(34);
	}

	private void upgradeTo1_8_33() {
		ApplicationUtil.executeSql( "UPDATE bulkmessagesourcegroup_bulkmessagesources SET bulk_source_type = 'CompetitionEntry' WHERE bulk_source_type = 'COMPTN_ENTRY" );
		ApplicationUtil.executeSql( "UPDATE bulkmessagesourcegroup_bulkmessagesources SET bulk_source_type = 'CompetitionPrizeWinner' WHERE bulk_source_type = 'COMPTN_PRIZE_WINNER" );
		
		ApplicationUtil.executeSql( "UPDATE aplosemail_messagesourcelist SET message_source_type = 'CompetitionEntry' WHERE message_source_type = 'COMPTN_ENTRY" );
		ApplicationUtil.executeSql( "UPDATE aplosemail_messagesourcelist SET message_source_type = 'CompetitionPrizeWinner' WHERE message_source_type = 'COMPTN_PRIZE_WINNER" );
		
		ApplicationUtil.executeSql( "UPDATE aplosemail SET email_generator_type = 'CompetitionEntry' WHERE email_generator_type = 'COMPTN_ENTRY" );
		ApplicationUtil.executeSql( "UPDATE aplosemail SET email_generator_type = 'CompetitionPrizeWinner' WHERE email_generator_type = 'COMPTN_PRIZE_WINNER" );
		
		setPatchVersionNumber(33);
	}
	
	private void upgradeTo1_8_32() {
		dropColumn( CmsConfiguration.class, "paymentSuccessCpr_id" );
		dropColumn( CmsConfiguration.class, "paymentFailureCpr_id" );
		setPatchVersionNumber(32);
	}

	private void upgradeTo1_8_31() {
		List<CmsWebsite> websiteList = new BeanDao( CmsWebsite.class ).getAll();
		for( int i = 0, n = websiteList.size(); i < n; i++ ) {
			SiteBeanDao cssResourceDao = new SiteBeanDao( CssResource.class );
			cssResourceDao.setWebsiteId( websiteList.get( i ).getId() );
			cssResourceDao.setOrderBy( "bean.id ASC" );
			CssResource cssResource = cssResourceDao.getFirstBeanResult(); 	
			if( cssResource != null ) {
				websiteList.get( i ).setEditorCss( cssResource );
				websiteList.get( i ).saveDetails();
			}
		}
		setPatchVersionNumber(31);
	}
	
	private void upgradeTo1_8_30() {
		dropColumn( CmsConfiguration.class, "exhibitionCpr_id" );
		setPatchVersionNumber(30);
	}
	
	private void upgradeTo1_8_29() {
		List<GalleryModule> galleryModules = new BeanDao( GalleryModule.class ).getAll();
		for( int i = 0, n = galleryModules.size(); i < n; i ++ ) {
			if( galleryModules.get( i ).getCategories().size() > 0 ) {
				galleryModules.get( i ).setIncludingCategories( true );
				galleryModules.get( i ).saveDetails();
			}
		}
		setPatchVersionNumber(29);
	}	
	
	private void upgradeTo1_8_28() {
		ApplicationUtil.executeSql( "UPDATE HostedVideo SET numberOfRelatedVideos = 0 WHERE numberOfRelatedVideos IS NULL" );
		setPatchVersionNumber(28);
	}	
	
	private void upgradeTo1_8_27() {
		ApplicationUtil.executeSql( "UPDATE JavascriptResource SET version = 0 WHERE version IS NULL" );
		ApplicationUtil.executeSql( "UPDATE CssResource SET version = 0 WHERE version IS NULL" );
		
		setPatchVersionNumber(27);
	}	
	
	private void upgradeTo1_8_26() {
		ApplicationUtil.executeSql( "UPDATE gallerymodule SET isOpeningInGallery = true WHERE isOpeningInGallery IS NULL" );
		
		setPatchVersionNumber(26);
	}	
	
	private void upgradeTo1_8_25() {
		ApplicationUtil.executeSql( "UPDATE gallerymodule SET isOpeningInGallery = true WHERE isOpeningInGallery IS NULL" );
		List<CmsPageRevision> cmsPageRevisionList = new BeanDao( CmsPageRevision.class ).setIsReturningActiveBeans(null).getAll();
		for( CmsPageRevision cmsPageRevision : cmsPageRevisionList ) {
			cmsPageRevision.saveDetails();
		}
		
		setPatchVersionNumber(25);
	}	
	
	private void upgradeTo1_8_24() {
		ApplicationUtil.executeSql( "UPDATE JavascriptResource SET version = 0 WHERE version IS NULL" );
		ApplicationUtil.executeSql( "UPDATE CssResource SET version = 0 WHERE version IS NULL" );
		List<TopLevelTemplate> topLevelTemplateList = new BeanDao( TopLevelTemplate.class ).setIsReturningActiveBeans(null).getAll();
		for( TopLevelTemplate tempTopLevelTemplate : topLevelTemplateList ) {
			tempTopLevelTemplate.saveDetails();
		}
		
		List<InnerTemplate> innerTemplateList = new BeanDao( InnerTemplate.class ).setIsReturningActiveBeans(null).getAll();
		for( InnerTemplate tempInnerTemplate : innerTemplateList ) {
			tempInnerTemplate.saveDetails();
		}
		
		setPatchVersionNumber(24);
	}	
	
//	private void upgradeTo1_8_23() {
//		upgrade41F(); //pdfmodule
//		setPatchVersionNumber(22);
//	}	
//	
//	private void upgradeTo1_8_22() {
//		upgrade41A(); //blogentry
//		upgrade41B(); //backgroundimagemapping
//		upgrade41C(); //exhibition
//		upgrade41D(); //contentbox
//		upgrade41E(); //flashmodule
//		setPatchVersionNumber(22);
//	}
//	
//	private void upgrade41F() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, fileName  FROM pdfmodule WHERE fileName IS NOT NULL" ).list();
//		PdfModule tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (PdfModule) new AqlBeanDao( PdfModule.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], PdfImageKey.PDF.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void upgrade41E() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, fileName, defaultImageName  FROM flashmodule WHERE defaultImageName IS NOT NULL OR fileName IS NOT NULL" ).list();
//		FlashModule tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (FlashModule) new AqlBeanDao( FlashModule.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], FlashImageKey.MOVIE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], FlashImageKey.DEFAULT.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void upgrade41D() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, imageUrl FROM contentbox WHERE imageUrl IS NOT NULL" ).list();
//		ContentBox tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (ContentBox) new AqlBeanDao( ContentBox.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], ContentBoxImageKey.IMAGE.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void upgrade41C() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, logoUrl, exhibitionImageUrl, prImage1Url, prImage2Url, prImage3Url FROM exhibition WHERE logoUrl IS NOT NULL OR exhibitionImageUrl IS NOT NULL OR prImage1Url IS NOT NULL OR prImage2Url IS NOT NULL OR prImage3Url IS NOT NULL" ).list();
//		Exhibition tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (Exhibition) new AqlBeanDao( Exhibition.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], ExhibitionImageKey.LOGO.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], ExhibitionImageKey.EXHIBITION.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 3 ], ExhibitionImageKey.PR_ONE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 4 ], ExhibitionImageKey.PR_TWO.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 5 ], ExhibitionImageKey.PR_THREE.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void upgrade41B() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, imageUrl FROM backgroundimagemapping WHERE imageUrl IS NOT NULL" ).list();
//		BackgroundImageMapping tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (BackgroundImageMapping) new AqlBeanDao( BackgroundImageMapping.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BackgroundImageKey.BACKGROUND.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void upgrade41A() {
//		List<Object[]> fileUrls = ApplicationUtil.executeSql( "SELECT id, imageUrl FROM blogentry WHERE imageUrl IS NOT NULL" ).list();
//		BlogEntry tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (BlogEntry) new AqlBeanDao( BlogEntry.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BlogEntryImageKey.IMAGE.name());
//			tempBean.aqlSaveDetails();
//		}
//	}
//	
//	private void putFileDetails(FileDetailsOwnerInter tempBean, String fileUrl, String enumvalue) {
//		if ( !CommonUtil.isNullOrEmpty( fileUrl ) ) {
//			FileDetails tempFileDetails = new FileDetails( tempBean, enumvalue );
//			tempFileDetails.setFilename( fileUrl );
//			tempFileDetails.aqlSaveDetails();
//			tempBean.getFileDetailsOwnerHelper().setFileDetails( tempFileDetails, enumvalue, null );
//		}
//	}
//	
//	
//
//	private void upgradeTo1_8_21() {
//		List<GalleryImage> galleryImages = new AqlBeanDao( GalleryImage.class ).setIsReturningActiveBeans(null).getAll();
//		for( GalleryImage tempGalleryImage : galleryImages ) {
//			Object[] galleryImageUrls = (Object[]) ApplicationUtil.executeSql( "SELECT imageFilename, thumbnailFilename FROM GalleryImage WHERE id = " + tempGalleryImage.getId() ).uniqueResult();
//			if( galleryImageUrls[ 0 ] != null ) {
//				FileDetails fileDetails = new FileDetails();
//				fileDetails.setFilename( (String) galleryImageUrls[ 0 ] );
//				fileDetails.setFileDetailsOwner(tempGalleryImage);
//				fileDetails.setFileDetailsKey(GalleryImage.GalleryImageFileDetailsKey.IMAGE.name());
//				fileDetails.aqlSaveDetails();
//				tempGalleryImage.setImageDetails( fileDetails );
//				tempGalleryImage.aqlSaveDetails();
//			}
//			if( galleryImageUrls[ 1 ] != null ) {
//				FileDetails fileDetails = new FileDetails();
//				fileDetails.setFilename( (String) galleryImageUrls[ 1 ] );
//				fileDetails.setFileDetailsOwner(tempGalleryImage);
//				fileDetails.setFileDetailsKey(GalleryImage.GalleryImageFileDetailsKey.THUMBNAIL.name());
//				fileDetails.aqlSaveDetails();
//				tempGalleryImage.setThumbnailDetails( fileDetails );
//				tempGalleryImage.aqlSaveDetails();
//			}
//		}
//		setPatchVersionNumber(21);
//	}
//
//	private void upgradeTo1_8_20() {
//		ApplicationUtil.executeSql( "ALTER TABLE cmsConfiguration MODIFY newsletterHeader LONGTEXT" );
//		ApplicationUtil.executeSql( "ALTER TABLE cmsConfiguration MODIFY newsletterFooter LONGTEXT" );
//		setPatchVersionNumber(20);
//	}
//	
//	private void upgradeTo1_8_19() {
////		ApplicationUtil.executeSql( "UPDATE newsletter SET isIncludingCommonHeaderAndFooter=false WHERE isIncludingCommonHeaderAndFooter IS NULL" );
////		ApplicationUtil.executeSql( "UPDATE newsletter SET isIncludingNewsletterHeaderAndFooter=true WHERE isIncludingNewsletterHeaderAndFooter IS NULL" );
//		setPatchVersionNumber(19);
//	}
//
//	private void upgradeTo1_8_18() {
//		ApplicationUtil.executeSql( "UPDATE cmsPage SET sslProtocolEnum=3 WHERE sslProtocolEnum IS NULL" );
//		setPatchVersionNumber(18);
//	}
//
//	private void upgradeTo1_8_17() {
//		ApplicationUtil.executeSql( "UPDATE cmsPage SET cachedMappingPath='/' WHERE cachedMappingPath IS NULL" );
//		setPatchVersionNumber(17);
//	}
//
//	private void upgradeTo1_8_16() {
//		List<Website> websiteList = new AqlBeanDao( Website.class ).getAll();
//		for( int i = 0, n = websiteList.size(); i < n; i++ ) {
//			if( websiteList.get( i ) instanceof CmsWebsite ) {
//				CmsWebsite cmsWebsite = (CmsWebsite) websiteList.get( i );
//				CmsPageRevision unsubscribePage = CmsDatabaseLoader.createUnsubscribePage( cmsWebsite, CommonUtil.getAdminUser(), cmsWebsite.getMainTemplate(), cmsWebsite.getHiddenMenu() );
//				cmsWebsite.setUnsubscribePage( unsubscribePage );
//				cmsWebsite.aqlSaveDetails();
//			}
//		}
//		
//		setPatchVersionNumber(16);
//	}
//
//	private void upgradeTo1_8_15() {
//		ApplicationUtil.executeSql( "UPDATE cmsconfiguration SET newsletterFooter=newsletterBottom WHERE newsletterFooter IS NULL" );
//		ApplicationUtil.executeSql( "UPDATE cmsconfiguration SET newsletterHeader=newsletterTop WHERE newsletterHeader IS NULL" );
//		setPatchVersionNumber(15);
//	}
//
//	private void upgradeTo1_8_14() {
//		ApplicationUtil.executeSql( "UPDATE cmsconfiguration SET isDefaultSendNewsletterOnce=false WHERE isDefaultSendNewsletterOnce IS NULL" );
//		setPatchVersionNumber(14);
//	}
//	
//	private void upgradeTo1_8_13() {
//		ApplicationUtil.executeSql( "UPDATE newsletter SET isSendOnlyOnce = true where isSendOnlyOnce IS NULL" );
//		setPatchVersionNumber(13);
//	}
//	
//	private void upgradeTo1_8_12() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE newsletter SET htmlBody = newsletterBody where htmlBody IS NULL" );
//		} catch (Exception e) {}
//		setPatchVersionNumber(12);
//	}
//	
//	private void upgradeTo1_8_11() {
//		ApplicationUtil.executeSql( "UPDATE hostedVideo SET width = 480, height = 300" );
//		setPatchVersionNumber(11);
//	}
//	
//	private void upgradeTo1_8_10() {
//		List<CmsPage> cmsPageList = new AqlBeanDao( CmsPage.class ).getAll();
//		for( CmsPage tempCmsPage : cmsPageList ) {
//			tempCmsPage.updateCachedMappingPath();
//			tempCmsPage.aqlSaveDetails();
//		}
//		setPatchVersionNumber(10);
//	}
//	
//	private void upgradeTo1_8_9() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE cmsPage SET numberOfRowsInColumn = 12 WHERE numberOfRowsInColumn IS NULL" );
//		} catch (Exception e) {}
//		setPatchVersionNumber(9);
//	}
//	
//	private void upgradeTo1_8_8() {
//		ApplicationUtil.executeSql( "UPDATE generatormenucmsAtom SET generationType = 2" );
//		ApplicationUtil.executeSql( "UPDATE generatormenucmsAtom SET generationType = 1 WHERE cmsPageGenerator_id IS NOT NULL" );
//		setPatchVersionNumber(8);
//	}
//	
//	private void upgradeTo1_8_7() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE website SET defaultSslProtocolEnum = 1" );
//		} catch (Exception e) {}
//		setPatchVersionNumber(7);
//	}
//	
//	private void upgradeTo1_8_6() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE generatormenucmsatom SET cmsAtomIdStr = 'generatorMenu' WHERE cmsAtomIdStr = 'productTypeMenu'" );
//		} catch( Exception ex ) {
//			//  this should have been in ecommerce instead
//		}
//		setPatchVersionNumber(6);
//	}
//	
//	private void upgradeTo1_8_5() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE generatormenucmsatom g LEFT OUTER JOIN producttypemenucmsAtom p on g.id = p.id SET g.cmsPageGenerator_id = p.ecommercePageGenerator_id" );
//		} catch( Exception ex ) {
//			//  this should have been in ecommerce instead
//		}
//		setPatchVersionNumber(5);
//	}
//
//	private void upgradeTo1_8_4() {
//		try {
//			StringBuffer strBuf = new StringBuffer();
//			strBuf.append( "INSERT INTO generatormenucmsatom (`id`, `active`, `deletable`," );
//			strBuf.append( "`editable`, `persistentData`, `dateCreated`, `dateInactivated`, `dateLastModified`," );
//			strBuf.append( "`displayId`, `userIdCreated`, `userIdInactivated`, `userIdLastModified`, `name`," );
//			strBuf.append( "`cmsAtomIdStr`, `owner_id`, `parentWebsite_id`, `generatorItemClass`)" );
//			strBuf.append( " SELECT `id`, `active`, `deletable`, `editable`, `persistentData`, `dateCreated`, " );
//			strBuf.append( "`dateInactivated`, `dateLastModified`, `displayId`, `userIdCreated`, `userIdInactivated`, " );
//			strBuf.append( "`userIdLastModified`, `name`, `cmsAtomIdStr`, `owner_id`, `parentWebsite_id`," );
//			strBuf.append( "'com.aplos.ecommerce.beans.ProductType' FROM producttypemenucmsAtom" );
//			ApplicationUtil.executeSql( strBuf.toString() );
//			
//			strBuf = new StringBuffer();
//			strBuf.append( "INSERT INTO positionedgeneratormenuitem (`id`, `active`, `deletable`," );
//			strBuf.append( "`editable`, `persistentData`, `dateCreated`, `dateInactivated`, `dateLastModified`," );
//			strBuf.append( "`displayId`, `userIdCreated`, `userIdInactivated`, `userIdLastModified`," );
//			strBuf.append( "`generatorMenuItem_type`, `generatorMenuItem_id`, `positionIdx`, `owner_id`)" );
//			strBuf.append( " SELECT `id`, `active`, `deletable`, `editable`, `persistentData`, `dateCreated`," );
//			strBuf.append( "`dateInactivated`, `dateLastModified`, `displayId`, `userIdCreated`, `userIdInactivated`," );
//			strBuf.append( "`userIdLastModified`, 'PRODUCT_TYPE', `productType_id`, `positionIdx`," );
//			strBuf.append( "`owner_id` FROM websiteProductTypeMenuItem");
//			ApplicationUtil.executeSql( strBuf.toString() );
//			
//			strBuf = new StringBuffer();
//			strBuf.append( "INSERT INTO generatormenucmsatom_positionedgeneratormenuitem (`GeneratorMenuCmsAtom_id`," );
//			strBuf.append( "`generatorMenuItems_id`) SELECT `ProductTypeMenuCmsAtom_id`," );
//			strBuf.append( "`productTypeMenuItems_id` FROM producttypemenucmsatom_websiteproducttypemenuitem" );
//			ApplicationUtil.executeSql( strBuf.toString() );
//		} catch( Exception ex ) {
//			//  this should have been in ecommerce instead
//		}
//		setPatchVersionNumber(4);
//	}
//
//	private void upgradeTo1_8_3() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE cmsPage SET DTYPE='CmsPageGenerator' WHERE DTYPE='EcommercePageGenerator'" );
//			ApplicationUtil.executeSql( "UPDATE cmsPage SET generatorMenuCmsAtom_id=productTypeMenuCmsAtom_id" );
//		} catch( Exception ex ) {
//			// field probably doesn't exist
//		}
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_8_2() {
//		ApplicationUtil.executeSql( "UPDATE cmspagerevision_cmsatomlist SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		ApplicationUtil.executeSql( "UPDATE cmspagerevision_cmsatompassedthroughmap SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		ApplicationUtil.executeSql( "UPDATE innertemplate_cmsatomlist SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		ApplicationUtil.executeSql( "UPDATE innertemplate_cmsatompassedthroughmap SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		ApplicationUtil.executeSql( "UPDATE usercmsmodule_cmsatomlist SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		ApplicationUtil.executeSql( "UPDATE cmsAtomPassThrough SET cmsAtom_type='GENERATOR_MENU_ATOM' WHERE cmsAtom_type='PRODUCT_TYPE_MENU_ATOM'" );
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_8_1() {
//		ApplicationUtil.executeSql( "UPDATE userLevelUtil SET DTYPE='CmsUserLevelUtil'" );
//		ApplicationUtil.executeSql( "UPDATE userLevelUtil SET basicUserLevel_id = (SELECT basicUserLevel_id from CommonConfiguration LIMIT 1) WHERE basicUserLevel_id IS NULL" );
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_8_0() {
//		//project in maven
//		setMinorVersionNumber(8);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_7_0() {
//		//fork for using files for cms page revisions
//		setMinorVersionNumber(7);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_6_4() {
//		ApplicationUtil.executeSql( "UPDATE competition SET nonClaimantEmailsSent=false WHERE nonClaimantEmailsSent IS NULL" );
//		setPatchVersionNumber(4);
//	}
//
//	private void upgradeTo1_6_3() {
//		ApplicationUtil.executeSql( "UPDATE cmsconfiguration SET paginationItemsPerPage=15 WHERE paginationItemsPerPage IS NULL" );
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_6_2() {
//
//		ApplicationUtil.executeSql( "UPDATE CmsPage SET DTYPE = 'CmsPage' WHERE DTYPE IS NULL" );
//
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_6_1() {
//		ApplicationUtil.executeSql( "UPDATE galleryModule SET selectedGalleryView = 0 WHERE selectedGalleryView IS NULL" );
//
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_6_0() {
//
//		ApplicationUtil.executeSql( "UPDATE " + AplosBean.getTableName(SystemUser.class) + " SET USER_TYPE = 'AplosCmsUser' WHERE USER_TYPE = 'AploraUser'" );
//
//		setMinorVersionNumber(6);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_5_3() {
//
//		try {
//			ApplicationUtil.executeSql( "UPDATE UnconfigurableDeveloperCmsAtom SET cmsAtomIdStr = moduleIdStr WHERE cmsAtomIdStr IS NULL" );
//			Map<Class<?>, PersistentClass> persistentClassMap = ApplicationUtil.getAplosContextListener().getPersistentApplication().getPersistentClassMap();
//			for( Class<?> tempClass : persistentClassMap.keySet() ) {
//				if( ConfigurableDeveloperCmsAtom.class.isAssignableFrom( tempClass ) ) {
//					try {
//						ApplicationUtil.executeSql( "UPDATE " + tempClass.getSimpleName() + " SET cmsAtomIdStr = moduleIdStr WHERE cmsAtomIdStr IS NULL" );
//					} catch( Exception ex ) {
//						//just ignore
//					}
//				}
//			}
//		} catch( Exception ex ) {
//			//just ignore, moduleIdStr wont exist in new projects
//		}
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_5_2() {
//		StringBuffer hqlBuf = new StringBuffer();
//		hqlBuf.append( "INSERT INTO `hostedvideo` (`id`, `active`, `deletable`," );
//		hqlBuf.append( "`persistentData`, `dateCreated`, `dateInactivated`, `dateLastModified`, `displayId`," );
//		hqlBuf.append( " `userIdCreated`, `userIdInactivated`, `userIdLastModified`, `description`, `title`," );
//		hqlBuf.append( " `url`, `videoID`, `owner_id`) " );
//		hqlBuf.append( "SELECT `id`, `active`, `deletable`, `persistentData`, `dateCreated`, `dateInactivated`," );
//		hqlBuf.append( " `dateLastModified`, `displayId`, `userIdCreated`, `userIdInactivated`, `userIdLastModified`," );
//		hqlBuf.append( " `description`, `title`, `url`, `videoID`, `owner_id` FROM hostedvideo" );
//		ApplicationUtil.executeSql( hqlBuf.toString() );
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_5_1() {
//		ApplicationUtil.executeSql( "DELETE FROM cmspagerevision_cmsatomlist WHERE cmsAtom_type='BLOG_OR_NEWS_MODULE'" );
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_5_0() {
//		setMinorVersionNumber(5);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_4_1() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE mailout SET companyDetails_Id = (SELECT defaultCompanyDetails_id FROM commonConfiguration limit 1)" );
//		} catch( Exception ex ) {
//			ex.printStackTrace();
//		}
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_4_0() {
//		setMinorVersionNumber(4);
//		setPatchVersionNumber(0);
//	}
//
//	private CmsModuleDbConfig getCmsModuleDbConfig() {
//		return (CmsModuleDbConfig) getCmsModule().getModuleDbConfig();
//	}
//
//	private CmsModule getCmsModule() {
//		return (CmsModule) getAplosModule();
//	}
//
//	private void upgradeTo1_3_5() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE GalleryModule SET numberOfColumns = columnsNumber" );
//		} catch( Exception ex ) {
//			ex.printStackTrace();
//		}
//
//		setPatchVersionNumber(5);
//	}
//
//	private void upgradeTo1_3_4() {
//		ApplicationUtil.executeSql( "UPDATE CmsConfiguration SET newsletterTop = 'Dear {SUBSCRIBER_NAME},'" );
//		ApplicationUtil.executeSql( "UPDATE CmsConfiguration SET newsletterBottom = ''" );
//
//		setPatchVersionNumber(4);
////		moduleConfiguration.setModuleVersionPatch(4);
//	}
//
//	private void upgradeTo1_3_3() {
//		try {
//			ApplicationUtil.executeSql( "UPDATE CmsPageRevision SET parentWebsite_id = parentSite_id" );
//			ApplicationUtil.executeSql( "UPDATE CmsPage SET parentWebsite_id = parentSite_id" );
//			ApplicationUtil.executeSql( "UPDATE MenuNode SET parentWebsite_id = parentSite_id" );
//			ApplicationUtil.executeSql( "UPDATE InnerTemplate SET parentWebsite_id = parentSite_id" );
//			ApplicationUtil.executeSql( "UPDATE TopLevelTemplate SET parentWebsite_id = parentSite_id" );
//			ApplicationUtil.executeSql( "UPDATE CssResource SET parentWebsite_id = parentSite_id" );
//		} catch( Exception ex ) {
//			try {
//				ApplicationUtil.executeSql( "UPDATE CmsPageRevision SET parentWebsite_id = 1" );
//				ApplicationUtil.executeSql( "UPDATE CmsPage SET parentWebsite_id = 1" );
//				ApplicationUtil.executeSql( "UPDATE MenuNode SET parentWebsite_id = 1" );
//				ApplicationUtil.executeSql( "UPDATE InnerTemplate SET parentWebsite_id = 1" );
//				ApplicationUtil.executeSql( "UPDATE TopLevelTemplate SET parentWebsite_id = 1" );
//				ApplicationUtil.executeSql( "UPDATE CssResource SET parentWebsite_id = 1" );
//			} catch( Exception e ) {
//				e.printStackTrace();
//				// probably because these fields never existed
//			}
//			// probably because these fields never existed
//		}
//
//		setPatchVersionNumber(3);
////		moduleConfiguration.setModuleVersionPatch(3);
//	}
//
//	private void upgradeTo1_3_2() {
//		ApplicationUtil.executeSql( "UPDATE ContentBox SET name = CONCAT('Content Box ',(positionIdx + 1))" );
//
//		setPatchVersionNumber(2);
////		moduleConfiguration.setModuleVersionPatch(2);
//	}
//
//	private void upgradeTo1_3_1() {
//		ApplicationUtil.executeSql( "UPDATE CmsPageRevision SET parentWebsite_id = 1" );
//		ApplicationUtil.executeSql( "UPDATE CmsPage SET parentWebsite_id = 1" );
//		ApplicationUtil.executeSql( "UPDATE MenuNode SET parentWebsite_id = 1" );
//		ApplicationUtil.executeSql( "UPDATE InnerTemplate SET parentWebsite_id = 1" );
//		ApplicationUtil.executeSql( "UPDATE TopLevelTemplate SET parentWebsite_id = 1" );
//		ApplicationUtil.executeSql( "UPDATE CssResource SET parentWebsite_id = 1" );
//
//		setPatchVersionNumber(1);
////		moduleConfiguration.setModuleVersionPatch(1);
//	}
//
//	private void upgradeTo1_3_0() {
//		StringBuffer sql = new StringBuffer( "INSERT INTO website (`id`, `active`, `deletable`, `persistentData`, `dateCreated`," );
//		sql.append( "`dateInactivated`, `dateLastModified`, `displayId`, `userIdCreated`, `userIdInactivated`," );
//		sql.append( "`userIdLastModified`, `name`, `owner_id`," );
//		sql.append( "`WEBSITE_TYPE`, `defaultPageDescription`, `defaultPageKeywords`, `googleAnalyticsId`," );
//		sql.append( "`live`, `holdingPage_id`, `pageNotFoundPage_id`)" );
//
//		sql.append( " SELECT id, active, deletable, persistentData, dateCreated, dateInactivated," );
//		sql.append( "dateLastModified, displayId, userIdCreated, userIdInactivated, userIdLastModified, " );
//		sql.append( "name, owner_id, 'CmsWebsite', defaultPageDescription, defaultPageKeywords," );
//		sql.append( "googleAnalyticsId, live, holdingPage_id, pageNotFoundPage_id FROM Site" );
//
//		ApplicationUtil.executeSql( sql.toString() );
//		HibernateUtil.startNewTransaction();
//		List<Object> siteId = ApplicationUtil.executeSql( "SELECT site_id FROM CmsConfiguration" ).list();
//		ApplicationUtil.executeSql( "UPDATE CmsConfiguration SET cmsWebsite_id = " + siteId.get( 0 ) );
//
//		setMinorVersionNumber(3);
//		setPatchVersionNumber(0);
////		moduleConfiguration.setModuleVersionMinor(3);
////		moduleConfiguration.setModuleVersionPatch(0);
//	}
//
//	private void upgradeTo1_2_1() {
//		Session session = HibernateUtil.getCurrentSession();
//		List<Object[]> cmsAtomPassThroughList = session.createSQLQuery( "SELECT id, cmsAtom_type FROM cmsatompassthrough WHERE cmsAtom_type = 'PLACE_MODULE'").list();
//		for( Object[] cmsAtomPassThrough : cmsAtomPassThroughList ) {
//			if( cmsAtomPassThrough[ 1 ].toString().equals( "PLACE_MODULE" ) ) {
//				session.createSQLQuery( "DELETE FROM cmspagerevision_cmsatompassedthroughmap WHERE mapKey = " + cmsAtomPassThrough[ 0 ] );
//				session.createSQLQuery( "DELETE FROM innerTemplate_cmsatompassedthroughmap WHERE mapKey = " + cmsAtomPassThrough[ 0 ] );
//				session.createSQLQuery( "DELETE FROM innerTemplate_cmsatompassthrough WHERE cmsAtomPassThroughList_id = " + cmsAtomPassThrough[ 0 ] );
//				session.createSQLQuery( "DELETE FROM topLevelTemplate_cmsatompassthrough WHERE cmsAtomPassThroughList_id = " + cmsAtomPassThrough[ 0 ] );
//				session.createSQLQuery( "DELETE FROM cmsatompassthrough WHERE id = " + cmsAtomPassThrough[ 0 ] );
//			}
//		}
//		session.createSQLQuery( "UPDATE unconfigurabledevelopercmsatom SET name = moduleIdStr" );
//
//		setPatchVersionNumber(1);
////		moduleConfiguration.setModuleVersionPatch(1);
//	}
//
//	private void upgradeTo1_2_0() {
//		Session session = HibernateUtil.getCurrentSession();
//		session.createSQLQuery( "UPDATE innerTemplate SET cmsTemplate_id = parentTemplate_id, cmsTemplate_type = 'TOP_LEVEL_TEMPLATE'" );
//		List<Object[]> cprCphList = session.createSQLQuery( "SELECT * FROM cmspagerevision_placeholdercontent").list();
//		HashMap<Long,HashMap<String,Long>> innerTemplateCmsAtomPassThroughMap = new HashMap<Long,HashMap<String,Long>>();
//		HashMap<Long,HashMap<String,Long>> topLevelTemplateCmsAtomPassThroughMap = new HashMap<Long,HashMap<String,Long>>();
//		for( Object[] cprCphRow : cprCphList ) {
//			if( cprCphRow[ 1 ].equals( "CMS_PLACEHOLDER_CONTENT" ) ) {
//				session.createSQLQuery( "INSERT INTO cmspagerevision_cmsplaceholdercontent VALUES ( " + cprCphRow[ 0 ] + ", " + cprCphRow[ 2 ]+ ",'" + cprCphRow[ 3 ] + "')" );
//			} else {
//				Object cmsInfo[] = (Object[]) session.createSQLQuery( "SELECT cmsTemplate_type, cmsTemplate_id FROM cmspagerevision WHERE id = " + cprCphRow[ 0 ]  ).uniqueResult();
//				if( cmsInfo != null ) {
//					StringBuffer strBuf = new StringBuffer();
//					HashMap<String,Long> tempTemplateCmsAtomPassThroughMap;
//					if( cmsInfo[ 0 ].equals( "TOP_LEVEL_TEMPLATE" ) ) {
//						strBuf.append( "INSERT INTO toplevelTemplate_cmsatompassthrough" );
//						if( topLevelTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) ) == null ) {
//							topLevelTemplateCmsAtomPassThroughMap.put( Long.parseLong( cmsInfo[ 1 ].toString() ), new HashMap<String,Long>() );
//						}
//						tempTemplateCmsAtomPassThroughMap = topLevelTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) );
//					} else {
//						strBuf.append( "INSERT INTO innerTemplate_cmsatompassthrough " );
//						if( innerTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) ) == null ) {
//							innerTemplateCmsAtomPassThroughMap.put( Long.parseLong( cmsInfo[ 1 ].toString() ), new HashMap<String,Long>() );
//						}
//						tempTemplateCmsAtomPassThroughMap = innerTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) );
//					}
//
//					if( tempTemplateCmsAtomPassThroughMap.get(cprCphRow[ 3 ].toString()) == null ) {
//						session.createSQLQuery( "INSERT INTO cmsatompassthrough VALUES ( null, 1, 0, 1, NOW(), null, NOW(), null, 1, null, 1,'" + cprCphRow[ 1 ] + "'," + cprCphRow[ 2 ] + ", 1)" );
//						Long maxId = Long.parseLong( session.createSQLQuery( "SELECT MAX(id) FROM cmsatompassthrough" ).uniqueResult().toString() );
//						strBuf.append( " VALUES ( " + cmsInfo[ 1 ] + "," + maxId + ")" );
//						session.createSQLQuery( strBuf.toString() );
//						tempTemplateCmsAtomPassThroughMap.put(cprCphRow[ 3 ].toString(),maxId);
//					}
//					session.createSQLQuery( "INSERT INTO cmspagerevision_cmsatompassedthroughmap VALUES ( " + cprCphRow[ 0 ] + ", '" + cprCphRow[ 1 ] + "'," + cprCphRow[ 2 ] + "," + tempTemplateCmsAtomPassThroughMap.get(cprCphRow[ 3 ].toString()) + ")" );
//				}
//			}
//		}
//
//		List<Object[]> itCphList = session.createSQLQuery( "SELECT * FROM innertemplate_placeholdercontent").list();
//		for( Object[] itCphRow : itCphList ) {
//			if( itCphRow[ 1 ].equals( "CMS_PLACEHOLDER_CONTENT" ) ) {
//				session.createSQLQuery( "INSERT INTO innertemplate_cmsplaceholdercontent VALUES ( " + itCphRow[ 0 ] + ", " + itCphRow[ 2 ]+ ",'" + itCphRow[ 3 ] + "')" );
//			} else {
//
//				Object cmsInfo[] = (Object[]) session.createSQLQuery( "SELECT cmsTemplate_type, cmsTemplate_id FROM innertemplate WHERE id = " + itCphRow[ 0 ]  ).uniqueResult();
//				if( topLevelTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) ) == null ) {
//					topLevelTemplateCmsAtomPassThroughMap.put( Long.parseLong( cmsInfo[ 1 ].toString() ), new HashMap<String,Long>() );
//				}
//				HashMap<String,Long> tempTemplateCmsAtomPassThroughMap = topLevelTemplateCmsAtomPassThroughMap.get( Long.parseLong( cmsInfo[ 1 ].toString() ) );
//				if( tempTemplateCmsAtomPassThroughMap.get(itCphRow[ 1 ].toString()) == null ) {
//					session.createSQLQuery( "INSERT INTO cmsatompassthrough VALUES ( null, 1, 0, 1, NOW(), null, NOW(), null, 1, null, 1,'" + itCphRow[ 1 ] + "'," + itCphRow[ 2 ] + ", 1)" );
//					Long maxId = Long.parseLong( session.createSQLQuery( "SELECT MAX(id) FROM cmsatompassthrough" ).uniqueResult().toString() );
//					session.createSQLQuery( "INSERT INTO toplevelTemplate_cmsatompassthrough VALUES ( " + cmsInfo[ 1 ] + "," + maxId + ")" );
//					tempTemplateCmsAtomPassThroughMap.put(itCphRow[ 1 ].toString(),maxId);
//				}
//				session.createSQLQuery( "INSERT INTO innertemplate_cmsatompassedthroughmap VALUES ( " + itCphRow[ 0 ] + ", '" + itCphRow[ 1 ] + "'," + itCphRow[ 2 ] + "," + tempTemplateCmsAtomPassThroughMap.get(itCphRow[ 1 ].toString()) + ")" );
//			}
//		}
//
//		// clear the table first to stop duplicate key errors
//		// this shouldn't be a problem as it's a new table
//		try {
//			session.createSQLQuery( "DELETE FROM unconfigurabledevelopercmsatom" );
//			StringBuffer sqlBuf = new StringBuffer();
//			sqlBuf.append( "INSERT INTO unconfigurabledevelopercmsatom (`id`,`deletable`,`persistentData`,`active`,`dateCreated`,`dateInactivated`,`dateLastModified`,`displayId`,`userIdCreated`,`userIdInactivated`,`userIdLastModified`,`name`,`moduleIdStr`,`aplosModuleName`,`owner_id`) SELECT `id`,`deletable`,`persistentData`,`active`,`dateCreated`,`dateInactivated`,`dateLastModified`,`displayId`,`userIdCreated`,`userIdInactivated`,`userIdLastModified`,'',`moduleIdStr`,`aplosModuleName`,`owner_id`  FROM unconfigurabledevelopercmsmodule" );
//			session.createSQLQuery( sqlBuf.toString() );
//		} catch( Exception ex ) {
//			ex.printStackTrace();
//		}
//
//		setMinorVersionNumber(2);
//		setPatchVersionNumber(0);
////		moduleConfiguration.setModuleVersionMinor(2);
////		moduleConfiguration.setModuleVersionPatch(0);
//	}
//
//	private void upgradeTo1_1_2() {
////		CmsWebsite site = (CmsWebsite) new AqlBeanDao( CmsWebsite.class ).get( 1 );
////		if( site == null ) {
////			site = new CmsWebsite();
////		}
////		CmsConfiguration.getCmsConfiguration().setCmsWebsite( site );
////
//		setPatchVersionNumber(2);
////		moduleConfiguration.setModuleVersionPatch(2);
//	}
//
//	private void upgradeTo1_1_1() {
//		try {
//		//update to version 1.0
//				ApplicationUtil.executeSql("UPDATE competition SET announceDate=CONCAT(announceYear,'-',announceMonth,'-',announceDay),endDate=CONCAT(endYear,'-',endMonth,'-',endDay) WHERE announceDate IS NULL");
//		} catch( Exception ex ) {
//			ex.printStackTrace();
//			// probably because these fields never existed
//		}
//
//		setPatchVersionNumber(1);
////		moduleConfiguration.setModuleVersionMinor(1);
//	}
}
