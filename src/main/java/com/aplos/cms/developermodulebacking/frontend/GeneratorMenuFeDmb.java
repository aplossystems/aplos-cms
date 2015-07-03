package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.PositionedGeneratorMenuItem;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.GenerationType;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class GeneratorMenuFeDmb extends DeveloperModuleBacking {
	
	private static final long serialVersionUID = 3480562272310917939L;
	private GeneratorMenuItem selectedGeneratorMenuItem;
	private GeneratorMenuCmsAtom generatorMenuCmsAtom;

	public void productTypeSelected() {
		PositionedGeneratorMenuItem generatorMenuItem = JSFUtil.getBeanFromRequest( "generatorMenuItem" );
		GeneratorMenuCmsAtom generatorMenuCmsAtom = getGeneratorMenuCmsAtom();
		GeneratorMenuItem loadedGeneratorMenuItem = (GeneratorMenuItem) new BeanDao( (Class<? extends AplosAbstractBean>) generatorMenuCmsAtom.getGeneratorItemClass() ).get( generatorMenuItem.getGeneratorMenuItem().getId() );
		setSelectedGeneratorMenuItem(loadedGeneratorMenuItem);
	}
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		setGeneratorMenuCmsAtom( determineGeneratorMenuCmsAtom() );
//		HibernateUtil.initialise( getGeneratorMenuCmsAtom(), true );
		return super.responsePageLoad(developerCmsAtom);
	}

	public GeneratorMenuCmsAtom determineGeneratorMenuCmsAtom() {
		//check if we have the atom included in this current page
		GeneratorMenuCmsAtom generatorCmsAtom = CmsPageGenerator.checkForAtomInPage((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		if( generatorCmsAtom != null && !GenerationType.NONE.equals( generatorCmsAtom.getGenerationType() ) ) {
			return generatorCmsAtom;
		}

		//if we still have nothing, try to return the one in the session (if there is one, otherwise null)
		return (GeneratorMenuCmsAtom)JSFUtil.getBeanFromScope(GeneratorMenuCmsAtom.class);

	}

	public String getItemUrl() {
		PositionedGeneratorMenuItem generatorMenuItem = (PositionedGeneratorMenuItem) JSFUtil.getRequest().getAttribute( "menuBean" );
		String url = "";
		if (generatorMenuItem != null && generatorMenuItem.getGeneratorMenuItem() != null ) {
 			url += generatorMenuItem.getGeneratorMenuItem().getGeneratorMenuUrl();
		}

		return url;
	}

	public GeneratorMenuItem getSelectedGeneratorMenuItem() {
		return selectedGeneratorMenuItem;
	}

	public void setSelectedGeneratorMenuItem(GeneratorMenuItem selectedGeneratorMenuItem) {
		this.selectedGeneratorMenuItem = selectedGeneratorMenuItem;
	}

	public GeneratorMenuCmsAtom getGeneratorMenuCmsAtom() {
		return generatorMenuCmsAtom;
	}

	public void setGeneratorMenuCmsAtom(GeneratorMenuCmsAtom generatorMenuCmsAtom) {
		this.generatorMenuCmsAtom = generatorMenuCmsAtom;
	}

}

















