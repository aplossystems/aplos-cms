package com.aplos.cms.beans.pages;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.PositionedGeneratorMenuItem;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.SystemUser;

@Entity
@PluralDisplayName(name="CMS page generators")
@Cache
@DiscriminatorValue("CmsPageGenerator")
public class CmsPageGenerator extends CmsPage {
	private static final long serialVersionUID = -7885539988445579468L;
	@ManyToOne
	private GeneratorMenuCmsAtom generatorMenuCmsAtom;

	public CmsPageGenerator() {
		setName("Page Generator");
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		if( fullInitialisation ) {
//			HibernateUtil.initialise( getGeneratorMenuCmsAtom(), fullInitialisation );
//		}
//	}
	
	public static GeneratorMenuCmsAtom checkForAtomInPage( CmsPageRevision cmsPageRevision) {
		if (cmsPageRevision != null) {
			for (CmsAtom tempAtom : cmsPageRevision.getCmsAtomList()) {
				if (tempAtom instanceof GeneratorMenuCmsAtom) {
					return (GeneratorMenuCmsAtom) tempAtom;
				}
			}
		}
		return null;
	}

	@Override
	public void saveBean( SystemUser systemUser ) {
		setMapping("generator"); //this stops super causing us issues as this mapping check is ignored
		super.saveBean( systemUser );
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public List<GeneratorMenuItem> getGeneratorMenuItems() {
		List<GeneratorMenuItem> generatorItems = new ArrayList<GeneratorMenuItem>();
		for(PositionedGeneratorMenuItem item : getGeneratorMenuCmsAtom().getSortedGeneratorMenuItems()) {
			generatorItems.add(item.getGeneratorMenuItem());
		}
		return generatorItems;
	}

	public void initAtomBackend() {
		if (getGeneratorMenuCmsAtom() != null){
			getGeneratorMenuCmsAtom().initBackend();
		}
	}

	public void saveAtom() {
		if (getGeneratorMenuCmsAtom() != null) {
			DeveloperModuleBacking moduleBacking = getGeneratorMenuCmsAtom().getBackendModuleBacking();
			if( moduleBacking != null ) {
				moduleBacking.applyBtnAction();
			}
		}
	}

	public GeneratorMenuCmsAtom getGeneratorMenuCmsAtom() {
		return generatorMenuCmsAtom;
	}

	public void setGeneratorMenuCmsAtom(GeneratorMenuCmsAtom generatorMenuCmsAtom) {
		this.generatorMenuCmsAtom = generatorMenuCmsAtom;
	}

}
