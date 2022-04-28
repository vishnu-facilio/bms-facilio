package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddBulkToolStockTransactionsCommandV3 extends FacilioCommand {
        @Override
        public boolean executeCommand(Context context) throws Exception {

            List<V3ToolContext> toolsList =  (List<V3ToolContext>) context.get(FacilioConstants.ContextNames.TOOLS);

            if(CollectionUtils.isEmpty(toolsList)){
                String moduleName = Constants.getModuleName(context);
                Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                 toolsList = recordMap.get(moduleName);
            }
        ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);
        if (toolsList != null && !toolsList.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

            List<V3ToolTransactionContext> toolTransaction = new ArrayList<>();
            List<Long> toolIds = new ArrayList<>();
            List<Long> toolTypesIds = new ArrayList<>();
                for (V3ToolContext tool : toolsList) {
                    tool.setLastPurchasedDate(System.currentTimeMillis());
                    toolIds.add(tool.getId());
                    toolTypesIds.add(tool.getToolType().getId());
                    V3ToolTransactionContext transaction = new V3ToolTransactionContext();
                        if (!isRotating(tool) && tool.getQuantity()!=null && tool.getQuantity() > 0) {
                            transaction.setQuantity(tool.getQuantity());
                            transaction.setTransactionState(TransactionState.ADDITION.getValue());
                            transaction.setTool(tool);
                            transaction.setParentId(tool.getId());
                            transaction.setIsReturnable(false);
                            if (shipment != null) {
                                transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                                transaction.setShipment(shipment.getId());
                            } else {
                                transaction.setTransactionType(TransactionType.STOCK.getValue());
                            }
                            transaction.setToolType(tool.getToolType());
                            transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                            toolTransaction.add(transaction);
                        }

                }

            InsertRecordBuilder<V3ToolTransactionContext> readingBuilder = new InsertRecordBuilder<V3ToolTransactionContext>()
                    .module(module).fields(fields).addRecords(toolTransaction);
            readingBuilder.save();

            context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
            context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
            context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
            context.put(FacilioConstants.ContextNames.STORE_ROOM, toolsList.get(0).getStoreRoom().getId());
        }
        return false;
    }
    private boolean isRotating(V3ToolContext tool) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.TOOL_TYPES;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3ToolTypesContext> builder = new SelectRecordsBuilder<V3ToolTypesContext>()
                .module(module)
                .beanClass(V3ToolTypesContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(tool.getToolType().getId(), module));
        List<V3ToolTypesContext> records = builder.get();
        return records.get(0).isRotating();
    }

}
