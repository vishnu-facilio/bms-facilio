package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddBulkToolStockTransactionsCommandV2  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<Long> toolIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
        if (toolIds != null && !toolIds.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

            FacilioModule ptmodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
            List<FacilioField> ptfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
            Map<String, FacilioField> ptoolsFieldMap = FieldFactory.getAsMap(ptfields);
            List<LookupField> ptlookUpfields = new ArrayList<>();
            ptlookUpfields.add((LookupField) ptoolsFieldMap.get("toolType"));

            FacilioModule Toolmodule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
            Map<String, FacilioField> toolsFieldMap = FieldFactory.getAsMap(Toolfields);
            List<LookupField> lookUpfields = new ArrayList<>();
            lookUpfields.add((LookupField) toolsFieldMap.get("toolType"));

            List<ToolTransactionContext> toolTransaction = new ArrayList<>();

            List<ToolContext> tools = (List<ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            Map<Long, List<PurchasedToolContext>> toolVsPurchaseTool = (Map<Long, List<PurchasedToolContext>>) context
                    .get(FacilioConstants.ContextNames.PURCHASED_TOOL);
            if (tools != null && !tools.isEmpty()) {
                for (ToolContext tool : tools) {
                    ToolTransactionContext transaction = new ToolTransactionContext();
                    if (tool.getToolType().isRotating()) {
                        List<PurchasedToolContext> purchasedTools = toolVsPurchaseTool.get(tool.getId());
                        if (purchasedTools != null && !purchasedTools.isEmpty()) {
                            for (PurchasedToolContext purchaseTool : purchasedTools) {
                                transaction.setQuantity(1.0);
                                transaction.setTransactionState(TransactionState.ADDITION.getValue());
                                transaction.setTool(tool);
                                transaction.setParentId(tool.getId());
                                transaction.setIsReturnable(false);
                                transaction.setTransactionType(TransactionType.STOCK.getValue());
                                transaction.setToolType(tool.getToolType());
                                transaction.setPurchasedTool(purchaseTool);
                                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                                toolTransaction.add(transaction);
                            }
                        }
                    } else {
                        if (tool.getQuantity() > 0) {
                            transaction.setQuantity(tool.getQuantity());
                            transaction.setTransactionState(TransactionState.ADDITION.getValue());
                            transaction.setTool(tool);
                            transaction.setParentId(tool.getId());
                            transaction.setIsReturnable(false);
                            transaction.setTransactionType(TransactionType.STOCK.getValue());
                            transaction.setToolType(tool.getToolType());
                            transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                            toolTransaction.add(transaction);
                        }
                    }
                }
            }

            InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
                    .module(module).fields(fields).addRecords(toolTransaction);
            readingBuilder.save();
            context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
        }
        return false;
    }

}
