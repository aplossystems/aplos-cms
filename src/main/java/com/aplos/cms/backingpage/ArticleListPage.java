package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Article;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Article.class)
public class ArticleListPage extends ListPage {
	private static final long serialVersionUID = -6663593437435862418L;
}
