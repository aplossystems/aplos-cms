package com.aplos.cms.developermodulebacking;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.interfaces.DataTableStateCreator;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.servlets.EditorUploadServlet;
import com.aplos.common.utils.CommonUtil;

public abstract class DeveloperModuleBacking implements Serializable, DataTableStateCreator {
	private static final long serialVersionUID = 870535961096239307L;
	private static Logger logger = Logger.getLogger( DeveloperModuleBacking.class );

	private DeveloperCmsAtom developerCmsAtom;

	public boolean requestPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		genericPageLoad(developerCmsAtom);
		return true;
	}
	
	public void addAssociatedBackingsToScope() {}

	public SelectItem[] getViewSelectItems() {
		SelectItem selectItems[] = null;
		if( getDeveloperCmsAtom() instanceof ConfigurableDeveloperCmsAtom ) {
			List<String> viewExtensions = CmsConfiguration.getCmsConfiguration().getConfigurableCmsAtomViewMap().get( getDeveloperCmsAtom().getClass() );
			if( viewExtensions != null ) {
				selectItems = new SelectItem[ viewExtensions.size() ];
				String tempViewLabel;
				for( int i = 0, n = selectItems.length; i < n; i++ ) {
					tempViewLabel = CommonUtil.firstLetterToUpperCase( viewExtensions.get( i ).replace( "_", " " ) );
					selectItems[ i ] = new SelectItem( viewExtensions.get( i ), tempViewLabel );
				}
			}
		}
		
		if( selectItems == null ) {
			selectItems = new SelectItem[ 0 ];
		}
		return selectItems;
	}

	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		genericPageLoad(developerCmsAtom);
		return true;
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired();
	}

	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		this.developerCmsAtom = developerCmsAtom;
		return true;
	}

	public DeveloperCmsAtom getDeveloperCmsAtom() {
		return developerCmsAtom;
	}

	public void applyBtnAction() {
		developerCmsAtom.saveDetails();
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new AplosLazyDataModel( dataTableState, aqlBeanDao );
	}

	@Override
	public DataTableState getDefaultDataTableState( Class<?> parentClass ) {
		DataTableState dataTableState = CommonConfiguration.getCommonConfiguration().getDefaultDataTableState();
		dataTableState.setParentClass( parentClass );
		return dataTableState;
	}
	
	public void registerScan(String scannedStr) {
		if (developerCmsAtom != null) {
			logger.info( "Registered scan of '" + scannedStr + "' in " + developerCmsAtom.getName() +" atom " + developerCmsAtom.getId() );
		} else {
			logger.info( "Registered scan of '" + scannedStr + "' in atom");
		}
	}
	
}
