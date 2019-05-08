package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryRequestAPI {

	public static List<InventoryRequestLineItemContext> getLineItemsForInventoryRequest(String requestIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		List<LookupField>lookUpFields = new ArrayList<>();
		lookUpFields.add((LookupField) modBean.getField("item", module.getName()));
		lookUpFields.add((LookupField) modBean.getField("tool",  module.getName()));
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", requestIds, NumberOperators.EQUALS))
		;
				

		List<InventoryRequestLineItemContext> records = builder.get();
		return records;
		

	}
}
