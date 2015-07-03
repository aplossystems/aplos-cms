package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Competition.class)
public class CompetitionListPage extends ListPage {

	private static final long serialVersionUID = -6528089903370227138L;

	public String getEntryCount() {
		Competition comp = (Competition)JSFUtil.getRequest().getAttribute("tableBean");
		if (comp != null) {
			BeanDao dao = new BeanDao(CompetitionEntry.class);
			dao.setWhereCriteria("bean.competition.id=" + comp.getId());
			int count = dao.setIsReturningActiveBeans(true).getCountAll();
			return String.valueOf(count);
		}
		return "0";
	}


}
