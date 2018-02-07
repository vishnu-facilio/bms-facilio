package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AssetContext extends ResourceContext {
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
	
	private long localId = -1;
	public long getLocalId() {
		return localId;
	}
	public void setLocalId(long localId) {
		this.localId = localId;
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
