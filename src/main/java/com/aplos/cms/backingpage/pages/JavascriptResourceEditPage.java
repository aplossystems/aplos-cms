package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.JavascriptResource;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=JavascriptResource.class)
public class JavascriptResourceEditPage extends EditPage {
	private static final long serialVersionUID = -7420513009204131804L;
}
