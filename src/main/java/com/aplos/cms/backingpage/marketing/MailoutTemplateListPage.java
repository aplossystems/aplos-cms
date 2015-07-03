package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.MailoutTemplate;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=MailoutTemplate.class)
public class MailoutTemplateListPage extends ListPage {

	private static final long serialVersionUID = 5141438775572052808L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new MailoutTemplateLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class MailoutTemplateLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 6599918014485402433L;

		public MailoutTemplateLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}
}
