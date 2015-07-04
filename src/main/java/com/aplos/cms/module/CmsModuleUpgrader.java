package com.aplos.cms.module;

import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.ModuleUpgrader;

public class CmsModuleUpgrader extends ModuleUpgrader {

	public CmsModuleUpgrader( AplosModuleImpl aplosModule  ) {
		super(aplosModule, CmsConfiguration.class);
	}


	@Override
	protected void upgradeModule() {

		//don't use break, allow the rules to cascade
		switch (getMajorVersionNumber()) {
			case 1:

				switch (getMinorVersionNumber()) {
				}
		}
	}
}
