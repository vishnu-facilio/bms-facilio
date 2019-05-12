package com.facilio.bmsconsole.context;
import java.util.List;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GatePassLineItemsContext extends ModuleBaseWithCustomFields{
	
	private static final long serialVersionUID = 1L;
	private long gatePass;
	
	
	public long getGatePass() {
		return gatePass;
	}
	public void setGatePass(long gatePass) {
		this.gatePass = gatePass;
	}

	private InventoryType inventoryType;
	public InventoryType getInventoryTypeEnum() {
		return inventoryType;
	}
	public int getInventoryType() {
		if (inventoryType != null) {
			return inventoryType.getValue();
		}
		return -1;
	}
	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = InventoryType.valueOf(inventoryType);
	}
	
	private ItemTypesContext itemType;
	public ItemTypesContext getItemType() {
		return itemType;
	}
	public void setItemType(ItemTypesContext itemType) {
		this.itemType = itemType;
	}
	
	private ToolTypesContext toolType;
	public ToolTypesContext getToolType() {
		return toolType;
	}
	public void setToolType(ToolTypesContext toolType) {
		this.toolType = toolType;
	}
	
	private double quantity = -1;
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public GatePassLineItemsContext(InventoryType inventoryType, ItemTypesContext itemType, ToolTypesContext toolType,
			double quantity, String serialNumber) {
		super();
		this.inventoryType = inventoryType;
		this.itemType = itemType;
		this.toolType = toolType;
		this.quantity = quantity;
		this.serialNumber = serialNumber;
	}
	
	private String serialNumber;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	private List<Long> assetIds;
	
	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}
	
	private AssetContext asset;
	
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	public GatePassLineItemsContext() {
		
	}
}
