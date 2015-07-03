package com.aplos.cms.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.PositionedGeneratorMenuItem;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.enums.GenerationType;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@Entity
@SessionScoped
@DynamicMetaValueKey(oldKey="GENERATOR_MENU_ATOM")
public class GeneratorMenuCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany(fetch=FetchType.LAZY)
	private List<PositionedGeneratorMenuItem> generatorMenuItems = new ArrayList<PositionedGeneratorMenuItem>();
	private Class<? extends GeneratorMenuItem> generatorItemClass;
	private GenerationType generationType = GenerationType.MANUAL_LIST;
	private GeneratorMenuItemSortOption generatorMenuItemSortOption = GeneratorMenuItemSortOption.A_TO_Z;
	/*
	 * These are stored so that it is more optimised but also because sorting
	 * the generatorMenuItems causes hibernate to mark them as dirty and spend time saving them 
	 * when the session is commited. 
	 */
	@Transient
	private List<PositionedGeneratorMenuItem> sortedGeneratorMenuItems;
	
	
	public enum GeneratorMenuItemSortOption implements LabeledEnumInter {
		A_TO_Z ("A to Z"),
		NONE ("None");
		
		private String label;
		
		private GeneratorMenuItemSortOption( String label ) {
			this.label = label;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
	}
	
	@ManyToOne
	private CmsPageGenerator cmsPageGenerator;
	
	public GeneratorMenuCmsAtom() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Page generating menu";
	}
	
	public String getItemClassDisplayName() {
		return FormatUtil.breakCamelCase( generatorItemClass.getSimpleName() );
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getGeneratorMenuItems(), fullInitialisation);
//		HibernateUtil.initialise(getCmsPageGenerator(), fullInitialisation);
//	}
//
//	@Override
//	public boolean initBackend() {
//		super.initBackend();
//		this.hibernateInitialise( true );
//		return true;
//	}

	@Override
	public boolean initFrontend(boolean isRequestPageLoad) {
		super.initFrontend(isRequestPageLoad);
//		HibernateUtil.initialise(this, true );
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<PositionedGeneratorMenuItem> getSortedGeneratorMenuItems() {
		if( sortedGeneratorMenuItems == null ) { 
			if( getCmsPageGenerator() != null) {
				sortedGeneratorMenuItems = new ArrayList<PositionedGeneratorMenuItem>( getCmsPageGenerator().getGeneratorMenuCmsAtom().getGeneratorMenuItems() );
			} else {
				if( GeneratorMenuItemSortOption.A_TO_Z.equals( getGeneratorMenuItemSortOption() ) ) {
					sortedGeneratorMenuItems = new ArrayList<PositionedGeneratorMenuItem>( getGeneratorMenuItems() );
				} else {
					sortedGeneratorMenuItems = new ArrayList<PositionedGeneratorMenuItem>( (List<PositionedGeneratorMenuItem>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) getGeneratorMenuItems() ) );
				}
			}
			if( GeneratorMenuItemSortOption.A_TO_Z.equals( getGeneratorMenuItemSortOption() ) ) {
				Collections.sort( sortedGeneratorMenuItems, new GeneratorMenuItemComparator() );
			} 
		}
		return sortedGeneratorMenuItems;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		GeneratorMenuCmsAtom productTypeMenuAtom = new GeneratorMenuCmsAtom();
		productTypeMenuAtom.setGeneratorMenuItems(new ArrayList<PositionedGeneratorMenuItem>( getGeneratorMenuItems() ));
		return productTypeMenuAtom;
	}

	public List<PositionedGeneratorMenuItem> getGeneratorMenuItems() {
		return generatorMenuItems;
	}

	public void setGeneratorMenuItems(List<PositionedGeneratorMenuItem> generatorMenuItems) {
		this.generatorMenuItems = generatorMenuItems;
	}

	public Class<? extends GeneratorMenuItem> getGeneratorItemClass() {
		return generatorItemClass;
	}

	public void setGeneratorItemClass(Class<? extends GeneratorMenuItem> generatorItemClass) {
		this.generatorItemClass = generatorItemClass;
	}

	public CmsPageGenerator getCmsPageGenerator() {
		return cmsPageGenerator;
	}

	public void setCmsPageGenerator(CmsPageGenerator cmsPageGenerator) {
		this.cmsPageGenerator = cmsPageGenerator;
	}

	public GenerationType getGenerationType() {
		return generationType;
	}

	public void setGenerationType(GenerationType generationType) {
		this.generationType = generationType;
	}
	
	public GeneratorMenuItemSortOption getGeneratorMenuItemSortOption() {
		return generatorMenuItemSortOption;
	}

	public void setGeneratorMenuItemSortOption(
			GeneratorMenuItemSortOption generatorMenuItemSortOption) {
		this.generatorMenuItemSortOption = generatorMenuItemSortOption;
	}

	private class GeneratorMenuItemComparator implements Comparator<PositionedGeneratorMenuItem> {
		@Override
		public int compare(PositionedGeneratorMenuItem o1, PositionedGeneratorMenuItem o2) {
			return o1.getDisplayName().compareTo( o2.getDisplayName() );
		}
	}

}
