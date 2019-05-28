package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			lineItem = getLineItem(lineItem.getId());
				if(woItemQuantity <= (lineItem.getIssuedQuantity())) {
					updateRequestUsedQuantity(lineItem, woItemQuantity);
					return true;
				}
				return false;
		}
		throw new IllegalArgumentException("Please request approval for the item before using it");
	}
	
	public static InventoryRequestLineItemContext getLineItem(long lineItemId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SelectRecordsBuilder<InventoryRequestLineItemContext> builder = new SelectRecordsBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemId, module))
			
		;
		List<InventoryRequestLineItemContext> lineItems = builder.get();
		if(CollectionUtils.isNotEmpty(lineItems)) {
			return lineItems.get(0);
		}
		throw new IllegalArgumentException("No appropriate lineitem found");
		
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
				if(!toolType.isRotating() && woTool.getQuantity() <= (lineItems.get(0).getIssuedQuantity())) {
					return true;
				}
				else if(toolType.isRotating() && checkRotatingToolCountForWorkOrder(woTool.getTool().getId(), woTool.getParentId()) <= lineItems.get(0).getIssuedQuantity()) {
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
	
	public static boolean checkQuantityForWoItem(long parentTransactionId, double woItemQuantity, double childRemaingingQuantity) throws Exception {
		if(woItemQuantity - childRemaingingQuantity <= 0) {
			return true;
		}
		if(woItemQuantity - childRemaingingQuantity  <= getParentTransactionRecord(parentTransactionId).getRemainingQuantity()) {
			return true;
		}
		return false;
	}
	
	public static ItemTransactionsContext getParentTransactionRecord(long parentTransactionId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

		SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(itemTransactionsFields).table(itemTransactionsModule.getTableName())
				.moduleName(itemTransactionsModule.getName()).beanClass(ItemTransactionsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(parentTransactionId, itemTransactionsModule));
		List<ItemTransactionsContext> woIt = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(woIt)) {
			return woIt.get(0);
		}
		throw new IllegalArgumentException("No appropriate transaction found");
		}
	
public static ToolTransactionContext getParentTransactionRecordForTool(long parentTransactionId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

		SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
				.select(toolTransactionsFields).table(toolTransactionsModule.getTableName())
				.moduleName(toolTransactionsModule.getName()).beanClass(ToolTransactionContext.class)
				.andCondition(CriteriaAPI.getIdCondition(parentTransactionId, toolTransactionsModule));
		List<ToolTransactionContext> woIt = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(woIt)) {
			return woIt.get(0);
		}
		throw new IllegalArgumentException("No appropriate transaction found");
		}
	
public static boolean checkQuantityForWoTool(long parentTransactionId, double woToolQuantity) throws Exception {
		
	
	if(woToolQuantity <= getParentTransactionRecordForTool(parentTransactionId).getQuantity()) {
		return true;
	}
	return false;
	}
	
}
