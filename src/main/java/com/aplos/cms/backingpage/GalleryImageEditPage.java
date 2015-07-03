package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.GalleryImage;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=GalleryImage.class)
public class GalleryImageEditPage extends EditPage {
	private static final long serialVersionUID = -502822288968372970L;

}
