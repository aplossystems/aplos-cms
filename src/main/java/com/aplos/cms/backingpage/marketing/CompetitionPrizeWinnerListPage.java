package com.aplos.cms.backingpage.marketing;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompetitionPrizeWinner.class)
public class CompetitionPrizeWinnerListPage extends ListPage {

	private static final long serialVersionUID = -744534840050717011L;

	public String getClaimCompletedText() {
		CompetitionPrizeWinner competitionPrizeWinner = (CompetitionPrizeWinner) JSFUtil.getRequest().getAttribute("tableBean");
		if (competitionPrizeWinner != null) {
			if (competitionPrizeWinner.isClaimed()) {
				return "Yes";
			} else {
				return "No";
			}
		}
		return null;
	}

	public String backToCompetition(){
		JSFUtil.redirect(CompetitionEditPage.class);
		return null;
	}

	public String getPrizeSentText() {
		CompetitionPrizeWinner competitionPrizeWinner = (CompetitionPrizeWinner) JSFUtil.getRequest().getAttribute("tableBean");
		if (competitionPrizeWinner != null) {
			if (competitionPrizeWinner.isSent()) {
				return "Yes";
			} else {
				return "No";
			}
		}
		return null;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompetitionClaimLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CompetitionClaimLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -1804937212806916957L;

		public CompetitionClaimLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().setWhereCriteria("bean.competitionPrize.competition.id=" + JSFUtil.getBeanFromScope(Competition.class).getId());
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}


}
