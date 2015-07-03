package com.aplos.cms.beans;

import java.util.Date;

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
import com.aplos.common.interfaces.PositionedBean;

@Entity
@DynamicMetaValueKey(oldKey="EXHIBITION")
public class Exhibition extends AplosBean implements PositionedBean, FileDetailsOwnerInter {

	private static final long serialVersionUID = 7830375936666295830L;

	private Date startDate;
	private String title;
	private String website;
	private String venue;
	private String city;
	private String descriptiveDate;
	private String standNumber;
	@Column(columnDefinition="LONGTEXT")
	private String description;
//	private String logoUrl;
//	private String exhibitionImageUrl;
	private boolean visibleOnWebsite=true;
	@Column(columnDefinition="LONGTEXT")
	private String pressRelease;
//	private String prImage1Url;
//	private String prImage2Url;
//	private String prImage3Url;
	private Integer positionIdx;

	@ManyToOne
	private FileDetails logoDetails;
	@ManyToOne
	private FileDetails exhibitionImageDetails;
	@ManyToOne
	private FileDetails prOneDetails;
	@ManyToOne
	private FileDetails prTwoDetails;
	@ManyToOne
	private FileDetails prThreeDetails;

	@Transient
	private ExhibitionFdoh exhibitionFdoh = new ExhibitionFdoh(this);
	
	@Override
	public void saveBean( SystemUser systemUser ) {
		FileDetails.saveFileDetailsOwner(this, ExhibitionImageKey.values(), systemUser);
	}

	@Override
	public String getDisplayName() {
		if( getTitle() == null ) {
			return "New Exhibition";
		} else {
			return getTitle();
		}
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return exhibitionFdoh;
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public void setPressRelease(String pressRelease) {
		this.pressRelease = pressRelease;
	}

	public String getPressRelease() {
		return pressRelease;
	}

	public void setVisibleOnWebsite(boolean visibleOnWebsite) {
		this.visibleOnWebsite = visibleOnWebsite;
	}

	public boolean isVisibleOnWebsite() {
		return visibleOnWebsite;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescriptiveDate(String descriptiveDate) {
		this.descriptiveDate = descriptiveDate;
	}

	public String getDescriptiveDate() {
		return descriptiveDate;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getVenue() {
		return venue;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getWebsite() {
		return website;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setStandNumber(String standNumber) {
		this.standNumber = standNumber;
	}

	public String getStandNumber() {
		return standNumber;
	}

	public FileDetails getLogoDetails() {
		return logoDetails;
	}

	public void setLogoDetails(FileDetails logoDetails) {
		this.logoDetails = logoDetails;
	}

	public FileDetails getExhibitionImageDetails() {
		return exhibitionImageDetails;
	}

	public void setExhibitionImageDetails(FileDetails exhibitionImageDetails) {
		this.exhibitionImageDetails = exhibitionImageDetails;
	}

	public FileDetails getPrOneDetails() {
		return prOneDetails;
	}

	public void setPrOneDetails(FileDetails prOneDetails) {
		this.prOneDetails = prOneDetails;
	}

	public FileDetails getPrTwoDetails() {
		return prTwoDetails;
	}

	public void setPrTwoDetails(FileDetails prTwoDetails) {
		this.prTwoDetails = prTwoDetails;
	}

	public FileDetails getPrThreeDetails() {
		return prThreeDetails;
	}

	public void setPrThreeDetails(FileDetails prThreeDetails) {
		this.prThreeDetails = prThreeDetails;
	}
	
	
	

	public enum ExhibitionImageKey {
		LOGO,
		EXHIBITION,
		PR_ONE,
		PR_TWO,
		PR_THREE;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class ExhibitionFdoh extends SaveableFileDetailsOwnerHelper {
		public ExhibitionFdoh( Exhibition exhibition ) {
			super( exhibition );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (ExhibitionImageKey.LOGO.name().equals(fileDetailsKey) ||
					ExhibitionImageKey.EXHIBITION.name().equals(fileDetailsKey) ||
						ExhibitionImageKey.PR_ONE.name().equals(fileDetailsKey) ||
							ExhibitionImageKey.PR_TWO.name().equals(fileDetailsKey) ||
								ExhibitionImageKey.PR_THREE.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.EXHIBITION_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (ExhibitionImageKey.LOGO.name().equals(fileDetailsKey)) {
				setLogoDetails(fileDetails);
			} else if (ExhibitionImageKey.EXHIBITION.name().equals(fileDetailsKey)) {
				setExhibitionImageDetails(fileDetails);
			} else if (ExhibitionImageKey.PR_ONE.name().equals(fileDetailsKey)) {
				setPrOneDetails(fileDetails);
			} else if (ExhibitionImageKey.PR_TWO.name().equals(fileDetailsKey)) {
				setPrTwoDetails(fileDetails);
			} else if (ExhibitionImageKey.PR_THREE.name().equals(fileDetailsKey)) {
				setPrThreeDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
				if (ExhibitionImageKey.LOGO.name().equals(fileDetailsKey)) {
					return getLogoDetails();
				} else if (ExhibitionImageKey.EXHIBITION.name().equals(fileDetailsKey)) {
					return getExhibitionImageDetails();
				} else if (ExhibitionImageKey.PR_ONE.name().equals(fileDetailsKey)) {
					return getPrOneDetails();
				} else if (ExhibitionImageKey.PR_TWO.name().equals(fileDetailsKey)) {
					return getPrTwoDetails();
				} else if (ExhibitionImageKey.PR_THREE.name().equals(fileDetailsKey)) {
					return getPrThreeDetails();
				}
			return null;
		}
	}

}
