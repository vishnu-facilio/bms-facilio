package com.facilio.bmsconsoleV3.commands.transferRequest;

import java.util.*;

import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
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
        if (!Objects.isNull(context.get(FacilioConstants.ContextNames.TOOL_TYPES)) && transferRequests.get(0).getIsStaged() && transferRequests.get(0).getIsCompleted()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            List<FacilioField> toolTransactionsFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
            Long storeRoomId = transferRequests.get(0).getTransferToStore().getId();
            List<V3TransferRequestLineItemContext> toolTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
            for(V3TransferRequestLineItemContext toolTypeLineItem : toolTypesList) {
            List<V3ToolTransactionContext> toolTransactiosnToBeAdded = new ArrayList<>();
            long toolTypeId = toolTypeLineItem.getToolType().getId();
            V3ToolContext tool = V3ToolsApi.getToolsForTypeAndStore(storeRoomId, toolTypeId);

            V3ToolTypesContext toolType = toolTypeLineItem.getToolType();
            double quantityTransferred = toolTypeLineItem.getQuantity();
            V3ToolTransactionContext woTool = setWorkorderToolObj(quantityTransferred, tool, toolType,transferRequests.get(0).getId());
            toolTransactiosnToBeAdded.add(woTool);
            V3Util.createRecordList(toolTransactionsModule, FieldUtil.getAsMapList(toolTransactiosnToBeAdded,V3ToolTransactionContext.class),null,null);

        }
        }
        return false;
    }
   private V3ToolTransactionContext setWorkorderToolObj(double quantity, V3ToolContext tool, V3ToolTypesContext toolTypes,Long id){
        V3ToolTransactionContext woTool = new V3ToolTransactionContext();
        woTool.setTransactionState(TransactionState.TRANSFERRED_TO);
        woTool.setIsReturnable(false);
        woTool.setQuantity(quantity);
        woTool.setTransactionType(TransactionType.STOCK.getValue());
        woTool.setParentId(id);
        woTool.setTool(tool);
        woTool.setStoreRoom(tool.getStoreRoom());
        woTool.setToolType(toolTypes);
        woTool.setSysModifiedTime(System.currentTimeMillis());
        woTool.setApprovedState(1);
        woTool.setRemainingQuantity(0);
        return woTool;
    }
}
