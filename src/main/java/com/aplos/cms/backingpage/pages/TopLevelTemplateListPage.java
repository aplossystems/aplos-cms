package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=TopLevelTemplate.class)
public class TopLevelTemplateListPage extends ListPage {
	private static final long serialVersionUID = -7536477006494040838L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new TopLevelTemplateLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class TopLevelTemplateLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public TopLevelTemplateLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}

}
