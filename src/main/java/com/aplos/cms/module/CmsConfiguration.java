package com.aplos.cms.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.cms.beans.ContactFormSubmission;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.cms.enums.UnconfigurableAtomEnum;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.enums.BulkMessageFinderEnum;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.interfaces.BulkMessageFinderInter;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.module.ModuleConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
public class CmsConfiguration extends ModuleConfiguration {
	private static final long serialVersionUID = 8307954302058891326L;

	private int paginationItemsPerPage = 15;
	private boolean isDefaultSendNewsletterOnce = false;
	
	@Transient
	private static HashMap<Class<? extends ConfigurableDeveloperCmsAtom>,List<String>> configurableCmsAtomViewMap = new HashMap<Class<? extends ConfigurableDeveloperCmsAtom>,List<String>>();

	public enum CmsUnconfigurableAtomEnum implements UnconfigurableAtomEnum {
		ISSUE_REPORTED ( "issueReported", "Issue reported", SslProtocolEnum.DONT_CHANGE ),
		SESSION_TIMEOUT ( "sessionTimeout", "Session timeout", SslProtocolEnum.DONT_CHANGE ),
		TEST ( "test", "Test", SslProtocolEnum.DONT_CHANGE ),
		BG_IMAGE_MAPPER ("backgroundImageMapper", "Background image mapper", SslProtocolEnum.DONT_CHANGE),
		COMPETITION_CLAIM_PRIZE ( "competitionClaim", "Competition : Claim Prize", SslProtocolEnum.FORCE_SSL ),
		SITEMAP ( "siteMap", "Sitemap", SslProtocolEnum.DONT_CHANGE ),
		PAGE_NOT_FOUND ( "pageNotFound", "Page Not Found", SslProtocolEnum.DONT_CHANGE ),
		UNSUBSCRIBE ( "unsubscribe", "Unsubscribe", SslProtocolEnum.DONT_CHANGE ),
		THREE_D_REDIRECT ( "threeDRedirect", "3D redirect", SslProtocolEnum.FORCE_SSL );

		String cmsAtomIdStr;
		String cmsAtomName;
		SslProtocolEnum sslProtocolEnum;
		private boolean isActive = true;

		private CmsUnconfigurableAtomEnum( String cmsAtomIdStr, String cmsAtomName, SslProtocolEnum sslProtocolEnum ) {
			this.cmsAtomIdStr = cmsAtomIdStr;
			this.cmsAtomName = cmsAtomName;
			this.sslProtocolEnum = sslProtocolEnum;
		}

		@Override
		public String getCmsAtomIdStr() {
			return cmsAtomIdStr;
		}

		@Override
		public String getCmsAtomName() {
			return cmsAtomName;
		}
		
		@Override
		public SslProtocolEnum getSslProtocolEnum() {
			return sslProtocolEnum;
		}

