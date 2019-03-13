package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AssetContext extends ResourceContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AssetState state;
	public int getState() {
		if(state != null) {
			return state.getIntVal();
		}
		return -1;
	}
	public void setState(int state) {
		this.state = AssetState.STATE_MAP.get(state);
	}
	public void setState(AssetState state) {
		this.state = state;
	}
	public String getStateVal() {
		if(state != null) {
			return state.getStringVal();
		}
		return null;
	}
	
	private AssetTypeContext type;
	public AssetTypeContext getType() {
		return type;
	}
	public void setType(AssetTypeContext type) {
		this.type = type;
	}
	
	private AssetCategoryContext category;
	public AssetCategoryContext getCategory() {
		return category;
	}
	public void setCategory(AssetCategoryContext category) {
		this.category = category;
	}

	private long parentAssetId = -1;
	public long getParentAssetId() {
		return parentAssetId;
	}
	public void setParentAssetId(long parentAssetId) {
		this.parentAssetId = parentAssetId;
	}
	
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}
	
	@Override
	public long getSpaceId() {
		// TODO Auto-generated method stub
		if (space != null) {
			return space.getId();
		}
		return super.getSpaceId();
	}
	
	private AssetDepartmentContext department;
	public AssetDepartmentContext getDepartment() {
		return department;
	}
	public void setDepartment(AssetDepartmentContext department) {
		this.department = department;
	}

	private String manufacturer;
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	private String model;
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	private String serialNumber;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	private String tagNumber;
	public String getTagNumber() {
		return tagNumber;
	}
	public void setTagNumber(String tagNumber) {
		this.tagNumber = tagNumber;
	}
	
	private String partNumber;
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	
	private int unitPrice = -1;
	public int getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	private String supplier;
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	private long purchasedDate = -1;
	public long getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(long purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	
	private long retireDate;
	public long getRetireDate() {
		return retireDate;
	}
	public void setRetireDate(long retireDate) {
		this.retireDate = retireDate;
	}
	
	private long warrantyExpiryDate = -1;
	public long getWarrantyExpiryDate() {
		return warrantyExpiryDate;
	}
	public void setWarrantyExpiryDate(long warrantyExpiryDate) {
		this.warrantyExpiryDate = warrantyExpiryDate;
	}
	
	public long getLocalId() {
		return super.getLocalId();
	}
	public void setLocalId(long localId) {
		super.setLocalId(localId);
	}
	
	private Boolean hideToCustomer;
	public Boolean getHideToCustomer() {
		return hideToCustomer;
	}
	public void setHideToCustomer(Boolean hideToCustomer) {
		this.hideToCustomer = hideToCustomer;
	}
	public Boolean isHideToCustomer() {
		if(hideToCustomer != null) {
			return hideToCustomer.booleanValue();
		}
		return false;
	}

	
	@Override
	public ResourceType getResourceTypeEnum() {
		return ResourceType.ASSET;
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getResourceType() {
		return ResourceType.ASSET.getValue();
	}
	
	private String geoLocation;
	public String getGeoLocation() {
		return geoLocation;
	}
	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}
	
	private Boolean geoLocationEnabled;
	public Boolean getGeoLocationEnabled() {
		return geoLocationEnabled;
	}
	public void setGeoLocationEnabled(Boolean geoLocationEnabled) {
		this.geoLocationEnabled = geoLocationEnabled;
	}
	public boolean isGeoLocationEnabled() {
		if (geoLocationEnabled != null) {
			return geoLocationEnabled.booleanValue();
		}
		return false;
	}
	
	private String currentLocation;
	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	private Boolean designatedLocation;
	public Boolean getDesignatedLocation() {
		return designatedLocation;
	}
	public void setDesignatedLocation(Boolean designatedLocation) {
		this.designatedLocation = designatedLocation;
	}
	public boolean isDesignatedLocation() {
		if (designatedLocation != null) {
			return designatedLocation.booleanValue();
		}
		return false;
	}

	private int boundaryRadius = -1;
	public int getBoundaryRadius() {
		return boundaryRadius;
	}
	public void setBoundaryRadius(int boundaryRadius) {
		this.boundaryRadius = boundaryRadius;
	}
	
	private double distanceMoved = -1;
	public double getDistanceMoved() {
		return distanceMoved;
	}
	public void setDistanceMoved(double distanceMoved) {
		this.distanceMoved = distanceMoved;
	}

	private SiteContext identifiedLocation;
	public SiteContext getIdentifiedLocation() {
		return identifiedLocation;
	}
	public void setIdentifiedLocation(SiteContext identifiedLocation) {
		this.identifiedLocation = identifiedLocation;
	}
	
	private String url;
	
	public String getUrl() {
		return "app/at/asset/all/" + getId() + "/overview";
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public static enum AssetState {
		ACTIVE(1, "Active"),
		IN_STORE(2, "In Store"),
		IN_REPAIR(3, "In Repair"),
		INACTIVE(4, "Inactive"),
		RETIRED(5, "Retired")
		;
		
		private int intVal;
		private String strVal;
		
		private AssetState(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static AssetState getType(int val) {
			return STATE_MAP.get(val);
		}
		
		private static final Map<Integer, AssetState> STATE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AssetState> initTypeMap() {
			Map<Integer, AssetState> typeMap = new HashMap<>();
			
			for(AssetState type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AssetState> getAllTypes() {
			return STATE_MAP;
		}
	}
}
