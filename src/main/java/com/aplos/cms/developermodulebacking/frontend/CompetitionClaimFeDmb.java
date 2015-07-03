package com.aplos.cms.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class CompetitionClaimFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8293628697246686816L;

	private CompetitionPrizeWinner competitionPrizeWinner = null;
	private String completeMessage = null;

	public CompetitionClaimFeDmb() {}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		if (competitionPrizeWinner == null) {
			String claimCode = JSFUtil.getRequestParameter("claimCode");
			if (claimCode != null && !claimCode.equals("")) {
				competitionPrizeWinner = CompetitionPrizeWinner.getCompetitionPrizeWinnerIdByCode(claimCode);
				if (competitionPrizeWinner != null) {
					if (competitionPrizeWinner.isClaimed()) {
						completeMessage = "Your claim has been placed. We're processing it and you should hear from us or receive your prize soon.";
					}
				} else {
					completeMessage = "Sorry, your claim has not been recognised. If you received a winners email from us, please contact us directly to pursue your claim.";
				}
			} else {
				completeMessage = "Sorry, some required information is missing, please contact us directly to pursue your claim.";
			}
		}
		return super.responsePageLoad(developerCmsAtom);
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("claimPrizeBtn");
	}

	public String submitClaim() {
		competitionPrizeWinner.setClaimed(true);
		competitionPrizeWinner.saveDetails();
		completeMessage = "Thanks! Your claim has been submitted, you should receive your prize or be contacted by us shortly.";
		return null;
	}

	public void setCompetitionPrizeWinner(CompetitionPrizeWinner competitionPrizeWinner) {
		this.competitionPrizeWinner = competitionPrizeWinner;
	}

	public CompetitionPrizeWinner getCompetitionPrizeWinner() {
		return competitionPrizeWinner;
	}

	public void setCompleteMessage(String completeMessage) {
		this.completeMessage = completeMessage;
	}

	public String getCompleteMessage() {
		return completeMessage;
	}

}














