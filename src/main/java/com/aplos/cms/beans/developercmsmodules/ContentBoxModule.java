package com.aplos.cms.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.ContentBox;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.SystemUser;

@Entity
@DynamicMetaValueKey(oldKey="CONTENT_BOX_MODULE")
public class ContentBoxModule extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany(mappedBy = "contentBoxModule")
	private List<ContentBox> contentBoxes = new ArrayList<ContentBox>();

	private Boolean displayAnchorMenu;

	@Override
	public String getName() {
		return "Content Box";
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(getContentBoxes(), fullInitialisation);
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

	public void removeContentBox( ContentBox contentBoxToRemove ) {
		contentBoxes.remove(contentBoxToRemove);
		for( ContentBox tempContentBox : contentBoxes ) {
			if( tempContentBox.getPositionIdx() > contentBoxToRemove.getPositionIdx() ) {
				tempContentBox.setPositionIdx( tempContentBox.getPositionIdx() - 1 );
			}
		}
		saveDetails();
	}

	public void contentBoxPositionChanged( ContentBox reOrderedContentBox, int oldPositionIdx ) {
		//make sure we keep all numbers consecutive - ie account for position changing in either direction
		if ( oldPositionIdx > reOrderedContentBox.getPositionIdx()) {
			for( ContentBox tempContentBox : contentBoxes ) {
				if( !tempContentBox.getId().equals( reOrderedContentBox.getId() ) ) {
					if( tempContentBox.getPositionIdx() >= reOrderedContentBox.getPositionIdx() && tempContentBox.getPositionIdx() < oldPositionIdx ) {
						tempContentBox.setPositionIdx( tempContentBox.getPositionIdx() + 1 );
					}
				}
			}
			saveDetails();
		} else if( oldPositionIdx < reOrderedContentBox.getPositionIdx() ) {
			for( ContentBox tempContentBox : contentBoxes ) {
				if( !tempContentBox.getId().equals( reOrderedContentBox.getId() ) ) {
					if( tempContentBox.getPositionIdx() > oldPositionIdx && tempContentBox.getPositionIdx() <= reOrderedContentBox.getPositionIdx() ) {
						tempContentBox.setPositionIdx( tempContentBox.getPositionIdx() - 1 );
					}
				}
			}
			saveDetails();
		}
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ContentBoxModule contentBoxAtom = new ContentBoxModule();
		contentBoxAtom.setContentBoxes(contentBoxes);
		return contentBoxAtom;
	}

	@Override
	public void saveBean(SystemUser systemUser) {
		for (int i=0, n=contentBoxes.size() ; i<n ; i++) {
			contentBoxes.get(i).saveDetails(systemUser);
		}
		super.saveBean(systemUser);
	}

	public void removeCurrentContentBox() {

	}

	public void setContentBoxes(List<ContentBox> contentBoxes) {
		this.contentBoxes = contentBoxes;
	}

	public List<ContentBox> getContentBoxes() {
		return contentBoxes;
	}

	public void setDisplayAnchorMenu(Boolean displayAnchorMenu) {
		this.displayAnchorMenu = displayAnchorMenu;
	}

	public Boolean getDisplayAnchorMenu() {
		return displayAnchorMenu;
	}
}
