package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.MailoutTemplate;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=MailoutTemplate.class)
public class MailoutTemplateEditPage extends EditPage {
	private static final long serialVersionUID = 3673468053698285545L;
}
