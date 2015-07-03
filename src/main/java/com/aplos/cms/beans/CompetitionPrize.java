package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
@ManagedBean
@SessionScoped
public class CompetitionPrize extends AplosBean implements PositionedBean {

	private static final long serialVersionUID = -8036708419427050968L;

	@ManyToOne
	private Competition competition;
	@OneToMany( mappedBy = "competitionPrize" )
	private List<CompetitionPrizeWinner> winners = new ArrayList<CompetitionPrizeWinner>();
	private String prizeName;
	private Integer positionIdx;
	private Integer prizeCount = 1;

//	@Override
//	public void hibernateInitialiseAfterCheck(boolean fullInitialisation) {
//		super.hibernateInitialiseAfterCheck(fullInitialisation);
//		HibernateUtil.initialiseList(winners, fullInitialisation);
//	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public boolean isWinnersSelected() {
		if (winners != null && winners.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean canAddWinner() {
		if (winners == null) {
			winners = new ArrayList<CompetitionPrizeWinner>();
		}
		if (prizeCount == null || prizeCount < 1) {
			prizeCount = 1;
		}
		if (winners.size() < prizeCount) {
			return true;
		}
		return false;
	}

	public void addWinner(CompetitionPrizeWinner newWinner) {
		if (!winners.contains(newWinner)){
			winners.add(newWinner);
		}
	}

	public String getWinnersUnorderedList() {
		if (winners == null) {
			return "<ul class='aplos-winner-names'><li>Not Selected</li></ul>";
		} else {
			StringBuffer buff = new StringBuffer("<ul class='aplos-winner-names'>");
			if (winners.size() > 0) {
				for (CompetitionPrizeWinner winner : winners) {
					buff.append("<li>");
					buff.append(winner.getSubscriber().getFullName());
					buff.append("</li>");
				}
			} else {
				buff.append("<li>Not Selected</li>");
			}
			buff.append("</ul>");
			return buff.toString();
		}
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public void setPrizeCount(Integer prizeCount) {
		this.prizeCount = prizeCount;
	}

	public Integer getPrizeCount() {
		return prizeCount;
	}

	public void setWinners(List<CompetitionPrizeWinner> winners) {
		this.winners = winners;
	}

	public List<CompetitionPrizeWinner> getWinners() {
		return winners;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	public Competition getCompetition() {
		return competition;
	}

}






