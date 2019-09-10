package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;



public class ValidateAndCreateValuesForInputTaskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
//		if(ActivityType.ADD_TASK_INPUT == context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(recordIds != null && !recordIds.isEmpty()) {
				Map<Long, TaskContext> oldTasks = TicketAPI.getTaskMap(recordIds);
				// Bulk operation for closing multiple tasks check only.
				for(int i = 0; i < recordIds.size(); i++) {
					TaskContext completeRecord = oldTasks.get(recordIds.get(i));
					if(completeRecord != null) {
						if(task.getStatusNew() != -1) {
							if(task.getStatusNewEnum() == TaskStatus.CLOSED) {
								if (completeRecord.getInputTypeEnum() != InputType.NONE && ((completeRecord.getInputValue() == null || completeRecord.getInputValue().isEmpty())) && (task.getInputValue() == null || task.getInputValue().isEmpty())) {
									throw new IllegalArgumentException("Input task cannot be closed without entering input value");
								}
								
								if(completeRecord.isAttachmentRequired()) {
									List<AttachmentContext> attachments = AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.TASK_ATTACHMENTS, recordIds.get(i));
									if (attachments == null || attachments.isEmpty() ) {
										throw new IllegalArgumentException("Atleast one file has to be attached since attachment is required to close the task");
									}
								}
							}
						}
						if((task.getInputValue() != null && !task.getInputValue().isEmpty()) || (task.getInputValues() != null && !task.getInputValues().isEmpty())) {
							if(task.getInputTime() == -1) {
								task.setInputTime(System.currentTimeMillis());
							}
							switch(completeRecord.getInputTypeEnum()) {
								case READING:
									if (completeRecord.getReadingFieldId() != -1) {
										addReading(task, completeRecord, completeRecord.getReadingField(), context, false);
									}
									break;
								case NUMBER:
									Double.parseDouble(task.getInputValue());
									break;
								case RADIO:
									if (completeRecord.getReadingFieldId() != -1) {
										EnumField enumField = (EnumField) completeRecord.getReadingField();
										if(enumField.getIndex(task.getInputValue()) == -1) {
											throw new IllegalArgumentException("Invalid input value");
										}
									}
									else {
										List<String> options = TicketAPI.getTaskInputOptions(completeRecord.getId());
										if(!options.contains(task.getInputValue())) {
											throw new IllegalArgumentException("Invalid input value");
										}
									}
									break;
//								case CHECKBOX:
//									List<String> options = TicketAPI.getTaskInputOptions(completeRecord.getId());
//									if (!task.getInputValues().stream().allMatch(input -> options.contains(input))) {
//										throw new IllegalArgumentException("Invalid input value");
//									}
//									task.setInputValue(StringUtils.join(task.getInputValues(), ","));
//									break;
								case BOOLEAN:
									if (completeRecord.getReadingFieldId() != -1) {
										BooleanField booleanField = (BooleanField) task.getReadingField();
										if (!(task.getInputValue().equals("true") || task.getInputValue().equals("false") || task.getInputValue().equals(booleanField.getTrueVal()) || task.getInputValue().equals(booleanField.getFalseVal()))) {
											throw new IllegalArgumentException("Invalid input value");
										}
									}
									break;
								case NONE:
									task.setInputValue(null);
								default:
									break;
							}
						}
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("Task cannot be null during updation of Task");
		}
//		}
		return false;
	}
	
	private void addReading(TaskContext newTask, TaskContext oldTask, FacilioField field, Context context, boolean isPMReading) throws Exception {
		
		long pmId = -1;
		if (isPMReading) {
			WorkOrderContext wo = getWO(oldTask.getParentTicketId());
			pmId = wo.getPm() != null ? wo.getPm().getId() : -1;
		}
		
		if (!isPMReading || pmId != -1) {
			FacilioModule readingModule = field.getModule();
			ReadingContext reading = new ReadingContext();
			reading.setId(oldTask.getReadingDataId());
			reading.addReading(field.getName(), newTask.getInputValue());
			reading.setTtime(newTask.getInputTime());
			long resourceId = isPMReading ? pmId : oldTask.getResource().getId();
			reading.setParentId(resourceId);
			if (oldTask.getLastReading() == null) {
				ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, field);
				newTask.setLastReading(meta.getValue() != null ? meta.getValue() : -1);
			}
			
			if (isPMReading) {
				ReadingsAPI.addDefaultPMTaskReading(oldTask, reading);
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_NAME, readingModule.getName());
			context.put(FacilioConstants.ContextNames.READING, reading);
			context.put(FacilioConstants.ContextNames.RECORD, reading);						
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
		}
	}
	
	private WorkOrderContext getWO(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<WorkOrderContext> workorders = builder.get();
		if(workorders != null && !workorders.isEmpty()) {
			return workorders.get(0);
		}
		return null;
	}
	
}
