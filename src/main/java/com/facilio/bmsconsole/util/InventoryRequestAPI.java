package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryRequestAPI {

	public static List<InventoryRequestLineItemContext> getLineItemsForInventoryRequest(String requestIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		

		LookupFieldMeta itemField = new LookupFieldMeta((LookupField) fieldsAsMap.get("item"));
		LookupField itemTypeField = (LookupField) modBean.getField("itemType", FacilioConstants.ContextNames.ITEM);
		itemField.addChildLookupFIeld(itemTypeField);
		
		LookupField storeRoomField = (LookupField) modBean.getField("storeRoom", FacilioConstants.ContextNames.ITEM);
		itemField.addChildLookupFIeld(storeRoomField);
		
		LookupFieldMeta toolField = new LookupFieldMeta((LookupField) fieldsAsMap.get("tool"));
		LookupField toolTypeField = (LookupField) modBean.getField("toolType", FacilioConstants.ContextNames.TOOL);
		toolField.addChildLookupFIeld(toolTypeField);
		
		LookupField storeRoomForToolField = (LookupField) modBean.getField("storeRoom", FacilioConstants.ContextNames.TOOL);
		toolField.addChildLookupFIeld(storeRoomForToolField);
		
		
		List<LookupField>fetchLookup = Arrays.asList(itemField,toolField);
		
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", requestIds, NumberOperators.EQUALS))
				.fetchLookups(fetchLookup)
		;
				

		List<InventoryRequestLineItemContext> records = builder.get();
		return records;
		

	}
}
