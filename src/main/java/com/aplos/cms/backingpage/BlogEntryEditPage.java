package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.BlogEntry;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BlogEntry.class)
public class BlogEntryEditPage extends EditPage {
	private static final long serialVersionUID = 6734902285862541081L;
}