		public boolean isActive() {
			return isActive;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	public enum CmsBulkMessageFinderEnum implements BulkMessageFinderEnum {
		COMPETITION_ENTRY ( CompetitionEntry.class, "Competition Entry" ),
		COMPETITION_PRIZE_WINNER ( CompetitionPrizeWinner.class, "Competition Prize Winner" ),
		CONTACT_FORM_SUBMISSION ( ContactFormSubmission.class, "Contact form submission" );
		
		Class<? extends BulkMessageFinderInter> bulkMessageFinderClass; 
		String bulkMessageFinderName;
		boolean isActive = true;
		
		private CmsBulkMessageFinderEnum( Class<? extends BulkMessageFinderInter> bulkMessageFinderClass, String bulkMessageFinderName ) {
			this.bulkMessageFinderClass = bulkMessageFinderClass;
			this.bulkMessageFinderName = bulkMessageFinderName;
		}
		
		@Override
		public Class<? extends BulkMessageFinderInter> getBulkMessageFinderClass() {
			return bulkMessageFinderClass;
		}
		
		@Override
		public String getBulkMessageFinderName() {
			return bulkMessageFinderName;
		}

		@Override
		public boolean isActive() {
			return isActive;
		}

		@Override
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	@Column(columnDefinition="LONGTEXT")
	private String newsletterHeader;
	@Column(columnDefinition="LONGTEXT")
	private String newsletterFooter;
	private String defaultStyleClass = "aplos-cms-edit-area";
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision threeDAuthCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision testimonialsCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision blogCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision caseStudyCpr;
	private boolean isShowingBookingPrices = false;
	private boolean isShowingPageDeveloperOptions = false;

	@Transient
	private Map<UnconfigurableAtomEnum, UnconfigurableDeveloperCmsAtom> ucdCmsAtomMap = new HashMap<UnconfigurableAtomEnum, UnconfigurableDeveloperCmsAtom>();


	@Override
	public int getMaximumModuleVersionMajor() {
		return 1;
	}

	@Override
	public int getMaximumModuleVersionMinor() {
		return 11;
	}

	@Override
	public int getMaximumModuleVersionPatch() {
		return 0;
	}
	
	public static List<UnconfigurableAtomEnum> getActiveUnconfigurableAtomEnums( UnconfigurableAtomEnum[] unconfigurableAtomEnums ) {
		List<UnconfigurableAtomEnum> activeEnums = new ArrayList<UnconfigurableAtomEnum>( Arrays.asList(unconfigurableAtomEnums) );
		for( int i = activeEnums.size() - 1; i > -1; i-- ) {
			if( !activeEnums.get( i ).isActive() ) {
				activeEnums.remove( i );
			}
		}
		return activeEnums;
	}

	@Override
	public boolean recursiveModuleConfigurationInit(AplosContextListener aplosContextListener, int loopCount) {
		super.recursiveModuleConfigurationInit(aplosContextListener, loopCount);

		if( loopCount == 0 ) {
			CmsModuleDbConfig cmsModuleDbConfig = ((CmsModule) aplosContextListener.getAplosModuleByClass( CmsModule.class )).getCmsModuleDbConfig();
			if( !cmsModuleDbConfig.isCompetitionUsed() ) {
				CmsUnconfigurableAtomEnum.COMPETITION_CLAIM_PRIZE.setActive(false);
				CmsEmailTemplateEnum.COMPETITION_CONSULATION.setActive(false);
				CmsEmailTemplateEnum.COMPETITION_NON_CLAIMANT.setActive(false);
				CmsEmailTemplateEnum.COMPETITION_WINNER.setActive(false);
				CmsBulkMessageFinderEnum.COMPETITION_ENTRY.setActive(false);
				CmsBulkMessageFinderEnum.COMPETITION_PRIZE_WINNER.setActive(false);
			}
			CmsConfiguration.getCmsConfiguration().registerUnconfigurableCmsAtomEnums( getActiveUnconfigurableAtomEnums( CmsUnconfigurableAtomEnum.values() ) );
			CommonConfiguration commonConfiguration = CommonConfiguration.getCommonConfiguration(); 
			commonConfiguration.registerEmailTemplateEnums( CommonConfiguration.getActiveEmailTemplateEnums( CmsEmailTemplateEnum.values() ) );
			commonConfiguration.registerBulkMessageFinderEnums( CommonConfiguration.getActiveBulkMessageFinderEnums( CmsBulkMessageFinderEnum.values() ) );
			return true;
		} else if( loopCount == 1 ) {
			loadOrCreateUnconfigurationAtoms();
		}
		return false;
	}
	
	public void addConfigurationCmsAtomView( Class<? extends ConfigurableDeveloperCmsAtom> developerCmsAtomClass, String viewExtension ) {
		if( getConfigurableCmsAtomViewMap().get( developerCmsAtomClass ) == null ) {
			getConfigurableCmsAtomViewMap().put( developerCmsAtomClass, new ArrayList<String>() );
		}
		getConfigurableCmsAtomViewMap().get( developerCmsAtomClass ).add( viewExtension );
	}

	public void setThreeDAuthCpr(CmsPageRevision threeDAuthCpr) {
		this.threeDAuthCpr = threeDAuthCpr;
	}

	public CmsPageRevision getThreeDAuthCpr() {
		return threeDAuthCpr;
	}

	public void redirectToThreeDAuthCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getThreeDAuthCpr() ), true );
	}

	public UnconfigurableDeveloperCmsAtom getUnconfigurableDeveloperCmsAtom( UnconfigurableAtomEnum unconfigurableAtomEnum ) {
		return ucdCmsAtomMap.get( unconfigurableAtomEnum );
	}

	public void registerUnconfigurableCmsAtomEnums( List<? extends UnconfigurableAtomEnum> unconfigurableAtomEnums ) {
		for( UnconfigurableAtomEnum tempUnconfigurableAtomEnum : unconfigurableAtomEnums ) {
			ucdCmsAtomMap.put( tempUnconfigurableAtomEnum, null );
		}
	}

