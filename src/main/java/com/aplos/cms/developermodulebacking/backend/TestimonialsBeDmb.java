package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.TestimonialsModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@ViewScoped
public class TestimonialsBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 4099730540460398379L;
	
	private TestimonialsModule testimonialsModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setTestimonialsAtom((TestimonialsModule) developerCmsAtom);
		return true;
	}

	public void setTestimonialsAtom(TestimonialsModule testimonialsModule) {
		this.testimonialsModule = testimonialsModule;
	}

	public TestimonialsModule getTestimonialsAtom() {
		return testimonialsModule;
	}

}
