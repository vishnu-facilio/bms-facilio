package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderLabourCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderLabourModule = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
		List<FacilioField> workorderLabourFields = modBean.getAllFields(FacilioConstants.ContextNames.WO_LABOUR);
		Map<String, FacilioField> labourFieldsMap = FieldFactory.getAsMap(workorderLabourFields);
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) labourFieldsMap.get("labour")));
		List<WorkOrderLabourContext> workorderLabour = (List<WorkOrderLabourContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkOrderLabourContext> workorderLabourlist = new ArrayList<>();
		List<WorkOrderLabourContext> labourToBeAdded = new ArrayList<>();
		if (workorderLabour != null) {
			long parentId = workorderLabour.get(0).getParentId();
			for (WorkOrderLabourContext woLabour : workorderLabour) {
				WorkOrderContext workorder = getWorkorder(parentId);
				
				if (woLabour.getId() > 0) {
					SelectRecordsBuilder<WorkOrderLabourContext> selectBuilder = new SelectRecordsBuilder<WorkOrderLabourContext>()
							.select(workorderLabourFields).table(workorderLabourModule.getTableName())
							.moduleName(workorderLabourModule.getName()).beanClass(WorkOrderLabourContext.class)
							.andCondition(CriteriaAPI.getIdCondition(woLabour.getId(), workorderLabourModule))
							.fetchLookups(lookUpfields);
					        ;	
					List<WorkOrderLabourContext> labourContext = selectBuilder.get();        
					woLabour = setWorkorderItemObj(labourContext.get(0).getLabour(), parentId, workorder, woLabour);
					workorderLabourlist.add(woLabour);
					updateWorkorderLabour(workorderLabourModule, workorderLabourFields, woLabour);
				}
				else
				{
					labourToBeAdded.add(woLabour);
					workorderLabourlist.add(woLabour);
					
				}
			}
			if (labourToBeAdded != null && !labourToBeAdded.isEmpty()) {
				addWorkorderLabour(workorderLabourModule, workorderLabourFields, labourToBeAdded);
			}
			
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderLabourlist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 3);
		}

		return false;
	}

	private WorkOrderLabourContext setWorkorderItemObj(LabourContext labour, long parentId, WorkOrderContext workorder, WorkOrderLabourContext workorderLabour) {
		WorkOrderLabourContext woLabour = new WorkOrderLabourContext();
		woLabour.setIssueTime(workorderLabour.getIssueTime());
		woLabour.setReturnTime(workorderLabour.getReturnTime());
		woLabour.setDuration(workorderLabour.getDuration());
		int duration = 0;
		if (woLabour.getDuration() <= 0) {
			if (woLabour.getIssueTime() <= 0) {
				woLabour.setIssueTime(workorder.getEstimatedStart());
			}
			if (woLabour.getReturnTime() <= 0) {
				woLabour.setReturnTime(workorder.getEstimatedEnd());
				if (woLabour.getIssueTime() >= 0) {
					duration = getEstimatedWorkDuration(woLabour.getIssueTime(), woLabour.getReturnTime());
				} else {
					duration = 0;
				}
			}
		} else {
			duration = (int) (woLabour.getDuration() / (1000 * 60 * 60));
			if (woLabour.getIssueTime() >= 0) {
				woLabour.setReturnTime(woLabour.getIssueTime() + woLabour.getDuration());
			}
		}
		woLabour.setParentId(parentId);
		double costOccured = 0;
		if (labour.getCost() > 0) {
			costOccured = labour.getCost() * duration;
		}
		woLabour.setCost(costOccured);
		return woLabour;
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

	private void addWorkorderLabour(FacilioModule module, List<FacilioField> fields, List<WorkOrderLabourContext> labour)
			throws Exception {
		InsertRecordBuilder<WorkOrderLabourContext> readingBuilder = new InsertRecordBuilder<WorkOrderLabourContext>()
				.module(module).fields(fields).addRecords(labour);
		readingBuilder.save();
	}

	private void updateWorkorderLabour(FacilioModule module, List<FacilioField> fields, WorkOrderLabourContext tool)
			throws Exception {

		UpdateRecordBuilder<WorkOrderLabourContext> updateBuilder = new UpdateRecordBuilder<WorkOrderLabourContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);

	}
	private void updateLabour(LabourContext labour) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.LABOUR);
		UpdateRecordBuilder<LabourContext> updateBuilder = new UpdateRecordBuilder<LabourContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(labour.getId(), module));
		updateBuilder.update(labour);

	}

	public static int getEstimatedWorkDuration(long issueTime, long returnTime) {
		long duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}
		int hours = (int) ((duration / (1000 * 60 * 60)));
		return hours;
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