package com.aplos.cms.beans.bookings;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.FormatUtil;

@Entity
public class BandBRoomType extends AplosBean {
	private static final long serialVersionUID = -7919408707031176161L;
	
	private String name;
	private BigDecimal cost = new BigDecimal( 0 );
	@Column(columnDefinition="TEXT") 
	private String description;
	
	@Override
	public String getDisplayName() {
		return getName();
	}
	
	public String getCostStr() {
		return FormatUtil.formatUkCurrency(getCost());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
