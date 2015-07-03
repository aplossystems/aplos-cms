package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.Testimonial;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Testimonial.class)
public class TestimonialListPage extends ListPage {

	private static final long serialVersionUID = 7870677098988085481L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao beanDao) {
		return new TestimonialLazyDataModel(dataTableState, beanDao);
	}

	public class TestimonialLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public TestimonialLazyDataModel(DataTableState dataTableState, BeanDao beanDao) {
			super(dataTableState, beanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.comment LIKE :searchText OR bean.city LIKE :searchText";
		}

	}

}
