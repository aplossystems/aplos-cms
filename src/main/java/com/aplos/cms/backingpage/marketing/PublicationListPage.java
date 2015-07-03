package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Publication;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Publication.class)
public class PublicationListPage extends ListPage {

	private static final long serialVersionUID = -268681117285235839L;

}