	/*
	 * This should only be used before application startup, before the ucdCmsAtomMap
	 * is populated.
	 */
	public UnconfigurableDeveloperCmsAtom loadOrCreateUnconfigurationAtom( UnconfigurableAtomEnum unconfigurableAtomEnum ) {
		String moduleName = CommonUtil.getModuleName( unconfigurableAtomEnum.getClass().getName() );
		String atomIdString = unconfigurableAtomEnum.getCmsAtomIdStr();
		String atomName = unconfigurableAtomEnum.getCmsAtomName();

		BeanDao unconfigurableAtomDao = new BeanDao( UnconfigurableDeveloperCmsAtom.class );
		unconfigurableAtomDao.setWhereCriteria("bean.cmsAtomIdStr='" + atomIdString + "'");
		UnconfigurableDeveloperCmsAtom unconfigurableCmsAtom = unconfigurableAtomDao.getFirstBeanResult();
		if (unconfigurableCmsAtom == null) {
			unconfigurableCmsAtom = new UnconfigurableDeveloperCmsAtom( moduleName, atomIdString, atomName );
			unconfigurableCmsAtom.saveDetails();
		}
		return unconfigurableCmsAtom;
	}

	public void loadOrCreateUnconfigurationAtoms() {
		BeanDao unconfigurableAtomDao = new BeanDao( UnconfigurableDeveloperCmsAtom.class );
		//unconfigurableAtomDao.addWhereCriteria( "bean.aplosModuleName = '" + moduleName + "'" );
		//unconfigurableAtomDao.addWhereCriteria( "bean.cmsAtomIdStr = '" + unconfigurableAtomEnum.getCmsAtomIdStr() + "'" );
		List<UnconfigurableDeveloperCmsAtom> unconfigurableAtomList = unconfigurableAtomDao.setIsReturningActiveBeans(true).getAll();
		Map<String,UnconfigurableDeveloperCmsAtom> unconfigurableAtomMap = new HashMap<String,UnconfigurableDeveloperCmsAtom>();

		for ( UnconfigurableDeveloperCmsAtom tempUnconfigurableDeveloperCmsAtom : unconfigurableAtomList ) {
//			tempUnconfigurableDeveloperCmsAtom.hibernateInitialise( true );
			unconfigurableAtomMap.put( tempUnconfigurableDeveloperCmsAtom.getCmsAtomIdStr(), tempUnconfigurableDeveloperCmsAtom );
		}
		
		boolean updateCmsPageRevisions = false;

		for ( UnconfigurableAtomEnum unconfigurableAtomEnum : ucdCmsAtomMap.keySet() ) {
			UnconfigurableDeveloperCmsAtom unconfigurableAtom = unconfigurableAtomMap.get( unconfigurableAtomEnum.getCmsAtomIdStr() );

			if ( unconfigurableAtom == null ) {
				String moduleName = CommonUtil.getModuleName( unconfigurableAtomEnum.getClass().getName() );
				unconfigurableAtom = new UnconfigurableDeveloperCmsAtom( moduleName, unconfigurableAtomEnum.getCmsAtomIdStr(), unconfigurableAtomEnum.getCmsAtomName() );
				unconfigurableAtom.setSslProtocolEnum( unconfigurableAtomEnum.getSslProtocolEnum() );
				unconfigurableAtom.saveDetails();
			}
			/*
			 * This is just here to update old entries before SslProtocolEnums were added to them
			 * and can be removed at a later date once they have been updated.
			 * 9 April 2012.
			 */
			if( unconfigurableAtom.getSslProtocolEnum() == null ) {
				ApplicationUtil.getAplosContextListener().handleError( new Exception( "SSL Protocol Enum should not be null" ), false );
				unconfigurableAtom.setSslProtocolEnum( unconfigurableAtomEnum.getSslProtocolEnum() );
				unconfigurableAtom.saveDetails();
				updateCmsPageRevisions = true;
			}

			ucdCmsAtomMap.put( unconfigurableAtomEnum, unconfigurableAtom );
		}
		
		if( updateCmsPageRevisions ) {
			updateCmsPageRevisions();
		}
	}
	
	/*
	 * This is just here to update old entries before SslProtocolEnums were added to them
	 * and can be removed at a later date once they have been updated.
	 * 9 April 2012.
	 */
	public void updateCmsPageRevisions() {
//		HibernateUtil.startNewTransaction();
		List<CmsPageRevision> cmsPageRevisionList = new BeanDao( CmsPageRevision.class ).getAll();
		boolean forceSsl;
		for( CmsPageRevision cmsPageRevision : cmsPageRevisionList ) {
			
			if( cmsPageRevision.getCmsPage().getSslProtocolEnum() == null ) {
				forceSsl = false;
				for( CmsAtom cmsAtom : cmsPageRevision.getCmsAtomList() ) {
					if( SslProtocolEnum.FORCE_SSL.equals( cmsAtom.getSslProtocolEnum() ) ) {
						forceSsl = true;
						break;
					}
				}
	
				if( !forceSsl ) {
					Iterator<CmsAtom> iterator = (Iterator<CmsAtom>) cmsPageRevision.getCmsAtomPassedThroughMap().values().iterator();
					while( iterator.hasNext() ) {
						if( SslProtocolEnum.FORCE_SSL.equals( iterator.next().getSslProtocolEnum() ) ) {
							forceSsl = true;
							break;
						}
					}
				}
				int enumIdx;
				if( forceSsl ) {
					enumIdx = 0;
				} else {
					enumIdx = 3;
				}
				ApplicationUtil.executeSql( "UPDATE cmsPage SET sslProtocolEnum = " + enumIdx + " WHERE id = " + cmsPageRevision.getCmsPage().getId() );
				
			}
		}
	}

