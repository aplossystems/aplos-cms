package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.CssResource;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CssResource.class)
public class CssResourceListPage extends ListPage {
	private static final long serialVersionUID = -380514554570683954L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CssResourceLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CssResourceLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7262559730968505557L;

		public CssResourceLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.description LIKE :similarSearchText";
		}

	}
}
