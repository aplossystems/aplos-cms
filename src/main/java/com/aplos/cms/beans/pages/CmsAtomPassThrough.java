package com.aplos.cms.beans.pages;

import java.util.List;

import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;

@Entity
@PluralDisplayName(name="CMS atom pass throughs")
public class CmsAtomPassThrough extends AplosBean {
	private static final long serialVersionUID = -6453756233384286390L;

	@Any( metaColumn = @Column( name = "cmsAtom_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn( name = "cmsAtom_id" )
	@DynamicMetaValues
	@Cascade({CascadeType.ALL})
	private CmsAtom cmsAtom;

	public CmsAtomPassThrough() {
		
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.getImplementation(cmsAtom, true, true);
//	}

	public CmsAtomPassThrough( CmsAtom cmsAtom ) {
		this.cmsAtom = cmsAtom;
	}

	public String getAllInsertTexts() {
		DeveloperCmsAtom developerCmsAtom = (DeveloperCmsAtom) cmsAtom;
		StringBuffer insertTextsBuf = new StringBuffer();
		if( developerCmsAtom != null ) {
			List<Integer> files = developerCmsAtom.getInsertFileNumbers(developerCmsAtom.getAplosModuleName(), developerCmsAtom.getFrontEndBodyName());
			for (Integer i : files) {
				addNewInsertText(insertTextsBuf, i);
			}
		}
		return insertTextsBuf.toString();
	}

	public void addNewInsertText( StringBuffer insertTextsBuf, int count ) {
		if( insertTextsBuf.length() != 0 ) {
			insertTextsBuf.append( "," );
		}
		insertTextsBuf.append( "{_CAPT_" + getId() +  "_" + count + "}" );
	}

	public CmsAtom getCmsAtomNewInstance() {
		return (CmsAtom) CommonUtil.getNewInstance( ApplicationUtil.getClass( cmsAtom ), null );
	}

	public void setCmsAtom(CmsAtom cmsAtom) {
		this.cmsAtom = cmsAtom;
	}

	public CmsAtom getCmsAtom() {
		return cmsAtom;
	}

//	public void setViewId(Integer viewId) {
//		this.viewId = viewId;
//	}
//
//	public Integer getViewId() {
//		return viewId;
//	}
}
