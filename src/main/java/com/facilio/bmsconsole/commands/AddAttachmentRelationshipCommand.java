package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Logger;

public class AddAttachmentRelationshipCommand implements Command {

	private static Logger LOGGER = Logger.getLogger(AddAttachmentRelationshipCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME);
		if(moduleName == null ) {
			moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		}
		
		Long recordId = null;
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds == null || recordIds.isEmpty()) {
			recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		}
		else {
			recordId = recordIds.get(0); //Have to discuss why this is done like this.
		}
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Invalid module name during addition of attachments");
		}
		
		if(recordId == null || recordId == -1) {
			throw new IllegalArgumentException("Invalid record id during addition of attachments");
		}
		FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.IDS_TO_UPDATE_COUNT, Collections.singletonList(recordId));
		FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, context.get(FacilioConstants.ContextNames.MODULE_NAME));
		
		List<AttachmentContext> attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST);
		if(attachments != null && !attachments.isEmpty()) {
			for(AttachmentContext attachment : attachments) {
				attachment.setParentId(recordId);
			}
			
//			if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
//				LOGGER.info("Inserting attachment for record id : "+recordId);
//				TransactionManager tm = FTransactionManager.getTransactionManager();
//				if (tm != null) {
//					Transaction t = tm.getTransaction();
//					LOGGER.info("Connection & free connection size before adding attachments : "+((FacilioTransaction) t).getConnectionSize()+"::"+((FacilioTransaction) t).getFreeConnectionSize());
//				}
//			}
			
			AttachmentsAPI.addAttachments(attachments, moduleName);
			List<Long> attachmentIds = new ArrayList<>();
			for (AttachmentContext ac : attachments) {
				attachmentIds.add(ac.getId());
			}

			
			context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, AttachmentsAPI.getAttachments(moduleName, attachmentIds));
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)) {
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TICKET_ATTACHMENTS);
			}
			List<AttachmentContext> attachment = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
			JSONArray attachmentNames = new JSONArray();
     		JSONObject attach = new JSONObject();
			List<Object> attachmentActivity = new ArrayList<>();
     		long parentId = (long) recordId;
     		
     		List<Long> singlerecordId = new ArrayList<>();
     		if(recordIds == null || recordIds.isEmpty()) {
     			 singlerecordId = Collections.singletonList(recordId);
     		}
     		
     		if(moduleName.equals("ticketattachments")) {
			for(AttachmentContext attaches : attachment) {
				attachmentNames.add(attaches.getFileName());
		  		JSONObject info = new JSONObject();
						info.put("Filename", attaches.getFileName());
						info.put("Url", attaches.getPreviewUrl());
						attachmentActivity.add(info);
			}
			attach.put("attachment", attachmentActivity);
			 CommonCommandUtil.addActivityToContext(parentId, -1, WorkOrderActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
     		}
     		else if(moduleName.equals("taskattachments")) {
    			List<TaskContext> oldTasks = getTasks(singlerecordId);
    			Map<Long, TaskContext> oldTicketMap = new HashMap<>();
    			if(oldTasks != null && !oldTasks.isEmpty()) {
    				for(TaskContext oldTask : oldTasks) {
    					oldTicketMap.put(oldTask.getId(), oldTask);
    				}
    			}
    		
					TaskContext oldTask = oldTicketMap.get(singlerecordId.get(0));
    			long parentAttachmentId = oldTask.getParentTicketId();
     			for(AttachmentContext attaches : attachment) {
    				attachmentNames.add(attaches.getFileName());
    		  		JSONObject info = new JSONObject();
					long TaskId = attaches.getParentId();
					List<Long> taskid = Collections.singletonList(TaskId);
        			List<TaskContext> Task = getTasks(taskid);
					info.put("subject", Task.get(0).getSubject());
    						info.put("Filename", attaches.getFileName());
    						info.put("Url", attaches.getPreviewUrl());
    						info.put("type", attaches.getType());
    						attachmentActivity.add(info);
    			}
    			attach.put("taskattachment", attachmentActivity);
    			 CommonCommandUtil.addActivityToContext(parentAttachmentId, -1, WorkOrderActivityType.ADD_TASK_ATTACHMENT, attach, (FacilioContext) context);
     		} 

		}
		
		return false;
	}
	
	private List<TaskContext> getTasks(List<Long> ids) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<TaskContext> tasks = builder.get();
		if(tasks != null && !tasks.isEmpty()) {
			return tasks;
		}
		return null;
	}
}