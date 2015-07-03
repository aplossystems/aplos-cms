package com.aplos.cms.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.JobOffer;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;

@Entity
@DynamicMetaValueKey(oldKey="JOB_OFFER_ATOM")
public class JobOfferCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<JobOffer> jobOffers = new ArrayList<JobOffer>();

	@Override
	public String getName() {
		return "Job Offers";
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getJobOffers(), fullInitialisation);
//	}
//
//	@Override
//	public boolean initBackend() {
//		super.initBackend();
//		this.hibernateInitialise( true );
//		return true;
//	}
//
//	@Override
//	public boolean initFrontend(boolean isRequestPageLoad) {
//		super.initFrontend(isRequestPageLoad);
//		this.hibernateInitialise( true );
//		return true;
//	}

	@SuppressWarnings("unchecked")
	public List<JobOffer> getSortedJobOfferList() {
		return (List<JobOffer>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) getJobOffers() );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		JobOfferCmsAtom jobOffersAtom = new JobOfferCmsAtom();
		jobOffersAtom.setJobOffers(new ArrayList<JobOffer>( jobOffers ));
		return jobOffersAtom;
	}

	public void setJobOffers(List<JobOffer> jobOffers) {
		this.jobOffers = jobOffers;
	}

	public List<JobOffer> getJobOffers() {
		return jobOffers;
	}
}
