package components;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.cms.beans.Timetable;
import com.aplos.cms.beans.bookings.BandBBooking;
import com.aplos.cms.beans.bookings.BandBRoom;
import com.aplos.cms.beans.bookings.CalendarDay;
import com.aplos.cms.beans.bookings.RoomRates;
import com.aplos.cms.enums.RoomAvailability;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

public class bookingCalendar extends UINamingContainer {
	private final Calendar cal = new GregorianCalendar();

	private enum PropertyKeys {
		selectedRoom,
		selectedDayIdx,
		currentDate,
		selectedBandBRoomId,
		calendarDayList,
		bandBRoomList;
	}



	public bookingCalendar() {
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		if( getBandBRoomList() == null ) {
			Date currentDate = getCurrentDate();
			if( currentDate == null ) {
				currentDate = new Date();
				cal.setTime( currentDate );
				int date = cal.get(Calendar.DATE);
				int month = cal.get(Calendar.MONTH);
				int year = cal.get(Calendar.YEAR);
				cal.clear();
				cal.set(year, month, date);
				currentDate = cal.getTime();
				setCurrentDate( currentDate );
			}

			List<Timetable> timetableList = new BeanDao( Timetable.class ).getAll();
			Timetable timetable = null;
			if( timetableList.size() == 0 ) {
				timetable = new Timetable();
				timetable.setShowingPrices(true);
				timetable.saveDetails();
			} else {
				timetable = timetableList.get( 0 );
			}

			timetable.addToScope();

			setBandBRoomList(new BeanDao( BandBRoom.class ).getAll());
//			Hibernate.initialize(getBandBRoomList());
			if( (getSelectedRoom() == null || getBandBRoomList().indexOf( getSelectedRoom() ) == -1) && getBandBRoomList().size() > 0 ) {
				setSelectedRoom( getBandBRoomList().get( 0 ) );
			}
			for( int i = 0, n = getBandBRoomList().size(); i < n; i++ ) {
				if( getBandBRoomList().get( i ).getId().equals( getSelectedRoom().getId() ) ) {
					setSelectedRoom( getBandBRoomList().get( i ) );
				}
			}
		}
		super.encodeBegin(context);
	}

	public String goToSelectedRoom() {
		((BandBRoom) JSFUtil.getRequest().getAttribute( "bandBRoomVar" )).addToScope();
		return null;
	}

	public void changeSelectedRoom() {
		if( getSelectedBandBRoomId() != null ) {
			setSelectedRoom( (BandBRoom) new BeanDao( BandBRoom.class ).get( getSelectedBandBRoomId() ) );
		}
	}

	public String getCalendarDayListJson() {
		StringBuffer calendarDayListJsonStrBuf = new StringBuffer( "[" );

		if( getCalendarDayList() != null ) {
			for( int i = 0, n = getCalendarDayList().size(); i < n; i++ ) {
				calendarDayListJsonStrBuf.append( "{price : " + getCalendarDayList().get( i ).getPrice() );
				calendarDayListJsonStrBuf.append( ", rateUnit : '" + CommonUtil.emptyIfNull(getCalendarDayList().get( i ).getRateUnit()) + "'" );
				calendarDayListJsonStrBuf.append( ", availability : " + getCalendarDayList().get( i ).getRoomAvailability().ordinal() + "}" );
				if( i != (getCalendarDayList().size()-1) ) {
					calendarDayListJsonStrBuf.append( "," );
				}
			}
		}

		calendarDayListJsonStrBuf.append( "]" );

		return calendarDayListJsonStrBuf.toString();
	}

	public void createCalendarDayList() {
		Date startOfMonth;
		Date endOfMonth;
		cal.setTime(getCurrentDate());
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, daysInMonth);
		endOfMonth = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		startOfMonth = cal.getTime();
		setCalendarDayList(new ArrayList<CalendarDay>());
		for( int i = 0, n = daysInMonth; i < n; i++ ) {
			getCalendarDayList().add( new CalendarDay() );
		}

