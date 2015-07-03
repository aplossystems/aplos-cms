package com.aplos.cms.beans;

import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
@PluralDisplayName(name="product type menu items")
public class PositionedGeneratorMenuItem extends AplosBean implements PositionedBean {
	private static final long serialVersionUID = -7203029270270090216L;

	@Any( metaColumn = @Column( name = "generatorMenuItem_type" ), fetch=FetchType.LAZY )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn(name="generatorMenuItem_id")
	@DynamicMetaValues
	private GeneratorMenuItem generatorMenuItem;
	private Integer positionIdx;

	@Override
	public String getDisplayName() {
		if( getGeneratorMenuItem() == null ) {
			return "New Menu Item";
		} else {
			return getGeneratorMenuItem().getDisplayName();
		}
	}
	
//	@Override
//	public void hibernateInitialiseAfterCheck(boolean fullInitialisation) {
//		super.hibernateInitialiseAfterCheck(fullInitialisation);
//		HibernateUtil.initialise( getGeneratorMenuItem(), fullInitialisation );
//	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public GeneratorMenuItem getGeneratorMenuItem() {
		return generatorMenuItem;
	}

	public void setGeneratorMenuItem(GeneratorMenuItem generatorMenuItem) {
		this.generatorMenuItem = generatorMenuItem;
	}

}
