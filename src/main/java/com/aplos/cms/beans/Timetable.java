package com.aplos.cms.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
public class Timetable extends AplosBean {
	private static final long serialVersionUID = -3212743099065283124L;
	private boolean isShowingPrices;

	public void setShowingPrices(boolean isShowingPrices) {
		this.isShowingPrices = isShowingPrices;
	}

	public boolean isShowingPrices() {
		return isShowingPrices;
	}


}
