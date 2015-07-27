package com.aplos.cms.beans.pages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageRevision.PageStatus;
import com.aplos.cms.utils.CmsUtil;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@PluralDisplayName(name="CMS pages")
@Cache
@DiscriminatorValue("CmsPage")
public class CmsPage extends AplosSiteBean {
	private static final long serialVersionUID = -4817094405033026749L;

	private String name = "New Page";
	private String mapping;
	/*
	 * This is required for efficiency but also when the page is deleted.  As it's
	 * removed from the tree the mapping path cannot be calculated.
	 */
	private String cachedMappingPath = "/";
	/*
	 * This is for a drop down menu if the page has children that are
	 * included in a menu
	 */
	private int numberOfRowsInColumn = 12;

	private String canonicalPath = null;
	
	private Double siteMapPriority = 0.5;
	
	private String facebookTitle;
	private String facebookDescription;
	private String facebookType;
	private String facebookUrl;
	private String facebookImage;

//	@OneToOne
//	@JoinColumn(name = "cmsPageRevision_fk")
//	private CmsPageRevision cmsPageRevision;

	@OneToMany(fetch=FetchType.LAZY)
	@Cache
	private Set<SystemUser> authors = new HashSet<SystemUser>();
	private SslProtocolEnum sslProtocolEnum = SslProtocolEnum.SYSTEM_DEFAULT;

	@Transient
	private String facebookTitleOverride;
	@Transient
	private String facebookDescriptionOverride;
	@Transient
	private String facebookTypeOverride;
	@Transient
	private String facebookUrlOverride;
	@Transient
	private String facebookImageOverride;

	private PageStatus status = PageStatus.PUBLISHED;
	
	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public void saveBean( SystemUser systemUser ) {
		// The / mapping is used for the home page and should be ignored from
		// the checks.
		if ( getMapping() == null || (!getMapping().equals( "/" ) && !getMapping().equals( "alias" ) && !getMapping().equals( "generator" ) )) {
			if (getMapping() == null || getMapping().equals( "" )) {
				setMapping( createMapping(name) );
			} else {
				setMapping( CommonUtil.makeSafeUrlMapping( getMapping() ) );
			}
		}
		boolean updateChildMappings = false;
		if( !isNew() ) {
			String mappingSql = "SELECT mapping FROM " + AplosBean.getTableName( CmsPage.class ) + " WHERE id = " + getId();
			Object[] firstResult = ApplicationUtil.getFirstResult( mappingSql );
			if( firstResult != null ) {
				String originalMapping = (String) firstResult[0];
				if( !getMapping().equals( originalMapping ) ) {
					updateChildMappings = true;
				}
			}
		}
		super.saveBean( systemUser );
		/*
		 * If this flushing is used then stupid hibernate tries to save everything in the session
		 * which could cause Transient Object exceptions.
		 */
//		HibernateUtil.getCurrentSession().flush();
		if( updateChildMappings ) {
			List<MenuNode> menuNodeList = new BeanDao( MenuNode.class ).addWhereCriteria( "bean.cmsPage.id = " + getId() ).getAll();
			for( MenuNode tempMenuNode : menuNodeList ) {
				tempMenuNode.updateChildrensCachedMappingPath( true );
			}	
		}
	}
	
