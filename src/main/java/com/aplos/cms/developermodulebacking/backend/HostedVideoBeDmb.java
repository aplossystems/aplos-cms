package com.aplos.cms.developermodulebacking.backend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class HostedVideoBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1664818719723132424L;


	public SelectItem[] getHostedVideoSelectItemBeans() {
		BeanDao dao = new BeanDao(HostedVideo.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "No Video");
	}

}
