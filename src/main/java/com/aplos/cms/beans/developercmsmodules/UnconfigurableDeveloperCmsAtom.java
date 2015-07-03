package com.aplos.cms.beans.developercmsmodules;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="UNCONFIGURABLE_DEVELOPER_CMS_MODULE")
public class UnconfigurableDeveloperCmsAtom extends DeveloperCmsAtom {
	private static final long serialVersionUID = -79401839499878676L;

	private String aplosModuleName;

	public UnconfigurableDeveloperCmsAtom() {}

	public UnconfigurableDeveloperCmsAtom( String aplosModuleName, String moduleIdStr, String name ) {
		setCmsAtomIdStr( moduleIdStr );
		setAplosModuleName( aplosModuleName );
		setName( name );
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public String getBackendBodyUrl() {
		return "";
	}

	@Override
	public String getAplosModuleName() {
		return aplosModuleName;
	}

	public void setAplosModuleName(String aplosModuleName) {
		this.aplosModuleName = aplosModuleName;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
//		UnconfigurableDeveloperCmsAtom unconfigurableDeveloperCmsAtom = new UnconfigurableDeveloperCmsAtom(aplosModuleName, getModuleIdStr());
//		return unconfigurableDeveloperCmsAtom;
		return this;
	}
}
