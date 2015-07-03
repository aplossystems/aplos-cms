package com.aplos.cms.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Exhibition;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ExhibitionCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@ManagedBean
@ViewScoped
public class ExhibitionBeDmb extends DeveloperModuleBacking {
	/**
	 *
	 */
	private static final long serialVersionUID = 3600495381780940426L;
	private ExhibitionCmsAtom exhibitionCmsAtom;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		exhibitionCmsAtom = (ExhibitionCmsAtom) developerCmsAtom;
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) exhibitionCmsAtom.getExhibitions(), Exhibition.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		getPositionedBeanHelper().saveCurrentPositionedBean();
	}

	public void saveExhibitionsAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		exhibitionCmsAtom.saveDetails(JSFUtil.getLoggedInUser());
	}

	public ExhibitionCmsAtom getExhibitionCmsAtom() {
		return exhibitionCmsAtom;
	}

	public void setExhibitionCmsAtom(ExhibitionCmsAtom exhibitionCmsAtom) {
		this.exhibitionCmsAtom = exhibitionCmsAtom;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}
}
