package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.StockedToolsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolsContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWorkorderToolsListCommand implements Command {

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
			SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
					.select(workorderItemsFields).table(workorderItemsModule.getTableName())
					.moduleName(workorderItemsModule.getName()).beanClass(WorkorderToolsContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderItemsFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS));

			List<WorkorderToolsContext> workorderTools = selectBuilder.get();
			if (workorderTools != null && !workorderTools.isEmpty()) {
				for (WorkorderToolsContext woTools : workorderTools) {
					StockedToolsContext stockedTool = geStockedTools(woTools.getStockedTool().getId());
					StoreRoomContext storeRoom = StoreroomApi
							.getStoreRoom(stockedTool.getStoreRoom().getId());
					stockedTool.setStoreRoom(storeRoom);
					ToolsContext tool = ToolsApi.getTool(stockedTool.getTool().getId());
					stockedTool.setTool(tool);
					woTools.setStockedTool(stockedTool);
				}
			}
			context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS, workorderTools);
		}
		return false;
	}
	
	public static StockedToolsContext geStockedTools(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STOCKED_TOOLS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STOCKED_TOOLS);
		SelectRecordsBuilder<StockedToolsContext> selectBuilder = new SelectRecordsBuilder<StockedToolsContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(StockedToolsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<StockedToolsContext> inventries =  selectBuilder.get();
		if(inventries!=null &&!inventries.isEmpty()) {
			return inventries.get(0);
		}
		return null;
	}
}
