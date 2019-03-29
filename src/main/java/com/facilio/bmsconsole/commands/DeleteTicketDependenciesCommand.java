package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteTicketDependenciesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			List<Long> parentIds = new ArrayList<>(recordIds);
			List<Long> taskIds = getTaskIds(recordIds);
			if (taskIds != null && !taskIds.isEmpty()) {
				parentIds.addAll(taskIds);
			}
			deleteAttachments(parentIds);
			
			if (taskIds != null && !taskIds.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
				TicketAPI.deleteTickets(module, taskIds);
			}
		}
		return false;
	}
	
	private List<Long> getTaskIds(List<Long> recordIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TASK);
		FacilioField parentId = FieldFactory.getAsMap(fields).get("parentTicketId");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
															.select(fields)
															.module(module)
															.beanClass(TaskContext.class)
															.andCondition(CriteriaAPI.getCondition(parentId, recordIds, PickListOperators.IS));
		List<TaskContext> tasks = builder.get();
		if (tasks != null && !tasks.isEmpty()) {
			return tasks.stream().map(TaskContext::getId).collect(Collectors.toList());
		}
		return null;		
	}
	
	private void deleteAttachments(List<Long> parentIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		FacilioField field = FieldFactory.getAsMap(fields).get("parentId");
		
		SelectRecordsBuilder<AttachmentContext> attachmentBuilder = new SelectRecordsBuilder<AttachmentContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(AttachmentContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, parentIds, PickListOperators.IS))
																	;
		List<AttachmentContext> attachments = attachmentBuilder.get();
		if (attachments != null && !attachments.isEmpty()) {
			List<Long> fileIds = attachments.stream().map(AttachmentContext::getFileId).collect(Collectors.toList());
			FileStoreFactory.getInstance().getFileStore().deleteFiles(fileIds);
		}
	}

}
