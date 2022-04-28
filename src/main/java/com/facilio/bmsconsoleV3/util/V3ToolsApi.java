package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3ToolsApi {
    public static V3ToolTypesContext getToolTypes(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

        SelectRecordsBuilder<V3ToolTypesContext> selectBuilder = new SelectRecordsBuilder<V3ToolTypesContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

        List<V3ToolTypesContext> tools = selectBuilder.get();

        if (tools != null && !tools.isEmpty()) {
            return tools.get(0);
        }
        return null;
    }

    public static V3ToolContext getTool(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField>lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldsAsMap.get("toolType"));
        lookUpfields.add((LookupField) fieldsAsMap.get("storeRoom"));
        SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ToolContext.class).andCondition(CriteriaAPI.getIdCondition(id, module))
                .fetchSupplements(lookUpfields);

        List<V3ToolContext> tools = selectBuilder.get();

        if (tools != null && !tools.isEmpty()) {
            return tools.get(0);
        }
        return null;
    }

    public static Map<String, Long> getToolTypesMap() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

        SelectRecordsBuilder<V3ToolTypesContext> selectBuilder = new SelectRecordsBuilder<V3ToolTypesContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ToolTypesContext.class);

        List<V3ToolTypesContext> tools = selectBuilder.get();
        Map<String, Long> toolNameVsIdMap = new HashMap<>();
        if (tools != null && !tools.isEmpty()) {
            for(V3ToolTypesContext toolType : tools) {
                toolNameVsIdMap.put(toolType.getName(), toolType.getId());
            }
            return toolNameVsIdMap;
        }
        return null;
    }

    public static List<V3ToolContext> getToolsForStore(long storeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ToolContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                ;
        List<V3ToolContext> tools = selectBuilder.get();
        return tools;
    }

    public static V3ToolTransactionContext getToolTransactionsForRequestedLineItem(long requestedLineItem) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolTransactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        List<FacilioField> toolTransactionFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

        SelectRecordsBuilder<V3ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<V3ToolTransactionContext>()
                .select(toolTransactionFields).table(toolTransactionModule.getTableName())
                .moduleName(toolTransactionModule.getName()).beanClass(V3ToolTransactionContext.class)
                .andCondition(CriteriaAPI.getCondition("REQUESTED_LINEITEM", "requestedLineItem", String.valueOf(requestedLineItem), NumberOperators.EQUALS));

        List<V3ToolTransactionContext> toolTransactions = selectBuilder.get();
        if(!CollectionUtils.isEmpty(toolTransactions)) {
            return toolTransactions.get(0);
        }
        throw new IllegalArgumentException("Tool shoud be issued before being used");
    }

    public static List<V3ToolContext> getToolsForType(List<Long> toolTypeIds) throws Exception {
        String ids = StringUtils.join(toolTypeIds, ",");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ToolContext.class)
                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(ids), NumberOperators.EQUALS))

                ;
        List<V3ToolContext> tools = selectBuilder.get();
        if(!CollectionUtils.isEmpty(tools)) {
            return tools;
        }
        throw new IllegalArgumentException("No appropriate tool found");
    }


    public static long getLastPurchasedToolDateForToolId(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
        long lastPurchasedDate = -1;
        SelectRecordsBuilder<V3AssetContext> itemselectBuilder = new SelectRecordsBuilder<V3AssetContext>()
                .select(assetFields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingTool"), String.valueOf(id), NumberOperators.EQUALS))
                .orderBy("PURCHASED_DATE DESC");
        List<V3AssetContext> assetscontext = itemselectBuilder.get();
        if(assetscontext!=null && !assetscontext.isEmpty()) {
            lastPurchasedDate = assetscontext.get(0).getPurchasedDate();
        }

        return lastPurchasedDate;
    }

    public static void updateLastPurchasedDateForTool(V3ToolContext tool)
            throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                .module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
        updateBuilder.update(tool);
    }

    public static void updatelastPurchaseddetailsInToolType(long id) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

        FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);

        FacilioModule transactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        List<FacilioField> transactionFields = modBean
                .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        Map<String, FacilioField> transactionFieldMap = FieldFactory.getAsMap(transactionFields);

        long lastPurchasedDate = -1, lastIssuedDate = -1;

        SelectRecordsBuilder<V3ToolContext> builder = new SelectRecordsBuilder<V3ToolContext>()
                .select(toolFields).moduleName(toolModule.getName())
                .andCondition(CriteriaAPI.getCondition(toolFieldMap.get("toolType"), String.valueOf(id),
                        NumberOperators.EQUALS))
                .beanClass(V3ToolContext.class).orderBy("LAST_PURCHASED_DATE DESC");

        List<V3ToolContext> tools = builder.get();
        long storeRoomId = -1;
        V3ToolContext tool;
        if (tools != null && !tools.isEmpty()) {
            tool = tools.get(0);
            storeRoomId = tool.getStoreRoom().getId();
            if(tool.getLastPurchasedDate()!=null){
                lastPurchasedDate = tool.getLastPurchasedDate();
            }
        }

        SelectRecordsBuilder<V3ToolTransactionContext> issuetransactionsbuilder = new SelectRecordsBuilder<V3ToolTransactionContext>()
                .select(transactionFields).moduleName(transactionModule.getName())
                .andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("toolType"),
                        String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("transactionState"),
                        String.valueOf(2), NumberOperators.EQUALS))
                .beanClass(V3ToolTransactionContext.class).orderBy("CREATED_TIME DESC");

        List<V3ToolTransactionContext> transactions = issuetransactionsbuilder.get();
        V3ToolTransactionContext transaction;
        if (transactions != null && !transactions.isEmpty()) {
            transaction = transactions.get(0);
            lastIssuedDate = transaction.getSysCreatedTime();
        }

        V3ToolTypesContext toolType = new V3ToolTypesContext();
        toolType.setId(id);
        toolType.setLastPurchasedDate(lastPurchasedDate);
        toolType.setLastIssuedDate(lastIssuedDate);

        UpdateRecordBuilder<V3ToolTypesContext> updateBuilder = new UpdateRecordBuilder<V3ToolTypesContext>()
                .module(toolTypesModule).fields(modBean.getAllFields(toolTypesModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(id, toolTypesModule));

        updateBuilder.update(toolType);
        StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
    }

    public static V3ToolContext getTool(V3ToolTypesContext toolType, V3StoreRoomContext storeroom) throws Exception {
        V3ToolContext toolc = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
        SelectRecordsBuilder<V3ToolContext> itemselectBuilder = new SelectRecordsBuilder<V3ToolContext>().select(toolFields)
                .table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(V3ToolContext.class)
                .andCondition(CriteriaAPI.getCondition(toolFieldsMap.get("storeRoom"),
                        String.valueOf(storeroom.getId()), NumberOperators.EQUALS)
                );

        List<V3ToolContext> tools = itemselectBuilder.get();
        if (tools != null && !tools.isEmpty()) {
            for (V3ToolContext tool : tools) {
                if (tool.getToolType().getId() == toolType.getId()) {
                    return tool;
                }
            }
            return addTool(toolModule, toolFields,  storeroom, toolType);
        } else {
            return addTool(toolModule, toolFields, storeroom, toolType);
        }
    }

    public static V3ToolContext addTool(FacilioModule module, List<FacilioField> fields, V3StoreRoomContext store, V3ToolTypesContext toolType) throws Exception {
        V3ToolContext tool = new V3ToolContext();
        tool.setStoreRoom(store);
        tool.setToolType(toolType);
        InsertRecordBuilder<V3ToolContext> readingBuilder = new InsertRecordBuilder<V3ToolContext>().module(module)
                .fields(fields);
        readingBuilder.withLocalId();
        readingBuilder.insert(tool);
        return tool;
    }

    public static V3ToolContext getToolsForTypeAndStore(long storeId, long toolTypeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        SelectRecordsBuilder<V3ToolContext> selectBuilder = new SelectRecordsBuilder<V3ToolContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ToolContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(toolTypeId), NumberOperators.EQUALS))

                ;
        List<V3ToolContext> tools = selectBuilder.get();
        if(!CollectionUtils.isEmpty(tools)) {
            return tools.get(0);
        }
        throw new IllegalArgumentException("Tool(s) not available in selected store");
    }
}
