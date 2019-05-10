package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryRequestAPI {

	public static List<InventoryRequestLineItemContext> getLineItemsForInventoryRequest(String requestIds, String itemIds, String toolIds) throws Exception {
		
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
		
		Criteria idCriteria = new Criteria();
		if(itemIds != null) {
			idCriteria.addAndCondition(CriteriaAPI.getCondition("ITEM", "item" , itemIds, NumberOperators.EQUALS));
		}
		if(toolIds != null) {
			idCriteria.addOrCondition(CriteriaAPI.getCondition("TOOL", "tool" , toolIds, NumberOperators.EQUALS));
		}
        builder.andCriteria(idCriteria);
		List<InventoryRequestLineItemContext> records = builder.get();
		return records;
		

	}
	
	public static boolean checkQuantityForWoItemNeedingApproval(long itemId, long parentId, double woItemQuantity) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("ITEM", "item", String.valueOf(itemId), NumberOperators.EQUALS))
			
		;
		if(parentId != -1) {
			builder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
		}
		List<InventoryRequestLineItemContext> lineItems = builder.get();
		if(CollectionUtils.isNotEmpty(lineItems)) {
			if(woItemQuantity <= lineItems.get(0).getQuantity()) {
				updateRequestUsedQuantity(lineItems.get(0).getId(), woItemQuantity);
				return true;
			}
			return false;
		}
		return false;
	}
	public static boolean checkQuantityForWoToolNeedingApproval(long toolId, long parentId, double woToolQuantity) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("TOOL", "tool", String.valueOf(toolId), NumberOperators.EQUALS))
			
		;
		if(parentId != -1) {
			builder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
		}
		List<InventoryRequestLineItemContext> lineItems = builder.get();
		if(CollectionUtils.isNotEmpty(lineItems)) {
			if(woToolQuantity <= lineItems.get(0).getQuantity()) {
				updateRequestUsedQuantity(lineItems.get(0).getId(), woToolQuantity);
				return true;
			}
			return false;
		}
		return false;
	}
	
	public static void updateRequestUsedQuantity(long lineItemId, double usedQuantity) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField usedQuantityField = modBean.getField("usedQuantity", lineItemModule.getName());
		updateMap.put("usedQuantity", usedQuantity);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		updatedfields.add(usedQuantityField);
		UpdateRecordBuilder<InventoryRequestLineItemContext> updateBuilder = new UpdateRecordBuilder<InventoryRequestLineItemContext>()
						.module(lineItemModule)
						.fields(updatedfields)
						.andCondition(CriteriaAPI.getIdCondition(lineItemId, lineItemModule));
	   updateBuilder.updateViaMap(updateMap);
	}
	
}
