package com.aplos.cms.beans.bookings;

import java.util.Date;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
@PluralDisplayName(name="room rates")
public class RoomRates extends AplosBean {
	private static final long serialVersionUID = 3591178948963957934L;

	@ManyToOne
	private BandBRoom bandBRoom;
	private boolean isForMonday;
	private boolean isForTuesday;
	private boolean isForWednesday;
	private boolean isForThursday;
	private boolean isForFriday;
	private boolean isForSaturday;
	private boolean isForSunday;
	private double roomRate = 95.00;
	private Date startDate;
	private Date finishDate;
	private String rateUnit;

	public RoomRates() {}

	public void setBandBRoom(BandBRoom bandBRoom) {
		this.bandBRoom = bandBRoom;
	}
	public BandBRoom getBandBRoom() {
		return bandBRoom;
	}

	public boolean isDifferentDays( RoomRates anotherRoomRates ) {
		if( (this.isForMonday && anotherRoomRates.isForMonday) ||
				(this.isForTuesday && anotherRoomRates.isForTuesday) ||
				(this.isForWednesday && anotherRoomRates.isForWednesday) ||
				(this.isForThursday && anotherRoomRates.isForThursday) ||
				(this.isForFriday && anotherRoomRates.isForFriday) ||
				(this.isForSaturday && anotherRoomRates.isForSaturday) ||
				(this.isForSunday && anotherRoomRates.isForSunday) ) {
			return false;
		} else {
			return true;
		}

	}

	public boolean isDaySelected() {
		if( this.isForMonday ||
				this.isForTuesday ||
				this.isForWednesday ||
				this.isForThursday ||
				this.isForFriday ||
				this.isForSaturday ||
				this.isForSunday ) {
			return true;
		} else {
			return false;
		}
	}

	public void setForMonday(boolean isForMonday) {
		this.isForMonday = isForMonday;
	}

	public boolean isForMonday() {
		return isForMonday;
	}

	public void setForTuesday(boolean isForTuesday) {
		this.isForTuesday = isForTuesday;
	}

	public boolean isForTuesday() {
		return isForTuesday;
	}

	public void setForWednesday(boolean isForWednesday) {
		this.isForWednesday = isForWednesday;
	}

	public boolean isForWednesday() {
		return isForWednesday;
	}

	public void setForThursday(boolean isForThursday) {
		this.isForThursday = isForThursday;
	}

	public boolean isForThursday() {
		return isForThursday;
	}

	public void setForFriday(boolean isForFriday) {
		this.isForFriday = isForFriday;
	}

	public boolean isForFriday() {
		return isForFriday;
	}

	public void setForSaturday(boolean isForSaturday) {
		this.isForSaturday = isForSaturday;
	}

	public boolean isForSaturday() {
		return isForSaturday;
	}

	public void setForSunday(boolean isForSunday) {
		this.isForSunday = isForSunday;
	}

	public boolean isForSunday() {
		return isForSunday;
	}

	public void setRoomRate(double roomRate) {
		this.roomRate = roomRate;
	}

	public double getRoomRate() {
		return roomRate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setRateUnit(String rateUnit) {
		this.rateUnit = rateUnit;
	}

	public String getRateUnit() {
		return rateUnit;
	}
}
