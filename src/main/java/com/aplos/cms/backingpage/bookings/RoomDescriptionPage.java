package com.aplos.cms.backingpage.bookings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.bookings.RoomRates;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RoomRates.class)
public class RoomDescriptionPage extends EditPage {
	private static final long serialVersionUID = 1082617469908875968L;
}
