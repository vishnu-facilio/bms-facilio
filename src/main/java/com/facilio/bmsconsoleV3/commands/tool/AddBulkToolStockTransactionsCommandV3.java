package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddBulkToolStockTransactionsCommandV3 extends FacilioCommand {
        @Override
        public boolean executeCommand(Context context) throws Exception {
        ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);
        List<V3PurchasedToolContext> purchasedTools = (List<V3PurchasedToolContext>) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
        List<V3ToolTransactionContext> toolTransaction = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(purchasedTools)){
            for(V3PurchasedToolContext purchasedTool:purchasedTools){
                V3ToolTransactionContext transaction = new V3ToolTransactionContext();
                transaction.setQuantity(purchasedTool.getQuantity());
                transaction.setTransactionState(TransactionState.ADDITION.getValue());
                transaction.setTool(purchasedTool.getTool());
                transaction.setParentId(purchasedTool.getId());
                transaction.setPurchasedTool(purchasedTool);
                transaction.setIsReturnable(false);
                if (shipment != null) {
                    transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                    transaction.setShipment(shipment.getId());
                } else {
                    transaction.setTransactionType(TransactionType.STOCK.getValue());
                }
                transaction.setToolType(purchasedTool.getToolType());
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                transaction.setStoreRoom(purchasedTool.getTool().getStoreRoom());
                toolTransaction.add(transaction);
            }
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
        V3Util.createRecordList(module, FieldUtil.getAsMapList(toolTransaction,V3ToolTransactionContext.class),null,null);
        context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransaction);
        context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);

        return false;
    }

}
