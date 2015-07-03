package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.SubscriptionChannel;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SubscriptionChannel.class)
public class SubscriptionChannelListPage extends ListPage {

	private static final long serialVersionUID = 785908206761852606L;
}
