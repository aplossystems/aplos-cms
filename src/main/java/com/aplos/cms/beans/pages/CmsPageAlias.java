package com.aplos.cms.beans.pages;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;

@Entity
@PluralDisplayName(name="CMS page aliases")
@Cache
@DiscriminatorValue("CmsPageAlias")
public class CmsPageAlias extends CmsPage {
	private static final long serialVersionUID = 2813052089271554936L;

	@ManyToOne //(cascade={CascadeType.ALL}) // do not cascade all otherwise deleting an alias deletes the original :S
	private CmsPage cmsPage;

	public CmsPageAlias() {}

	public CmsPageAlias(CmsPage cmsPage) {
		this.cmsPage = cmsPage;
		this.setName(cmsPage.getName());
		this.setMapping( cmsPage.getMapping() );
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialise(getCmsPage(), fullInitialisation);
//	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getName() {
		if (super.getName() == null || super.getName().equals("")) {
			return cmsPage.getName();
		}
		return super.getName();
	}

	public void setCmsPage(CmsPage cmsPage) {
		this.cmsPage = cmsPage;
	}

	public CmsPage getCmsPage() {
		return cmsPage;
	}

}
