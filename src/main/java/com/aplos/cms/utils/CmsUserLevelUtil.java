package com.aplos.cms.utils;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.OneToOne;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.utils.UserLevelUtil;

@Entity
public class CmsUserLevelUtil extends UserLevelUtil {
	private static final long serialVersionUID = -8134193277014075307L;

	@OneToOne(fetch=FetchType.LAZY)
	private UserLevel basicUserLevel; //basic is not default, this is a limited account for budget websites

	public UserLevel getBasicUserLevel() {
		return basicUserLevel;
	}

	public void setBasicUserLevel(UserLevel basicUserLevel) {
		this.basicUserLevel = basicUserLevel;
	}
}
