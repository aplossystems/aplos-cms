package com.aplos.cms.beans;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.util.StringUtil;

import antlr.StringUtils;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.EmailGenerator;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;

@Entity
public class ArticleComment extends AplosBean implements EmailGenerator {
	private static final long serialVersionUID = 8792924061528631468L;
	
	@ManyToOne
	private Article article;
	private String username;
	private String website;
	@Column(columnDefinition="LONGTEXT")
	private String comment;
	private Date showOnWebsiteDate = new Date();
	
	public String getCommentHtml() {
		String commentHtml = "";
		if( !CommonUtil.isNullOrEmpty( comment ) ) {
			commentHtml = StringEscapeUtils.escapeHtml( comment );
			commentHtml = commentHtml.replace( "\n", "<br/>" );
		}
		return commentHtml;
	}
	
	public String getShowOnWebsiteDateTimeStr() {
		return FormatUtil.formatDateTime( getShowOnWebsiteDate(), true );
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getShowOnWebsiteDate() {
		return showOnWebsiteDate;
	}
	public void setShowOnWebsiteDate(Date showOnWebsiteDate) {
		this.showOnWebsiteDate = showOnWebsiteDate;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
}
