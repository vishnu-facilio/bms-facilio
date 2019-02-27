package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWorkorderItemsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderItemsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderItemsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderItemsFieldMap = FieldFactory.getAsMap(workorderItemsFields);
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			SelectRecordsBuilder<WorkorderItemContext> selectBuilder = new SelectRecordsBuilder<WorkorderItemContext>()
					.select(workorderItemsFields).table(workorderItemsModule.getTableName())
					.moduleName(workorderItemsModule.getName()).beanClass(WorkorderItemContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderItemsFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS));

			List<WorkorderItemContext> workorderItems = selectBuilder.get();
			if (workorderItems != null && !workorderItems.isEmpty()) {
				for (WorkorderItemContext woItems : workorderItems) {
					InventryContext inventry = getInventry(woItems.getInventory().getId());
					StoreRoomContext storeRoom = StoreroomApi
							.getStoreRoom(inventry.getStoreRoom().getId());
					inventry.setStoreRoom(storeRoom);
					ItemsContext item = ItemsApi.getItem(inventry.getItem().getId());
					inventry.setItem(item);
					woItems.setInventory(inventry);
				}
			}
			context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS, workorderItems);
		}
		return false;
	}
	
	public static InventryContext getInventry(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTRY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTRY);
		SelectRecordsBuilder<InventryContext> selectBuilder = new SelectRecordsBuilder<InventryContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(InventryContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<InventryContext> inventries =  selectBuilder.get();
		if(inventries!=null &&!inventries.isEmpty()) {
			return inventries.get(0);
		}
		return null;
	}

}