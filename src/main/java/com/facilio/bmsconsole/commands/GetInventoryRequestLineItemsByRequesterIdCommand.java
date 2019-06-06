package com.facilio.bmsconsole.commands;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetInventoryRequestLineItemsByRequesterIdCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Long requestedFor = (Long) context.get(FacilioConstants.ContextNames.REQUESTER);
		Integer status = (Integer) context.get(FacilioConstants.ContextNames.STATUS);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		
		SelectRecordsBuilder<InventoryRequestContext> builder = new SelectRecordsBuilder<InventoryRequestContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("REQUESTED_FOR", "requestedFor", String.valueOf(requestedFor), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", "", CommonOperators.IS_EMPTY))
		;
		
		if(status != null) {
			builder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(status), EnumOperators.IS));
		}
        
		List<InventoryRequestContext> records = builder.get();
		StringJoiner idString = new StringJoiner(",");
		for(InventoryRequestContext request : records)	{
			idString.add(String.valueOf(request.getId()));
		}
		List<InventoryRequestLineItemContext> lineItems = InventoryRequestAPI.getLineItemsForInventoryRequest(idString.toString(), null, null);
		context.put(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		return false;
	}
}
