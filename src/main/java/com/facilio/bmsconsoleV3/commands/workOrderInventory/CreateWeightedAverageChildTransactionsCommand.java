package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CreateWeightedAverageChildTransactionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkorderItemContext> workOrderItems = recordMap.get(moduleName);
        List<? extends V3ItemTransactionsContext> itemTransactions = (List<V3ItemTransactionsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Boolean isWeightedAverage = (Boolean) context.get(FacilioConstants.ContextNames.IS_WEIGHTED_AVERAGE);
        FacilioModule itemTransactionModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        List<FacilioField> itemTransactionFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        if(isWeightedAverage!=null && isWeightedAverage && CollectionUtils.isNotEmpty(itemTransactions)){
            V3WorkorderItemContext workOrderItem = workOrderItems.get(0); // since we'll have only one workOrder item for weighted avg
            for(V3ItemTransactionsContext itemTransaction : itemTransactions){
                itemTransaction.setParentTransactionId(workOrderItem.getId());
            }
            V3RecordAPI.addRecord(false,itemTransactions,itemTransactionModule,itemTransactionFields);
        }
        return false;
    }
}
