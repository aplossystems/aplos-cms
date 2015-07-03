package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Mailout;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.module.CommonConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Mailout.class)
public class MailoutListPage extends ListPage {

	private static final long serialVersionUID = -4913726289145561055L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new MailoutLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class MailoutLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public MailoutLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			super.goToNew();
			Mailout mailout = getAssociatedBeanFromScope();
			mailout.setCompanyDetails( CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails() );
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.mailoutTemplate.name LIKE :similarSearchText";
		}

	}


}
