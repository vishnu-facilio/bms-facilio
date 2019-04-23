package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseOrderContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendorContext) {
		this.vendor = vendorContext;
	}
	
	private StoreRoomContext storeRoom;
	
	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(StoreRoomContext storeroom) {
		this.storeRoom = storeroom;
	}

	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	private User requestedBy;

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	private double totalCost = -1;
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getTotalCost() {
		return totalCost;
	}
	
	private List<PurchaseOrderLineItemContext> lineItems;
	public List<PurchaseOrderLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseOrderLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	private ReceivableContext receivableContext;
	public ReceivableContext getReceivableContext() {
		return receivableContext;
	}
	public void setReceivableContext(ReceivableContext receivableContext) {
		this.receivableContext = receivableContext;
	}
	
	public static PurchaseOrderContext fromPurchaseRequest(List<PurchaseRequestContext> list) throws Exception {
		PurchaseOrderContext purchaseOrderContext = new PurchaseOrderContext();
		purchaseOrderContext.setStatus(Status.REQUESTED);
		if(!CollectionUtils.isEmpty(list)) {
			purchaseOrderContext.setName(list.get(0).getName());
			purchaseOrderContext.setDescription(list.get(0).getDescription());
			purchaseOrderContext.setShipToAddress(list.get(0).getShipToAddress());
			purchaseOrderContext.setBillToAddress(list.get(0).getBillToAddress());
		}
		long vendorId = -1;
		long storeRoomId = -1;
		
		Map<Long,PurchaseOrderLineItemContext> toolTypeItems = new HashMap<Long, PurchaseOrderLineItemContext>();
		Map<Long,PurchaseOrderLineItemContext> itemTypeItems = new HashMap<Long, PurchaseOrderLineItemContext>();
		double quantity = 0.0;
	
		for(PurchaseRequestContext pr : list) {
			if(pr.getStatusEnum() != PurchaseRequestContext.Status.APPROVED) {
				throw new IllegalArgumentException("Only Purchase Requests with Approved status can be converted to Purchase Order");
			}
			if(vendorId == -1 && pr.getVendor() != null) {
				purchaseOrderContext.setVendor(pr.getVendor());
				vendorId = 	pr.getVendor().getId();
			}
			if(storeRoomId == -1 && pr.getStoreRoom() != null) {
				purchaseOrderContext.setStoreRoom(pr.getStoreRoom());
				storeRoomId = pr.getStoreRoom().getId();
			}
		 	if(pr.getVendor() != null && pr.getVendor().getId() != vendorId) {
				throw new IllegalArgumentException("Cannot create single PO for multiple vendors");
			}
			if(pr.getStoreRoom() != null && pr.getStoreRoom().getId() != storeRoomId) {
				throw new IllegalArgumentException("Cannot create single PO for multiple storeroom items");
			}
			if (CollectionUtils.isNotEmpty(pr.getLineItems())) {
				for (PurchaseRequestLineItemContext prItem : pr.getLineItems()) {
					if(prItem.getInventoryTypeEnum() == InventoryType.ITEM) {
						if(!itemTypeItems.containsKey(prItem.getItemType().getId())) {
							itemTypeItems.put(prItem.getItemType().getId(), PurchaseOrderLineItemContext.from(prItem));
						}
						else {
							PurchaseOrderLineItemContext itemTypeLineItem = itemTypeItems.get(prItem.getItemType().getId());
							quantity = itemTypeLineItem.getQuantity() + prItem.getQuantity(); 
							itemTypeLineItem.setQuantity(quantity);
						}
					}
					else {
						if(!toolTypeItems.containsKey(prItem.getToolType().getId())) {
							toolTypeItems.put(prItem.getToolType().getId(), PurchaseOrderLineItemContext.from(prItem));
						}
						else {
							PurchaseOrderLineItemContext toolTypeLineItem = toolTypeItems.get(prItem.getToolType().getId());
							quantity = toolTypeLineItem.getQuantity() + prItem.getQuantity(); 
							toolTypeLineItem.setQuantity(quantity);
						}
					}
					
				}
			}
			
		}
		
		List<PurchaseOrderLineItemContext> poLineItems = new ArrayList(itemTypeItems.values());
		poLineItems.addAll(new ArrayList(toolTypeItems.values()));
		purchaseOrderContext.setLineItems(poLineItems);
	
	
		
		return purchaseOrderContext;
	}
	
	private static LocationContext getLocationContext() {
		
		LocationContext location = new LocationContext();
		location.setCity(null);
		location.setState(null);
		location.setStreet(null);
		location.setCountry(null);
		location.setZip(null);
		return location;
	
	}

	
	public void addLineItems(List<PurchaseOrderLineItemContext> lineItems) {
		if (CollectionUtils.isEmpty(lineItems)) {
			return;
		}
		
		if (this.lineItems == null) {
			this.lineItems = new ArrayList<PurchaseOrderLineItemContext>();
		}
		this.lineItems.addAll(lineItems);
	}

	public static enum Status {
		REQUESTED(),
		APPROVED(),
		REJECTED(),
		ORDERED(),
		PARTIALLY_RECEIVED(),
		RECEIVED,
		COMPLETED();
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long orderedTime = -1;
	public long getOrderedTime() {
		return orderedTime;
	}
	public void setOrderedTime(long orderedTime) {
		this.orderedTime = orderedTime;
	}
	
	private long requiredTime = -1;
	public long getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(long requiredTime) {
		this.requiredTime = requiredTime;
	}
	
	private LocationContext shipToAddress;

	public LocationContext getShipToAddress() {
		return shipToAddress;
	}

	public void setShipToAddress(LocationContext location) {
		this.shipToAddress = location;
	}

	public long getShipToAddressId() {
		// TODO Auto-generated method stub
		if (shipToAddress != null) {
			return shipToAddress.getId();
		}
		return -1;
	}

	private LocationContext billToAddress;

	public LocationContext getBillToAddress() {
		return billToAddress;
	}

	public void setBillToAddress(LocationContext location) {
		this.billToAddress = location;
	}

	public long getBillToAddressId() {
		// TODO Auto-generated method stub
		if (billToAddress != null) {
			return billToAddress.getId();
		}
		return -1;
	}

    private double quantityReceived = -1;
	
	public double getQuantityReceived() {
		return quantityReceived;
	}
	public void setQuantityReceived(double quantityReceived) {
		this.quantityReceived = quantityReceived;
	}

	private double totalQuantity = -1;
	public double getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	
	private long completedTime = -1;
	public long getCompletedTime() {
		return completedTime;
	}
	public void setCompletedTime(long completedTime) {
		this.completedTime = completedTime;
	}
}
