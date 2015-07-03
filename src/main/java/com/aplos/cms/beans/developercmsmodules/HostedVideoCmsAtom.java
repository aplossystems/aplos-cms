package com.aplos.cms.beans.developercmsmodules;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;

@Entity
@DynamicMetaValueKey(oldKey="HOSTED_VIDEO_ATOM")
public class HostedVideoCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 3307652038072619407L;
	
	@ManyToOne
	private HostedVideo hostedVideo;

	@Override
	public String getName() {
		return "You Tube";
	}
	
	@Override
	public DeveloperCmsAtom getCopy() {
		HostedVideoCmsAtom hostedVideoCmsAtomCopy = new HostedVideoCmsAtom();
		hostedVideoCmsAtomCopy.setHostedVideo( getHostedVideo() );
		return hostedVideoCmsAtomCopy;
	}

	public HostedVideo getHostedVideo() {
		return hostedVideo;
	}

	public void setHostedVideo(HostedVideo hostedVideo) {
		this.hostedVideo = hostedVideo;
	}
}
