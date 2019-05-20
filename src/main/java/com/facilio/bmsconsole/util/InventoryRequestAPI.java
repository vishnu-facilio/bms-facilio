package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryRequestAPI {

	public static List<InventoryRequestLineItemContext> getLineItemsForInventoryRequest(String requestIds, String itemIds, String toolIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		

		List<LookupField>lookUpFields = new ArrayList<>();
		lookUpFields.add((LookupField) modBean.getField("itemType", module.getName()));
		lookUpFields.add((LookupField) modBean.getField("toolType", module.getName()));
		lookUpFields.add((LookupField) modBean.getField("storeRoom", module.getName()));
		lookUpFields.add((LookupField) modBean.getField("asset", module.getName()));
		
		
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", requestIds, NumberOperators.EQUALS))
				.fetchLookups(lookUpFields)
		;
		
		List<InventoryRequestLineItemContext> records = builder.get();
		return records;
		

	}
	
	public static boolean checkQuantityForWoItemNeedingApproval(ItemTypesContext itemType, InventoryRequestLineItemContext lineItem, double woItemQuantity) throws Exception {
		if(lineItem != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			
			SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
					.module(module)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
					.select(fields)
					.andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module))
				
			;
			List<InventoryRequestLineItemContext> lineItems = builder.get();
			if(CollectionUtils.isNotEmpty(lineItems)) {
				if(woItemQuantity <= (lineItems.get(0).getQuantity())) {
					if(!itemType.isRotating()) {
						updateRequestUsedQuantity(lineItems.get(0), woItemQuantity);
					}
					else {
						updateRequestUsedQuantity(lineItems.get(0), lineItems.get(0).getUsedQuantity() + woItemQuantity);
					}
					return true;
				}
				return false;
			}
			return false;
		}
		throw new IllegalArgumentException("Please request approval for the item before using it");
	}
	public static boolean checkQuantityForWoToolNeedingApproval(ToolTypesContext toolType, InventoryRequestLineItemContext lineItem, WorkorderToolsContext woTool) throws Exception {
		if(lineItem != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			
			SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
					.module(module)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
					.select(fields)
					.andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module))
				
			;
			List<InventoryRequestLineItemContext> lineItems = builder.get();
			if(CollectionUtils.isNotEmpty(lineItems)) {
				if(!toolType.isRotating() && woTool.getQuantity() <= (lineItems.get(0).getQuantity())) {
					return true;
				}
				else if(toolType.isRotating() && checkRotatingToolCountForWorkOrder(woTool.getTool().getId(), woTool.getParentId()) < lineItems.get(0).getQuantity()) {
						return true;	
				}
				return false;
			}
			return false;
		}
		throw new IllegalArgumentException("Please request approval for the tool before using it");
	}
	
	public static double checkRotatingToolCountForWorkOrder(long toolId, long parentId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		List<FacilioField> workorderToolsFields = modBean.getAllFields(workorderToolsModule.getName());
		
		SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
				.select(workorderToolsFields).table(workorderToolsModule.getTableName())
				.moduleName(workorderToolsModule.getName()).beanClass(WorkorderToolsContext.class)
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId",String.valueOf(parentId) , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("TOOL", "tool",String.valueOf(toolId) , NumberOperators.EQUALS))
			;
		
		List<WorkorderToolsContext> workorderTools = selectBuilder.get();
		return workorderTools.size();
		
		
	}
	public static void updateRequestUsedQuantity(InventoryRequestLineItemContext lineItem, double usedQuantity) throws Exception {
		
		if(lineItem != null) {
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
							.andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), lineItemModule))
							;
		   updateBuilder.updateViaMap(updateMap);
		}
	}
	
}