		setPrices( getCalendarDayList(), cal, endOfMonth, startOfMonth, daysInMonth );
		setAvailability( getCalendarDayList(), cal, endOfMonth, startOfMonth );
	}

	public void setAvailability( List<CalendarDay> calendarDayList, Calendar cal, Date endOfMonth, Date startOfMonth ) {
		if( getSelectedRoom() != null ) {
			String endOfMonthStr = FormatUtil.formatDateForDB(endOfMonth);
			String startOfMonthStr = FormatUtil.formatDateForDB(startOfMonth);

			BeanDao bandBBookingDao = new BeanDao( BandBBooking.class );
			bandBBookingDao.addWhereCriteria( "bookingDate>='" + startOfMonthStr + "'" );
			bandBBookingDao.addWhereCriteria( "bookingDate<='" + endOfMonthStr + "'" );
			bandBBookingDao.addWhereCriteria( "bean.bandBRoom.id = " + getSelectedRoom().getId() );

			List<BandBBooking> bandBBookingList = bandBBookingDao.setIsReturningActiveBeans(true).getAll();
			CalendarDay tempCalendarDay;
			for( int i = 0, n = bandBBookingList.size(); i < n; i++ ) {
				cal.setTime( bandBBookingList.get( i ).getBookingDate() );
				tempCalendarDay = calendarDayList.get( cal.get( Calendar.DAY_OF_MONTH ) - 1 );
				tempCalendarDay.setRoomAvailability( RoomAvailability.BOOKED );
				tempCalendarDay.setPrice( bandBBookingList.get( i ).getPrice() );
			}
		}
	}

	public void setPrices( List<CalendarDay> calendarDayList, Calendar cal, Date endOfMonth, Date startOfMonth, int daysInMonth ) {
		if( getSelectedRoom() != null ) {
			String endOfMonthStr = FormatUtil.formatDateForDB(endOfMonth);
			String startOfMonthStr = FormatUtil.formatDateForDB(startOfMonth);

			BeanDao roomRatesDao = new BeanDao( RoomRates.class );
			roomRatesDao.addWhereCriteria( "startDate<='" + endOfMonthStr + "'" );
			roomRatesDao.addWhereCriteria( "finishDate>='" + startOfMonthStr + "'" );
			roomRatesDao.addWhereCriteria( "bandBRoom.id = " + getSelectedRoom().getId() );

			List<RoomRates> roomRateList = roomRatesDao.getAll();
			RoomRates tempRoomRate;
			int rateStartDayInMonth;
			int rateFinishDayInMonth;
			cal.setTime( startOfMonth );
			int firstWeekDayIdx = cal.get( Calendar.DAY_OF_WEEK );
			int weekDayIdx;

			for( int i = 0, n = roomRateList.size(); i < n; i++ ) {
				tempRoomRate = roomRateList.get( i );
				if( tempRoomRate.getStartDate().before( startOfMonth ) ) {
					rateStartDayInMonth = 1;
				} else {
					cal.setTime( tempRoomRate.getStartDate() );
					rateStartDayInMonth =  cal.get( Calendar.DAY_OF_MONTH );
				}

				if( tempRoomRate.getFinishDate().after( endOfMonth ) ) {
					rateFinishDayInMonth = daysInMonth;
				} else {
					cal.setTime( tempRoomRate.getFinishDate() );
					rateFinishDayInMonth =  cal.get( Calendar.DAY_OF_MONTH );
				}

				weekDayIdx = (rateStartDayInMonth + (firstWeekDayIdx - 3)) % 7;
				boolean isPriced;
				for( int j = rateStartDayInMonth - 1, p = rateFinishDayInMonth; j < p; j++ ) {
					isPriced = false;
					switch( weekDayIdx ) {
						case 0:
							if( tempRoomRate.isForMonday() ) {
								isPriced = true;
							}
							break;
						case 1:
							if( tempRoomRate.isForTuesday() ) {
								isPriced = true;
							}
							break;
						case 2:
							if( tempRoomRate.isForWednesday() ) {
								isPriced = true;
							}
							break;
						case 3:
							if( tempRoomRate.isForThursday() ) {
								isPriced = true;
							}
							break;
						case 4:
							if( tempRoomRate.isForFriday() ) {
								isPriced = true;
							}
							break;
						case 5:
							if( tempRoomRate.isForSaturday() ) {
								isPriced = true;
							}
							break;
						case 6:
							if( tempRoomRate.isForSunday() ) {
								isPriced = true;
							}
							break;

					}

					if( isPriced ) {
						setCalendarDayPrice( calendarDayList.get( j ), tempRoomRate.getRoomRate() );
						calendarDayList.get( j ).setRateUnit( tempRoomRate.getRateUnit() );
					}
					weekDayIdx++;
					weekDayIdx = weekDayIdx % 7;
				}
			}
		}
	}

	public void toggleSelectedDayAvailability() {
		if( getSelectedRoom() != null ) {
			cal.setTime( getCurrentDate() );
			cal.set(Calendar.DAY_OF_MONTH, getSelectedDayIdx() );
			Date selectedDate = cal.getTime();
			BeanDao bandBBookinDAO = new BeanDao( BandBBooking.class );
			bandBBookinDAO.addWhereCriteria( "bean.bookingDate = '" + FormatUtil.formatDateForDB(selectedDate) + "'" );
			bandBBookinDAO.addWhereCriteria( "bean.bandBRoom.id = " + getSelectedRoom().getId() );
			List<BandBBooking> bandBBookingList = bandBBookinDAO.setIsReturningActiveBeans(true).getAll();

			if( bandBBookingList.size() > 0 ) {
				if( bandBBookingList.get( 0 ).isActive() ) {
					bandBBookingList.get( 0 ).delete();
				}
			} else {
				BandBBooking bandBBooking = new BandBBooking();
				bandBBooking.setBandBRoom( getSelectedRoom() );
				bandBBooking.setBookingDate( selectedDate );
				bandBBooking.setPrice( getCalendarDayList().get( getSelectedDayIdx() - 1 ).getPrice() );

				bandBBooking.saveDetails();
			}
//			HibernateUtil.startNewTransaction();
			createCalendarDayList();
		}
	}

	public void setCalendarDayPrice( CalendarDay calendarDay, double roomRate ) {
		calendarDay.setPrice( roomRate );
		calendarDay.setRoomAvailability( RoomAvailability.AVAILABLE );
	}

	public void setCurrentDateStr(String currentDateStr) {
		setCurrentDate( FormatUtil.parseDate( FormatUtil.getDBSimpleDateFormat(), currentDateStr ) );
	}

	public String getCurrentDateStr() {
		return FormatUtil.formatDateForDB( getCurrentDate() );
	}

	public String getCurrentDateForJavascript() {
		cal.setTime( getCurrentDate() );
		return "new Date( " + cal.get( Calendar.YEAR ) + "," + cal.get( Calendar.MONTH ) + "," + cal.get( Calendar.DAY_OF_MONTH ) + " )";
	}

	public boolean isBandBRoomSelected(BandBRoom bandBRoom) {
		if( getSelectedRoom() != null && bandBRoom != null && getSelectedRoom().getId().equals( bandBRoom.getId() ) ) {
			return true;
		} else {
			return false;
		}
	}

	public void setSelectedBandBRoomId( Long selectedBandBRoomId ) {
		getStateHelper().put( PropertyKeys.selectedBandBRoomId.name(), selectedBandBRoomId );
	}

	public Long getSelectedBandBRoomId() {
		return (Long) getStateHelper().eval( PropertyKeys.selectedBandBRoomId.name() );
	}

	public void setCurrentDate( Date currentDate ) {
		getStateHelper().put( PropertyKeys.currentDate.name(), currentDate );
	}

	public Date getCurrentDate() {
		return (Date) getStateHelper().eval( PropertyKeys.currentDate.name() );
	}

	public void setSelectedDayIdx( Integer selectedDayIdx ) {
		getStateHelper().put( PropertyKeys.selectedDayIdx.name(), selectedDayIdx );
	}

	public Integer getSelectedDayIdx() {
		return (Integer) getStateHelper().eval( PropertyKeys.selectedDayIdx.name() );
	}

	public void setSelectedRoom( BandBRoom bAndBRoom ) {
		getStateHelper().put( PropertyKeys.selectedRoom.name(), bAndBRoom );
	}

	public BandBRoom getSelectedRoom() {
		return (BandBRoom) getStateHelper().eval( PropertyKeys.selectedRoom.name() );
	}

	public void setCalendarDayList(List<CalendarDay> calendarDayList) {
		getStateHelper().put( PropertyKeys.calendarDayList.name(), calendarDayList );
	}

	public List<CalendarDay> getCalendarDayList() {
		return (List<CalendarDay>) getStateHelper().eval( PropertyKeys.calendarDayList.name() );
	}

	public void setBandBRoomList(List<BandBRoom> bandBRoomList) {
		getStateHelper().put( PropertyKeys.bandBRoomList.name(), bandBRoomList );
	}

	public List<BandBRoom> getBandBRoomList() {
		return (List<BandBRoom>) getStateHelper().eval( PropertyKeys.bandBRoomList.name() );
	}

}
