package com.aplos.cms.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;

@ManagedBean
@ViewScoped
public class VideoFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 1539477510944447666L;

	public List<HostedVideo> getVideos() {
		List<HostedVideo> video_list = new BeanDao( HostedVideo.class ).setIsReturningActiveBeans( true ).getAll();
		if (video_list==null) {
			return new ArrayList<HostedVideo>();
		}
		return video_list;
	}
}
