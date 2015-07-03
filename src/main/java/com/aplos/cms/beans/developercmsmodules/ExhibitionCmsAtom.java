package com.aplos.cms.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.Exhibition;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.SystemUser;

@Entity
@DynamicMetaValueKey(oldKey="EXHIBITION_ATOM")
public class ExhibitionCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<Exhibition> exhibitions = new ArrayList<Exhibition>();

	@Override
	public String getName() {
		return "Exhibitions";
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getExhibitions(), fullInitialisation);
//	}
//
//	@Override
//	public boolean initBackend() {
//		super.initBackend();
//		this.hibernateInitialise( true );
//		return true;
//	}
//
//	@Override
//	public boolean initFrontend(boolean isRequestPageLoad) {
//		super.initFrontend(isRequestPageLoad);
//		this.hibernateInitialise( true );
//		return true;
//	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ExhibitionCmsAtom exhibitionsAtom = new ExhibitionCmsAtom();
		exhibitionsAtom.setExhibitions(new ArrayList<Exhibition>( getExhibitions() ));
		return exhibitionsAtom;
	}

	@Override
	public void saveBean(SystemUser systemUser) {
		for (int i=0, n=getExhibitions().size() ; i<n ; i++) {
			getExhibitions().get(i).saveDetails(systemUser);
		}
		super.saveBean(systemUser);
	}

	public void setExhibitions(List<Exhibition> exhibitions) {
		this.exhibitions = exhibitions;
	}

	public List<Exhibition> getExhibitions() {
		return exhibitions;
	}
}
