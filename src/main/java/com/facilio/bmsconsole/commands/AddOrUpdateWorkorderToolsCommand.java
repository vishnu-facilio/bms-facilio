package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderToolsCommand implements Command {

	@SuppressWarnings("unchecked")
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
				ToolContext stockedTools = getStockedTools(workorderTool.getTool().getId());
				ToolTypesContext toolTypes = getToolType(stockedTools.getToolType().getId());
				if (stockedTools.getQuantity() < workorderTool.getQuantity()) {
					throw new IllegalStateException("Insufficient quantity in inventory!");
				} else {
					if (toolTypes.isIndividualTracking()) {
						List<Long> purchasedToolIds = (List<Long>) context
								.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
						List<PurchasedToolContext> purchasedTool = getPurchasedToolsListFromId(purchasedToolIds);
						if (purchasedTool != null) {
							for (PurchasedToolContext pTool : purchasedTool) {
								WorkorderToolsContext woTool = new WorkorderToolsContext();
								pTool.setIsUsed(true);
								woTool = setWorkorderItemObj(pTool, 1, stockedTools, parentId, workorder,
										workorderTool);
								updatePurchasedTool(pTool);
								if (workorderTool.getId() <= 0) {
									// Insert
									workorderToolslist.add(woTool);
									toolsToBeAdded.add(woTool);
								} else {
									// update
									workorderToolslist.add(woTool);
									updateWorkorderTools(workorderToolsModule, workorderToolsFields, woTool);
								}
							}
						}
					} else {
						WorkorderToolsContext woTool = new WorkorderToolsContext();
						woTool = setWorkorderItemObj(null, 1, stockedTools, parentId, workorder, workorderTool);
						if (workorderTool.getId() <= 0) {
							// Insert
							workorderToolslist.add(woTool);
							toolsToBeAdded.add(woTool);
						} else {
							// update
							workorderToolslist.add(woTool);
							updateWorkorderTools(workorderToolsModule, workorderToolsFields, woTool);
						}
					}

					// double costOccured = 0;
					// int duration = 0;
					//
					// if (workorderTool.getDuration() <= 0) {
					// if (workorderTool.getIssueTime() <= 0) {
					// workorderTool.setIssueTime(workorder.getEstimatedStart());
					// }
					// if (workorderTool.getReturnTime() <= 0) {
					// workorderTool.setReturnTime(workorder.getEstimatedEnd());
					// if (workorderTool.getIssueTime() >= 0) {
					// duration =
					// getEstimatedWorkDuration(workorderTool.getIssueTime(),
					// workorderTool.getReturnTime());
					// } else {
					// duration = 0;
					// }
					// }
					// } else {
					// duration = (int) (workorderTool.getDuration() / (1000 *
					// 60 * 60));
					// if (workorderTool.getIssueTime() >= 0) {
					// workorderTool.setReturnTime(workorderTool.getIssueTime()
					// + workorderTool.getDuration());
					// }
					// }
					//
					// if (stockedTools.getRate() > 0) {
					// costOccured = stockedTools.getRate() * duration;
					// }
					// workorderTool.setTool(stockedTools);
					// workorderTool.setCost(costOccured);
					// workorderTool.setParentId(parentId);
					// workorderTool.setSysModifiedTime(System.currentTimeMillis());
					//
					// if (workorderTool.getId() <= 0) {
					// // Insert
					// workorderToolslist.add(workorderTool);
					// toolsToBeAdded.add(workorderTool);
					// } else {
					// // update
					// workorderToolslist.add(workorderTool);
					// updateWorkorderTools(workorderToolsModule,
					// workorderToolsFields, workorderTool);
					// }
					// break;

				}
			}
			if (toolsToBeAdded != null && !toolsToBeAdded.isEmpty()) {
				addWorkorderTools(workorderToolsModule, workorderToolsFields, toolsToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderTools.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.TOOL_ID, workorderTools.get(0).getTool().getId());
			context.put(FacilioConstants.ContextNames.TOOL_IDS,
					Collections.singletonList(workorderTools.get(0).getTool().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderToolslist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
		}

		return false;
	}

	private WorkorderToolsContext setWorkorderItemObj(PurchasedToolContext purchasedtool, double quantity,
			ToolContext tool, long parentId, WorkOrderContext workorder, WorkorderToolsContext workorderTools) {
		WorkorderToolsContext woTool = new WorkorderToolsContext();
		woTool.setIssueTime(workorderTools.getIssueTime());
		woTool.setReturnTime(workorderTools.getReturnTime());
		woTool.setDuration(workorderTools.getDuration());
		int duration = 0;
		if (woTool.getDuration() <= 0) {
			if (woTool.getIssueTime() <= 0) {
				woTool.setIssueTime(workorder.getEstimatedStart());
			}
			if (woTool.getReturnTime() <= 0) {
				woTool.setReturnTime(workorder.getEstimatedEnd());
				if (woTool.getIssueTime() >= 0) {
					duration = getEstimatedWorkDuration(woTool.getIssueTime(), woTool.getReturnTime());
				} else {
					duration = 0;
				}
			}
		} else {
			duration = (int) (woTool.getDuration() / (1000 * 60 * 60));
			if (woTool.getIssueTime() >= 0) {
				woTool.setReturnTime(woTool.getIssueTime() + woTool.getDuration());
			}
		}
		woTool.setTransactionType(TransactionType.WORKORDER);
		woTool.setTransactionState(TransactionState.ISSUE);
		woTool.setIsReturnable(false);
		if (purchasedtool != null) {
			woTool.setPurchasedTool(purchasedtool);
		}

		woTool.setQuantity(quantity);
		woTool.setTool(tool);
		woTool.setSysModifiedTime(System.currentTimeMillis());
		woTool.setParentId(parentId);
		double costOccured = 0;
		if (tool.getRate() > 0) {
			costOccured = tool.getRate() * duration;
		}
		woTool.setCost(costOccured);
		return woTool;
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

		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id);

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

	public static ToolTypesContext getToolType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
				.beanClass(ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, itemTypesModule));

		List<ToolTypesContext> toolTypes = itemTypesselectBuilder.get();
		if (toolTypes != null && !toolTypes.isEmpty()) {
			return toolTypes.get(0);
		}
		return null;
	}

	public static List<PurchasedToolContext> getPurchasedToolsListFromId(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<PurchasedToolContext> purchasedToolList = selectBuilder.get();

		if (purchasedToolList != null && !purchasedToolList.isEmpty()) {
			return purchasedToolList;
		}
		return null;
	}

	private void updatePurchasedTool(PurchasedToolContext purchasedTool) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		UpdateRecordBuilder<PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedTool.getId(), module));
		updateBuilder.update(purchasedTool);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");
	}
}