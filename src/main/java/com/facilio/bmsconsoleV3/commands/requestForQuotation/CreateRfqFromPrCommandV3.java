package com.facilio.bmsconsoleV3.commands.requestForQuotation;


import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.common.protocol.types.Field;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CreateRfqFromPrCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map< String, List> recordMap = (Map < String, List > ) context.get(Constants.RECORD_MAP);
        Map < String, Object > bodyParams = Constants.getBodyParams(context);
        List <V3RequestForQuotationContext> requestForQuotationContexts = (List <V3RequestForQuotationContext> ) recordMap.get(moduleName);

        if (!MapUtils.isEmpty(bodyParams) && bodyParams.containsKey("createRfq") && (boolean) bodyParams.get("createRfq") && CollectionUtils.isNotEmpty(requestForQuotationContexts.get(0).getRecordIds())) {


                List<V3PurchaseRequestContext> purchaseRequests = new ArrayList<>();
                Long storeId = -1l;
                V3StoreRoomContext storeRoom = new V3StoreRoomContext();
                LocationContext shipToAddress = null;
                String description = "";
                for (Long prId: requestForQuotationContexts.get(0).getRecordIds()) {

                    String prModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST;
                    V3PurchaseRequestContext purchaseRequest= (V3PurchaseRequestContext) V3Util.getRecord(prModuleName,prId,null);
                    if(storeId==-1l && purchaseRequest.getStoreRoom() != null){
                        storeId=purchaseRequest.getStoreRoom().getId();
                        storeRoom = purchaseRequest.getStoreRoom();
                    }
                    if(shipToAddress==null && purchaseRequest.getShipToAddress()!=null && purchaseRequest.getShipToAddress().getId()>0){
                        shipToAddress=purchaseRequest.getShipToAddress();
                    }
//                    if(purchaseRequest.getStoreRoom()==null){
//                        throw new IllegalArgumentException("Storeroom cannot be empty");
//                    }
                    if(purchaseRequest.getStoreRoom()!=null && purchaseRequest.getStoreRoom().getId()!=storeId && storeId!=-1l){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot create single RFQ for multiple storeroom items");
                    }
                    if(purchaseRequest.getDescription()!=null){
                        description = description.concat("#PR ").concat(purchaseRequest.getId() + ". ").concat(purchaseRequest.getDescription()).concat("\n");
                    }
                    purchaseRequests.add(purchaseRequest);

                }
                String name =requestForQuotationContexts.get(0).getName();
                V3RequestForQuotationContext requestForQuotation = setRequestForQuotation(storeRoom,shipToAddress,name,description);

                List<V3RequestForQuotationLineItemsContext> rfqLineItemsToBeAdded = new ArrayList<>();

                for(V3PurchaseRequestContext purchaseRequest : purchaseRequests) {
                List<V3RequestForQuotationLineItemsContext>  rfqLineItems = setRequestForQuotationLineItems(purchaseRequest.getLineItems());
                rfqLineItemsToBeAdded.addAll(rfqLineItems);
                }

                requestForQuotation.setRequestForQuotationLineItems(rfqLineItemsToBeAdded);
                //to set subform
                requestForQuotation.setSubForm(getSubFormData(rfqLineItemsToBeAdded));

                List <V3RequestForQuotationContext> rfqListToBeCreated = new ArrayList<>();
                rfqListToBeCreated.add(requestForQuotation);
                recordMap.put(moduleName, rfqListToBeCreated);

                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,requestForQuotationContexts.get(0).getRecordIds());

//logic to create multiple RFQs from PRs with different storerooms

