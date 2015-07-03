package com.aplos.cms.developermodulebacking.frontend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.Testimonial;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.TestimonialsModule;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class TestimonialsFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -5659703543700088455L;

	private TestimonialsModule testimonialsAtom;
	private List<Testimonial> cachedTestimonials = null;
	private Testimonial selectedTestimonial;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setTestimonialsAtom((TestimonialsModule) developerCmsAtom);
		if (cachedTestimonials == null) {
			cachedTestimonials = new BeanDao(Testimonial.class).setIsReturningActiveBeans(true).getAll();
		}
		if (cachedTestimonials != null) {
			int idx = ((Double) Math.floor( Math.random() * cachedTestimonials.size() )).intValue();
			if( cachedTestimonials.size() > 0 ) {
				selectedTestimonial = cachedTestimonials.get(idx);
			}
		}
		return true;
	}

	public void setTestimonialsAtom(TestimonialsModule testimonialsAtom) {
		this.testimonialsAtom = testimonialsAtom;
	}

	public TestimonialsModule getTestimonialsAtom() {
		return testimonialsAtom;
	}

	public List<Testimonial> getCachedTestimonials() {
		return cachedTestimonials;
	}

	public void setCachedTestimonials(List<Testimonial> cachedTestimonials) {
		this.cachedTestimonials = cachedTestimonials;
	}
	
	public boolean isTestimonialsPageAvailable() {
		return CmsConfiguration.getCmsConfiguration().getTestimonialsCpr() != null;
	}
	
	public String getOccupationAndCity() {
		StringBuffer occupationAndCity = new StringBuffer();
		if( getSelectedTestimonial() != null ) {
			if( !CommonUtil.isNullOrEmpty( getSelectedTestimonial().getOccupation() ) ) {
				occupationAndCity.append( getSelectedTestimonial().getOccupation() );
			}
			
			if( !CommonUtil.isNullOrEmpty( getSelectedTestimonial().getCity() ) ) {
				if( occupationAndCity.length() > 0 ) {
					occupationAndCity.append( ", ");
				}
				occupationAndCity.append( getSelectedTestimonial().getCity() );
			}
		}

		return occupationAndCity.toString();
	}
	
	public void goToTestimonialsPage() {
		CmsPageRevision testimonialsCpr = CmsConfiguration.getCmsConfiguration().getTestimonialsCpr();
		if (testimonialsCpr != null) {
			JSFUtil.redirect(new CmsPageUrl(testimonialsCpr), true);
		} else {
			JSFUtil.addMessageForError("The testimonials page has not been set in the practicecover configuration");
		}
	}

	public Testimonial getSelectedTestimonial() {
		return selectedTestimonial;
	}

	public void setSelectedTestimonial(Testimonial selectedTestimonial) {
		this.selectedTestimonial = selectedTestimonial;
	}
	
	

}
