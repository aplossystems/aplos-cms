package com.aplos.cms.developermodulebacking.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.model.UploadedFile;

import com.aplos.cms.beans.ContentBox;
import com.aplos.cms.beans.ContentBox.ContentBoxLayout;
import com.aplos.cms.beans.ContentBox.HeadingLevel;
import com.aplos.cms.beans.developercmsmodules.ContentBoxModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class ContentBoxBeDmb extends DeveloperModuleBacking {

	/**
	 *
	 */
	private static final long serialVersionUID = 6537499332398694382L;
	private ContentBox selectedContentBox;
	private ContentBox currentContentBox;
	private ContentBoxModule contentBoxAtom;

	@Transient
	private UploadedFile imageUploadedFile;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		contentBoxAtom = (ContentBoxModule) developerCmsAtom;
		if (contentBoxAtom.getContentBoxes().size() == 0) {
			setSelectedContentBox(null);
			setCurrentContentBox(null);
		}
		else if( !contentBoxAtom.getContentBoxes().contains(currentContentBox) || selectedContentBox == null ) {
			setSelectedContentBox(contentBoxAtom.getContentBoxes().get(0));
			setCurrentContentBox(getSelectedContentBox());
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		if (currentContentBox != null) {
			saveContentBox(currentContentBox);
		}
	}

	public String addNewContentBox() {
		if (currentContentBox != null) {
			saveContentBox(currentContentBox);
		}
		contentBoxAtom.saveDetails();
		ContentBox contentBox = new ContentBox();
		contentBox.setPositionIdx(contentBoxAtom.getContentBoxes().size());
		contentBox.setName( "Content Box " + (contentBox.getPositionIdx() + 1) );
		contentBox.setContentBoxModule(contentBoxAtom);
		contentBoxAtom.getContentBoxes().add(contentBox);
		selectedContentBox = currentContentBox = contentBox;
		return null;
	}

	public void deleteCurrentContentBox() {
		contentBoxAtom.removeContentBox( currentContentBox );
		contentBoxAtom.saveDetails();
		currentContentBox.hardDelete();
		if( contentBoxAtom.getContentBoxes().size() > 0 ) {
			setSelectedContentBox(contentBoxAtom.getContentBoxes().get(0));
			setCurrentContentBox(getSelectedContentBox());
		}
		else {
			setSelectedContentBox(null);
			setCurrentContentBox(null);
		}
	}

	public void saveBoxesAndAtom() {
		if (currentContentBox != null) {
			saveContentBox( currentContentBox );
		}
		contentBoxAtom.saveDetails(JSFUtil.getLoggedInUser());
	}

	public List<SelectItem> getContentBoxLayoutSelectItems() {
		return CommonUtil.getEnumSelectItems(ContentBoxLayout.class, null);
	}

	public List<ContentBox> getSortedContentBoxes() {
		List<ContentBox> sortedContentBoxes = contentBoxAtom.getContentBoxes();
		Collections.sort(sortedContentBoxes);
		return sortedContentBoxes;
	}

	public SelectItem[] getContentBoxSelectItems() {
		SelectItem[] selectItems = AplosAbstractBean.getSelectItemBeans(getSortedContentBoxes(), false);
		return selectItems;
	}

	public SelectItem[] getPositionIdxSelectItems() {
		SelectItem[] selectItems = new SelectItem[contentBoxAtom.getContentBoxes().size()];

		int contentBoxListSizeMinusOne = contentBoxAtom.getContentBoxes().size() - 1;
		List<ContentBox> sortedContentBoxList = CommonUtil.getShallowCopy(contentBoxAtom.getContentBoxes());
		Collections.sort(sortedContentBoxList, new ContentBoxComparator() );
		for( int i = 0, n = sortedContentBoxList.size(); i < n; i++ ) {
			if( i == currentContentBox.getPositionIdx() ) {
				selectItems[i] = new SelectItem(i,"Current position");
			} else if( i == 0 ) {
				selectItems[i] = new SelectItem(i,"Beginning of list");
			} else if( i == contentBoxListSizeMinusOne ) {
				selectItems[i] = new SelectItem(i,"End of list");
			} else {
				if( i < currentContentBox.getPositionIdx() ) {
					selectItems[i] = new SelectItem(i,"Before " + sortedContentBoxList.get( i ).getName() );
				} else {
					selectItems[i] = new SelectItem(i,"Before " + sortedContentBoxList.get( i + 1 ).getName() );
				}
			}
		}
		return selectItems;
	}

	public void deleteCurrentImage() {
		currentContentBox.setImageDetails(null);
	}

	public void changeCurrentContentBox(ValueChangeEvent event) {
		if( event.getPhaseId().equals( PhaseId.UPDATE_MODEL_VALUES ) ) {
			saveContentBox( currentContentBox );
			currentContentBox = selectedContentBox;
		} else {
			event.setPhaseId( PhaseId.UPDATE_MODEL_VALUES );
			event.queue();
			return;
		}
	}

	public void saveContentBox( ContentBox contentBox ) {
		int oldPositionIdx;
		if( contentBox.isNew() ) {
//			contentBox.aqlSaveDetails();
			contentBox.saveDetails(JSFUtil.getLoggedInUser());
			oldPositionIdx = contentBoxAtom.getContentBoxes().size() - 1;
		} else {
			oldPositionIdx = (Integer) ApplicationUtil.getFirstResult( "SELECT positionIdx FROM " + AplosBean.getTableName( ContentBox.class ) + " WHERE id = " + contentBox.getId() )[0];
		}
		if( oldPositionIdx != contentBox.getPositionIdx() ) {
			contentBoxAtom.contentBoxPositionChanged( contentBox, oldPositionIdx );
		}
	}

	public List<SelectItem> getHeadingLevelSelectItems() {
		List<SelectItem> headingLevelSelectItems = new ArrayList<SelectItem>();
		headingLevelSelectItems.add(new SelectItem(null, "None"));
		headingLevelSelectItems.addAll(CommonUtil.getEnumSelectItems(HeadingLevel.class, null));
		return headingLevelSelectItems;
	}

	public ContentBoxModule getContentBoxAtom() {
		return contentBoxAtom;
	}

	public void setContentBoxAtom(ContentBoxModule contentBoxAtom) {
		this.contentBoxAtom = contentBoxAtom;
	}

	public void setSelectedContentBox(ContentBox selectedContentBox) {
		this.selectedContentBox = selectedContentBox;
	}

	public ContentBox getSelectedContentBox() {
		return selectedContentBox;
	}

	public void setCurrentContentBox(ContentBox currentContentBox) {
		this.currentContentBox = currentContentBox;
	}

	public ContentBox getCurrentContentBox() {
		return currentContentBox;
	}

	public void setImageUploadedFile(UploadedFile imageUploadedFile) {
		this.imageUploadedFile = imageUploadedFile;
	}

	public UploadedFile getImageUploadedFile() {
		return imageUploadedFile;
	}

	private class ContentBoxComparator implements Comparator<ContentBox> {
		@Override
		public int compare(ContentBox contentBox1, ContentBox contentBox2) {
			if( contentBox1.getPositionIdx() > contentBox2.getPositionIdx() ) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
