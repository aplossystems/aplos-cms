package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.JavascriptResource;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=JavascriptResource.class)
public class JavascriptResourceListPage extends ListPage {
	private static final long serialVersionUID = -3171534951174773143L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new JavascriptResourceLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class JavascriptResourceLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 2532323208311056559L;

		public JavascriptResourceLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.description LIKE :similarSearchText";
		}

	}
}
