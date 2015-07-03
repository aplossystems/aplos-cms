package com.aplos.cms.beans.bookings;

import java.util.Date;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
public class BandBBooking extends AplosBean {
	private static final long serialVersionUID = -4524426276171569640L;

	@ManyToOne
	private BandBRoom bandBRoom;
	private double price;
	private Date bookingDate;

	public void setPrice(double price) {
		this.price = price;
	}
	public double getPrice() {
		return price;
	}
	public void setBandBRoom(BandBRoom bandBRoom) {
		this.bandBRoom = bandBRoom;
	}
	public BandBRoom getBandBRoom() {
		return bandBRoom;
	}
	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	public Date getBookingDate() {
		return bookingDate;
	}
}
