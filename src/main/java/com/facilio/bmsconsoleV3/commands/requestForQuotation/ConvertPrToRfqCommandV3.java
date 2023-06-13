package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertPrToRfqCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if(CollectionUtils.isNotEmpty(recordIds)){

            List<V3PurchaseRequestContext> purchaseRequests = new ArrayList<>();
            String description = "";
            String name="RFQ Created From PRs ";

            for (Long prId: recordIds) {
                String prModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST;
                V3PurchaseRequestContext purchaseRequest= (V3PurchaseRequestContext) V3Util.getRecord(prModuleName,prId,null);

                if(purchaseRequest.getDescription()!=null){
                    description = description.concat("#PR ").concat(purchaseRequest.getId() + ". ").concat(purchaseRequest.getDescription()).concat("\n");
                }
                name= name.concat("#").concat(prId + " ");
                purchaseRequests.add(purchaseRequest);
            }

            V3RequestForQuotationContext requestForQuotationContext = new V3RequestForQuotationContext();

            List<V3RequestForQuotationLineItemsContext> rfqLineItemsToBeAdded = new ArrayList<>();
            for(V3PurchaseRequestContext purchaseRequest : purchaseRequests) {
                List<V3RequestForQuotationLineItemsContext>  rfqLineItems = setRequestForQuotationLineItems(purchaseRequest.getLineItems());
                rfqLineItemsToBeAdded.addAll(rfqLineItems);
            }

            requestForQuotationContext.setName(name);
            requestForQuotationContext.setDescription(description);
            requestForQuotationContext.setRequestForQuotationLineItems(rfqLineItemsToBeAdded);

            context.put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, requestForQuotationContext);
        }

        return false;
    }
    private List<V3RequestForQuotationLineItemsContext> setRequestForQuotationLineItems(List<V3PurchaseRequestLineItemContext> purchaseRequestLineItems) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<V3RequestForQuotationLineItemsContext> rfqLineItems = new ArrayList<>();

        for(V3PurchaseRequestLineItemContext prLineItem : purchaseRequestLineItems){

            V3RequestForQuotationLineItemsContext rfqLineItem = new V3RequestForQuotationLineItemsContext();

            rfqLineItem.setInventoryType(prLineItem.getInventoryType());
            if(prLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex()){
                Map<String, Object> map = FieldUtil.getAsProperties(prLineItem.getItemType());
                V3ItemTypesContext itemType = FieldUtil.getAsBeanFromMap(map, V3ItemTypesContext.class);
                rfqLineItem.setItemType(itemType);
            }
            else if(prLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex()){
                Map<String, Object> map = FieldUtil.getAsProperties(prLineItem.getToolType());
                V3ToolTypesContext toolType = FieldUtil.getAsBeanFromMap(map, V3ToolTypesContext.class);
                rfqLineItem.setToolType(toolType);
            }
            else if(prLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex()){
                Map<String, Object> map = FieldUtil.getAsProperties(prLineItem.getService());
                V3ServiceContext service = FieldUtil.getAsBeanFromMap(map, V3ServiceContext.class);
                rfqLineItem.setService(service);
            }
            if(prLineItem.getDescription()!=null){
                rfqLineItem.setDescription(prLineItem.getDescription());
            }
            rfqLineItem.setUnitPrice(prLineItem.getUnitPrice());
            rfqLineItem.setQuantity(prLineItem.getQuantity());
            rfqLineItem.setUnitOfMeasure(prLineItem.getUnitOfMeasure());
            rfqLineItem.setTax(prLineItem.getTax());

            rfqLineItems.add(rfqLineItem);
        }
        return rfqLineItems;
    }
}
