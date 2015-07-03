package com.aplos.cms.beans.bookings;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.CommonUtil;

@Entity
@DynamicMetaValueKey(oldKey={"BANDB_ROOM"})
public class BandBRoom extends AplosBean implements FileDetailsOwnerInter {
	private static final long serialVersionUID = 3591178948963957934L;

	private String roomName;
	@Column(columnDefinition="LONGTEXT")
	private String shortDescription;
	@Column(columnDefinition="LONGTEXT")
	private String description;
	@ManyToOne
	private BandBRoomType bandBRoomType;
	
	@ManyToOne
	private FileDetails imageDetails;
	
	@ManyToOne
	private CmsPageRevision cmsPageRevision;
	
	@Transient
	private BandBRoomFdoh bandBRoomFdoh = new BandBRoomFdoh(this);
	private Integer positionIdx;
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return bandBRoomFdoh;
	}

	@Override
	public String getDisplayName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	public Integer getPositionIdx() {
		return positionIdx;
	}
	
	public String getDescriptionHtml() {
		return CommonUtil.getStringOrEmpty( getDescription() ).replace( "\n", "<br/>" );
	}

	public String getImageUrl( boolean addContext ) {
		if( getImageDetails() == null || getImageDetails().getFilename().equals( "" ) ) {
			return "";
		} else {
			return getImageDetails().getFullFileUrl(addContext);
		}
	}

	public String getFullImageUrl() {
		return getImageUrl( true );
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		if (this.isNew()) {
			super.saveBean(currentUser);
		}
		FileDetails.saveFileDetailsOwner(this, BandBRoomImageKey.values(), currentUser);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}
	
	public String getCmsPageRevisionUrl() {
		if( getCmsPageRevision() != null ) {
			CmsPageUrl cmsPageUrl = new CmsPageUrl( getCmsPageRevision().getCmsPage(), true );
			return cmsPageUrl.toString();
		}
		return null;
	}
	
	public enum BandBRoomImageKey {
		IMAGE;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	public CmsPageRevision getCmsPageRevision() {
		return cmsPageRevision;
	}

	public void setCmsPageRevision(CmsPageRevision cmsPageRevision) {
		this.cmsPageRevision = cmsPageRevision;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public BandBRoomType getBandBRoomType() {
		return bandBRoomType;
	}

	public void setBandBRoomType(BandBRoomType bandBRoomType) {
		this.bandBRoomType = bandBRoomType;
	}

	private class BandBRoomFdoh extends SaveableFileDetailsOwnerHelper {
		public BandBRoomFdoh( BandBRoom bandBRoom ) {
			super( bandBRoom );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BandBRoomImageKey.IMAGE.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.B_AND_B_ROOM_IMAGE_DIR.getDirectoryPath( includeServerWorkPath );
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BandBRoomImageKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( BandBRoomImageKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}
}
