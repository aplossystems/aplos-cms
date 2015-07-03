package com.aplos.cms.templates.emailtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.ArticleComment;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.EmailTemplate;
import com.aplos.common.beans.communication.SingleEmailRecord;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
public class ArticleCommentAddedEmail extends EmailTemplate<SystemUser,ArticleComment> {
	private static final long serialVersionUID = -6897809802751614047L;

	public ArticleCommentAddedEmail() {
	}
	
	public String getDefaultName() {
		return "Ariticle comment added";
	}

	@Override
	public String getDefaultSubject() {
		return "Ariticle comment added";
	}
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "articleCommentAdded.html" );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			SystemUser bulkEmailRecipient, ArticleComment articleComment,
			SingleEmailRecord singleEmailRecord) {
		super.addContentJDynamiTeValues(jDynamiTe, bulkEmailRecipient, articleComment,
				singleEmailRecord);
		jDynamiTe.setVariable("ARTICLE_TITLE", CommonUtil.getStringOrEmpty(articleComment.getArticle().getTitle()));
		jDynamiTe.setVariable("ARTICLE_COMMENT", CommonUtil.getStringOrEmpty(articleComment.getComment()));
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return CmsEmailTemplateEnum.ARTICLE_COMMENT_ADDED;
	}

}
