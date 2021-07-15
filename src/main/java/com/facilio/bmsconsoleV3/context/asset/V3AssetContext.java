package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.AssetDepartmentContext;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3AssetContext extends V3ResourceContext {

    private static final long serialVersionUID = 1L;

    private Boolean downtimeStatus;
    private Long lastDowntimeId;
    private AssetState state;
    private String moduleName;
    private V3AssetTypeContext type;
    private V3AssetCategoryContext category;
    private Long parentAssetId;
    private V3BaseSpaceContext space;
    private AssetDepartmentContext department;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String tagNumber;
    private String partNumber;
    private Long unitPrice;
    private String supplier;
    private Long purchasedDate;
    private Long retireDate;
    private Long warrantyExpiryDate;
    private Boolean hideToCustomer;
    private String geoLocation;
    private Boolean geoLocationEnabled;
    private String currentLocation;
    private Boolean designatedLocation;
    private Long boundaryRadius;
    private Double distanceMoved;
    private V3SiteContext identifiedLocation;
    private Long salvageAmount;
    private Long currentPrice;
    private V3PurchaseOrderContext purchaseOrder;
    public Boolean isUsed;
    public Boolean connected;
    public Boolean moveApprovalNeeded;
    private Long currentSpaceId;
    private Long lastIssuedTime;
    private User lastIssuedToUser;
    private Long lastIssuedToWo;
    private List<Long> hazardIds;

    // Change to V3 Context once done
    private ToolContext rotatingTool;
    private ItemContext rotatingItem;

    public Boolean getDowntimeStatus() {
        return downtimeStatus;
    }

    public void setDowntimeStatus(Boolean downtimeStatus) {
        this.downtimeStatus = downtimeStatus;
    }

    public Long getLastDowntimeId() {
        return lastDowntimeId;
    }

    public void setLastDowntimeId(Long lastDowntimeId) {
        this.lastDowntimeId = lastDowntimeId;
    }

    public AssetState getState() {
        return state;
    }

    public void setState(AssetState state) {
        this.state = state;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public V3AssetTypeContext getType() {
        return type;
    }

    public void setType(V3AssetTypeContext type) {
        this.type = type;
    }

    public V3AssetCategoryContext getCategory() {
        return category;
    }

    public void setCategory(V3AssetCategoryContext category) {
        this.category = category;
    }

    public Long getParentAssetId() {
        return parentAssetId;
    }

    public void setParentAssetId(Long parentAssetId) {
        this.parentAssetId = parentAssetId;
    }

    @Override
    public V3BaseSpaceContext getSpace() {
        return space;
    }

    @Override
    public void setSpace(V3BaseSpaceContext space) {
        this.space = space;
    }

    public AssetDepartmentContext getDepartment() {
        return department;
    }

    public void setDepartment(AssetDepartmentContext department) {
        this.department = department;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public Long getRetireDate() {
        return retireDate;
    }

    public void setRetireDate(Long retireDate) {
        this.retireDate = retireDate;
    }

    public Long getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(Long warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public Boolean getHideToCustomer() {
        return hideToCustomer;
    }

    public void setHideToCustomer(Boolean hideToCustomer) {
        this.hideToCustomer = hideToCustomer;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Boolean getGeoLocationEnabled() {
        return geoLocationEnabled;
    }

    public void setGeoLocationEnabled(Boolean geoLocationEnabled) {
        this.geoLocationEnabled = geoLocationEnabled;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Boolean getDesignatedLocation() {
        return designatedLocation;
    }

    public void setDesignatedLocation(Boolean designatedLocation) {
        this.designatedLocation = designatedLocation;
    }

    public Long getBoundaryRadius() {
        return boundaryRadius;
    }

    public void setBoundaryRadius(Long boundaryRadius) {
        this.boundaryRadius = boundaryRadius;
    }

    public Double getDistanceMoved() {
        return distanceMoved;
    }

    public void setDistanceMoved(Double distanceMoved) {
        this.distanceMoved = distanceMoved;
    }

    public V3SiteContext getIdentifiedLocation() {
        return identifiedLocation;
    }

    public void setIdentifiedLocation(V3SiteContext identifiedLocation) {
        this.identifiedLocation = identifiedLocation;
    }

    public Long getSalvageAmount() {
        return salvageAmount;
    }

    public void setSalvageAmount(Long salvageAmount) {
        this.salvageAmount = salvageAmount;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public V3PurchaseOrderContext getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(V3PurchaseOrderContext purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Boolean getMoveApprovalNeeded() {
        return moveApprovalNeeded;
    }

    public void setMoveApprovalNeeded(Boolean moveApprovalNeeded) {
        this.moveApprovalNeeded = moveApprovalNeeded;
    }

    public Long getCurrentSpaceId() {
        return currentSpaceId;
    }

    public void setCurrentSpaceId(Long currentSpaceId) {
        this.currentSpaceId = currentSpaceId;
    }

    public Long getLastIssuedTime() {
        return lastIssuedTime;
    }

    public void setLastIssuedTime(Long lastIssuedTime) {
        this.lastIssuedTime = lastIssuedTime;
    }

    public User getLastIssuedToUser() {
        return lastIssuedToUser;
    }

    public void setLastIssuedToUser(User lastIssuedToUser) {
        this.lastIssuedToUser = lastIssuedToUser;
    }

    public Long getLastIssuedToWo() {
        return lastIssuedToWo;
    }

    public void setLastIssuedToWo(Long lastIssuedToWo) {
        this.lastIssuedToWo = lastIssuedToWo;
    }

    public List<Long> getHazardIds() {
        return hazardIds;
    }

    public void setHazardIds(List<Long> hazardIds) {
        this.hazardIds = hazardIds;
    }

    public ToolContext getRotatingTool() {
        return rotatingTool;
    }

    public void setRotatingTool(ToolContext rotatingTool) {
        this.rotatingTool = rotatingTool;
    }

    public ItemContext getRotatingItem() {
        return rotatingItem;
    }

    public void setRotatingItem(ItemContext rotatingItem) {
        this.rotatingItem = rotatingItem;
    }


    @Override
    public ResourceType getResourceTypeEnum() {
        return ResourceType.ASSET;
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public int getResourceType() {
        return ResourceContext.ResourceType.ASSET.getValue();
    }

    public String getUrl() {
        return FacilioProperties.getConfig("clientapp.url") + "/app/at/asset/all/" + getId() + "/overview";
    }

    public enum AssetState {
        ACTIVE(1, "Active"),
        IN_STORE(2, "In Store"),
        IN_REPAIR(3, "In Repair"),
        INACTIVE(4, "Inactive"),
        RETIRED(5, "Retired")
        ;

        private int intVal;
        private String strVal;

        AssetState(int intVal, String strVal) {
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
