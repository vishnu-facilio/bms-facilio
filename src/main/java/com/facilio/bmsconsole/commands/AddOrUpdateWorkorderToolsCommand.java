package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateWorkorderToolsCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		List<FacilioField> workorderToolsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
		Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(workorderToolsFields);
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);

		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) toolFieldsMap.get("tool"));
		lookUpfields.add((LookupField) toolFieldsMap.get("toolType"));
		List<WorkorderToolsContext> workorderTools = (List<WorkorderToolsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderToolsContext> workorderToolslist = new ArrayList<>();
		List<WorkorderToolsContext> toolsToBeAdded = new ArrayList<>();
		long toolTypesId = -1;
		if (CollectionUtils.isNotEmpty(workorderTools)) {
			long parentId = workorderTools.get(0).getParentId();
			
			for (WorkorderToolsContext workorderTool : workorderTools) {
				long parentTransactionId = workorderTool.getParentTransactionId();
				WorkOrderContext workorder = getWorkorder(parentId);
				ToolContext tool = getStockedTools(workorderTool.getTool().getId());
				toolTypesId = tool.getToolType().getId();
				ToolTypesContext toolTypes = getToolType(toolTypesId);
				StoreRoomContext storeRoom = tool.getStoreRoom();
				if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
					if(!V3InventoryRequestAPI.checkQuantityForWoToolNeedingApproval(toolTypes, workorderTool.getRequestedLineItem(), workorderTool)) {
						throw new IllegalArgumentException("Please check the quantity approved/issued in the request");
					}
				}
				else if(workorderTool.getParentTransactionId() > 0) {
					if(!InventoryRequestAPI.checkQuantityForWoTool(workorderTool.getParentTransactionId(), workorderTool.getQuantity())){
						throw new IllegalArgumentException("Please check the quantity issued");
					}
				}
				
				if (workorderTool.getId() > 0) {
					if (!eventTypes.contains(EventType.EDIT)) {
						eventTypes.add(EventType.EDIT);
					}
					SelectRecordsBuilder<WorkorderToolsContext> selectBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
							.select(workorderToolsFields).table(workorderToolsModule.getTableName())
							.moduleName(workorderToolsModule.getName()).beanClass(WorkorderToolsContext.class)
							.andCondition(CriteriaAPI.getIdCondition(workorderTool.getId(), workorderToolsModule))
							.fetchSupplements(lookUpfields);
					;
					List<WorkorderToolsContext> woIt = selectBuilder.get();
					if (woIt != null) {
						WorkorderToolsContext wTool = woIt.get(0);
						if ((wTool.getQuantity() + wTool.getTool().getCurrentQuantity()) < workorderTool
								.getQuantity()) {
							throw new IllegalArgumentException("Insufficient quantity in inventory!");
						} else {
							ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
							if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
									approvalState = ApprovalState.APPROVED;
							}
							if (toolTypes.isRotating()) {
								wTool = setWorkorderItemObj(null, 1, tool, parentId,
										workorder, workorderTool, approvalState, wTool.getAsset(), workorderTool.getRequestedLineItem(), parentTransactionId, context);
							
						
							} else {
								wTool = setWorkorderItemObj(null, workorderTool.getQuantity(), tool, parentId,
										workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem(), parentTransactionId, context);
							}

							// update
							wTool.setId(workorderTool.getId());
							workorderToolslist.add(wTool);
							updateWorkorderTools(workorderToolsModule, workorderToolsFields, wTool);
						}
					}
				} else {
					if (!eventTypes.contains(EventType.CREATE)) {
						eventTypes.add(EventType.CREATE);
					}
					if (workorderTool.getRequestedLineItem() == null && workorderTool.getParentTransactionId() <= 0 && tool.getQuantity() < workorderTool.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
						if (workorderTool.getRequestedLineItem() != null && workorderTool.getRequestedLineItem().getId() > 0) {
							approvalState = ApprovalState.APPROVED;
						}
						if (toolTypes.isRotating()) {
							List<Long> assetIds = workorderTool.getAssetIds();
							List<AssetContext> assets = getAssetsFromId(assetIds);
							if (assets != null) {
								for (AssetContext asset : assets) {
									if (workorderTool.getRequestedLineItem() == null && workorderTool.getParentTransactionId() <= 0 && asset.isUsed()) {
										throw new IllegalArgumentException("Insufficient quantity in inventory!");
									}
									WorkorderToolsContext woTool = new WorkorderToolsContext();
									asset.setIsUsed(true);
									woTool = setWorkorderItemObj(null, 1, tool, parentId, workorder,
											workorderTool, approvalState, asset, workorderTool.getRequestedLineItem(), parentTransactionId, context);
									updatePurchasedTool(asset);
									workorderToolslist.add(woTool);
									toolsToBeAdded.add(woTool);
								}
							}
						} else {
							WorkorderToolsContext woTool = new WorkorderToolsContext();
							woTool = setWorkorderItemObj(null, workorderTool.getQuantity(), tool, parentId,
									workorder, workorderTool, approvalState, null, workorderTool.getRequestedLineItem(), parentTransactionId, context);
							workorderToolslist.add(woTool);
							toolsToBeAdded.add(woTool);
						}
					}
				}
			}
			if (toolsToBeAdded != null && !toolsToBeAdded.isEmpty()) {
				addWorkorderTools(workorderToolsModule, workorderToolsFields, toolsToBeAdded);
			}
			if(CollectionUtils.isNotEmpty(workorderToolslist)) {
				List<Long> recordIds = new ArrayList<>();
				for(WorkorderToolsContext ws : workorderToolslist){
					recordIds.add(ws.getId());
				}
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORKORDER_TOOLS);
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
													  ApprovalState approvalState, AssetContext asset, V3InventoryRequestLineItemContext lineItem, long parentTransactionId, Context context) throws Exception{
		WorkorderToolsContext woTool = new WorkorderToolsContext();
		woTool.setIssueTime(workorderTools.getIssueTime());
		woTool.setReturnTime(workorderTools.getReturnTime());
		woTool.setDuration(workorderTools.getDuration());
		woTool.setTransactionState(TransactionState.USE);
		
		if(lineItem != null) {
			woTool.setRequestedLineItem(lineItem);
			woTool.setParentTransactionId(ToolsApi.getToolTransactionsForRequestedLineItem(lineItem.getId()).getId());
			
		}

		double duration = 0;
		if (woTool.getDuration() <= 0) {
//			if (woTool.getIssueTime() <= 0) {
//				woTool.setIssueTime(workorder.getScheduledStart());
//			}
//			if (woTool.getReturnTime() <= 0) {
//				woTool.setReturnTime(workorder.getEstimatedEnd());
//			}
			if (woTool.getIssueTime() >= 0 && woTool.getReturnTime() >= 0) {
				duration = getEstimatedWorkDuration(woTool.getIssueTime(), woTool.getReturnTime());
			} else {
				if(workorder.getActualWorkDuration() > 0) {
					double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
					duration = Math.round(hours*100.0)/100.0;
				}
				else{
					duration = workorderTools.getId() > 0 ? 0 : 1;
				}
			}
		} else {
			duration = woTool.getDuration();
			if (woTool.getIssueTime() >= 0) {
				woTool.setReturnTime((long) (woTool.getIssueTime() + (woTool.getDuration() * (60*60*1000))));
			}
		}
		woTool.setTransactionType(TransactionType.WORKORDER);
		woTool.setIsReturnable(false);
		Double rate = null;
		if (purchasedtool != null) {
			woTool.setPurchasedTool(purchasedtool);
			rate= purchasedtool.getRate();
		} 
		if(asset!=null) {
			woTool.setAsset(asset);
		}
		woTool.setApprovedState(approvalState);
		woTool.setRemainingQuantity(quantity);
		woTool.setQuantity(quantity);
		woTool.setTool(tool);
		woTool.setStoreRoom(tool.getStoreRoom());
		woTool.setToolType(tool.getToolType());
		woTool.setSysModifiedTime(System.currentTimeMillis());
		woTool.setParentId(parentId);
		woTool.setRate(rate);
		double costOccured = 0;
		if (tool.getRate() > 0) {
			costOccured = tool.getRate() * duration * woTool.getQuantity();
		}
		woTool.setCost(costOccured);
		if(workorder!=null) {
			woTool.setWorkorder(workorder);
		}
		
		if(parentTransactionId != -1) {
			woTool.setParentTransactionId(parentTransactionId);
		}

		JSONObject newinfo = new JSONObject();
		
		if (tool.getToolType() != null) {
			ToolTypesContext toolType = ToolsApi.getToolTypes(tool.getToolType().getId()); 
			if(toolType != null && toolType.isRotating() && asset != null && woTool.getTransactionStateEnum() == TransactionState.USE) {
				if(woTool.getIssuedTo() != null) {
					asset.setLastIssuedToUser(woTool.getIssuedTo());
				}
				if(woTool.getWorkorder() != null) {
					asset.setLastIssuedToWo(woTool.getWorkorder().getId());
				}
				asset.setLastIssuedTime(System.currentTimeMillis());
				AssetsAPI.updateAsset(asset, asset.getId());

			
				if(woTool.getTransactionTypeEnum() == TransactionType.WORKORDER) {
					newinfo.put("woId", woTool.getParentId());
				}
				CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.USE, newinfo,
						(FacilioContext) context);
			}
		}

		woTool.setDuration(duration*3600);
		
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
				.fetchSupplements(lookUpfields);

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

	public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
		double duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}
		
		double hours = duration / (60*60*1000);
		return Math.round(hours*100.0)/100.0;
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