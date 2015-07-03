package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.annotations.persistence.Entity;

@Entity
public class TestimonialsModule extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = -7100603240946892812L;
	
	static {
		CmsConfiguration cmsConfiguration = CmsConfiguration.getCmsConfiguration();
		cmsConfiguration.addConfigurationCmsAtomView( TestimonialsModule.class, "pick one random" );
		cmsConfiguration.addConfigurationCmsAtomView( TestimonialsModule.class, "display all" );
		cmsConfiguration.addConfigurationCmsAtomView( TestimonialsModule.class, "rotate" );
	}
	
	public TestimonialsModule() {
		super();
		setViewExtension("rotate");
	}

	@Override
	public String getName() {
		return "Testimonals";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		TestimonialsModule copiedAtom = new TestimonialsModule();
		copiedAtom.copy( this );
		return copiedAtom;
	}

}
