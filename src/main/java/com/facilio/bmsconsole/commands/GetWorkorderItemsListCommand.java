package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;



public class GetWorkorderItemsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderItemsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderItemsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderItemsFieldMap = FieldFactory.getAsMap(workorderItemsFields);
			List<LookupField>lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) workorderItemsFieldMap.get("purchasedItem"));
			lookUpfields.add((LookupField) workorderItemsFieldMap.get("asset"));
			lookUpfields.add((LookupField) workorderItemsFieldMap.get("requestedLineItem"));
			
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			SelectRecordsBuilder<WorkorderItemContext> selectBuilder = new SelectRecordsBuilder<WorkorderItemContext>()
					.select(workorderItemsFields).table(workorderItemsModule.getTableName())
					.moduleName(workorderItemsModule.getName()).beanClass(WorkorderItemContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderItemsFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS))
					.fetchSupplements(lookUpfields);
				

			List<WorkorderItemContext> workorderItems = selectBuilder.get();
			if (workorderItems != null && !workorderItems.isEmpty()) {
				for (WorkorderItemContext woItems : workorderItems) {
					ItemContext inventry = getInventry(woItems.getItem().getId());
					StoreRoomContext storeRoom = StoreroomApi
							.getStoreRoom(inventry.getStoreRoom().getId());
					inventry.setStoreRoom(storeRoom);
					ItemTypesContext item = ItemsApi.getItemTypes(inventry.getItemType().getId());
					inventry.setItemType(item);
					woItems.setItem(inventry);
					if(woItems.getParentTransactionId() > 0) {
						woItems.setRemainingQuantity(InventoryRequestAPI.getParentTransactionRecord(woItems.getParentTransactionId()).getRemainingQuantity());
					}
				}
			}
			context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS, workorderItems);
		}
		return false;
	}
	
	public static ItemContext getInventry(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ItemContext> inventries =  selectBuilder.get();
		if(inventries!=null &&!inventries.isEmpty()) {
			return inventries.get(0);
		}
		return null;
	}

}