package com.aplos.cms.beans;

import java.util.Date;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.FormatUtil;

@Entity
public class Testimonial extends AplosBean {

	private static final long serialVersionUID = -3989655478999498618L;
	
	private String city;
	@Column(columnDefinition="LONGTEXT")
	private String comment;
	private String occupation;
	private Date submissionDate = null;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}
	
	public String getSubmissionDateStdStr() {
		return FormatUtil.formatDate(submissionDate);
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

}
