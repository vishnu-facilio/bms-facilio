package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePurchaseOrdersCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = (List<V3RequestForQuotationLineItemsContext>) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<Long,V3PurchaseOrderContext> vendorToPurchaseOrderMap = new HashMap<>();

        for(V3RequestForQuotationLineItemsContext requestForQuotationLineItem : requestForQuotationLineItems){
            long vendorId = requestForQuotationLineItem.getAwardedTo().getId();
            V3VendorContext vendor = requestForQuotationLineItem.getAwardedTo();
            if(vendorToPurchaseOrderMap.containsKey(vendorId)){
              V3PurchaseOrderContext  purchaseOrderObj = vendorToPurchaseOrderMap.get(vendorId);
                List<V3PurchaseOrderLineItemContext> purchaseOrderObjLineItems  = purchaseOrderObj.getLineItems();
                V3PurchaseOrderLineItemContext purchaseOrderLineItem = setPurchaseOrderLineItems(requestForQuotationLineItem);
                purchaseOrderObjLineItems.add(purchaseOrderLineItem);
                purchaseOrderObj.setLineItems(purchaseOrderObjLineItems);
                vendorToPurchaseOrderMap.replace(vendorId,purchaseOrderObj);
            }
            else{

                V3PurchaseOrderLineItemContext purchaseOrderLineItem = setPurchaseOrderLineItems(requestForQuotationLineItem);
                List<V3PurchaseOrderLineItemContext> purchaseOrderLineItems= new ArrayList<>();
                purchaseOrderLineItems.add(purchaseOrderLineItem);
                V3PurchaseOrderContext purchaseOrder = setPurchaseOrder(requestForQuotation,purchaseOrderLineItems,vendor);
                vendorToPurchaseOrderMap.put(vendorId,purchaseOrder);
            }
        }
        List<V3PurchaseOrderContext> purchaseOrderRecords = new ArrayList<V3PurchaseOrderContext> (vendorToPurchaseOrderMap.values());
        V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER),FieldUtil.getAsMapList(purchaseOrderRecords, V3PurchaseOrderContext.class),null,null);

        return false;
    }
    private V3PurchaseOrderLineItemContext setPurchaseOrderLineItems(V3RequestForQuotationLineItemsContext requestForQuotationLineItem) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        V3PurchaseOrderLineItemContext purchaseOrderLineItem = new V3PurchaseOrderLineItemContext();
        purchaseOrderLineItem.setQuantity(requestForQuotationLineItem.getQuantity());
        purchaseOrderLineItem.setUnitPrice(requestForQuotationLineItem.getAwardedPrice());
        purchaseOrderLineItem.setInventoryType(requestForQuotationLineItem.getInventoryType());
        purchaseOrderLineItem.setUnitOfMeasure(1l);
        if(requestForQuotationLineItem.getDescription()!=null){
            purchaseOrderLineItem.setDescription(requestForQuotationLineItem.getDescription());
        }
        if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex())){
            Map<String, Object> map = FieldUtil.getAsProperties(requestForQuotationLineItem.getItemType());
            ItemTypesContext itemType = FieldUtil.getAsBeanFromMap(map, ItemTypesContext.class);
            purchaseOrderLineItem.setItemType(itemType);
        }
        else if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex())){
            Map<String, Object> map = FieldUtil.getAsProperties(requestForQuotationLineItem.getToolType());
            ToolTypesContext toolType = FieldUtil.getAsBeanFromMap(map, ToolTypesContext.class);
            purchaseOrderLineItem.setToolType(toolType);
        }
        else if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex())){
            Map<String, Object> map = FieldUtil.getAsProperties(requestForQuotationLineItem.getService());
            ServiceContext service = FieldUtil.getAsBeanFromMap(map, ServiceContext.class);
            purchaseOrderLineItem.setService(service);
        }
        return purchaseOrderLineItem;
    }
    private V3PurchaseOrderContext setPurchaseOrder(V3RequestForQuotationContext requestForQuotation, List<V3PurchaseOrderLineItemContext> purchaseOrderLineItems, V3VendorContext vendor){
        V3PurchaseOrderContext purchaseOrder = new V3PurchaseOrderContext();
        purchaseOrder.setName(requestForQuotation.getName());
        if(requestForQuotation.getDescription()!=null){
            purchaseOrder.setDescription(requestForQuotation.getDescription());
        }
        purchaseOrder.setVendor(vendor);
        purchaseOrder.setStoreRoom(requestForQuotation.getStoreRoom());
        purchaseOrder.setBillToAddress(requestForQuotation.getBillToAddress());
        purchaseOrder.setShipToAddress(requestForQuotation.getShipToAddress());
        purchaseOrder.setLineItems(purchaseOrderLineItems);
        return purchaseOrder;
    }

}
