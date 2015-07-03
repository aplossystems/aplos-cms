package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CaseStudy;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CaseStudy.class)
public class CaseStudyEditPage extends EditPage {
	private static final long serialVersionUID = 3205778985178323703L;
	private String newKeyword;

	public String addKeyword() {
		CaseStudy caseStudy = resolveAssociatedBean();
		if ( CommonUtil.isNullOrEmpty( getNewKeyword() ) ) {
			JSFUtil.addMessageForError("Please enter valid text for this keyword");
		} else {
			caseStudy.getKeywords().add(getNewKeyword());
		}
		return null;
	}

	public String removeKeyword() {
		CaseStudy caseStudy = resolveAssociatedBean();
		String keyword = (String) JSFUtil.getRequest().getAttribute("tableBean");
		caseStudy.getKeywords().remove(keyword);
		return null;
	}
	
	public String getNewKeyword() {
		return newKeyword;
	}
	public void setNewKeyword(String newKeyword) {
		this.newKeyword = newKeyword;
	}

}
