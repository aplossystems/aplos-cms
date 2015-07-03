package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=HostedVideo.class)
public class HostedVideoListPage extends ListPage  {

	private static final long serialVersionUID = -5366421670117244203L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new HostedVideoLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class HostedVideoLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -2916078804430026982L;

		public HostedVideoLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.title LIKE :similarSearchText";
		}

	}
}
