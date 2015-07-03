package com.aplos.cms.backingpage.marketing;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompetitionEntry.class)
public class CompetitionEntryListPage extends ListPage {

	private static final long serialVersionUID = 323152468474794405L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompetitionEntryLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CompetitionEntryLazyDataModel extends AplosLazyDataModel {
		private static final long serialVersionUID = -6527645252493887424L;

		public CompetitionEntryLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			dataTableState.setShowingIdColumn(false);
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().setWhereCriteria("bean.competition.id=" + JSFUtil.getBeanFromScope(Competition.class).getId());
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

	public String backToCompetition(){
		JSFUtil.redirect(CompetitionEditPage.class);
		return null;
	}

}
