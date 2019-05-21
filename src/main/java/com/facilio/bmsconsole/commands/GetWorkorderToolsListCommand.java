package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetWorkorderToolsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderToolsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderToolsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderToolsFieldMap = FieldFactory.getAsMap(workorderToolsFields);
			List<LookupField>lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) workorderToolsFieldMap.get("purchasedTool"));
			lookUpfields.add((LookupField) workorderToolsFieldMap.get("asset"));
			lookUpfields.add((LookupField) workorderToolsFieldMap.get("requestedLineItem"));
			
			
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
					.select(workorderToolsFields).table(workorderToolsModule.getTableName())
					.moduleName(workorderToolsModule.getName()).beanClass(WorkorderToolsContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderToolsFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS))
					.fetchLookups(lookUpfields);

			List<WorkorderToolsContext> workorderTools = selectBuilder.get();
			if (workorderTools != null && !workorderTools.isEmpty()) {
				for (WorkorderToolsContext woTools : workorderTools) {
					ToolContext stockedTool = geStockedTools(woTools.getTool().getId());
					StoreRoomContext storeRoom = StoreroomApi
							.getStoreRoom(stockedTool.getStoreRoom().getId());
					stockedTool.setStoreRoom(storeRoom);
					ToolTypesContext tool = ToolsApi.getToolTypes(stockedTool.getToolType().getId());
					stockedTool.setToolType(tool);
					woTools.setTool(stockedTool);
				}
			}
			context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS, workorderTools);
		}
		return false;
	}
	
	public static ToolContext geStockedTools(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ToolContext> inventries =  selectBuilder.get();
		if(inventries!=null &&!inventries.isEmpty()) {
			return inventries.get(0);
		}
		return null;
	}
}
