package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.annotations.persistence.Entity;

@Entity
public class ContactPageCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 8064623238745083071L;
	
	private boolean isNameShowing = true;
	private boolean isNameRequired = true;
	private boolean isPhoneShowing = true;
	private boolean isPhoneRequired = false;
	private boolean isSubjectShowing = true;
	private boolean isSubjectRequired = true;
	private boolean isDuplicateEmailShowing = true;
	
	static {
		CmsConfiguration cmsConfiguration = CmsConfiguration.getCmsConfiguration();
		cmsConfiguration.addConfigurationCmsAtomView( ContactPageCmsAtom.class, "vertical" );
		cmsConfiguration.addConfigurationCmsAtomView( ContactPageCmsAtom.class, "horizontal" );
	}
	
	public ContactPageCmsAtom() {
		setViewExtension( "vertical" );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		return null;
	}
	
	@Override
	public String getName() {
		return "Contact us";
	}

	public boolean isNameShowing() {
		return isNameShowing;
	}

	public void setNameShowing(boolean isNameShowing) {
		this.isNameShowing = isNameShowing;
	}

	public boolean isNameRequired() {
		return isNameRequired;
	}

	public void setNameRequired(boolean isNameRequired) {
		this.isNameRequired = isNameRequired;
	}

	public boolean isPhoneShowing() {
		return isPhoneShowing;
	}

	public void setPhoneShowing(boolean isPhoneShowing) {
		this.isPhoneShowing = isPhoneShowing;
	}

	public boolean isPhoneRequired() {
		return isPhoneRequired;
	}

	public void setPhoneRequired(boolean isPhoneRequired) {
		this.isPhoneRequired = isPhoneRequired;
	}

	public boolean isSubjectShowing() {
		return isSubjectShowing;
	}

	public void setSubjectShowing(boolean isSubjectShowing) {
		this.isSubjectShowing = isSubjectShowing;
	}

	public boolean isSubjectRequired() {
		return isSubjectRequired;
	}

	public void setSubjectRequired(boolean isSubjectRequired) {
		this.isSubjectRequired = isSubjectRequired;
	}

	public boolean isDuplicateEmailShowing() {
		return isDuplicateEmailShowing;
	}

	public void setDuplicateEmailShowing(boolean isDuplicateEmailShowing) {
		this.isDuplicateEmailShowing = isDuplicateEmailShowing;
	}
}
