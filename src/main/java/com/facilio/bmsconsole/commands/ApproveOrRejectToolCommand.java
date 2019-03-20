package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ApproveOrRejectToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> toolTransactionsFieldMap = FieldFactory.getAsMap(toolTransactionsFields);

		List<Long> recordIds =  (List<Long>) context
				.get(FacilioConstants.ContextNames.RECORD_ID);
		List<Long> parentIds = new ArrayList<>();
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("tool")));
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("purchasedTool")));
		lookUpfields.add(new LookupFieldMeta((LookupField) toolTransactionsFieldMap.get("toolType")));
		
		SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>().select(toolTransactionsFields)
				.table(toolTransactionsModule.getTableName()).moduleName(toolTransactionsModule.getName()).beanClass(ToolTransactionContext.class)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, toolTransactionsModule))
				.fetchLookups(lookUpfields);
		
		List<ToolTransactionContext> toolTransactions = selectBuilder.get();
		for(ToolTransactionContext transactions : toolTransactions) {
			parentIds.add(transactions.getParentId());
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTransactions);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
		return false;
	}

}