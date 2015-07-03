package com.aplos.cms.beans.bookings;

import com.aplos.cms.enums.RoomAvailability;

public class CalendarDay {
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
