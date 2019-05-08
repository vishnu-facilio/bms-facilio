package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class GetActiveContractPriceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		int inventoryType = (Integer)context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		Condition condition = new Condition();
		if(inventoryType == 1) {
			Long itemTypeId = (Long)context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID);
			condition = CriteriaAPI.getCondition("ITEM_TYPE", "itemType", String.valueOf(itemTypeId) , NumberOperators.EQUALS);
		}
		else if(inventoryType == 2) {
			Long toolTypeId = (Long)context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
			condition = CriteriaAPI.getCondition("TOOL_TYPE", "toolType", String.valueOf(toolTypeId) , NumberOperators.EQUALS);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule contractModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACTS);
		
		FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS_LINE_ITEMS);
		List<FacilioField> lineItemFields = modBean.getAllFields(lineItemModule.getName());
		Map<String, FacilioField> lineItemFieldMap = FieldFactory.getAsMap(lineItemFields);
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(lineItemFieldMap.get("unitPrice"));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields).table(contractModule.getTableName())
				.innerJoin(lineItemModule.getTableName()).on(contractModule.getTableName()+".ID = "+lineItemModule.getTableName()+".PURCHASE_CONTRACT")
				.andCondition(condition)
				.andCondition(CriteriaAPI.getCondition(contractModule.getTableName()+".ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(contractModule.getTableName()+".STATUS", "status", String.valueOf(ContractsContext.Status.APPROVED.getValue()) , NumberOperators.EQUALS))
			    ;
		List<Map<String, Object>> list = selectBuilder.get();
		if(!CollectionUtils.isEmpty(list)) {
			context.put(FacilioConstants.ContextNames.UNIT_PRICE, list.get(0).get("unitPrice"));
		}
		return false;
	}

}
