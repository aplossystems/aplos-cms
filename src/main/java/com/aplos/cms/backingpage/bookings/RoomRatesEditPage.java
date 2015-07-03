package com.aplos.cms.backingpage.bookings;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import com.aplos.cms.beans.bookings.RoomRates;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@PluralDisplayName( name="room rates" )
@AssociatedBean(beanClass=RoomRates.class)
public class RoomRatesEditPage extends EditPage {

	private static final long serialVersionUID = 9070456814584453997L;

	public RoomRatesEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener(this) {

			private static final long serialVersionUID = 8609288705438255826L;

			@Override
			public void actionPerformed(boolean redirect) {
				if( ((RoomRatesEditPage) getBackingPage()).checkRatesAreValid() ) {
					super.actionPerformed(redirect);
				}
			}
		});
	}

	public boolean checkRatesAreValid() {
		RoomRates roomRates = resolveAssociatedBean();
		if( !roomRates.isDaySelected() ) {
			JSFUtil.addMessage( "Please select at least one day", FacesMessage.SEVERITY_WARN );
			return false;
		}
		BeanDao roomRatesDAO = new BeanDao( RoomRates.class );
		roomRatesDAO.addWhereCriteria( "startDate < '" + FormatUtil.formatDateForDB( roomRates.getFinishDate() ) + "'" );
		roomRatesDAO.addWhereCriteria( "finishDate > '" + FormatUtil.formatDateForDB( roomRates.getStartDate() ) + "'" );
		roomRatesDAO.addWhereCriteria( "id != " + roomRates.getId() );
		roomRatesDAO.addWhereCriteria( "bandBRoom.id = " + roomRates.getBandBRoom().getId() );
		List<RoomRates> roomRateList = roomRatesDAO.setIsReturningActiveBeans(true).getAll();
		List<RoomRates> matchingRoomRateList = new ArrayList<RoomRates>();
		for( int i = 0, n = roomRateList.size(); i < n; i++ ) {
			if( !roomRates.isDifferentDays( roomRateList.get( i ) ) ) {
				matchingRoomRateList.add( roomRateList.get( i ) );
			}
		}

		if( matchingRoomRateList.size() > 0  ) {
			String ids[] = new String[ matchingRoomRateList.size() ];
			for( int i = 0, n = ids.length; i < n; i++ ) {
				ids[ i ] = matchingRoomRateList.get( i ).getId().toString();
			}
			String idString = StringUtils.join( ids, ',' );
			if( ids.length > 1 ) {
				idString = "ids : " + idString;
			} else {
				idString = "id : " + idString;
			}
			JSFUtil.addMessage( "This cannot be added as it overlaps with room rates with " + idString, FacesMessage.SEVERITY_WARN );
			return false;
		}
		return true;
	}

}