	public String getFacebookMetaTags() {
		if( !CommonUtil.isNullOrEmpty( determineFacebookTitle() )  ) {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append( "<meta  property=\"og:title\" content=\"" );
			strBuf.append( determineFacebookTitle() ).append( "\" />" );

			if( !CommonUtil.isNullOrEmpty( determineFacebookDescription() )  ) {
				strBuf.append( "<meta  property=\"og:description\" content=\"" );
				strBuf.append( determineFacebookDescription() ).append( "\" />" );
			}

			if( !CommonUtil.isNullOrEmpty( determineFacebookType() )  ) {
				strBuf.append( "<meta  property=\"og:type\" content=\"" );
				strBuf.append( determineFacebookType() ).append( "\" />" );
			}

			if( !CommonUtil.isNullOrEmpty( determineFacebookUrl() )  ) {
				strBuf.append( "<meta  property=\"og:url\" content=\"" );
				strBuf.append( determineFacebookUrl() ).append( "\" />" );
			}

			if( !CommonUtil.isNullOrEmpty( determineFacebookImage() )  ) {
				strBuf.append( "<meta  property=\"og:image\" content=\"" );
				strBuf.append( determineFacebookImage() ).append( "\" />" );
			}
			return strBuf.toString();
		}
		return "";
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		if( fullInitialisation ) {
//			HibernateUtil.initialiseSet(getAuthors(),fullInitialisation);
//		}
//	}

	public void validateMapping(FacesContext context, UIComponent toValidate, Object value) {
		if( !CmsUtil.validateMapping(this,(String)value) ) {
			((UIInput)toValidate).setValid(false);
		}
	}

	public String createMapping(String value) {
		// Remove invalid chars
		String mapping = CommonUtil.makeSafeUrlMapping(value);

		BeanDao cmsPageDao = new BeanDao( CmsPage.class );
		cmsPageDao.setSelectCriteria("bean.mapping");
		cmsPageDao.addWhereCriteria( "bean.mapping like '" + mapping + "%'" );
		if( !this.isNew() ) {
			cmsPageDao.addWhereCriteria( "bean.id != " + getId() );
		}
		List<String> mappings = cmsPageDao.getAll();

		int count = 0;
		String newMapping = mapping;
		while (mappings.contains( newMapping )) {
			newMapping = mapping.concat( String.valueOf(++count) );
		}

		return newMapping;
	}

	public boolean isAuthor(SystemUser user) {
		if (user == null) { return false; }
		if (user.isAdmin()) { return true; }
		if (getOwner() != null && getOwner().equals( user )) {
			return true;
		} else {
			return authors.contains(user);
		}
	}

	public Set<SystemUser> getAuthors() {
		return authors;
	}

	public void setAuthors( Set<SystemUser> authors ) {
		this.authors = authors;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public String determineFacebookTitle() {
		if( CommonUtil.isNullOrEmpty( getFacebookTitleOverride() ) ) {
			return getFacebookTitle();
		} else {
			return getFacebookTitleOverride();
		}
	}

	public String determineFacebookType() {
		if( CommonUtil.isNullOrEmpty( getFacebookTypeOverride() ) ) {
			return getFacebookType();
		} else {
			return getFacebookTypeOverride();
		}
	}

	public String determineFacebookUrl() {
		if( CommonUtil.isNullOrEmpty( getFacebookUrlOverride() ) ) {
			return getFacebookUrl();
		} else {
			return getFacebookUrlOverride();
		}
	}

	public String determineFacebookImage() {
		if( CommonUtil.isNullOrEmpty( getFacebookImageOverride() ) ) {
			return getFacebookImage();
		} else {
			return getFacebookImageOverride();
		}
	}

	public String determineFacebookDescription() {
		if( CommonUtil.isNullOrEmpty( getFacebookDescriptionOverride() ) ) {
			return getFacebookDescription();
		} else {
			return getFacebookDescriptionOverride();
		}
	}

	public static String getMappingPath( Long cmsPageId ) {
		//  This has been done through sql as it was too slow using hibernate
		MenuNode currNode = null;
		List<Object[]> resultList = ApplicationUtil.getResults( "select menuNode.id, menuNode.parent_id, cmsPage.mapping from MenuNode menuNode left outer join MenuNode parentMenuNode on parentMenuNode.parent_id=menuNode.id, CmsPage cmsPage where menuNode.cmsPage_id=cmsPage.id and parentMenuNode.cmsPage_id=" + cmsPageId + " and menuNode.active=1 order by menuNode.id desc limit 1");
		StringBuffer mappingStrBuf = new StringBuffer();

		while( resultList.size() > 0 && resultList.get( 0 )[ 2 ] != null ) {
			mappingStrBuf.insert(0, resultList.get( 0 )[ 2 ] + "/");
			resultList = ApplicationUtil.getResults( "select menuNode.id, menuNode.parent_id, cmsPage.mapping from CmsPage cmsPage left outer join MenuNode menuNode on menuNode.cmsPage_id=cmsPage.id where menuNode.cmsPage_id=cmsPage.id and menuNode.id=" + resultList.get( 0 )[ 1 ] + " and menuNode.active=1 order by menuNode.id desc limit 1");
		}
		String parentPath = mappingStrBuf.toString();
		//removed the empty check as wizard buttons were ending up with the path
		//contextmapping instead of context/mapping 05/09/11
		if ( !parentPath.startsWith( "/" ) ){ // && !parentPath.equals( "" ) ) {
			parentPath = "/" + parentPath;
		}
		return parentPath;
	}

	public String getMappingPath() {
		return getMappingPath( getId() );
	}

	/**
	 * @deprecated
	 * Please use getFullMapping( includeExtension );
	 * @return
	 */

	@Deprecated
	public String getFullMapping() {
		return getCachedMappingPath() + getMapping();
	}
	
	public void updateCachedMappingPath() {
		setCachedMappingPath( getMappingPath() );
	}

	public String getFullMapping( boolean includeExtension ) {
		String fullMapping = JSFUtil.getContextPath() + getCachedMappingPath() + getMapping();
		/*
		 * This was added in for the home page which returns a mappingPath of / 
		 * and a mapping of / making //.aplos which broke the home page.  This affected
		 * the system when you changed from the https backend to the http home page.
		 */
		fullMapping = fullMapping.replace( "//", "/" );
		if( includeExtension ) {
			fullMapping = fullMapping + ".aplos";
		}
		return fullMapping;
	}

	public void setStatus( PageStatus status ) {
		this.status = status;
	}

	public PageStatus getStatus() {
		return status;
	}

	public CmsPageRevision getLatestRevision() {
		BeanDao cmsPageRevisionDao = new BeanDao( CmsPageRevision.class );
		cmsPageRevisionDao.addWhereCriteria( "cmsPage.id = " + getId() );
		cmsPageRevisionDao.addWhereCriteria( "current = true OR draft = true" );
		List<CmsPageRevision> cmsPageRevisionList = cmsPageRevisionDao.getAll();
		for( int i = 0, n = cmsPageRevisionList.size(); i < n; i++ ) {
			if( cmsPageRevisionList.get( i ).isCurrent() ) {
				// must have a draft so return this.
				return cmsPageRevisionList.get( i );
			}
		} 
		
		return null;
	}

	public int getNumberOfRowsInColumn() {
		return numberOfRowsInColumn;
	}

	public void setNumberOfRowsInColumn(int numberOfRowsInColumn) {
		this.numberOfRowsInColumn = numberOfRowsInColumn;
	}

	public String getCachedMappingPath() {
		return cachedMappingPath;
	}

	public void setCachedMappingPath(String cachedMappingPath) {
		this.cachedMappingPath = cachedMappingPath;
	}

	public SslProtocolEnum getSslProtocolEnum() {
		return sslProtocolEnum;
	}

	public void setSslProtocolEnum(SslProtocolEnum sslProtocolEnum) {
		this.sslProtocolEnum = sslProtocolEnum;
	}

	public String getCanonicalPath() {
		return canonicalPath;
	}

	public void setCanonicalPath(String canonicalPath) {
		this.canonicalPath = canonicalPath;
	}

	public Double getSiteMapPriority() {
		return siteMapPriority;
	}

	public void setSiteMapPriority(Double siteMapPriority) {
		this.siteMapPriority = siteMapPriority;
	}

	public String getFacebookTitle() {
		return facebookTitle;
	}

	public void setFacebookTitle(String facebookTitle) {
		this.facebookTitle = facebookTitle;
	}

	public String getFacebookType() {
		return facebookType;
	}

	public void setFacebookType(String facebookType) {
		this.facebookType = facebookType;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public String getFacebookImage() {
		return facebookImage;
	}

	public void setFacebookImage(String facebookImage) {
		this.facebookImage = facebookImage;
	}

	public String getFacebookDescription() {
		return facebookDescription;
	}

	public void setFacebookDescription(String facebookDescription) {
		this.facebookDescription = facebookDescription;
	}

	public String getFacebookTitleOverride() {
		return facebookTitleOverride;
	}

	public void setFacebookTitleOverride(String facebookTitleOverride) {
		this.facebookTitleOverride = facebookTitleOverride;
	}

	public String getFacebookDescriptionOverride() {
		return facebookDescriptionOverride;
	}

	public void setFacebookDescriptionOverride(
			String facebookDescriptionOverride) {
		this.facebookDescriptionOverride = facebookDescriptionOverride;
	}

	public String getFacebookTypeOverride() {
		return facebookTypeOverride;
	}

	public void setFacebookTypeOverride(String facebookTypeOverride) {
		this.facebookTypeOverride = facebookTypeOverride;
	}

	public String getFacebookUrlOverride() {
		return facebookUrlOverride;
	}

	public void setFacebookUrlOverride(String facebookUrlOverride) {
		this.facebookUrlOverride = facebookUrlOverride;
	}

	public String getFacebookImageOverride() {
		return facebookImageOverride;
	}

	public void setFacebookImageOverride(String facebookImageOverride) {
		this.facebookImageOverride = facebookImageOverride;
	}
}
