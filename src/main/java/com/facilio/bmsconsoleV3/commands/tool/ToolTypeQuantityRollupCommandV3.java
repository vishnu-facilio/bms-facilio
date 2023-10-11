package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolTypeQuantityRollupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> toolTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_TYPES_IDS);
        if (toolTypesIds != null && !toolTypesIds.isEmpty()) {
            FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

            FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
            Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);

            FacilioModule transactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> transactionFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            Map<String, FacilioField> transactionFieldMap = FieldFactory.getAsMap(transactionFields);

            long lastPurchasedDate = -1, lastIssuedDate = -1;
            double lastPurchasedPrice = -1;

            if (toolTypesIds != null && !toolTypesIds.isEmpty()) {
                for (Long id : toolTypesIds) {

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
                        if(tool.getLastPurchasedPrice() != null){
                            lastPurchasedPrice = tool.getLastPurchasedPrice();
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

                    double currentQuantity = getTotalQuantity(id, toolModule, toolFieldMap,true);
                    double quantity = getTotalQuantity(id, toolModule, toolFieldMap,false);
                    V3ToolTypesContext toolType = new V3ToolTypesContext();
                    toolType.setId(id);
                    toolType.setCurrentQuantity(currentQuantity);
                    toolType.setQuantity(quantity);
                    toolType.setLastPurchasedDate(lastPurchasedDate);
                    toolType.setLastPurchasedPrice(lastPurchasedPrice);
                    toolType.setLastIssuedDate(lastIssuedDate);

                    UpdateRecordBuilder<V3ToolTypesContext> updateBuilder = new UpdateRecordBuilder<V3ToolTypesContext>()
                            .module(toolTypesModule).fields(modBean.getAllFields(toolTypesModule.getName()))
                            .andCondition(CriteriaAPI.getIdCondition(id, toolTypesModule));

                    updateBuilder.update(toolType);
                    StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
                }
            }
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
        }
        return false;
    }

    public static double getTotalQuantity(long id, FacilioModule toolModule, Map<String, FacilioField> toolFieldMap, Boolean getCurrentQuantity)
            throws Exception {

        if (id <= 0) {
            return 0d;
        }

        List<FacilioField> field = new ArrayList<>();
        if(getCurrentQuantity){
            field.add(FieldFactory.getField("totalQuantity", "sum(CURRENT_QUANTITY)", FieldType.DECIMAL));
        }
        else{
            field.add(FieldFactory.getField("quantity", "sum(QUANTITY)", FieldType.DECIMAL));
        }

        SelectRecordsBuilder<V3ToolContext> builder = new SelectRecordsBuilder<V3ToolContext>()
                .select(field).moduleName(toolModule.getName()).andCondition(CriteriaAPI
                        .getCondition(toolFieldMap.get("toolType"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalQuantity") != null && getCurrentQuantity) {
                return (double) rs.get(0).get("totalQuantity");
            }
            else if (rs.get(0).get("quantity") != null && !getCurrentQuantity){
                return (double) rs.get(0).get("quantity");
            }
            return 0d;
        }
        return 0d;
    }
}
