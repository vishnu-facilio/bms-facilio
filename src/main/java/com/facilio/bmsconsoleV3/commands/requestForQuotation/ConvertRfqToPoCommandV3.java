package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertRfqToPoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        Long vendorId = (Long) context.get(FacilioConstants.ContextNames.VENDOR_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3RequestForQuotationLineItemsContext> selectRecordsBuilder = new SelectRecordsBuilder<V3RequestForQuotationLineItemsContext>()
                .module(module)
                .beanClass(V3RequestForQuotationLineItemsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(requestForQuotation.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("AWARDED_TO", "awardedTo", String.valueOf(vendorId), NumberOperators.EQUALS));
        List<V3RequestForQuotationLineItemsContext> lineItems = selectRecordsBuilder.get();

        V3VendorContext vendor = new V3VendorContext();
        vendor.setId(vendorId);
        V3PurchaseOrderContext purchaseOrder = setPurchaseOrder(requestForQuotation,vendor);
        purchaseOrder.setLineItems(setPurchaseOrderLineItems(lineItems));

        context.put(FacilioConstants.ContextNames.PURCHASE_ORDER, purchaseOrder);
        return false;
    }
    private List<V3PurchaseOrderLineItemContext> setPurchaseOrderLineItems(List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<V3PurchaseOrderLineItemContext> purchaseOrderLineItems = new ArrayList<>();
        for(V3RequestForQuotationLineItemsContext requestForQuotationLineItem : requestForQuotationLineItems){
            V3PurchaseOrderLineItemContext purchaseOrderLineItem = new V3PurchaseOrderLineItemContext();
            purchaseOrderLineItem.setQuantity(requestForQuotationLineItem.getQuantity());
            purchaseOrderLineItem.setUnitPrice(requestForQuotationLineItem.getAwardedPrice());
            purchaseOrderLineItem.setInventoryType(requestForQuotationLineItem.getInventoryType());
            purchaseOrderLineItem.setUnitOfMeasure(1l);
            if(requestForQuotationLineItem.getDescription()!=null){
                purchaseOrderLineItem.setDescription(requestForQuotationLineItem.getDescription());
            }
            if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex())){
                purchaseOrderLineItem.setItemType(requestForQuotationLineItem.getItemType());
            }
            else if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex())){
                purchaseOrderLineItem.setToolType(requestForQuotationLineItem.getToolType());
            }
            else if(requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex())){
                purchaseOrderLineItem.setService(requestForQuotationLineItem.getService());
            }
            purchaseOrderLineItems.add(purchaseOrderLineItem);
        }

        return purchaseOrderLineItems;
    }
    private V3PurchaseOrderContext setPurchaseOrder(V3RequestForQuotationContext requestForQuotation, V3VendorContext vendor){
        V3PurchaseOrderContext purchaseOrder = new V3PurchaseOrderContext();
        purchaseOrder.setName(requestForQuotation.getName());
        purchaseOrder.setRequestForQuotation(requestForQuotation);
        if(requestForQuotation.getDescription()!=null){
            purchaseOrder.setDescription(requestForQuotation.getDescription());
        }
        purchaseOrder.setVendor(vendor);
        if(requestForQuotation.getStoreRoom()!=null){
            purchaseOrder.setStoreRoom(requestForQuotation.getStoreRoom());
        }
        if(requestForQuotation.getBillToAddress()!=null){
            purchaseOrder.setBillToAddress(requestForQuotation.getBillToAddress());
        }
        if(requestForQuotation.getShipToAddress()!=null){
            purchaseOrder.setShipToAddress(requestForQuotation.getShipToAddress());
        }
        return purchaseOrder;
    }

}
