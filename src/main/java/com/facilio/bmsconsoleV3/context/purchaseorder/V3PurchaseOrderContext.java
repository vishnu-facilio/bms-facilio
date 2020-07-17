package com.facilio.bmsconsoleV3.context.purchaseorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.v3.context.V3Context;

public class V3PurchaseOrderContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private V3VendorContext vendor;
	private V3StoreRoomContext storeRoom;
	private Status status;
	private User requestedBy;
	private Double totalCost;
	private List<V3PurchaseOrderLineItemContext> lineItems;
	private V3ReceivableContext receivableContext;
	private Long orderedTime;
	private Long requiredTime;
	private LocationContext shipToAddress;
	private LocationContext billToAddress;
    private Double quantityReceived;
	private Double totalQuantity;
	private Long completedTime;
	private ContractsContext contract;
	private List<V3PoAssociatedTermsContext> termsAssociated;

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatusEnum() {
		return status;
	}
	
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
	
	public static V3PurchaseOrderContext fromPurchaseRequest(List<V3PurchaseRequestContext> list) throws Exception {
		V3PurchaseOrderContext purchaseOrderContext = new V3PurchaseOrderContext();
		purchaseOrderContext.setStatus(Status.REQUESTED);
		if(!CollectionUtils.isEmpty(list)) {
			purchaseOrderContext.setName(list.get(0).getName());
			purchaseOrderContext.setDescription(list.get(0).getDescription());
			purchaseOrderContext.setShipToAddress(list.get(0).getShipToAddress());
			purchaseOrderContext.setBillToAddress(list.get(0).getBillToAddress());
			if(MapUtils.isNotEmpty(list.get(0).getData())) {
				purchaseOrderContext.setData(list.get(0).getData());
			}
		}
		long vendorId = -1;
		long storeRoomId = -1;
		
		Map<Long,V3PurchaseOrderLineItemContext> toolTypeItems = new HashMap<Long, V3PurchaseOrderLineItemContext>();
		Map<Long,V3PurchaseOrderLineItemContext> itemTypeItems = new HashMap<Long, V3PurchaseOrderLineItemContext>();
		ArrayList<V3PurchaseOrderLineItemContext> others = new ArrayList<V3PurchaseOrderLineItemContext>();
		double quantity = 0.0;
	
		for(V3PurchaseRequestContext pr : list) {
			if(pr.getStatusEnum() != V3PurchaseRequestContext.Status.APPROVED) {
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
				for (V3PurchaseRequestLineItemContext prItem : pr.getLineItems()) {
					if(prItem.getInventoryTypeEnum() == InventoryType.ITEM) {
						if(!itemTypeItems.containsKey(prItem.getItemType().getId())) {
							itemTypeItems.put(prItem.getItemType().getId(), V3PurchaseOrderLineItemContext.from(prItem));
						}
						else {
							V3PurchaseOrderLineItemContext itemTypeLineItem = itemTypeItems.get(prItem.getItemType().getId());
							quantity = itemTypeLineItem.getQuantity() + prItem.getQuantity(); 
							itemTypeLineItem.setQuantity(quantity);
						}
					}
					else if(prItem.getInventoryTypeEnum() == InventoryType.TOOL) {
						if(!toolTypeItems.containsKey(prItem.getToolType().getId())) {
							toolTypeItems.put(prItem.getToolType().getId(), V3PurchaseOrderLineItemContext.from(prItem));
						}
						else {
							V3PurchaseOrderLineItemContext toolTypeLineItem = toolTypeItems.get(prItem.getToolType().getId());
							quantity = toolTypeLineItem.getQuantity() + prItem.getQuantity(); 
							toolTypeLineItem.setQuantity(quantity);
						}
					}
					else if(prItem.getInventoryTypeEnum() == InventoryType.OTHERS) {
						others.add(V3PurchaseOrderLineItemContext.from(prItem));
					}
					
				}
			}	
		}
		
		List<V3PurchaseOrderLineItemContext> poLineItems = new ArrayList(itemTypeItems.values());
		poLineItems.addAll(new ArrayList(toolTypeItems.values()));
		poLineItems.addAll(others);
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

	public void addLineItems(List<V3PurchaseOrderLineItemContext> lineItems) {
		if (CollectionUtils.isEmpty(lineItems)) {
			return;
		}
		if (this.lineItems == null) {
			this.lineItems = new ArrayList<V3PurchaseOrderLineItemContext>();
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

	public LocationContext getShipToAddress() {
		return shipToAddress;
	}

	public void setShipToAddress(LocationContext location) {
		this.shipToAddress = location;
	}

	public Long getShipToAddressId() {
		if (shipToAddress != null) {
			return shipToAddress.getId();
		}
		return -1l;
	}

	public LocationContext getBillToAddress() {
		return billToAddress;
	}

	public void setBillToAddress(LocationContext location) {
		this.billToAddress = location;
	}

	public Long getBillToAddressId() {
		if (billToAddress != null) {
			return billToAddress.getId();
		}
		return -1l;
	}

	public ContractsContext getContract() {
		return contract;
	}
	public void setContract(ContractsContext contract) {
		this.contract = contract;
	}
	public V3VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(V3VendorContext vendor) {
		this.vendor = vendor;
	}
	public V3StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(V3StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public List<V3PurchaseOrderLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<V3PurchaseOrderLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public V3ReceivableContext getReceivableContext() {
		return receivableContext;
	}
	public void setReceivableContext(V3ReceivableContext receivableContext) {
		this.receivableContext = receivableContext;
	}
	public Long getOrderedTime() {
		return orderedTime;
	}
	public void setOrderedTime(Long orderedTime) {
		this.orderedTime = orderedTime;
	}
	public Long getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(Long requiredTime) {
		this.requiredTime = requiredTime;
	}
	public Double getQuantityReceived() {
		return quantityReceived;
	}
	public void setQuantityReceived(Double quantityReceived) {
		this.quantityReceived = quantityReceived;
	}
	public Double getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Long getCompletedTime() {
		return completedTime;
	}
	public void setCompletedTime(Long completedTime) {
		this.completedTime = completedTime;
	}
	public List<V3PoAssociatedTermsContext> getTermsAssociated() {
		return termsAssociated;
	}
	public void setTermsAssociated(List<V3PoAssociatedTermsContext> termsAssociated) {
		this.termsAssociated = termsAssociated;
	}
}
