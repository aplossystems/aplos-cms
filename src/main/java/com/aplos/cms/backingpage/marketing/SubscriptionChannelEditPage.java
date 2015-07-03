package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.SubscriptionChannel;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SubscriptionChannel.class)
public class SubscriptionChannelEditPage extends EditPage {
	private static final long serialVersionUID = -379849207889115055L;

}
