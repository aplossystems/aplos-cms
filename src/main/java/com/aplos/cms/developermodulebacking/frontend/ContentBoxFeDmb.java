package com.aplos.cms.developermodulebacking.frontend;

import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.ContentBox;
import com.aplos.cms.beans.ContentBox.HeadingLevel;
import com.aplos.cms.beans.developercmsmodules.ContentBoxModule;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class ContentBoxFeDmb extends DeveloperModuleBacking {

	/**
	 *
	 */
	private static final long serialVersionUID = 8707686126256845653L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);

		ContentBoxModule contentBoxModule = (ContentBoxModule) JSFUtil.getBeanFromScope(ContentBoxModule.class);
		if (contentBoxModule.getDisplayAnchorMenu() != null && contentBoxModule.getDisplayAnchorMenu()) {
			List<ContentBox> sortedContentBoxes = getSortedContentBoxes();
			for (ContentBox contentBox : sortedContentBoxes) {
				contentBox.setHeadingString(getBoxHeadingString(contentBox));
			}
		}
		return true;
	}

	public List<ContentBox> getSortedContentBoxes() {
		ContentBoxModule contentBoxModule = (ContentBoxModule) JSFUtil.getBeanFromScope(ContentBoxModule.class);
		List<ContentBox> sortedContentBoxes = contentBoxModule.getContentBoxes();
		Collections.sort(sortedContentBoxes);
		return sortedContentBoxes;
	}

	public boolean getDisplayHeading() {
		ContentBox contentBox = (ContentBox) JSFUtil.getRequest().getAttribute("contentBox");
		return contentBox.getHeadingLevel() != null;
	}

	public boolean getDisplayAnchorMenu() {
		ContentBoxModule contentBoxModule = (ContentBoxModule) JSFUtil.getBeanFromScope(ContentBoxModule.class);
		return contentBoxModule.getDisplayAnchorMenu() != null && contentBoxModule.getDisplayAnchorMenu();
	}

	public String getBoxHeadingString(ContentBox contentBox) {
		List<ContentBox> sortedContentBoxes = getSortedContentBoxes();
		int contentBoxPosition = sortedContentBoxes.indexOf(contentBox);
		int heading1 = 0;
		int heading2 = 0;
		int heading3 = 0;

		for (int i=0 ; i<contentBoxPosition ; i++) {
			if (sortedContentBoxes.get(i).getHeadingLevel() != null) {
				if (sortedContentBoxes.get(i).getHeadingLevel().equals(HeadingLevel.HEADING_1)) {
					heading1 = heading1+1;
					heading2 = 0;
					heading3 = 0;
				}
				else if (sortedContentBoxes.get(i).getHeadingLevel().equals(HeadingLevel.HEADING_2)) {
					heading2 = heading2+1;
					heading3 = 0;
				}
				else if (sortedContentBoxes.get(i).getHeadingLevel().equals(HeadingLevel.HEADING_3)) {
					heading3 = heading3+1;
				}
			}
		}

		StringBuffer stringBuffer = new StringBuffer();

		if (heading1 != 0) {
			if (contentBox.getHeadingLevel() == HeadingLevel.HEADING_1) {
				stringBuffer.append(heading1+1);
			}
			else {
				stringBuffer.append(heading1);
			}
		}
		else {
			stringBuffer.append("1");
		}
		stringBuffer.append(".");

		if (contentBox.getHeadingLevel() == HeadingLevel.HEADING_2 || contentBox.getHeadingLevel() == HeadingLevel.HEADING_3) {
			if (heading2 != 0) {
				if (contentBox.getHeadingLevel() == HeadingLevel.HEADING_2) {
					stringBuffer.append(heading2+1);
				}
				else {
					stringBuffer.append(heading2);
				}
			}
			else {
				stringBuffer.append("1");
			}
			stringBuffer.append(".");
		}

		if (contentBox.getHeadingLevel() == HeadingLevel.HEADING_3) {
			if (heading3 != 0) {
				stringBuffer.append(heading3+1);
			}
			else {
				stringBuffer.append("1");
			}
			stringBuffer.append(".");
		}

		return stringBuffer.toString();
	}
}
