package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.amazonaws.services.kms.model.UnsupportedOperationException;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.BooleanField;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;



public class ValidateAndCreateValuesForInputTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
//		if(ActivityType.ADD_TASK_INPUT == context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(recordIds != null && !recordIds.isEmpty()) {
				Map<Long, TaskContext> oldTasks = getTask(recordIds);
				// Bulk operation for closing multiple tasks check only.
				for(int i = 0; i < recordIds.size(); i++) {
					TaskContext completeRecord = oldTasks.get(recordIds.get(i));
					if(completeRecord != null) {
						if(task.getStatus() != null && task.getStatus().getId() != -1) {
							TicketStatusContext status = TicketAPI.getStatus("Closed");
							if(status.getId() == task.getStatus().getId()) {
								if (completeRecord.getInputTypeEnum() != InputType.NONE && ((completeRecord.getInputValue() == null || completeRecord.getInputValue().isEmpty())) && (task.getInputValue() == null || task.getInputValue().isEmpty())) {
									throw new UnsupportedOperationException("Input task cannot be closed without entering input value");
								}
								
								if(completeRecord.isAttachmentRequired()) {
									List<AttachmentContext> attachments = TicketAPI.getRelatedAttachments(recordIds.get(i));
									if (attachments == null || attachments.isEmpty() ) {
										throw new UnsupportedOperationException("Atleast one file has to be attached since attachment is required to close the task");
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
									if (completeRecord.getReadingFieldId() != -1) {
										addReading(task, completeRecord, completeRecord.getReadingField(), context, true);
									}
									break;
								case RADIO:
									if (completeRecord.getReadingFieldId() != -1) {
										EnumField enumField = (EnumField) completeRecord.getReadingField();
										if(enumField.getIndex(task.getInputValue()) == -1) {
											throw new IllegalArgumentException("Invalid input value");
										}
										addReading(task, completeRecord, enumField, context, true);
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
								case TEXT:
									if (completeRecord.getReadingFieldId() != -1) {
										addReading(task, completeRecord, completeRecord.getReadingField(), context, true);
									}
									break;
								case BOOLEAN:
									if (completeRecord.getReadingFieldId() != -1) {
										BooleanField booleanField = (BooleanField) task.getReadingField();
										if (!(task.getInputValue().equals("true") || task.getInputValue().equals("false") || task.getInputValue().equals(booleanField.getTrueVal()) || task.getInputValue().equals(booleanField.getFalseVal()))) {
											throw new IllegalArgumentException("Invalid input value");
										}
										addReading(task, completeRecord, completeRecord.getReadingField(), context, true);
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
				reading.addReading("woId", oldTask.getParentTicketId());
				reading.addReading("taskId", oldTask.getId());
				reading.addReading("taskUniqueId", oldTask.getUniqueId());
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_NAME, readingModule.getName());
			context.put(FacilioConstants.ContextNames.READING, reading);
			context.put(FacilioConstants.ContextNames.RECORD, reading);						
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		}
	}
	
	private Map<Long, TaskContext> getTask(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		Map<Long, TaskContext> tasks = builder.getAsMap();
		if(tasks != null && !tasks.isEmpty()) {
			for (Entry<Long, TaskContext> entry : tasks.entrySet()) {
				TaskContext task = entry.getValue();
				if (task.getReadingFieldId() != -1) {
					task.setReadingField(modBean.getField(task.getReadingFieldId()));
				}
			}
			return tasks;
		}
		return null;
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
