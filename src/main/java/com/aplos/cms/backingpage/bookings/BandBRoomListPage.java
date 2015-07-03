package com.aplos.cms.backingpage.bookings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.bookings.BandBRoom;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BandBRoom.class)
public class BandBRoomListPage extends ListPage {
	private static final long serialVersionUID = -2299874234052162955L;
}