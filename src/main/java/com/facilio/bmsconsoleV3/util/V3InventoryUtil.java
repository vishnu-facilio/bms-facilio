package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.enums.InventoryReservationStatus;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceInventoryReservationContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class V3InventoryUtil {
    public static List<V3PurchasedItemContext> getPurchasedItemsBasedOnCostType(V3ItemContext item, V3BinContext bin)
            throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
        if (item.getCostTypeEnum() == null || item.getCostType() <= 0
                || item.getCostType().equals(CostType.FIFO.getIndex()) || item.getCostType().equals(CostType.WEIGHTED_AVERAGE.getIndex())) {
            purchasedItems = getPurchasedItemList(item.getId(), bin, " asc", purchasedItemModule,
                    purchasedItemFields);
        } else if (item.getCostType().equals(CostType.LIFO.getIndex())) {
            purchasedItems = getPurchasedItemList(item.getId(), bin, " desc", purchasedItemModule,
                    purchasedItemFields);
        }
        return purchasedItems;
    }

    public static List<V3PurchasedItemContext> getPurchasedItemList(long id, V3BinContext bin, String orderByType,
            FacilioModule module,
            List<FacilioField> fields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(module));
        }

        SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedItemContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
                        NumberOperators.GREATER_THAN))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);
        if(bin != null){
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("bin"), String.valueOf(bin.getId()),
                            NumberOperators.EQUALS));
        }

        List<V3PurchasedItemContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }

    public static List<V3PurchasedToolContext> getPurchasedToolsBasedOnCostType(V3ToolContext tool,V3BinContext bin,
            Boolean getOnlyAvailableTools) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule purchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
        List<FacilioField> purchasedToolFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
        List<V3PurchasedToolContext> purchasedTool = new ArrayList<>();
        if (tool.getCostTypeEnum() == null || tool.getCostType() <= 0
                || tool.getCostType().equals(CostType.FIFO.getIndex())) {
            purchasedTool = getPurchasedToolList(tool.getId(), " asc", purchasedToolModule,
                    purchasedToolFields,bin, getOnlyAvailableTools);
        } else if (tool.getCostType().equals(CostType.LIFO.getIndex())) {
            purchasedTool = getPurchasedToolList(tool.getId(), " desc", purchasedToolModule,
                    purchasedToolFields,bin, getOnlyAvailableTools);
        }
        return purchasedTool;
    }

    public static List<V3PurchasedToolContext> getPurchasedToolList(long id, String orderByType, FacilioModule module,
            List<FacilioField> fields,V3BinContext bin, Boolean getOnlyAvailableTools) throws Exception {
        String quantityFieldName = getOnlyAvailableTools ? "currentQuantity" : "quantity";
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedToolContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedToolContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("tool"), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(quantityFieldName), String.valueOf(0),
                        NumberOperators.GREATER_THAN))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

        if(bin != null){
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("bin"), String.valueOf(bin.getId()),
                    NumberOperators.EQUALS));
        }

        List<V3PurchasedToolContext> purchasedToolList = selectBuilder.get();

        if (purchasedToolList != null && !purchasedToolList.isEmpty()) {
            return purchasedToolList;
        }
        return null;
    }

    public static Double getServiceCost(V3ServiceContext service, Double duration, Double quantity) {
        Double costOccured = null;
        if (service.getBuyingPrice() != null && service.getBuyingPrice() > 0) {
            if (service.getPaymentTypeEnum() == V3ServiceContext.PaymentType.FIXED) {
                costOccured = service.getBuyingPrice() * quantity;
            } else {
                costOccured = service.getBuyingPrice() * duration * quantity;
            }
        }
        return costOccured;
    }

    public static Double getWorkorderActualsDuration(Long issueTime, Long returnTime, V3WorkOrderContext workorder) {
        Double duration = null;
        if (issueTime != null && returnTime != null && issueTime >= 0 && returnTime >= 0) {
            duration = getEstimatedWorkDuration(issueTime, returnTime);
        } else {
            if (workorder.getActualWorkDuration() != null && workorder.getActualWorkDuration() > 0) {
                double hours = (((double) workorder.getActualWorkDuration()) / (60 * 60));
                duration = Math.round(hours * 100.0) / 100.0;
            }
        }
        return duration;
    }

    public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
        double duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }
        return duration / 1000;
    }

    public static Long getReturnTimeFromDurationAndIssueTime(Double duration, Long issueTime) {
        Long returnTime = null;
        if (issueTime != null && issueTime >= 0) {
            returnTime = (long) (issueTime + (duration * 1000));
        }
        return returnTime;
    }

    public static Long getIssueTimeFromDurationAndReturnTime(Double duration, Long returnTime) {
        Long issueTime = null;
        if (returnTime != null && returnTime >= 0) {
            issueTime = (long) (returnTime - (duration * 1000));
        }
        return issueTime;
    }

    public static Boolean hasVendorPortalAccess(Long vendorId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String vendorContact = FacilioConstants.ContextNames.VENDOR_CONTACT;
        List<FacilioField> fields = modBean.getAllFields(vendorContact);

        SelectRecordsBuilder<V3VendorContactContext> builder = new SelectRecordsBuilder<V3VendorContactContext>()
                .moduleName(vendorContact)
                .select(fields)
                .beanClass(V3VendorContactContext.class)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("VENDOR_PORTAL_ACCESS", "isVendorPortalAccess",
                        String.valueOf(true), BooleanOperators.IS));

        List<V3VendorContactContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
            return true;
        }
        return false;
    }

    public static void rollUpReservedItemForSoPlans(V3ItemTypesContext itemType, V3StoreRoomContext storeRoom,
            Integer reservationTypeInt, Double quantity, ServiceInventoryReservationContext reservation)
            throws Exception {
        Long itemTypeId = itemType != null ? itemType.getId() : null;
        Long storeRoomId = storeRoom != null ? storeRoom.getId() : null;
        V3ItemContext item = V3ItemsApi.getItem(itemTypeId, storeRoomId);
        ReservationType reservationType = ReservationType.valueOf(reservationTypeInt);
        updateReservedItem(item, itemTypeId, reservationType, quantity);
        addReservedItemTransaction(reservationType, item, quantity, null, reservation);
    }

    public static void rollUpReservedTool(V3ToolTypesContext toolType, V3StoreRoomContext storeRoom,
            Integer reservationTypeInt, Double quantity, InventoryReservationContext inventoryReservation)
            throws Exception {
        Long toolTypeId = toolType != null ? toolType.getId() : null;
        Long storeRoomId = storeRoom != null ? storeRoom.getId() : null;
        ReservationType reservationType = ReservationType.valueOf(reservationTypeInt);
        updateReservedToolAndToolTransaction(toolTypeId, storeRoomId, reservationType, quantity, inventoryReservation);
    }

    // optimize item and tool implementation, extract the methods
    public static void updateReservedToolAndToolTransaction(Long toolTypeId, Long storeRoomId,
            ReservationType reservationType, Double reservationQuantity,
            InventoryReservationContext inventoryReservation) throws Exception {
        V3ToolContext tool = V3ToolsApi.getTool(toolTypeId, storeRoomId);
        long toolId = tool.getId();
        Double reservedQuantity = tool.getReservedQuantity() == null ? 0 : tool.getReservedQuantity();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String toolModuleName = FacilioConstants.ContextNames.TOOL;
        FacilioModule module = modBean.getModule(toolModuleName);
        List<FacilioField> fields = modBean.getAllFields(toolModuleName);
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("reservedQuantity"));

        Double newReservedQuantity = reservedQuantity + reservationQuantity;
        Map<String, Object> map = new HashMap<>();
        map.put("reservedQuantity", newReservedQuantity);
        if (reservationType.getValue().equals(ReservationType.HARD.getValue())) {
            // to update available quantity in tool module
            Double availableQuantity = tool.getQuantity() == null ? 0 : tool.getQuantity();
            Double newAvailableQuantity = availableQuantity - reservationQuantity;
            updatedFields.add(fieldsMap.get("quantity"));
            map.put("quantity", newAvailableQuantity);
            // updating available quantity in tool type
            updateAvailableQuantityInToolType(reservationQuantity, toolTypeId);
        }
        UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(toolId, module));
        updateBuilder.updateViaMap(map);

        // adding tool transaction
        addReservedToolTransaction(reservationType, tool, reservationQuantity, inventoryReservation);
    }

    private static void updateAvailableQuantityInToolType(Double reservationQuantity, Long toolTypeId)
            throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.TOOL_TYPES;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3ToolTypesContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ToolTypesContext>()
                .module(module)
                .beanClass(V3ToolTypesContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(toolTypeId, module));

        List<V3ToolTypesContext> toolTypes = selectRecordsBuilder.get();

        Double availableQuantity = toolTypes.get(0).getQuantity();

        Double newAvailableQuantity = availableQuantity - reservationQuantity;

        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("quantity"));
        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newAvailableQuantity);

        UpdateRecordBuilder<V3ToolTypesContext> updateBuilder = new UpdateRecordBuilder<V3ToolTypesContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(toolTypeId, module));
        updateBuilder.updateViaMap(map);
    }

    private static void addReservedToolTransaction(ReservationType reservationType, V3ToolContext tool,
            Double reservationQuantity, InventoryReservationContext inventoryReservation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String toolModuleName = FacilioConstants.ContextNames.TOOL_TRANSACTIONS;
        FacilioModule module = modBean.getModule(toolModuleName);
        List<FacilioField> fields = modBean.getAllFields(toolModuleName);

        V3ToolTransactionContext toolTransaction = new V3ToolTransactionContext();
        toolTransaction.setTool(tool);
        toolTransaction.setToolType(tool.getToolType());
        toolTransaction.setStoreRoom(tool.getStoreRoom());
        toolTransaction.setQuantity(reservationQuantity);
        toolTransaction.setIsReturnable(false);
        toolTransaction.setTransactionType(TransactionType.RESERVATION);
        toolTransaction.setInventoryReservation(inventoryReservation);
        toolTransaction.setParentId(inventoryReservation.getId());

        if (reservationType.equals(ReservationType.HARD)) {
            toolTransaction.setTransactionState(TransactionState.HARD_RESERVE);
        } else if (reservationType.equals(ReservationType.SOFT)) {
            toolTransaction.setTransactionState(TransactionState.SOFT_RESERVE);
        }
        InsertRecordBuilder<V3ToolTransactionContext> readingBuilder = new InsertRecordBuilder<V3ToolTransactionContext>()
                .module(module).fields(fields).addRecord(toolTransaction);
        readingBuilder.save();
    }

    public static void rollUpReservedItem(V3ItemTypesContext itemType, V3StoreRoomContext storeRoom,
            Integer reservationTypeInt, Double quantity, InventoryReservationContext inventoryReservation)
            throws Exception {
        Long itemTypeId = itemType != null ? itemType.getId() : null;
        Long storeRoomId = storeRoom != null ? storeRoom.getId() : null;
        V3ItemContext item = V3ItemsApi.getItem(itemTypeId, storeRoomId);
        ReservationType reservationType = ReservationType.valueOf(reservationTypeInt);
        updateReservedItem(item, itemTypeId, reservationType, quantity);
        addReservedItemTransaction(reservationType, item, quantity, inventoryReservation, null);
    }

    public static void updateReservedItem(V3ItemContext item, Long itemTypeId, ReservationType reservationType,
            Double reservationQuantity) throws Exception {
        Double reservedQuantity = item.getReservedQuantity() == null ? 0 : item.getReservedQuantity();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String itemModuleName = FacilioConstants.ContextNames.ITEM;
        FacilioModule module = modBean.getModule(itemModuleName);
        List<FacilioField> fields = modBean.getAllFields(itemModuleName);
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("reservedQuantity"));

        Double newReservedQuantity = reservedQuantity + reservationQuantity;
        Map<String, Object> map = new HashMap<>();
        map.put("reservedQuantity", newReservedQuantity);
        if (reservationType.getValue().equals(ReservationType.HARD.getValue())) {
            // to update available quantity in item module
            Double availableQuantity = item.getQuantity() == null ? 0 : item.getQuantity();
            Double newAvailableQuantity = availableQuantity - reservationQuantity;
            updatedFields.add(fieldsMap.get("quantity"));
            map.put("quantity", newAvailableQuantity);
            // updating available quantity in item type
            updateAvailableQuantityInItemType(reservationQuantity, itemTypeId);
        }
        UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
        updateBuilder.updateViaMap(map);

    }

    public static void updateReservedQuantity(Long itemId, Double quantity) throws Exception {
        V3ItemContext item = V3ItemsApi.getItems(itemId);
        Double reservedQuantity = item.getReservedQuantity();
        Double newReservedQuantity = reservedQuantity - quantity;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String itemModuleName = FacilioConstants.ContextNames.ITEM;
        FacilioModule module = modBean.getModule(itemModuleName);
        List<FacilioField> fields = modBean.getAllFields(itemModuleName);

        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("reservedQuantity"));

        Map<String, Object> map = new HashMap<>();
        map.put("reservedQuantity", newReservedQuantity);

        UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(itemId, module));
        updateBuilder.updateViaMap(map);
    }
    // public static void updateReservedItemAndItemTransaction(Long itemTypeId, Long
    // storeRoomId, ReservationType reservationType, Double reservationQuantity,
    // InventoryReservationContext inventoryReservation) throws Exception {
    // V3ItemContext item = V3ItemsApi.getItem(itemTypeId, storeRoomId);
    // long itemId = item.getId();
    // Double reservedQuantity = item.getReservedQuantity() == null ? 0 :
    // item.getReservedQuantity();
    //
    // ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    // String itemModuleName = FacilioConstants.ContextNames.ITEM;
    // FacilioModule module = modBean.getModule(itemModuleName);
    // List<FacilioField> fields = modBean.getAllFields(itemModuleName);
    // List<FacilioField> updatedFields = new ArrayList<>();
    // Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
    // updatedFields.add(fieldsMap.get("reservedQuantity"));
    //
    // Double newReservedQuantity = reservedQuantity + reservationQuantity;
    // Map<String, Object> map = new HashMap<>();
    // map.put("reservedQuantity", newReservedQuantity);
    // if (reservationType.getValue().equals(ReservationType.HARD.getValue())) {
    // // to update available quantity in item module
    // Double availableQuantity = item.getQuantity() == null ? 0 :
    // item.getQuantity();
    // Double newAvailableQuantity = availableQuantity - reservationQuantity;
    // updatedFields.add(fieldsMap.get("quantity"));
    // map.put("quantity", newAvailableQuantity);
    // // updating available quantity in item type
    // updateAvailableQuantityInItemType(reservationQuantity, itemTypeId);
    // }
    // UpdateRecordBuilder<V3ItemContext> updateBuilder = new
    // UpdateRecordBuilder<V3ItemContext>()
    // .module(module).fields(updatedFields)
    // .andCondition(CriteriaAPI.getIdCondition(itemId, module));
    // updateBuilder.updateViaMap(map);
    //
    // // adding item transaction
    // addReservedItemTransaction(reservationType, item, reservationQuantity,
    // inventoryReservation);
    // }

    private static void updateAvailableQuantityInItemType(Double reservationQuantity, Long itemTypeId)
            throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.ITEM_TYPES;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3ItemTypesContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .module(module)
                .beanClass(V3ItemTypesContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(itemTypeId, module));

        List<V3ItemTypesContext> itemTypes = selectRecordsBuilder.get();

        Double availableQuantity = itemTypes.get(0).getQuantity();

        Double newAvailableQuantity = availableQuantity - reservationQuantity;

        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("quantity"));
        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newAvailableQuantity);

        UpdateRecordBuilder<V3ItemTypesContext> updateBuilder = new UpdateRecordBuilder<V3ItemTypesContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(itemTypeId, module));
        updateBuilder.updateViaMap(map);
    }

    private static void addReservedItemTransaction(ReservationType reservationType, V3ItemContext item,
            Double reservationQuantity, InventoryReservationContext inventoryReservation,
            ServiceInventoryReservationContext serviceInventoryReservation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String itemModuleName = FacilioConstants.ContextNames.ITEM_TRANSACTIONS;
        FacilioModule module = modBean.getModule(itemModuleName);
        List<FacilioField> fields = modBean.getAllFields(itemModuleName);

        V3ItemTransactionsContext itemTransaction = new V3ItemTransactionsContext();
        itemTransaction.setItem(item);
        itemTransaction.setItemType(item.getItemType());
        itemTransaction.setStoreRoom(item.getStoreRoom());
        itemTransaction.setQuantity(reservationQuantity);
        itemTransaction.setIsReturnable(false);
        itemTransaction.setTransactionType(TransactionType.RESERVATION);
        if (inventoryReservation != null && inventoryReservation.getId() > 0) {
            itemTransaction.setInventoryReservation(inventoryReservation);
            itemTransaction.setParentId(inventoryReservation.getId());
        } else if (serviceInventoryReservation != null && serviceInventoryReservation.getId() > 0) {
            itemTransaction.setServiceInventoryReservation(serviceInventoryReservation);
            itemTransaction.setParentId(serviceInventoryReservation.getId());
        }
        if (reservationType.equals(ReservationType.HARD)) {
            itemTransaction.setTransactionState(TransactionState.HARD_RESERVE);
        } else if (reservationType.equals(ReservationType.SOFT)) {
            itemTransaction.setTransactionState(TransactionState.SOFT_RESERVE);
        }
        InsertRecordBuilder<V3ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<V3ItemTransactionsContext>()
                .module(module).fields(fields).addRecord(itemTransaction);
        readingBuilder.save();
    }

    public static void validateReservationForItem(Double quantity, ReservationType reservationType,
            V3ItemTypesContext itemType, V3StoreRoomContext storeRoom) throws Exception {
        Long itemTypeId = itemType != null ? itemType.getId() : null;
        Long storeRoomId = storeRoom != null ? storeRoom.getId() : null;
        if (storeRoomId != null) {
            V3ItemContext item = V3ItemsApi.getItem(itemTypeId, storeRoomId);
            if (FacilioUtil.isEmptyOrNull(item)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not Present in the given storeroom");
            } else if (reservationType.getValue().equals(ReservationType.HARD.getValue()) &&
                    (item.getQuantity() == null || item.getQuantity() < quantity)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,
                        "Available quantity in store is less than the requested quantity");
            }
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be empty");
        }
    }

    public static void validateReservationForTool(Double quantity, ReservationType reservationType,
            V3ToolTypesContext toolType, V3StoreRoomContext storeRoom) throws Exception {
        Long toolTypeId = toolType != null ? toolType.getId() : null;
        Long storeRoomId = storeRoom != null ? storeRoom.getId() : null;
        if (storeRoomId != null) {
            V3ToolContext tool = V3ToolsApi.getTool(toolTypeId, storeRoomId);
            if (FacilioUtil.isEmptyOrNull(tool)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tool is not Present in the given storeroom");
            } else if (reservationType.getValue().equals(ReservationType.HARD.getValue()) &&
                    (tool.getQuantity() == null || tool.getQuantity() < quantity)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,
                        "Available quantity in store is less than the requested quantity");
            }
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be empty");
        }
    }

    public static void validateQuantityAndReservationType(Double reservationQuantity, ReservationType reservationType)
            throws RESTException {
        if (reservationQuantity == null || reservationQuantity <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
        }
        if (reservationType == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Reservation type cannot be empty");
        }
    }

    public static void validateItem(Long itemTypeId, Long storeRoomId, Double reservationQuantity,
            ReservationType reservationType) throws Exception {

    }

    public static V3ItemContext getItemWithStoreroomServingSites(Long itemTypeId, Long storeRoomId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String itemModuleName = FacilioConstants.ContextNames.ITEM;
        FacilioModule module = modBean.getModule(itemModuleName);
        List<FacilioField> fields = modBean.getAllFields(itemModuleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        LookupFieldMeta storeRoomField = new LookupFieldMeta((LookupField) fieldMap.get("storeRoom"));
        MultiLookupField servingSitesField = (MultiLookupField) modBean.getField("servingsites",
                FacilioConstants.ContextNames.STORE_ROOM);
        storeRoomField.addChildSupplement(servingSitesField);
        lookUpfields.add(storeRoomField);
        SelectRecordsBuilder<V3ItemContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemContext>()
                .module(module)
                .beanClass(V3ItemContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeRoomId),
                        NumberOperators.EQUALS))
                .fetchSupplements(lookUpfields);
        V3ItemContext item = selectRecordsBuilder.fetchFirst();
        if (item != null) {
            return item;
        }
        return null;
    }

    public static V3ToolContext getToolWithStoreroomServingSites(Long toolTypeId, Long storeRoomId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String toolModuleName = FacilioConstants.ContextNames.TOOL;
        FacilioModule module = modBean.getModule(toolModuleName);
        List<FacilioField> fields = modBean.getAllFields(toolModuleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        LookupFieldMeta storeRoomField = new LookupFieldMeta((LookupField) fieldMap.get("storeRoom"));
        MultiLookupField servingSitesField = (MultiLookupField) modBean.getField("servingsites",
                FacilioConstants.ContextNames.STORE_ROOM);
        storeRoomField.addChildSupplement(servingSitesField);
        lookUpfields.add(storeRoomField);
        SelectRecordsBuilder<V3ToolContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ToolContext>()
                .module(module)
                .beanClass(V3ToolContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(toolTypeId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeRoomId),
                        NumberOperators.EQUALS))
                .fetchSupplements(lookUpfields);
        V3ToolContext tool = selectRecordsBuilder.fetchFirst();
        if (tool != null) {
            return tool;
        }
        return null;
    }

    public static V3WorkOrderContext getWorkOrder(long woId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workOrder = V3RecordAPI.getRecord(workOrderModule.getName(), woId, V3WorkOrderContext.class);
        if (workOrder != null) {
            return workOrder;
        }
        return null;
    }

    public static void updateInventoryRequestReservationStatus(Long invReqId) throws Exception {
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId",
                String.valueOf(invReqId), NumberOperators.EQUALS);
        criteria.addAndCondition(condition);
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItemsRecords = V3RecordAPI
                .getRecordsListWithSupplements(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, null,
                        V3InventoryRequestLineItemContext.class, criteria, null);
        if (CollectionUtils.isNotEmpty(inventoryRequestLineItemsRecords)) {
            List<V3InventoryRequestLineItemContext> reservedInvReqLineItems = inventoryRequestLineItemsRecords.stream()
                    .filter(V3InventoryRequestLineItemContext::getIsReserved).collect(Collectors.toList());
            JSONObject iRBodyParams = new JSONObject();
            Map<String, Object> rawRecord = new HashMap<>();
            iRBodyParams.put("updateIRReservationStatus", true);
            if (reservedInvReqLineItems.size() == inventoryRequestLineItemsRecords.size()) {
                iRBodyParams.put("setIRFullyReserved", true);
                rawRecord.put("inventoryRequestReservationStatus",
                        V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex());
                V3Util.updateBulkRecords(FacilioConstants.ContextNames.INVENTORY_REQUEST, rawRecord,
                        Collections.singletonList(invReqId), iRBodyParams, null, false);
            } else if (reservedInvReqLineItems.size() > 0) {
                iRBodyParams.put("setIRPartiallyReserved", true);
                rawRecord.put("inventoryRequestReservationStatus",
                        V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex());
                V3Util.updateBulkRecords(FacilioConstants.ContextNames.INVENTORY_REQUEST, rawRecord,
                        Collections.singletonList(invReqId), iRBodyParams, null, false);
            }
        }
    }

    public static List<V3VendorQuotesLineItemsContext> getVendorQuoteLineItems(V3VendorQuotesContext vendorQuote)
            throws Exception {
        Long vendorQuoteId = vendorQuote.getId();
        String lineItemModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        fields = checkAndRemoveCounterPrice(vendorQuote, fields, fieldsAsMap);

        SelectRecordsBuilder<V3VendorQuotesLineItemsContext> builder = new SelectRecordsBuilder<V3VendorQuotesLineItemsContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(V3VendorQuotesLineItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("VENDOR_QUOTE_ID", "vendorQuotes", String.valueOf(vendorQuoteId),
                        NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
                        (LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service"),
                        (LookupField) fieldsAsMap.get("requestForQuotationLineItem")));
        List<V3VendorQuotesLineItemsContext> lineItems = builder.get();
        setTaxAmount(lineItems);
        return lineItems;
    }

    private static void setTaxAmount(List<V3VendorQuotesLineItemsContext> lineItems) throws Exception {
        List<Long> uniqueTaxIds = lineItems.stream().filter(lineItem -> lookupValueIsNotEmpty(lineItem.getTax()))
                .map(lineItem -> lineItem.getTax().getId()).distinct().collect(Collectors.toList());
        List<TaxContext> taxList = QuotationAPI.getTaxesForIdList(uniqueTaxIds);
        Map<Long, Double> taxIdVsRateMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(taxList)) {
            taxList.forEach(tax -> taxIdVsRateMap.put(tax.getId(), tax.getRate()));
        }
        if (CollectionUtils.isNotEmpty(lineItems)) {
            for (V3VendorQuotesLineItemsContext lineItem : lineItems) {
                if (!ObjectUtils.allNotNull(lineItem, lineItem.getTax(), lineItem.getQuantity(),
                        lineItem.getCounterPrice(), taxIdVsRateMap)) {
                    continue;
                }
                Double taxRate = taxIdVsRateMap.get(lineItem.getTax().getId());
                if (taxRate == null) {
                    continue;
                }
                Double taxAmount = taxRate * (lineItem.getQuantity() * lineItem.getCounterPrice()) / 100;
                lineItem.setTaxAmount(taxAmount);
            }
        }
    }

    public static List<FacilioField> checkAndRemoveCounterPrice(V3VendorQuotesContext vendorQuote,
            List<FacilioField> fields, Map<String, FacilioField> fieldsAsMap) throws Exception {
        if (canHideCounterPrice(vendorQuote)) {
            List<String> fieldsToRemove = Arrays.asList("counterPrice", "tax", "remarks");
            for (String field : fieldsToRemove) {
                if (fieldsAsMap.containsKey(field)) {
                    fieldsAsMap.remove(field);
                }
            }
            fields = fieldsAsMap.values().stream().collect(Collectors.toList());
        }
        return fields;
    }

    private static Boolean canHideCounterPrice(V3VendorQuotesContext vendorQuote) throws Exception {
        V3RequestForQuotationContext rfq = vendorQuote.getRequestForQuotation();
        if (vendorQuote.getVendor() != null && V3InventoryUtil.hasVendorPortalAccess(vendorQuote.getVendor().getId())
                && !AccountUtil.getCurrentApp().getLinkName()
                        .equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
            Boolean hideDetailsForClosedBid = rfq.getRfqType() != null
                    && rfq.getRfqType() == V3RequestForQuotationContext.RfqTypes.CLOSED_BID
                    && !rfq.getIsQuoteReceived();
            if (hideDetailsForClosedBid || !vendorQuote.getIsFinalized()) {
                return true;
            }
        }
        return false;
    }

    public static boolean lookupValueIsNotEmpty(ModuleBaseWithCustomFields context) {
        return context != null && context.getId() > 0;
    }

    public static void UpdateReservationRecordForActuals(V3TicketContext workOrder, Long reservationId, Double quantity)
            throws Exception {
        InventoryReservationContext reservation = V3RecordAPI.getRecord(
                FacilioConstants.ContextNames.INVENTORY_RESERVATION, reservationId, InventoryReservationContext.class);

        Double woQuantity = quantity;
        if (workOrder != null && reservation.getWorkOrder() != null
                && workOrder.getId() != reservation.getWorkOrder().getId()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR,
                    "Plan created in one Work Order cannot be issued to another");
        }
        if (reservation.getReservationStatusEnum().equals(InventoryReservationStatus.ISSUED)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selected reserved item is fully issued");
        }
        if (woQuantity > reservation.getBalanceReservedQuantity()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity is greater than balance reserved quantity");
        } else {
            Double balanceReservedQuantity = reservation.getBalanceReservedQuantity();
            balanceReservedQuantity -= woQuantity;
            Double issuedQuantity = reservation.getIssuedQuantity() != null ? reservation.getIssuedQuantity() : 0;
            issuedQuantity += woQuantity;

            if (balanceReservedQuantity > 0) {
                reservation.setReservationStatus(InventoryReservationStatus.PARTIALLY_ISSUED.getIndex());
            } else if (balanceReservedQuantity == 0) {
                reservation.setReservationStatus(InventoryReservationStatus.ISSUED.getIndex());
            }
            reservation.setIssuedQuantity(issuedQuantity);
            reservation.setBalanceReservedQuantity(balanceReservedQuantity);

            V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION,
                    reservation.getId(), FieldUtil.getAsJSON(reservation), null, null, null, null, null, null, null,
                    null, null);

        }
    }
}
