package com.aplos.cms.backingpage.bookings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.bookings.BandBRoomType;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BandBRoomType.class)
public class BandBRoomTypeEditPage extends EditPage {
	private static final long serialVersionUID = 5950513650360804320L;

}
