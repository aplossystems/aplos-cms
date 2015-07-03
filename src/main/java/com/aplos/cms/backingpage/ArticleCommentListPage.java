package com.aplos.cms.backingpage;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.cms.beans.Article;
import com.aplos.cms.beans.ArticleComment;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ArticleComment.class)
public class ArticleCommentListPage extends ListPage {
	private static final long serialVersionUID = -1072563523290932321L;
	
	public ArticleCommentListPage() {
		addRequiredStateBinding( Article.class );
	}
	
	@Override
	public AplosLazyDataModel getAplosLazyDataModel(
			DataTableState dataTableState, BeanDao beanDao) {
		return new ArticleCommentLdm(dataTableState, beanDao);
	}
	
	public class ArticleCommentLdm extends AplosLazyDataModel {
		private static final long serialVersionUID = 5230342884155444248L;

		public ArticleCommentLdm(DataTableState dataTableState, BeanDao beanDao) {
			super(dataTableState, beanDao);
		}
		
		@Override
		public List<Object> load(int first, int pageSize, String sortField,
				SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			Article article = JSFUtil.getBeanFromScope( Article.class );
			getBeanDao().addWhereCriteria( "bean.article.id = " + article.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}
		
		@Override
		public void goToNew() {
			super.goToNew();
			ArticleComment articleComment = resolveAssociatedBean();
			Article article = JSFUtil.getBeanFromScope( Article.class );
			articleComment.setArticle(article);
		}
	}
}
