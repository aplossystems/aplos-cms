package com.aplos.cms.backingpage.bookings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Timetable;
import com.aplos.cms.beans.bookings.BandBBooking;
import com.aplos.cms.beans.bookings.BandBRoom;
import com.aplos.cms.beans.bookings.RoomRates;
import com.aplos.cms.enums.RoomAvailability;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class TimetablePage extends BackingPage {
	private static final long serialVersionUID = -5660570824368437918L;
	private List<CalendarDay> calendarDayList;
	private List<BandBRoom> bandBRoomList;
	private BandBRoom selectedRoom;
	private int selectedDayIdx;
	private Date currentDate;
	private final Calendar cal = new GregorianCalendar();
	private Long selectedBandBRoomId;
	private String testValue = "not set";

	public TimetablePage() {
		currentDate = new Date();
		cal.setTime( currentDate );
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(year, month, date);
		currentDate = cal.getTime();

		List<Timetable> timetableList = new BeanDao( Timetable.class ).getAll();
		Timetable timetable = null;
		if( timetableList.size() == 0 ) {
			timetable = new Timetable();
			timetable.setShowingPrices(false);
			timetable.saveDetails();
		} else {
			timetable = timetableList.get( 0 );
		}

		timetable.addToScope();
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		bandBRoomList = new BeanDao( BandBRoom.class ).getAll();
//		Hibernate.initialize(bandBRoomList);
		if( (selectedRoom == null || bandBRoomList.indexOf( selectedRoom ) == -1) && bandBRoomList.size() > 0 ) {
			selectedRoom = bandBRoomList.get( 0 );
		}
		for( int i = 0, n = bandBRoomList.size(); i < n; i++ ) {
			if( bandBRoomList.get( i ).getId().equals( selectedRoom.getId() ) ) {
				selectedRoom = bandBRoomList.get( i );
			}
		}
		return true;
	}

	public String goToSelectedRoom() {
		((BandBRoom) JSFUtil.getRequest().getAttribute( "bandBRoomVar" )).addToScope();
		return null;
	}

	public void changeSelectedRoom() {
		if( selectedBandBRoomId != null ) {
			selectedRoom = (BandBRoom) new BeanDao( BandBRoom.class ).get( selectedBandBRoomId );
		}
	}

	public String getCalendarDayListJson() {
		StringBuffer calendarDayListJsonStrBuf = new StringBuffer( "[" );

		if( calendarDayList != null ) {
			for( int i = 0, n = calendarDayList.size(); i < n; i++ ) {
				calendarDayListJsonStrBuf.append( "{price : " + calendarDayList.get( i ).getPrice() );
				calendarDayListJsonStrBuf.append( ", rateUnit : '" + CommonUtil.getStringOrEmpty(calendarDayList.get( i ).getRateUnit()) + "'" );
				calendarDayListJsonStrBuf.append( ", availability : " + calendarDayList.get( i ).getRoomAvailability().ordinal() + "}" );
				if( i != (calendarDayList.size()-1) ) {
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
		cal.setTime(currentDate);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, daysInMonth);
		endOfMonth = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		startOfMonth = cal.getTime();
		calendarDayList = new ArrayList<CalendarDay>();
		for( int i = 0, n = daysInMonth; i < n; i++ ) {
			calendarDayList.add( new CalendarDay() );
		}

		setPrices( calendarDayList, cal, endOfMonth, startOfMonth, daysInMonth );
		setAvailability( calendarDayList, cal, endOfMonth, startOfMonth );

		setTestValue( "has been set" );
	}

	public void setAvailability( List<CalendarDay> calendarDayList, Calendar cal, Date endOfMonth, Date startOfMonth ) {
		if( selectedRoom != null ) {
			String endOfMonthStr = FormatUtil.formatDateForDB(endOfMonth);
			String startOfMonthStr = FormatUtil.formatDateForDB(startOfMonth);

			BeanDao bandBBookingDao = new BeanDao( BandBBooking.class );
			bandBBookingDao.addWhereCriteria( "bookingDate>='" + startOfMonthStr + "'" );
			bandBBookingDao.addWhereCriteria( "bookingDate<='" + endOfMonthStr + "'" );
			bandBBookingDao.addWhereCriteria( "bean.bandBRoom.id = " + selectedRoom.getId() );

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
		if( selectedRoom != null ) {
			String endOfMonthStr = FormatUtil.formatDateForDB(endOfMonth);
			String startOfMonthStr = FormatUtil.formatDateForDB(startOfMonth);

			BeanDao roomRatesDao = new BeanDao( RoomRates.class );
			roomRatesDao.addWhereCriteria( "startDate<='" + endOfMonthStr + "'" );
			roomRatesDao.addWhereCriteria( "finishDate>='" + startOfMonthStr + "'" );
			roomRatesDao.addWhereCriteria( "bandBRoom.id = " + selectedRoom.getId() );

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
		if( selectedRoom != null ) {
			cal.setTime( currentDate );
			cal.set(Calendar.DAY_OF_MONTH, selectedDayIdx );
			Date selectedDate = cal.getTime();
			BeanDao bandBBookinDAO = new BeanDao( BandBBooking.class );
			bandBBookinDAO.addWhereCriteria( "bean.bookingDate = '" + FormatUtil.formatDateForDB(selectedDate) + "'" );
			bandBBookinDAO.addWhereCriteria( "bean.bandBRoom.id = " + selectedRoom.getId() );
			List<BandBBooking> bandBBookingList = bandBBookinDAO.setIsReturningActiveBeans(true).getAll();

			if( bandBBookingList.size() > 0 ) {
				if( bandBBookingList.get( 0 ).isActive() ) {
					bandBBookingList.get( 0 ).delete();
				}
			} else {
				BandBBooking bandBBooking = new BandBBooking();
				bandBBooking.setBandBRoom( selectedRoom );
				bandBBooking.setBookingDate( selectedDate );
				bandBBooking.setPrice( calendarDayList.get( selectedDayIdx - 1 ).getPrice() );

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
		currentDate = FormatUtil.parseDate( FormatUtil.getDBSimpleDateFormat(), currentDateStr );
	}

	public String getCurrentDateStr() {
		return FormatUtil.formatDateForDB( currentDate );
	}

	public String getCurrentDateForJavascript() {
		cal.setTime( currentDate );
		return "new Date( " + cal.get( Calendar.YEAR ) + "," + cal.get( Calendar.MONTH ) + "," + cal.get( Calendar.DAY_OF_MONTH ) + " )";
	}

	public List<BandBRoom> getBandBRoomList() {
		return bandBRoomList;
	}

	public boolean isBandBRoomSelected() {
		if( selectedRoom == JSFUtil.getRequest().getAttribute( "bandBRoomVar" ) ) {
			return true;
		} else {
			return false;
		}
	}

	public void setSelectedDayIdx(int selectedDayIdx) {
		this.selectedDayIdx = selectedDayIdx;
	}

	public int getSelectedDayIdx() {
		return selectedDayIdx;
	}

	public void setSelectedBandBRoomId(Long selectedBandBRoomId) {
		this.selectedBandBRoomId = selectedBandBRoomId;
	}

	public Long getSelectedBandBRoomId() {
		return selectedBandBRoomId;
	}

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}

	private class CalendarDay {
		private double price;
		private String rateUnit;
		private RoomAvailability roomAvailability = RoomAvailability.NO_RATE;

		public void setPrice(double price) {
			this.price = price;
		}
		public double getPrice() {
			return price;
		}
		public void setRoomAvailability(RoomAvailability roomAvailability) {
			this.roomAvailability = roomAvailability;
		}
		public RoomAvailability getRoomAvailability() {
			return roomAvailability;
		}
		public void setRateUnit(String rateUnit) {
			this.rateUnit = rateUnit;
		}
		public String getRateUnit() {
			return rateUnit;
		}
	}
}
