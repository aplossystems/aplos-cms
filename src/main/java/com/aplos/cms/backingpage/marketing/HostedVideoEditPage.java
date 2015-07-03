package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=HostedVideo.class)
public class HostedVideoEditPage extends EditPage {
	private static final long serialVersionUID = 1736821333659059727L;
	
	@Override
	public void applyBtnAction() {
		saveAction();
		super.applyBtnAction();
	}
	
	@Override
	public void okBtnAction() {
		saveAction();
		super.okBtnAction();
	}
	
	public void saveAction() {
		HostedVideo hostedVideo = resolveAssociatedBean();
		hostedVideo.parseCode( hostedVideo.getPastedCode() );
	}
	

}
