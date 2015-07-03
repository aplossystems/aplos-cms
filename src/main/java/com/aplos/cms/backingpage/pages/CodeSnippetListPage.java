package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CodeSnippet.class)
public class CodeSnippetListPage extends ListPage {
	private static final long serialVersionUID = 1754028900369752956L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CodeSnippetLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CodeSnippetLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7809173540226622900L;

		public CodeSnippetLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getDataTableState().setShowingDeleteColumn(false);
			getDataTableState().setShowingEditPencilColumn(false);
			getDataTableState().setShowingIdColumn(true);
			getDataTableState().setShowingNewBtn(false);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.content LIKE :similarSearchText";
		}

	}
}
