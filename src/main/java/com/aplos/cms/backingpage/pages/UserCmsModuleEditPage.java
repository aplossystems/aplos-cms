package com.aplos.cms.backingpage.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.PlaceholderContentWrapper;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped // This needs to be available in session due to c:forEach bug.
@AssociatedBean(beanClass=UserCmsModule.class)
public class UserCmsModuleEditPage extends EditPage {
	private static final long serialVersionUID = -3215651103732666750L;

	private CmsAtom selectedCmsAtom;
	private List<PlaceholderContentWrapper> phcWrapperList;
	private CmsPlaceholderContent contentPhc;
	private UserCmsModule currentUserCmsModule;

	public UserCmsModuleEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener(this) {
			/**
			 *
			 */
			private static final long serialVersionUID = 4773861937935592515L;

			@Override
			public void actionPerformed(boolean redirect) {
				UserCmsModule userCmsModule = JSFUtil.getBeanFromScope( UserCmsModule.class );
				callModuleBackingsSaveBtnAction( userCmsModule );
				updateContent();

				super.actionPerformed(redirect);
			}
		});
		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener(this) {
			/**
			 *
			 */
			private static final long serialVersionUID = -4112453661943869689L;

			@Override
			public void actionPerformed(boolean redirect) {
				UserCmsModule userCmsModule = JSFUtil.getBeanFromScope( UserCmsModule.class );
				callModuleBackingsSaveBtnAction( userCmsModule );
				updateContent();
				userCmsModule.saveDetails();

				JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
			}
		});
	}

	@Override
	public boolean requestPageLoad() {
		super.requestPageLoad();
		updateContent();
		return true;
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		updatePhcWrapperList();
		UserCmsModule userCmsModule = resolveAssociatedBean();
		if (userCmsModule != null) {
			userCmsModule.initModules( false, false );
		}
		return true;
	}

	public void callModuleBackingsSaveBtnAction( UserCmsModule userCmsModule ) {
		for( CmsAtom tempCmsAtom : userCmsModule.getCmsAtomList() ) {
			if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				DeveloperModuleBacking moduleBacking = ((ConfigurableDeveloperCmsAtom) tempCmsAtom).getBackendModuleBacking();
				if( moduleBacking != null ) {
					moduleBacking.applyBtnAction();
				}
			}
		}
	}

	public void updateContent() {
		if( contentPhc != null ) {
			UserCmsModule userCmsModule = JSFUtil.getBeanFromScope( UserCmsModule.class );
			userCmsModule.setContent( contentPhc.getContent() );
		}
	}

	public List<PlaceholderContentWrapper> updatePhcWrapperList() {
		UserCmsModule userCmsModule = JSFUtil.getBeanFromScope( UserCmsModule.class );
		phcWrapperList = new ArrayList<PlaceholderContentWrapper>();

		PlaceholderContentWrapper tempPhcWrapper;
		tempPhcWrapper = new PlaceholderContentWrapper();
		tempPhcWrapper.setCphName( "Content" );
		if( contentPhc == null ) {
			contentPhc = new CmsPlaceholderContent();
			contentPhc.setContent( userCmsModule.getContent() );
		} else {
			if( !userCmsModule.equalsId( currentUserCmsModule ) ) {
				contentPhc.setContent( userCmsModule.getContent() );
			}
		}
		currentUserCmsModule = userCmsModule;
		tempPhcWrapper.setPlaceholderContent( contentPhc );
		phcWrapperList.add( tempPhcWrapper );
		for( CmsAtom cmsAtom : userCmsModule.getCmsAtomList() ) {
			if( cmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				tempPhcWrapper = new PlaceholderContentWrapper();
				tempPhcWrapper.setCphName( cmsAtom.getDisplayName() );
//				tempPhcWrapper.setPhcMap(getPlaceholderContentMap());
				tempPhcWrapper.setPlaceholderContent((ConfigurableDeveloperCmsAtom) cmsAtom);
				phcWrapperList.add( tempPhcWrapper );
			}
		}
		return phcWrapperList;
	}

	public void setPhcWrapperList(List<PlaceholderContentWrapper> phcWrapperList) {
		this.phcWrapperList = phcWrapperList;
	}

	public List<PlaceholderContentWrapper> getPhcWrapperList() {
		return phcWrapperList;
	}

	public void addCmsAtom() {
		//the category options return a null
		if (selectedCmsAtom != null) {
			CmsAtom newCmsAtom;
			if( selectedCmsAtom instanceof UnconfigurableDeveloperCmsAtom ) {
				newCmsAtom = selectedCmsAtom;
			} else {
				newCmsAtom = (CmsAtom) CommonUtil.getNewInstance( ApplicationUtil.getClass( selectedCmsAtom ), null );
			}
			((UserCmsModule) resolveAssociatedBean()).getCmsAtomList().add( newCmsAtom );
			if( newCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				((ConfigurableDeveloperCmsAtom) newCmsAtom).initBackend();
				newCmsAtom.saveDetails();
			}
		} else {
			JSFUtil.addMessageForError("Please select a valid atom");
		}
	}

	public void removeCmsAtom() {
		CmsAtom cmsAtom = (CmsAtom) JSFUtil.getRequest().getAttribute( "cmsAtom" );
		((UserCmsModule) resolveAssociatedBean()).getCmsAtomList().remove( cmsAtom );
	}


	public void setSelectedCmsAtom(CmsAtom selectedCmsAtom) {
		this.selectedCmsAtom = selectedCmsAtom;
	}


	public CmsAtom getSelectedCmsAtom() {
		return selectedCmsAtom;
	}

}