//                Map<Long,V3RequestForQuotationContext> storeVsRequestForQuotationMap = new HashMap<>();
//
//                for(V3PurchaseRequestContext purchaseRequest : purchaseRequests){
//                    Long storeRoomId = purchaseRequest.getStoreRoom().getId();
//                    if(storeVsRequestForQuotationMap.containsKey(storeRoomId)){
//                        V3RequestForQuotationContext requestForQuotationObj = storeVsRequestForQuotationMap.get(storeRoomId);
//                        List<V3RequestForQuotationLineItemsContext> rfqLineItemsObj = requestForQuotationObj.getRequestForQuotationLineItems();
//                        List<V3RequestForQuotationLineItemsContext> rfqLineItemsToBeAdded =setRequestForQuotationLineItems(purchaseRequest.getLineItems());
//                        rfqLineItemsObj.addAll(rfqLineItemsToBeAdded);
//                        requestForQuotationObj.setRequestForQuotationLineItems(rfqLineItemsObj);
//                        //to set subform
//                        requestForQuotationObj.setSubForm(getSubFormData(rfqLineItemsObj));
//
//                        storeVsRequestForQuotationMap.replace(storeRoomId,requestForQuotationObj);
//                    }
//                    else{
//                       List<V3RequestForQuotationLineItemsContext>  rfqLineItems = setRequestForQuotationLineItems(purchaseRequest.getLineItems());
//                       V3RequestForQuotationContext requestForQuotation = setRequestForQuotation(purchaseRequest,rfqLineItems,name);
//                       //to set subform
//                       requestForQuotation.setSubForm(getSubFormData(rfqLineItems));
//
//                       storeVsRequestForQuotationMap.put(storeRoomId,requestForQuotation);
//                    }
//                }

       }
        return false;
    }
    private Map<String, List<Map<String, Object>>> getSubFormData(List<V3RequestForQuotationLineItemsContext> rfqLineItems){
        List<Map<String, Object>> subFormList=  getSubFormList(rfqLineItems);
        Map<String, List<Map<String, Object>>> subForm = new HashMap<>();
        subForm.put("requestForQuotationLineItems",subFormList);
        return subForm;
    }
    private List<Map<String, Object>> getSubFormList(List<V3RequestForQuotationLineItemsContext> rfqLineItems){
        List<Map<String, Object>> subformList = new ArrayList<>();
        for(V3RequestForQuotationLineItemsContext rfqLineItem: rfqLineItems){
            Map<String, Object> subform  = new HashMap<>();
            subform.put("unitPrice",rfqLineItem.getUnitPrice());
            subform.put("inventoryType",rfqLineItem.getInventoryType());
            subform.put("unitOfMeasure",rfqLineItem.getUnitOfMeasure());
            subform.put("quantity",rfqLineItem.getQuantity());
            subform.put("description",rfqLineItem.getDescription()!=null ? rfqLineItem.getDescription() : null);
            subform.put("itemType",rfqLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex() ? rfqLineItem.getItemType() : null);
            subform.put("toolType",rfqLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex() ? rfqLineItem.getToolType() : null);
            //subform.put("service",rfqLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex() ? rfqLineItem.getService() : null);
            subformList.add(subform);
        }
        return subformList;
    }
    private V3RequestForQuotationContext setRequestForQuotation(V3StoreRoomContext storeRoom,LocationContext shipToAddress, String name, String description){
        V3RequestForQuotationContext requestForQuotation = new V3RequestForQuotationContext();
        requestForQuotation.setName(name);
        if(description!=null){
            requestForQuotation.setDescription(description);
        }
        requestForQuotation.setStoreRoom(storeRoom);
        requestForQuotation.setShipToAddress(shipToAddress);

        return requestForQuotation;
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
//            else if(prLineItem.getInventoryType()==V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex()){
//                Map<String, Object> map = FieldUtil.getAsProperties(prLineItem.getService());
//                V3ServiceContext service = FieldUtil.getAsBeanFromMap(map, V3ServiceContext.class);
//                rfqLineItem.setService(service);
//            }
            if(prLineItem.getDescription()!=null){
                rfqLineItem.setDescription(prLineItem.getDescription());
            }
            rfqLineItem.setUnitPrice(prLineItem.getUnitPrice());
            rfqLineItem.setQuantity(prLineItem.getQuantity());
            rfqLineItem.setUnitOfMeasure(prLineItem.getUnitOfMeasure());

            rfqLineItems.add(rfqLineItem);
        }
        return rfqLineItems;
    }
}
