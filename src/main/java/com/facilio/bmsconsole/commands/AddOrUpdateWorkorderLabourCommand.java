package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateWorkorderLabourCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderLabourModule = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
		List<FacilioField> workorderLabourFields = modBean.getAllFields(FacilioConstants.ContextNames.WO_LABOUR);
		Map<String, FacilioField> labourFieldsMap = FieldFactory.getAsMap(workorderLabourFields);
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);

		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) labourFieldsMap.get("labour"));
		List<WorkOrderLabourContext> workorderLabours = (List<WorkOrderLabourContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkOrderLabourContext> workorderLabourlist = new ArrayList<>();
		List<WorkOrderLabourContext> labourToBeAdded = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(workorderLabours)) {
			long parentId = workorderLabours.get(0).getParentId();
			WorkOrderContext workorder = getWorkorder(parentId);
			
			for (WorkOrderLabourContext woLabour : workorderLabours) {
//				woLabour.calculate();
				woLabour.setParent(workorder);

				if (woLabour.getId() > 0) {
					if (!eventTypes.contains(EventType.EDIT)) {
						eventTypes.add(EventType.EDIT);
					}
					SelectRecordsBuilder<WorkOrderLabourContext> selectBuilder = new SelectRecordsBuilder<WorkOrderLabourContext>()
							.select(workorderLabourFields).table(workorderLabourModule.getTableName())
							.moduleName(workorderLabourModule.getName()).beanClass(WorkOrderLabourContext.class)
							.andCondition(CriteriaAPI.getIdCondition(woLabour.getId(), workorderLabourModule))
							.fetchSupplements(lookUpfields);
					        ;	
					List<WorkOrderLabourContext> labourContext = selectBuilder.get();        
					woLabour = setWorkorderItemObj(labourContext.get(0).getLabour(), parentId, workorder, woLabour);
					workorderLabourlist.add(woLabour);
					updateWorkorderLabour(workorderLabourModule, workorderLabourFields, woLabour);
				}
				else
				{
					if (!eventTypes.contains(EventType.CREATE)) {
						eventTypes.add(EventType.CREATE);
					}
					woLabour = setWorkorderItemObj(woLabour.getLabour(), parentId, workorder, woLabour);
					labourToBeAdded.add(woLabour);
					workorderLabourlist.add(woLabour);
					
				}
			}
			if (labourToBeAdded != null && !labourToBeAdded.isEmpty()) {
				addWorkorderLabour(workorderLabourModule, workorderLabourFields, labourToBeAdded);
			}

			if(CollectionUtils.isNotEmpty(workorderLabourlist)) {
				List<Long> recordIds = new ArrayList<>();
				for(WorkOrderLabourContext ws : workorderLabourlist){
					recordIds.add(ws.getId());
				}
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WO_LABOUR);
			}
			
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workorderLabours.get(0).getParentId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderLabourlist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 3);
			context.put(FacilioConstants.ContextNames.WO_LABOUR_LIST, workorderLabourlist);
		}

		return false;
	}

	private WorkOrderLabourContext setWorkorderItemObj(LabourContext labour, long parentId, WorkOrderContext workorder, WorkOrderLabourContext workorderLabour) {
		WorkOrderLabourContext woLabour = new WorkOrderLabourContext();
		woLabour.setStartTime(workorderLabour.getStartTime());
		woLabour.setEndTime(workorderLabour.getEndTime());
		woLabour.setDuration(workorderLabour.getDuration());
		woLabour.setId(workorderLabour.getId());
		woLabour.setParent(workorderLabour.getParent());
		double duration = 0;
		if (woLabour.getDuration() <= 0) {
//			if (woLabour.getStartTime() <= 0) {
//				woLabour.setStartTime(workorder.getScheduledStart());
//			}
//			if (woLabour.getEndTime() <= 0) {
//				woLabour.setEndTime(workorder.getEstimatedEnd());
//			}
			if (woLabour.getStartTime() >= 0 && woLabour.getEndTime() >= 0) {
				duration = getEstimatedWorkDuration(woLabour.getStartTime(), woLabour.getEndTime());
			} else {
				if(workorder.getActualWorkDuration() > 0) {
					double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
					duration = Math.round(hours*100.0)/100.0;
				}
				else{
					duration = workorderLabour.getId() > 0 ? 0 : 1;
				}
			}
		} else {
			duration = woLabour.getDuration();
			if (woLabour.getStartTime() > 0) {
				long durationVal = (long) (woLabour.getDuration() * 60 * 60 * 1000);
				woLabour.setEndTime(woLabour.getStartTime() + durationVal);
			}
		}
		
		woLabour.setParentId(parentId);
		double costOccured = 0;
		if (labour.getCost() > 0) {
			costOccured = labour.getCost() * duration;
		}
		woLabour.setCost(costOccured);
		woLabour.setLabour(labour);
		woLabour.setDuration(duration * 3600);
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

	private void addWorkorderLabour(FacilioModule module, List<FacilioField> fields, List<WorkOrderLabourContext> woLabours)
			throws Exception {
		InsertRecordBuilder<WorkOrderLabourContext> readingBuilder = new InsertRecordBuilder<WorkOrderLabourContext>()
				.module(module).fields(fields).addRecords(woLabours);
		readingBuilder.save();
	}

	private void updateWorkorderLabour(FacilioModule module, List<FacilioField> fields, WorkOrderLabourContext labour)
			throws Exception {

		UpdateRecordBuilder<WorkOrderLabourContext> updateBuilder = new UpdateRecordBuilder<WorkOrderLabourContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(labour.getId(), module));
		updateBuilder.update(labour);

	}
	
	public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
		double duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}
		
		double hours = ((duration / (1000 * 60 * 60)));
		return Math.round(hours*100.0)/100.0;
	}

	
}