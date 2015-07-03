package com.aplos.cms.backingpage.bookings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.bookings.BandBRoom;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.cms.beans.bookings.BandBRoomType;
import com.aplos.common.BeanMenuHelper;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=BandBRoom.class)
public class BandBRoomEditPage extends EditPage {
	private static final long serialVersionUID = 1585272985710190365L;
	
	public BeanMenuHelper getBandBRoomTypeBmh() {
		return new BeanMenuHelper( BandBRoomType.class );
	}

}
