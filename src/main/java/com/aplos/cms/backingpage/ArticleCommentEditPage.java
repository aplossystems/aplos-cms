package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.ArticleComment;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ArticleComment.class)
public class ArticleCommentEditPage extends EditPage {
	private static final long serialVersionUID = -4359891554811896892L;

}
