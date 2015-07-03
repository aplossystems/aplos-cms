package com.aplos.cms.enums;

import com.aplos.cms.templates.emailtemplates.ArticleCommentAddedEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionConsolationEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionNonClaimantEmail;
import com.aplos.cms.templates.emailtemplates.CompetitionWinnerEmail;
import com.aplos.cms.templates.emailtemplates.ContactEmail;
import com.aplos.common.beans.communication.EmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;

public enum CmsEmailTemplateEnum implements EmailTemplateEnum {
	CONTACT ( ContactEmail.class ),
	COMPETITION_WINNER ( CompetitionWinnerEmail.class ),
	COMPETITION_CONSULATION ( CompetitionConsolationEmail.class ),
	COMPETITION_NON_CLAIMANT ( CompetitionNonClaimantEmail.class ),
	ARTICLE_COMMENT_ADDED ( ArticleCommentAddedEmail.class );

	private Class<? extends EmailTemplate> emailTemplateClass;
	boolean isActive = true;

	private CmsEmailTemplateEnum( Class<? extends EmailTemplate> emailTemplateClass ) {
		this.emailTemplateClass = emailTemplateClass;
	}

	@Override
	public Class<? extends EmailTemplate> getEmailTemplateClass() {
		return emailTemplateClass;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}