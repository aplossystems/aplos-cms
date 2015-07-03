package com.aplos.cms.beans;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.beans.SystemUser;

@Entity
@DiscriminatorValue( "AplosCmsUser" )
public class AplosCmsUser extends SystemUser {

	private static final long serialVersionUID = -4054796470115806683L;

	@CollectionOfElements(fetch=FetchType.EAGER)
	private Set<String> rights = new HashSet<String>();

	public boolean checkRights(String right) {
		if (isAdmin()) {
			return true;
		}
		if (rights.contains( right )) {
			return true;
		}

		// Check for wildcard in current rights
		String wild = right.replaceFirst( "\\.[^.]*$", ".*" );
		if (rights.contains( wild )) { return true; }

		wild = right.replaceFirst( "\\.[^.]*\\.[^.]*$", ".*" );
		if (rights.contains( wild )) { return true; }

		// Check for wildcard tail matching (e.g. users.*)
		if (!right.endsWith( "*" )) {
			return false;
		} else {
			for (String s : rights) {
				if (s.matches( Pattern.quote(right.replaceFirst( "\\*$", "" )) + ".*" )) {
					return true;
				}
			}
		}

		return false;
	}

	public void setRights( Set<String> rights ) {
		this.rights = rights;
	}

	public Set<String> getRights() {
		return rights;
	}

}
