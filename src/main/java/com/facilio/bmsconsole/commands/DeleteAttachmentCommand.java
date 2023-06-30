package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

public class DeleteAttachmentCommand extends FacilioCommand implements PostTransactionCommand {

	private Set<Long> idsToUpdateCount;
	private String moduleName;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId =  (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module =  modBean.getModule(moduleName);
		
		if (attachmentIdList != null && !attachmentIdList.isEmpty()) {
			
			SelectRecordsBuilder<AttachmentContext> attachmentBuilder = new SelectRecordsBuilder<AttachmentContext>()
					.select(modBean.getAllFields(moduleName))
					.module(module)
					.beanClass(AttachmentContext.class)
					.andCondition(CriteriaAPI.getIdCondition(attachmentIdList, module))
					;
			List<AttachmentContext> attachments = attachmentBuilder.get();
			if (attachments != null && !attachments.isEmpty()) {
				Set<Long> parentIds = attachments.stream().map(AttachmentContext::getParentId).collect(Collectors.toSet());
				idsToUpdateCount = parentIds;
				context.put(FacilioConstants.ContextNames.PARENT_ID_LIST,parentIds);
			}
			
			// TODO mark file as deleted if no reference for that file is available for all modules
			if (moduleName.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				if (attachments != null && !attachments.isEmpty()) {
					List<Long> fileIds = attachments.stream().map(AttachmentContext::getFileId).collect(Collectors.toList());
					FacilioFactory.getFileStore().deleteFiles(fileIds);
				}
			}			
			else if(moduleName.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS) && recordId > 0) {
				Long currentTime = (Long) context.get(FacilioConstants.ContextNames.CURRENT_TIME);
				if(currentTime == null){
					currentTime = -1L;
				}

				JSONArray attachmentNames = new JSONArray();
	     		JSONObject attach = new JSONObject();
				List<Object> attachmentActivity = new ArrayList<>();
				
    			TaskContext task = WorkOrderAPI.getTask(recordId);
    			long parentAttachmentId = task.getParentTicketId();
     			for(AttachmentContext attaches : attachments) {
    				attachmentNames.add(attaches.getFileName());
    		  		JSONObject info = new JSONObject();
					info.put("subject", task.getSubject());
					info.put("Filename", attaches.getFileName());
					info.put("Url", attaches.getPreviewUrl());
					info.put("type", attaches.getType());
					attachmentActivity.add(info);
    			}
    			attach.put("taskattachment", attachmentActivity);
    			CommonCommandUtil.addActivityToContext(parentAttachmentId, currentTime, WorkOrderActivityType.DELETE_TASK_ATTACHMENT, attach, (FacilioContext) context);
     		} 
			
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getIdCondition(attachmentIdList, module));

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		}
		
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		AttachmentsAPI.updateAttachmentCount(idsToUpdateCount, moduleName);
		return false;
	}

}
