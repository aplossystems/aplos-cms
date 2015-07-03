package com.aplos.cms.developermodulebacking.frontend;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Article;
import com.aplos.cms.beans.ArticleComment;
import com.aplos.cms.beans.BasicCmsContent;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.cms.enums.CmsRedirectFolder;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class ArticleFeDmb extends BasicCmsContentFeDmb {
	private static final long serialVersionUID = -5254221973530134503L;
	
	private ArticleComment newArticleComment = new ArticleComment();
	private List<ArticleComment> viewableComments;
	private String currentYear;
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);
		if( viewableComments == null && getSelectedBasicCmsContent() != null ) {
			Article article = (Article) getSelectedBasicCmsContent();
			BeanDao articleCommentDao = new BeanDao( ArticleComment.class );
			articleCommentDao.addWhereCriteria( "bean.article.id = " + article.getId() );
			articleCommentDao.addWhereCriteria( "bean.showOnWebsiteDate <= NOW()" );
			articleCommentDao.setOrderBy( "bean.showOnWebsiteDate DESC" );
			setViewableComments( articleCommentDao.getAll() );
		}
		return continueLoad;
	}
	
	public boolean isCommentValidationRequired() {
		return BackingPage.validationRequired("addNewCommentBtn");
	}
	
	public void addArticleComment() {
		if( !getCurrentYear().equals( String.valueOf( Calendar.getInstance().get( Calendar.YEAR ) ) ) ) {
			JSFUtil.addMessageForWarning( "Please set the correct year in the current year field" );
			return;
		}
		Article article = (Article) getSelectedBasicCmsContent();
		getNewArticleComment().setArticle( article );
		getNewArticleComment().setShowOnWebsiteDate(new Date());
		if( getNewArticleComment().getWebsite() != null && !getNewArticleComment().getWebsite().startsWith( "http" ) ) {
			getNewArticleComment().setWebsite("http://" + getNewArticleComment().getWebsite());
		}
		getNewArticleComment().saveDetails();
		getViewableComments().add( 0, getNewArticleComment() );

		SystemUser adminUser = CommonConfiguration.getCommonConfiguration().getDefaultAdminUser();
		AplosEmail aplosEmail = new AplosEmail( CmsEmailTemplateEnum.ARTICLE_COMMENT_ADDED, adminUser, getNewArticleComment() );
		aplosEmail.sendAplosEmailToQueue();
		
		setNewArticleComment(getNewArticleComment());
		
		JSFUtil.addMessage( "Your message has been added");
	}
	
	@Override
	public Class<? extends BasicCmsContent> getBeanClass() {
		return Article.class;
	}
	
	@Override
	public CmsRedirectFolder getCmsRedirectFolder() {
		return CmsRedirectFolder.ARTICLES;
	}

	public ArticleComment getNewArticleComment() {
		return newArticleComment;
	}

	public void setNewArticleComment(ArticleComment newArticleComment) {
		this.newArticleComment = newArticleComment;
	}

	public List<ArticleComment> getViewableComments() {
		return viewableComments;
	}

	public void setViewableComments(List<ArticleComment> viewableComments) {
		this.viewableComments = viewableComments;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}
}
