package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class FacilioItemTransactionFunctions extends FacilioModuleFunctionImpl {

	public void addOrUpdateItemTransactions(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		List<Map<String, Object>> itemsRaw = (List<Map<String,Object>>)objects.get(1);
		
		List<ItemTransactionsContext> itemTransaction = FieldUtil.getAsBeanListFromMapList(itemsRaw, ItemTransactionsContext.class);
		
		FacilioChain addWorkorderPartChain = TransactionChainFactory.getAddOrUpdateItemTransactionsChain();
		
		FacilioContext context = addWorkorderPartChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransaction);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		addWorkorderPartChain.execute();
		
	}
}
