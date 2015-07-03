package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.common.annotations.DataTransferIgnore;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.OneToOne;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
@Cache
public class MenuNode extends AplosSiteBean {

	private static final long serialVersionUID = -8246381239946324480L;

	/* Keep the page name so that we can access it from JSON */
	@Expose
	@SerializedName("data")
	private String name;

	@OneToOne(fetch=FetchType.LAZY)
	private CmsPage cmsPage;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id", insertable=false, updatable=false)
	private MenuNode parent;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@IndexColumn(name="position")
	@DataTransferIgnore
	@Cache
	private List<MenuNode> children = new ArrayList<MenuNode>();

	public MenuNode() {	}

	public MenuNode( Website website, MenuNode parent, CmsPage cmsPage) {
		this.setCmsPage(cmsPage);
		this.setParentWebsite( website );
		this.parent = parent;
		name = getCmsPage().getName();
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		if( fullInitialisation ) {
//			HibernateUtil.initialiseList(getChildren(), false);
//		}
//		setCmsPage((CmsPage)HibernateUtil.getImplementation(getCmsPage(),true,false));
//		HibernateUtil.initialise(getParent(), false);
//	}

	@Override
	public void saveBean( SystemUser systemUser ) {
		if (getCmsPage() != null ) {
			if( getCmsPage() instanceof CmsPageGenerator) {
				CmsPage saveableCmsPage = getCmsPage().getSaveableBean();
				saveableCmsPage.setName(getName());
				saveableCmsPage.saveDetails();
			}
		}
		super.saveBean( systemUser );
	}
	

	public void updateChildrensCachedMappingPath( boolean save ) {
		for( int i = 0, n = getChildren().size(); i < n; i++ ) {
			if( getChildren().get( i ) != null ) {
				getChildren().get( i ).updateChildrensCachedMappingPath( true );
				CmsPage cmsPage = getChildren().get( i ).getCmsPage().getSaveableBean();
				cmsPage.updateCachedMappingPath();
				cmsPage.saveDetails();
			}
		}
	}

	@Override
	public String getDisplayName() {
		if( cmsPage != null ) {
			return getCmsPage().getName();
		} else {
			if( name == null ) {
				return "";
			} else {
				return name;
			}
		}
	}

	public String getDisplayNameHtml() {
		if( cmsPage != null ) {
			StringBuffer strBuf = new StringBuffer( StringEscapeUtils.escapeHtml(getCmsPage().getName()) );
			if( getCmsPage().getLatestRevision().isDraft() ) {
				strBuf.append( "<i>(Draft)</i>" );
			}
			return strBuf.toString();
		} else {
			if( name == null ) {
				return "";
			} else {
				return name;
			}
		}
	}

	public MenuNode getParent() {
		return parent;
	}

	public void setParent( MenuNode parent ) {
		this.parent = parent;
	}

	public void addChild( MenuNode menuNode ) {
		children.add( menuNode );
		menuNode.setParent(this);
	}

	public List<MenuNode> getChildren() {
		return children;
	}

	public void setChildren( List<MenuNode> children ) {
		this.children = children;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCmsPage(CmsPage cmsPage) {
		this.cmsPage = cmsPage;
	}

	public CmsPage getCmsPage() {
		return cmsPage;
	}
}
