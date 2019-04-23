package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteWorkorderToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<WorkorderToolsContext> deletedWorkorderTools = (List<WorkorderToolsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (deletedWorkorderTools != null && !deletedWorkorderTools.isEmpty()) {
			for (WorkorderToolsContext workordertool : deletedWorkorderTools) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
				List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

				Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
				List<LookupField>lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) toolFieldsMap.get("toolType"));

				SelectRecordsBuilder<ToolContext> itemSelectBuilder = new SelectRecordsBuilder<ToolContext>()
						.select(toolFields).table(toolModule.getTableName()).moduleName(toolModule.getName())
						.beanClass(ToolContext.class).andCustomWhere(toolModule.getTableName() + ".ID = ?",
								workordertool.getTool().getId())
						.fetchLookups(lookUpfields);

				List<ToolContext> tools = itemSelectBuilder.get();
				if (tools != null && !tools.isEmpty()) {
					ToolContext tool = tools.get(0);
					if (tool.getToolType().isRotating()) {
						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
						List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);

						SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
								.select(fields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(PurchasedToolContext.class).andCustomWhere(module.getTableName() + ".ID = ?",
										workordertool.getPurchasedTool().getId());

						List<PurchasedToolContext> purchasedTools = selectBuilder.get();

						if (purchasedTools != null && !purchasedTools.isEmpty()) {
							PurchasedToolContext purchasedTool = purchasedTools.get(0);
							purchasedTool.setIsUsed(false);
							updatePurchasedTool(purchasedTool, module, fields);
						}
					}
				}

			}
		}

		return false;
	}

	private void updatePurchasedTool(PurchasedToolContext purchasedTool,FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedTool.getId(), module));
		updateBuilder.update(purchasedTool);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
