package com.aplos.cms.enums;

import com.aplos.common.enums.SslProtocolEnum;

public interface UnconfigurableAtomEnum {
	public String getCmsAtomIdStr();
	public String getCmsAtomName();
	public SslProtocolEnum getSslProtocolEnum();
	public void setActive(boolean isActive);
	public boolean isActive();
}
