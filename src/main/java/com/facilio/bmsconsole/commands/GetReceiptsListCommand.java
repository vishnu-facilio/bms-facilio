package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PoLineItemsSerialNumberContext;
import com.facilio.bmsconsole.context.ReceiptContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class GetReceiptsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long receivableId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (StringUtils.isNotEmpty(moduleName) && receivableId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ReceiptContext> builder = new SelectRecordsBuilder<ReceiptContext>()
					.module(module)
					.select(fields)
					.beanClass(ReceiptContext.class)
					.andCondition(CriteriaAPI.getCondition("RECEIVABLE_ID", "receivableId", String.valueOf(receivableId), NumberOperators.EQUALS))
			        .fetchLookup((LookupField) fieldsAsMap.get("lineItem"))
			        ;

			List<ReceiptContext> list = builder.get();
			fetchItemType(list);
			for(ReceiptContext item : list) {
				item.setNoOfSerialNumbers(getSerialNumberCount(item.getId(),item.getLineItem().getId()));
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		}
		return false;
	}
	
	private void fetchItemType(List<ReceiptContext> list) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(ReceiptContext receipt:list) {
			if(receipt.getLineItem().getInventoryTypeEnum() == InventoryType.ITEM) {
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
				List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
                SelectRecordsBuilder<ItemTypesContext> builder = new SelectRecordsBuilder<ItemTypesContext>()
						.module(module)
						.select(fields)
						.beanClass(ItemTypesContext.class)
						.andCondition(CriteriaAPI.getIdCondition(receipt.getLineItem().getItemType().getId(), module));
				List<ItemTypesContext> itemList = builder.get();
				if(!CollectionUtils.isEmpty(itemList)) {
					receipt.getLineItem().setItemType(itemList.get(0));
				}
				else {
					receipt.getLineItem().setItemType(null);
				}
			}
			else {
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
				List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);
                SelectRecordsBuilder<ToolTypesContext> toolBuilder = new SelectRecordsBuilder<ToolTypesContext>()
						.module(module)
						.select(fields)
						.beanClass(ToolTypesContext.class)
						.andCondition(CriteriaAPI.getIdCondition(receipt.getLineItem().getToolType().getId(), module));
				List<ToolTypesContext> toolList = toolBuilder.get();	
				if(!CollectionUtils.isEmpty(toolList)) {
					receipt.getLineItem().setToolType(toolList.get(0));
				}
				else {
					receipt.getLineItem().setToolType(null);
				}
			
			}
			
		}
		
		
	}
	
	private int getSerialNumberCount (long receiptId,long lineItemId) throws Exception {
		int count = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String polineitemserialnomodulename = FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS;
		List<FacilioField> fields = modBean.getAllFields(polineitemserialnomodulename);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<PoLineItemsSerialNumberContext> builder = new SelectRecordsBuilder<PoLineItemsSerialNumberContext>()
				.moduleName(polineitemserialnomodulename)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(polineitemserialnomodulename))
				.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("lineItem"), String.valueOf(lineItemId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("receiptId"), String.valueOf(receiptId), NumberOperators.EQUALS))
		        ;
		List<PoLineItemsSerialNumberContext> list = builder.get();
		if(list!=null && !list.isEmpty()) {
			count = list.size();
		}
		
		return count;
	}

}
