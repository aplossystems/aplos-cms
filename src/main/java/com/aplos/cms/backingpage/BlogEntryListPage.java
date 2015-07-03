package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.BlogEntry;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BlogEntry.class)
public class BlogEntryListPage extends ListPage {
	private static final long serialVersionUID = -1198989630562197323L;
}
