package com.aplos.cms.beans.developercmsmodules;

import java.net.URL;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;


@MappedSuperclass
@PluralDisplayName(name="configurable developer CMS atoms")
@DynamicMetaValueKey(oldKey="CONFIGURABLE_DEVELOPER_CMS_MODULE")
public abstract class ConfigurableDeveloperCmsAtom extends DeveloperCmsAtom {
	private static final long serialVersionUID = -2451075427355993254L;
	private String viewExtension;

	public ConfigurableDeveloperCmsAtom () {
		setCmsAtomIdStr( AplosBean.getBinding( getClass() ).replace( "Module", "" ).replace( "CmsAtom", "" ) );
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getAplosModuleName() {
		return CommonUtil.getModuleName( getClass().getName() );
	}
	
	public void copy(ConfigurableDeveloperCmsAtom srcAtom) {
		super.copy(srcAtom);
		setViewExtension( srcAtom.getViewExtension() ); 
	}

	public boolean initBackend() {
		return this.initModuleBacking( getBeDmbBinding(), false );
	}
	
	public String getBeDmbBinding() {
		return getCmsAtomIdStr() + "BeDmb";
	}
	
	public String getBinding( boolean addBackingModuleToScope ) {
		if( addBackingModuleToScope ) {
			addModuleBackingToScope( getBeDmbBinding(), getBackendModuleBacking() );
		}
		return super.getBinding();
	}
	
	@Override
	public String getFrontEndBodyName() {
		String fileName = getCmsAtomIdStr() + "Body";
		if( !CommonUtil.isNullOrEmpty( getViewExtension() ) ) {
			fileName = fileName + "_" + getViewExtension().replaceAll("\\s", "_");
		}
		return fileName;
	}

	public boolean initModule( boolean isFrontEnd, boolean isRequestPageLoad ) {
		if ( isFrontEnd ) {
			return initFrontend( isRequestPageLoad );
		} else {
			return initBackend();
		}
	}

	public DeveloperModuleBacking getBackendModuleBacking() {
		return CmsModule.getDeveloperModuleBacking( getBeDmbBinding(), this );
	}

	public String getBackendBodyUrl() {
		String fileName = "/" + getAplosModuleName() + "/developermodule/backend/" + getCmsAtomIdStr() + "Body.xhtml";
		URL url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
		if( url != null ) {
			return fileName;
		} else {
			return "";
		}
	}

	public String getBackendHeadUrl() {
		return "/" + getAplosModuleName() + "/developermodule/backend/" + getCmsAtomIdStr() + "Head.xhtml";
	}

	@Override
	public abstract String getName();

	public String getViewExtension() {
		return viewExtension;
	}

	public void setViewExtension(String viewExtension) {
		this.viewExtension = viewExtension;
	}
}
