package com.aplos.cms.beans.developercmsmodules;

import javax.faces.model.SelectItem;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.module.CommonConfiguration;

@Entity
@DynamicMetaValueKey(oldKey="GOOGLE_MAP_MODULE")
public class GoogleMapModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = 2641495852276910574L;

	private static final String MAP_TYPE_HYBRID = "google.maps.MapTypeId.HYBRID";
	private static final String MAP_TYPE_ROADMAP = "google.maps.MapTypeId.ROADMAP";
	private static final String MAP_TYPE_SATELLITE = "google.maps.MapTypeId.SATELLITE";
	private static final String MAP_TYPE_TERRAIN = "google.maps.MapTypeId.TERRAIN";

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address mapLocation;
	private Integer heightPx=250;
	private Integer widthPx=250;
	private Boolean disableUi=false;
	private String mapType = MAP_TYPE_HYBRID;
	private Integer zoomLevel = 8;

	public GoogleMapModule() {
		super();
		if (CommonConfiguration.getCommonConfiguration() != null) {
			CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
			if (companyDetails != null) {
				Address companyAddress = companyDetails.getAddress();
				if (companyAddress != null) {
					setMapLocation( companyAddress.getCopy() );
				}
			}
		}
		if( getMapLocation() == null ) {
			setMapLocation( new Address() );
			getMapLocation().initialiseNewBean();
		}
	}

	@Override
	public String getName() {
		return "Google Map";
	}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		mapLocation.hibernateInitialise( true );
//	}
//
//	@Override
//	public boolean initFrontend(boolean isRequestPageLoad) {
//		super.initFrontend(isRequestPageLoad);
//		this.hibernateInitialise( true );
//		return true;
//	}

	@Override
	public DeveloperCmsAtom getCopy() {
		GoogleMapModule googleMapAtom = new GoogleMapModule();
		googleMapAtom.setHeightPx(heightPx);
		googleMapAtom.setWidthPx(widthPx);
		googleMapAtom.setDisableUi(disableUi);
		googleMapAtom.setMapType(mapType);
		googleMapAtom.setZoomLevel(zoomLevel);
		return googleMapAtom;
	}

	public SelectItem[] getMapTypeSelectItems() {
		SelectItem[] items = new SelectItem[4];
		items[0] = new SelectItem(MAP_TYPE_HYBRID,"Hybrid");
		items[1] = new SelectItem(MAP_TYPE_ROADMAP,"Roadmap");
		items[2] = new SelectItem(MAP_TYPE_SATELLITE,"Satellite");
		items[3] = new SelectItem(MAP_TYPE_TERRAIN,"Terain");
		return items;
	}

	public SelectItem[] getZoomLevelSelectItems() {
		SelectItem[] items = new SelectItem[5];
		items[0] = new SelectItem(0,"Atlas Level - Completely Zoomed out");
		items[1] = new SelectItem(1,"Country Level");
		items[2] = new SelectItem(8,"Region Level");
		items[3] = new SelectItem(16,"Area Level");
		items[4] = new SelectItem(20,"Street Level - Zoomed in Close");
		return items;
	}

	public static String getGeolocationAddressString(Address address, String defaultForBlank) {
		StringBuffer locationBuffer = new StringBuffer();
		if (address != null) {
			if (address.getLine1() != null &&
			   !address.getLine1().equals("")) {
				locationBuffer.append(address.getLine1());
				locationBuffer.append(", ");
			}
			if (address.getLine2() != null &&
			   !address.getLine2().equals("")) {
				locationBuffer.append(address.getLine2());
				locationBuffer.append(", ");
			}
			if (address.getLine3() != null &&
			   !address.getLine3().equals("")) {
				locationBuffer.append(address.getLine3());
				locationBuffer.append(", ");
			}
			if (address.getCity() != null &&
			   !address.getCity().equals("")) {
				locationBuffer.append(address.getCity());
				locationBuffer.append(", ");
			}
			if (address.getState() != null &&
			   !address.getState().equals("")) {
				locationBuffer.append(address.getState());
				locationBuffer.append(", ");
			}
			if (address.getCountry() != null &&
			   !address.getCountry().equals("")) {
				locationBuffer.append(address.getCountry());
				locationBuffer.append(", ");
			}
			if (address.getPostcode() != null &&
			   !address.getPostcode().equals("")) {
				locationBuffer.append(address.getPostcode());
			}
		}
		if (locationBuffer.toString().equals("") && defaultForBlank != null) {
			return defaultForBlank;
		}
		return locationBuffer.toString();
	}

	public String getGeolocationAddressString() {
		//we always need to return some locations so return Aplos Address when no data is entered
		return getGeolocationAddressString(mapLocation, "Bedford Place, Southampton, Hampshire, United Kingdom, SO40 3WW");
	}


	public void setHeightPx(Integer heightPx) {
		this.heightPx = heightPx;
	}

	public Integer getHeightPx() {
		return heightPx;
	}

	public void setWidthPx(Integer widthPx) {
		this.widthPx = widthPx;
	}

	public Integer getWidthPx() {
		return widthPx;
	}

	public void setDisableUi(Boolean disableUi) {
		this.disableUi = disableUi;
	}

	public Boolean getDisableUi() {
		return disableUi;
	}

	public String getDisableUiString() {
		return (disableUi)?"true":"false";
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public String getMapType() {
		return mapType;
	}

	public void setZoomLevel(Integer zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public Integer getZoomLevel() {
		return zoomLevel;
	}

	public void setMapLocation(Address mapLocation) {
		this.mapLocation = mapLocation;
	}

	public Address getMapLocation() {
		return mapLocation;
	}

}
