package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.Testimonial;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Testimonial.class)
public class TestimonialEditPage extends EditPage {

	private static final long serialVersionUID = -1980253357744662643L;

}
