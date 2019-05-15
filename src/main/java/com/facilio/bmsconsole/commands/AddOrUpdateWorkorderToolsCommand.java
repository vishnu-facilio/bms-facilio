package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateWorkorderToolsCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		List<FacilioField> workorderToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(workorderToolsFields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) toolFieldsMap.get("tool"));
		lookUpfields.add((LookupField) toolFieldsMap.get("toolType"));
		List<WorkorderToolsContext> workorderTools = (List<WorkorderToolsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderToolsContext> workorderToolslist = new ArrayList<>();
		List<WorkorderToolsContext> toolsToBeAdded = new ArrayList<>();
		long toolTypesId = -1;
		if (workorderTools != null) {
			long parentId = workorderTools.get(0).getParentId();
			for (WorkorderToolsContext workorderTool : workorderTools) {
				WorkOrderContext workorder = getWorkorder(parentId);
				ToolContext tool = getStockedTools(workorderTool.getTool().getId());
				toolTypesId = tool.getToolType().getId();
				ToolTypesContext toolTypes = getToolType(toolTypesId);
				StoreRoomContext storeRoom = tool.getStoreRoom();
				if(toolTypes.isApprovalNeeded() || storeRoom.isApprovalNeeded()) {
					if(!InventoryRequestAPI.checkQuantityForWoToolNeedingApproval(toolTypes, workorderTool.getRequestedLineItem(), workorderTool)) {
						throw new IllegalArgumentException("Please check the quantity approved in the request");
					}
				}
				if (workorderTool.getId() > 0) {
					SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
							.select(workorderToolsFields).table(workorderToolsModule.getTableName())
							.moduleName(workorderToolsModule.getName()).beanClass(WorkorderToolsContext.class)
							.andCondition(CriteriaAPI.getIdCondition(workorderTool.getId(), workorderToolsModule))
							.fetchLookups(lookUpfields);
					;
					List<WorkorderToolsContext> woIt = selectBuilder.get();
					if (woIt != null) {
						WorkorderToolsContext wTool = woIt.get(0);
						if ((wTool.getQuantity() + wTool.getTool().getCurrentQuantity()) < workorderTool
								.getQuantity()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						} else {
							ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
							if (toolTypes.isApprovalNeeded() || storeRoom.isApprovalNeeded()) {
								approvalState = ApprovalState.APPROVED;
							}
							wTool = setWorkorderItemObj(null, workorderTool.getQuantity(), tool, parentId,
									workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem());
							// update
							wTool.setId(workorderTool.getId());
							workorderToolslist.add(wTool);
							updateWorkorderTools(workorderToolsModule, workorderToolsFields, wTool);
						}
					}
				} else {
					if (tool.getCurrentQuantity() < workorderTool.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
						if (toolTypes.isApprovalNeeded() || storeRoom.isApprovalNeeded()) {
							approvalState = ApprovalState.APPROVED;
						}
						if (toolTypes.isRotating()) {
							List<Long> assetIds = workorderTool.getAssetIds();
							List<AssetContext> assets = getAssetsFromId(assetIds);
							if (assets != null) {
								for (AssetContext asset : assets) {
									if(asset.isUsed()) {
										throw new IllegalArgumentException("Insufficient quantity in inventory!");
									}
									WorkorderToolsContext woTool = new WorkorderToolsContext();
									woTool = setWorkorderItemObj(null, 1, tool, parentId, workorder,
											workorderTool, approvalState, asset, workorderTool.getRequestedLineItem());
									updatePurchasedTool(asset);
									workorderToolslist.add(woTool);
									toolsToBeAdded.add(woTool);
								}
							}
						} else {
							WorkorderToolsContext woTool = new WorkorderToolsContext();
							woTool = setWorkorderItemObj(null, workorderTool.getQuantity(), tool, parentId,
									workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem());
							workorderToolslist.add(woTool);
							toolsToBeAdded.add(woTool);
						}
					}
				}
			}
			if (toolsToBeAdded != null && !toolsToBeAdded.isEmpty()) {
				addWorkorderTools(workorderToolsModule, workorderToolsFields, toolsToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderTools.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST,
					Collections.singletonList(workorderTools.get(0).getParentId()));
			context.put(FacilioConstants.ContextNames.TOOL_ID, workorderTools.get(0).getTool().getId());
			context.put(FacilioConstants.ContextNames.TOOL_IDS,
					Collections.singletonList(workorderTools.get(0).getTool().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderToolslist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypesId);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(toolTypesId));
			context.put(FacilioConstants.ContextNames.WO_TOOLS_LIST, workorderToolslist);
		}

		return false;
	}

	private WorkorderToolsContext setWorkorderItemObj(PurchasedToolContext purchasedtool, double quantity,
			ToolContext tool, long parentId, WorkOrderContext workorder, WorkorderToolsContext workorderTools,
			ApprovalState approvalState, AssetContext asset, InventoryRequestLineItemContext lineItem) {
		WorkorderToolsContext woTool = new WorkorderToolsContext();
		woTool.setIssueTime(workorderTools.getIssueTime());
		woTool.setReturnTime(workorderTools.getReturnTime());
		woTool.setDuration(workorderTools.getDuration());
		if(lineItem != null) {
			woTool.setRequestedLineItem(lineItem);
		}
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
		if(asset!=null) {
			woTool.setAsset(asset);
		}
		woTool.setApprovedState(approvalState);
		woTool.setRemainingQuantity(quantity);
		woTool.setQuantity(quantity);
		woTool.setTool(tool);
		woTool.setToolType(tool.getToolType());
		woTool.setSysModifiedTime(System.currentTimeMillis());
		woTool.setParentId(parentId);
		double costOccured = 0;
		if (tool.getRate() > 0) {
			costOccured = tool.getRate() * duration * woTool.getQuantity();
		}
		woTool.setCost(costOccured);
		if(workorder!=null) {
			woTool.setWorkorder(workorder);
			if(workorder.getAssignedTo()!=null) {
				woTool.setIssuedTo(workorder.getAssignedTo());
			}
		}
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
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) fieldMap.get("storeRoom"));
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id)
				.fetchLookups(lookUpfields);

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

	public static List<AssetContext> getAssetsFromId(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(AssetContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<AssetContext> purchasedToolList = selectBuilder.get();

		if (purchasedToolList != null && !purchasedToolList.isEmpty()) {
			return purchasedToolList;
		}
		return null;
	}

	private void updatePurchasedTool(AssetContext asset) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
		updateBuilder.update(asset);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");
	}
}