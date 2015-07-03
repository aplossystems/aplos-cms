package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CaseStudy;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CaseStudy.class)
public class CaseStudyListPage extends ListPage {
	private static final long serialVersionUID = 5511537647360687595L;
}
