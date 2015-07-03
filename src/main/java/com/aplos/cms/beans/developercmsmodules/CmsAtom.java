package com.aplos.cms.beans.developercmsmodules;

import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.enums.SslProtocolEnum;

@MappedSuperclass
public abstract class CmsAtom extends AplosSiteBean {
	private static final long serialVersionUID = -5977505854915787314L;
	private String name = "New Atom";
	private SslProtocolEnum sslProtocolEnum = SslProtocolEnum.DONT_CHANGE;

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract CmsAtom getCopy();

	public SslProtocolEnum getSslProtocolEnum() {
		return sslProtocolEnum;
	}

	public void setSslProtocolEnum(SslProtocolEnum sslProtocolEnum) {
		this.sslProtocolEnum = sslProtocolEnum;
	}
}
