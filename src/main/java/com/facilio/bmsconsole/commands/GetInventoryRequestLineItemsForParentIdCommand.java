package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetInventoryRequestLineItemsForParentIdCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		Integer status = (Integer) context.get(FacilioConstants.ContextNames.STATUS);
		
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		SelectRecordsBuilder<InventoryRequestContext> builder = new SelectRecordsBuilder<InventoryRequestContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
		;
				

		if(status != null) {
			builder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(status), EnumOperators.IS));
		}
        
		List<InventoryRequestContext> records = builder.get();
		StringJoiner idString = new StringJoiner(",");
		for(InventoryRequestContext request : records)	{
			idString.add(String.valueOf(request.getId()));
		}
		List<InventoryRequestLineItemContext> lineItems = InventoryRequestAPI.getLineItemsForInventoryRequest(idString.toString());
		context.put(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		return false;
	}

}
