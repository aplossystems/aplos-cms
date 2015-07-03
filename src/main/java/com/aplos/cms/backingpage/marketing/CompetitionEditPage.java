package com.aplos.cms.backingpage.marketing;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.aplos.cms.beans.Competition;
import com.aplos.cms.beans.CompetitionEntry;
import com.aplos.cms.beans.CompetitionPrize;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.cms.enums.CmsEmailTemplateEnum;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Competition.class)
public class CompetitionEditPage extends EditPage {

	private static final long serialVersionUID = 9133665850596177579L;
	private String newTermOrCondition;
	private String prizeName;
	private Integer prizeCount = 1;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		if( getPositionedBeanHelper() == null ) {
			Competition competition = JSFUtil.getBeanFromScope(Competition.class);
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) competition, (List<PositionedBean>) (List<? extends PositionedBean>) competition.getPrizeList(), CompetitionPrize.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		super.applyBtnAction();
	}

	@Override
	public void okBtnAction() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		super.okBtnAction();
	}

	public void viewEntries() {
		JSFUtil.redirect( CompetitionEntryListPage.class );
	}

	public void viewCompetitionClaims() {
		JSFUtil.redirect( CompetitionPrizeWinnerListPage.class );
	}

	public String addTermOrCondition() {
		if ( CommonUtil.getStringOrEmpty( getNewTermOrCondition() ).equals("") ) {
			JSFUtil.addMessageForError("Please enter valid text for this term or condition");
		} else {
			Competition competition = JSFUtil.getBeanFromScope(Competition.class);
			competition.addTermOrCondion(getNewTermOrCondition());
		}
		return null;
	}

	public String removeTermOrCondition() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		String bullet = (String) JSFUtil.getRequest().getAttribute("tableBean");
		competition.getTermsAndConditionsList().remove(bullet);
		return null;
	}

	public void setNewTermOrCondition(String newTermOrCondition) {
		this.newTermOrCondition = newTermOrCondition;
	}

	public String getNewTermOrCondition() {
		return newTermOrCondition;
	}

	public String removePrize() {
		CompetitionPrize competitionPrize = (CompetitionPrize) JSFUtil.getRequest().getAttribute("tableBean");
		//getPositionedBeanHelper().getSortedPositionedBeanList().indexOf( competitionPrize );
		getPositionedBeanHelper().setCurrentPositionedBean( competitionPrize );
		getPositionedBeanHelper().deleteCurrentPositionedBean();
		return null;
	}

	public String savePosition(AjaxBehaviorEvent event) {
		CompetitionPrize competitionPrize = (CompetitionPrize) JSFUtil.getRequest().getAttribute("tableBean");
		getPositionedBeanHelper().savePositionedBean( competitionPrize );
		return null;
	}

	public String addPrize() {
		if (prizeName != null && !prizeName.equals("")) {

			if (prizeCount != null && !prizeCount.equals("")) {
				Competition competition = JSFUtil.getBeanFromScope(Competition.class);
				getPositionedBeanHelper().addNewPositionedBean();
				((CompetitionPrize)getPositionedBeanHelper().getCurrentPositionedBean()).setPrizeCount(prizeCount);
				((CompetitionPrize)getPositionedBeanHelper().getCurrentPositionedBean()).setPrizeName(prizeName);
				((CompetitionPrize)getPositionedBeanHelper().getCurrentPositionedBean()).setCompetition(competition);

			} else {
				JSFUtil.addMessageForError("Please enter how many of this prize you wish to award");
			}

		} else {
			JSFUtil.addMessageForError("Please enter the prize you wish to add");
		}
		return null;
	}

	public Integer getEntryCount() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		if (competition != null) {
			BeanDao dao = new BeanDao(CompetitionEntry.class);
			dao.setWhereCriteria("bean.competition.id=" + competition.getId());
			int count = dao.setIsReturningActiveBeans(true).getCountAll();
			return count;
		}
		return 0;
	}

	public String chooseWinners() {
		//we need to know how many entries we have, as each entry can only
		//win one prize, so if we don't stop once we've exhausted them we'll
		//get stuck in a loop
		int entryCount = getEntryCount();
		int winnerCount = 0;
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		BeanDao competitonDao = new BeanDao(CompetitionEntry.class);
		competitonDao.setWhereCriteria("bean.competition.id=" + competition.getId());
		competitonDao.setOrderBy("RAND()");
		competitonDao.setMaxResults(1);
		for (CompetitionPrize competitionPrize : competition.getPrizeList()) {
			while (competitionPrize.canAddWinner() && winnerCount < entryCount) {
				CompetitionEntry winningEntry = competitonDao.setIsReturningActiveBeans(true).getFirstBeanResult();
				if (winningEntry != null) {
					CompetitionPrizeWinner competitionPrizeWinner = createNewCompetitionWinner();
					competitionPrizeWinner.setSubscriber(winningEntry.getSubscriber());
					competitionPrizeWinner.setCompetitionPrize(competitionPrize);
					competitionPrizeWinner.generateNewUniqueClaimCode();
					competitionPrizeWinner.getAddress().setContactFirstName(winningEntry.getSubscriber().getFirstName());
					competitionPrizeWinner.getAddress().setContactSurname(winningEntry.getSubscriber().getSurname());
					competitionPrizeWinner.saveDetails();
					competitionPrize.addWinner( competitionPrizeWinner );
					//make sure each entry can only win once
					competitonDao.addWhereCriteria("bean.id != " + winningEntry.getId());
					if (competition.getMaxEntriesPerUser() > 1) {
						//make sure each person can only win once
						competitonDao.addWhereCriteria("bean.subscriber.id != " + winningEntry.getSubscriber().getId());
					}
					winnerCount++;
				}
			}
		}
		competition.saveDetails();
		return null;
	}

	// This allows it to be overridden and supplied with a subclass
	public CompetitionPrizeWinner createNewCompetitionWinner() {
		return new CompetitionPrizeWinner();
	}

	public String emailWinners() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		List<BulkEmailSource> winnerList = new ArrayList<BulkEmailSource>();
		for (CompetitionPrize competitionPrize : competition.getPrizeList()) {
			winnerList.addAll(competitionPrize.getWinners());
		}
		AplosEmail aplosEmail = new AplosEmail( CmsEmailTemplateEnum.COMPETITION_WINNER, winnerList );
		if (aplosEmail.sendAplosEmailToQueue() != null) {
			competition.setWinningEmailsSent(true);
			competition.saveDetails();
			JSFUtil.addMessage("Winner emails sent.");
		} else {
			JSFUtil.addMessage("Winner email send failed.");
		}
		return null;
	}

	public String sendConsulationEmails() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		List<CompetitionEntry> competitionEntrants = new BeanDao( CompetitionEntry.class ).addWhereCriteria( "bean.competition.id = " + competition.getId() ).setIsReturningActiveBeans(true).getAll();
		for (CompetitionPrize competitionPrize : competition.getPrizeList()) {
			for (CompetitionPrizeWinner winner : competitionPrize.getWinners())	{
				for( CompetitionEntry competitionEntry : competitionEntrants ) {
					if ( competitionEntry.getSubscriber().getId().equals( winner.getSubscriber().getId() ) ) {
						competitionEntrants.remove( competitionEntry );
						break;
					}
				}
			}
		}

		AplosEmail aplosEmail = new AplosEmail( CmsEmailTemplateEnum.COMPETITION_CONSULATION, new ArrayList<BulkEmailSource>(competitionEntrants) );
		if (aplosEmail.sendAplosEmailToQueue() != null) {
			competition.setConsulationEmailsSent(true);
			competition.saveDetails();
			JSFUtil.addMessage("Consulation emails sent.");
		} else {
			JSFUtil.addMessage("Consulation email send failed.");
		}
		return null;
	}

	public String sendNonClaimantEmails() {
		Competition competition = JSFUtil.getBeanFromScope(Competition.class);
		List<BulkEmailSource> competitionPrizeWinners = new ArrayList<BulkEmailSource>();
		for (CompetitionPrize competitionPrize : competition.getPrizeList()) {
			for (CompetitionPrizeWinner winner : competitionPrize.getWinners())	{
				if ( !winner.isClaimed() ) {
					competitionPrizeWinners.add(winner);
				}
			}
		}

		AplosEmail aplosEmail = new AplosEmail( CmsEmailTemplateEnum.COMPETITION_NON_CLAIMANT, new ArrayList<BulkEmailSource>(competitionPrizeWinners) );
		if (aplosEmail.sendAplosEmailToQueue() != null) {
			competition.setNonClaimantEmailsSent(true);
			competition.saveDetails();
			JSFUtil.addMessage("Non claimant emails sent.");
		} else {
			JSFUtil.addMessage("Non claimant email send failed.");
		}

		return null;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}

	public Integer getRowPositionIdx() {
		CompetitionPrize competitionPrize = (CompetitionPrize) JSFUtil.getRequest().getAttribute("tableBean");
		//make sure the right one is selected in the next call (select items)
		if (competitionPrize != null) {
			getPositionedBeanHelper().setCurrentPositionedBean( competitionPrize );
			return competitionPrize.getPositionIdx();
		}
		return null;
	}

	public void setRowPositionIdx(Integer newIdx) {
		CompetitionPrize competitionPrize = (CompetitionPrize) JSFUtil.getRequest().getAttribute("tableBean");
		if (competitionPrize != null && newIdx != null) {
			getPositionedBeanHelper().setCurrentPositionedBean( competitionPrize );
			getPositionedBeanHelper().getCurrentPositionedBean().setPositionIdx(newIdx);
			getPositionedBeanHelper().saveCurrentPositionedBean();
		}
	}

	public void setPrizeCount(Integer prizeCount) {
		this.prizeCount = prizeCount;
	}

	public Integer getPrizeCount() {
		return prizeCount;
	}
}




















