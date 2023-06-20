package com.facilio.workflowv2.modulefunctions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.ScriptContext;

@ScriptModule(moduleName = FacilioConstants.ContextNames.ITEM_TRANSACTIONS)
public class FacilioItemTransactionFunctions extends FacilioModuleFunctionImpl {

	public void addOrUpdateItemTransactions(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		Map<String, Object> itemsRaw = (Map<String, Object>) objects.get(1);
		
		ItemTransactionsContext itemTransaction = FieldUtil.getAsBeanFromMap(itemsRaw, ItemTransactionsContext.class);
		
		FacilioChain addWorkorderPartChain = TransactionChainFactory.getAddOrUpdateItemTransactionsChain();
		
		FacilioContext context = addWorkorderPartChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(itemTransaction));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		addWorkorderPartChain.execute();
		
	}
}
