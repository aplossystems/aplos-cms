package com.aplos.cms.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompetitionPrizeWinner.class)
public class CompetitionPrizeWinnerEditPage extends EditPage {
	private static final long serialVersionUID = 4266539813597223617L;

}
