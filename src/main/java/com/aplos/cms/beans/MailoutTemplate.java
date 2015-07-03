package com.aplos.cms.beans;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
public class MailoutTemplate extends AplosBean {
	private static final long serialVersionUID = 946623444259278962L;

	private String name;

	@Column(columnDefinition="LONGTEXT")
	private String mailoutHeader;

    @Column(columnDefinition="LONGTEXT")
	private String mailoutFooter;

    @Column(columnDefinition="LONGTEXT")
	private String defaultMailoutContent;

	public MailoutTemplate() {}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMailoutHeader(String mailoutHeader) {
		this.mailoutHeader = mailoutHeader;
	}

	public String getMailoutHeader() {
		return mailoutHeader;
	}

	public void setMailoutFooter(String mailoutFooter) {
		this.mailoutFooter = mailoutFooter;
	}

	public String getMailoutFooter() {
		return mailoutFooter;
	}

	public void setDefaultMailoutContent(String defaultMailoutContent) {
		this.defaultMailoutContent = defaultMailoutContent;
	}

	public String getDefaultMailoutContent() {
		return defaultMailoutContent;
	}

	@Override
	public String getDisplayName() {
		return name;
	}
}
