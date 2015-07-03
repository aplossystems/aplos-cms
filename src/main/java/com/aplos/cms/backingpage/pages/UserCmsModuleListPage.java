package com.aplos.cms.backingpage.pages;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=UserCmsModule.class)
public class UserCmsModuleListPage extends ListPage {
	private static final long serialVersionUID = -6911845750078791935L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new UserCmsModuleLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class UserCmsModuleLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 5067213434366915537L;

		public UserCmsModuleLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}
}
