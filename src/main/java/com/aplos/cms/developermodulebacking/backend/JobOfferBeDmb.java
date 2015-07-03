package com.aplos.cms.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.JobOffer;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.JobOfferCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@ManagedBean
@ViewScoped
public class JobOfferBeDmb extends DeveloperModuleBacking {
	/**
	 *
	 */
	private static final long serialVersionUID = 7709128332672255448L;
	private JobOfferCmsAtom jobOfferCmsAtom;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		jobOfferCmsAtom = (JobOfferCmsAtom) developerCmsAtom;
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) jobOfferCmsAtom.getJobOffers(), JobOffer.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		getPositionedBeanHelper().saveCurrentPositionedBean();
	}

	public void saveJobOffersAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		jobOfferCmsAtom.saveDetails(JSFUtil.getLoggedInUser());
	}

	public JobOfferCmsAtom getJobOfferCmsAtom() {
		return jobOfferCmsAtom;
	}

	public void setJobOfferCmsAtom(JobOfferCmsAtom jobOfferCmsAtom) {
		this.jobOfferCmsAtom = jobOfferCmsAtom;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}
}
