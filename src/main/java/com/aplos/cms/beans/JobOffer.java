package com.aplos.cms.beans;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
public class JobOffer extends AplosBean implements PositionedBean {
	private static final long serialVersionUID = 1159849437700339305L;

	private String jobTitle = "New Job Offer";
	@Column(columnDefinition="LONGTEXT")
	private String jobDescription;

	private Integer positionIdx;

	public JobOffer() {}

	@Override
	public String getDisplayName() {
		if( jobTitle == null ) {
			return "New Job";
		} else {
			return jobTitle;
		}
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

}
