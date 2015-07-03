package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsWebsite.class)
public class CmsWebsiteListPage extends ListPage {

	private static final long serialVersionUID = 3363148002157899851L;
}