	public List<UnconfigurableDeveloperCmsAtom> getUnconfigurableDeveloperCmsAtomList() {
		return new ArrayList<UnconfigurableDeveloperCmsAtom>( ucdCmsAtomMap.values() );
	}

	public static CmsConfiguration getCmsConfiguration() {
		return (CmsConfiguration) getModuleConfiguration( CmsConfiguration.class );
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return CmsConfiguration.getCmsConfiguration();
	}

	public String getNewsletterHeader() {
		return newsletterHeader;
	}

	public void setNewsletterHeader( String newsletterHeader ) {
		if( newsletterHeader.equals( AplosAppConstants.DEFAULT_CKEDITOR_CONTENT ) ) {  // This fixes the ckEditor bug
			newsletterHeader = "";
		}
		this.newsletterHeader = newsletterHeader;
	}

	public String getNewsletterFooter() {
		return newsletterFooter;
	}

	public void setNewsletterFooter( String newsletterFooter ) {
		if( newsletterFooter.equals( AplosAppConstants.DEFAULT_CKEDITOR_CONTENT ) ) {  // This fixes the ckEditor bug
			newsletterFooter = "";
		}
		this.newsletterFooter = newsletterFooter;
	}

	public void setUcdCmsAtomMap(Map<UnconfigurableAtomEnum, UnconfigurableDeveloperCmsAtom> ucdCmsAtomMap) {
		this.ucdCmsAtomMap = ucdCmsAtomMap;
	}

	public Map<UnconfigurableAtomEnum, UnconfigurableDeveloperCmsAtom> getUcdCmsAtomMap() {
		return ucdCmsAtomMap;
	}

	public void setPaginationItemsPerPage(int paginationItemsPerPage) {
		this.paginationItemsPerPage = paginationItemsPerPage;
	}

	public int getPaginationItemsPerPage() {
		return paginationItemsPerPage;
	}

	public String getDefaultStyleClass() {
		return defaultStyleClass;
	}

	public void setDefaultStyleClass(String defaultStyleClass) {
		this.defaultStyleClass = defaultStyleClass;
	}

	public boolean isDefaultSendNewsletterOnce() {
		return isDefaultSendNewsletterOnce;
	}

	public void setDefaultSendNewsletterOnce(boolean isDefaultSendNewsletterOnce) {
		this.isDefaultSendNewsletterOnce = isDefaultSendNewsletterOnce;
	}

	public boolean isShowingBookingPrices() {
		return isShowingBookingPrices;
	}

	public void setShowingBookingPrices(boolean isShowingBookingPrices) {
		this.isShowingBookingPrices = isShowingBookingPrices;
	}

	public boolean isShowingPageDeveloperOptions() {
		return isShowingPageDeveloperOptions;
	}

	public void setShowingPageDeveloperOptions(boolean isShowingPageDeveloperOptions) {
		this.isShowingPageDeveloperOptions = isShowingPageDeveloperOptions;
	}

	public CmsPageRevision getBlogCpr() {
		return blogCpr;
	}

	public void setBlogCpr(CmsPageRevision blogCpr) {
		this.blogCpr = blogCpr;
	}

	public CmsPageRevision getCaseStudyCpr() {
		return caseStudyCpr;
	}

	public void setCaseStudyCpr(CmsPageRevision caseStudyCpr) {
		this.caseStudyCpr = caseStudyCpr;
	}

	public HashMap<Class<? extends ConfigurableDeveloperCmsAtom>,List<String>> getConfigurableCmsAtomViewMap() {
		return configurableCmsAtomViewMap;
	}

	public void setConfigurableCmsAtomViewMap(
			HashMap<Class<? extends ConfigurableDeveloperCmsAtom>,List<String>> configurableCmsAtomViewMap) {
		this.configurableCmsAtomViewMap = configurableCmsAtomViewMap;
	}

	public CmsPageRevision getTestimonialsCpr() {
		return testimonialsCpr;
	}

	public void setTestimonialsCpr(CmsPageRevision testimonialsCpr) {
		this.testimonialsCpr = testimonialsCpr;
	}

}
