package com.facilio.bmsconsoleV3.commands;

import java.util.*;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;


public class UpdateToolTransactionAfterTransferCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequests = recordMap.get(moduleName);
        if (!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID)) && transferRequests.get(0).getData().get("isStaged").equals(true) && transferRequests.get(0).getData().get("isCompleted").equals(true)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> toolTransactionsFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

            List<ToolTransactionContext> toolTransactiosnToBeAdded = new ArrayList<>();
            Long storeRoomId = transferRequests.get(0).getTransferToStore().getId();
            long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
            ToolContext tool = ToolsApi.getToolsForTypeAndStore(storeRoomId,toolTypeId);

            ToolTypesContext toolType = ToolsApi.getToolTypes(toolTypeId);
            double quantityTransferred = (double)context.get(FacilioConstants.ContextNames.TOTAL_QUANTITY);
            if (transferRequests != null && !transferRequests.isEmpty()) {

                    ToolTransactionContext woTool = setWorkorderToolObj(quantityTransferred, tool, toolType);
                    toolTransactiosnToBeAdded.add(woTool);

                InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
                        .module(toolTransactionsModule).fields(toolTransactionsFields).addRecords(toolTransactiosnToBeAdded);
                readingBuilder.save();
            }
        }
        return false;
    }
   private ToolTransactionContext setWorkorderToolObj(double quantity, ToolContext tool, ToolTypesContext toolTypes){
        ToolTransactionContext woTool = new ToolTransactionContext();
        woTool.setTransactionState(TransactionState.TRANSFERRED_TO);
        woTool.setIsReturnable(false);
        woTool.setQuantity(quantity);
        woTool.setTransactionType(TransactionType.STOCK.getValue());
        woTool.setParentId(tool.getId());
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setToolType(toolTypes);
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setApprovedState(1);
        woTool.setRemainingQuantity(0);
        return woTool;
    }
}
