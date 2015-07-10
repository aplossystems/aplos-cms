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
					case 11:
						switch(getPatchVersionNumber()) {
							case 0:
								upgradeTo1_11_1();
						}
				}
		}
	}
	
	private void upgradeTo1_11_1() {
		dropTable("article_comments", true);
		setPatchVersionNumber(1);
	}
}
