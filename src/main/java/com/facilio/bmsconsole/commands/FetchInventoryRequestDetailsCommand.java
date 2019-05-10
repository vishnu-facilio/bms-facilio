package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchInventoryRequestDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (inventoryRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
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
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", String.valueOf(inventoryRequestContext.getId()), NumberOperators.EQUALS))
					.fetchLookups(fetchLookup);
		
			List<InventoryRequestLineItemContext> list = builder.get();
			inventoryRequestContext.setLineItems(list);
		}
		return false;
	}

}
