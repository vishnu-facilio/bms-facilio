package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderToolsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		List<FacilioField> workorderToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		List<WorkorderToolsContext> workorderTools = (List<WorkorderToolsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderToolsContext> workorderToolslist = new ArrayList<>();
		List<WorkorderToolsContext> toolsToBeAdded = new ArrayList<>();

		if (workorderTools != null) {
			long parentId = workorderTools.get(0).getParentId();
			for (WorkorderToolsContext workorderTool : workorderTools) {
				WorkOrderContext workorder = getWorkorder(parentId);
				ToolContext stockedTools = getStockedTools(workorderTool.getStockedTool().getId());
				if (stockedTools.getQuantity() < workorderTool.getQuantity()) {
					throw new IllegalStateException("Insufficient quantity in inventory!");
				} else {
					double costOccured = 0;
					int duration = 0;

					if (workorderTool.getDuration() <= 0) {
						if (workorderTool.getIssueTime() <= 0) {
							workorderTool.setIssueTime(workorder.getEstimatedStart());
						}
						if (workorderTool.getReturnTime() <= 0) {
							workorderTool.setReturnTime(workorder.getEstimatedEnd());
							if (workorderTool.getIssueTime() >= 0) {
								duration = getEstimatedWorkDuration(workorderTool.getIssueTime(),workorderTool.getReturnTime());
							} else {
								duration = 0;
							}
						}
					}

					else {
						duration = (int) (workorderTool.getDuration()/(1000 * 60 * 60));
						if (workorderTool.getIssueTime() >= 0) {
							workorderTool.setReturnTime(workorderTool.getIssueTime() + workorderTool.getDuration());
						}
					}

					if (stockedTools.getRate() > 0) {
						costOccured = stockedTools.getRate() * duration;
					}
					workorderTool.setStockedTool(stockedTools);
					workorderTool.setCost(costOccured);
					workorderTool.setParentId(parentId);
					workorderTool.setSysModifiedTime(System.currentTimeMillis());

					if (workorderTool.getId() <= 0) {
						// Insert
						workorderToolslist.add(workorderTool);
						toolsToBeAdded.add(workorderTool);
					} else {
						// update
						workorderToolslist.add(workorderTool);
						updateWorkorderTools(workorderToolsModule, workorderToolsFields, workorderTool);
					}
					// break;

				}
			}
			if (toolsToBeAdded != null && !toolsToBeAdded.isEmpty()) {
				addWorkorderTools(workorderToolsModule, workorderToolsFields, toolsToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderTools.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.TOOL_ID, workorderTools.get(0).getStockedTool().getId());
			context.put(FacilioConstants.ContextNames.TOOL_IDS,
					Collections.singletonList(workorderTools.get(0).getStockedTool().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderToolslist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
		}

		return false;
	}

	public static WorkOrderContext getWorkorder(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);

		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(WorkOrderContext.class).andCustomWhere(module.getTableName() + ".ID = ?", id);

		List<WorkOrderContext> workorders = selectBuilder.get();

		if (workorders != null && !workorders.isEmpty()) {
			return workorders.get(0);
		}
		return null;
	}

	public static ToolContext getStockedTools(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolContext.class).andCustomWhere(module.getTableName() + ".ID = ?", id);

		List<ToolContext> stockedTools = selectBuilder.get();

		if (stockedTools != null && !stockedTools.isEmpty()) {
			return stockedTools.get(0);
		}
		return null;
	}

	private void addWorkorderTools(FacilioModule module, List<FacilioField> fields, List<WorkorderToolsContext> tools)
			throws Exception {
		InsertRecordBuilder<WorkorderToolsContext> readingBuilder = new InsertRecordBuilder<WorkorderToolsContext>()
				.module(module).fields(fields).addRecords(tools);
		readingBuilder.save();
	}

	private void updateWorkorderTools(FacilioModule module, List<FacilioField> fields, WorkorderToolsContext tool)
			throws Exception {

		UpdateRecordBuilder<WorkorderToolsContext> updateBuilder = new UpdateRecordBuilder<WorkorderToolsContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	public static int getEstimatedWorkDuration(long issueTime, long returnTime) {
		long duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}
		int hours = (int) ((duration / (1000 * 60 * 60)));
		return hours;
	}
}