package com.aplos.cms.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.PositionedGeneratorMenuItem;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom.GeneratorMenuItemSortOption;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.GenerationType;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@ManagedBean
@ViewScoped
public class GeneratorMenuBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1172673912780789854L;
	private GeneratorMenuCmsAtom generatorMenuAtom;
	private PositionedBeanHelper positionedBeanHelper;
	
	private String itemTypeDropdownValue = null; //generatorMenuCmsAtom.generatorItemClass

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setGeneratorMenuCmsAtom((GeneratorMenuCmsAtom) developerCmsAtom);
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) getGeneratorMenuCmsAtom().getGeneratorMenuItems(), PositionedGeneratorMenuItem.class ));
			if( getGeneratorMenuCmsAtom().getGeneratorItemClass() == null ) {
				CmsModule cmsModule = (CmsModule) ApplicationUtil.getAplosContextListener().getAplosModuleByClass( CmsModule.class );
				List<Class<? extends GeneratorMenuItem>> generatorItemClassList = cmsModule.getAvailableGeneratorItemClassList();
				if( generatorItemClassList.size() > 0 ) {
					getGeneratorMenuCmsAtom().setGeneratorItemClass( generatorItemClassList.get( 0 ) );
				}
			}
			setItemTypeDropdownValue(getGeneratorMenuCmsAtom().getGeneratorItemClass().getName());
		}
		return true;
	}
	
	public void updateItemType(AjaxBehaviorEvent event) {
		if (generatorMenuAtom != null){
			try {
				if (itemTypeDropdownValue == null) {
					generatorMenuAtom.setGeneratorItemClass(null);
				} else {
					generatorMenuAtom.setGeneratorItemClass((Class<? extends GeneratorMenuItem>) Class.forName(itemTypeDropdownValue));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<SelectItem> getGeneratorMenuItemSortOptionSelectItems() {
		return CommonUtil.getEnumSelectItems( GeneratorMenuItemSortOption.class );
	}

	public List<SelectItem> getGenerationTypeSelectItems() {
		return CommonUtil.getEnumSelectItems( GenerationType.class, "Please Select" );
	}

	public SelectItem[] getPageGeneratorSelectItems() {
		BeanDao pageGenDao = new BeanDao(CmsPageGenerator.class);
		return AplosAbstractBean.getSelectItemBeans(pageGenDao.setIsReturningActiveBeans(true).getAll());
	}

	public SelectItem[] getGeneratorItemClassSelectItems() {
		CmsModule cmsModule = (CmsModule) ApplicationUtil.getAplosContextListener().getAplosModuleByClass( CmsModule.class );
		List<Class<? extends GeneratorMenuItem>> generatorItemClassList = cmsModule.getAvailableGeneratorItemClassList();
		SelectItem selectItems[] = new SelectItem[ generatorItemClassList.size() ];
		for( int i = 0, n = generatorItemClassList.size(); i < n; i++ ) {
			selectItems[ i ] = new SelectItem( generatorItemClassList.get( i ).getName(), FormatUtil.breakCamelCase( generatorItemClassList.get( i ).getSimpleName() ) );
		}
		return selectItems;
		
	}

	@Override
	public void applyBtnAction() {
		if (getPositionedBeanHelper() != null) {
			getPositionedBeanHelper().saveCurrentPositionedBean();
		}
		super.applyBtnAction();
	}

	public SelectItem[] getGeneratorItemSelectItemBeans() {
		BeanDao dao = new BeanDao( (Class<? extends AplosAbstractBean>) getGeneratorMenuCmsAtom().getGeneratorItemClass() );
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}

	public GeneratorMenuCmsAtom getGeneratorMenuCmsAtom() {
		return generatorMenuAtom;
	}

	public void setGeneratorMenuCmsAtom(GeneratorMenuCmsAtom generatorMenuAtom) {
		this.generatorMenuAtom = generatorMenuAtom;
	}

	public String getItemTypeDropdownValue() {
		return itemTypeDropdownValue;
	}

	public void setItemTypeDropdownValue(String itemTypeDropdownValue) {
		this.itemTypeDropdownValue = itemTypeDropdownValue;
	}
}
