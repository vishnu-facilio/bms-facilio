package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class AddAttachmentRelationshipCommand extends FacilioCommand implements PostTransactionCommand {

	private static Logger LOGGER = Logger.getLogger(AddAttachmentRelationshipCommand.class.getName());
	private List<Long> idsToUpdateChain;
	private String moduleName;
	private String parentModuleName;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		moduleName = (String) context.get(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME);
		if(moduleName == null ) {
			moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		}
		
		this.parentModuleName =  (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);

		Long currentTime = (Long) context.get(FacilioConstants.ContextNames.CURRENT_TIME);
		if(currentTime == null){
			currentTime = -1L;
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
		
		idsToUpdateChain = Collections.singletonList(recordId);
//		FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.IDS_TO_UPDATE_COUNT, Collections.singletonList(recordId));
//		FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.MODULE_NAME, context.get(FacilioConstants.ContextNames.MODULE_NAME));
		
		List<AttachmentContext> attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST);
		if(attachments != null && !attachments.isEmpty()) {
			for(AttachmentContext attachment : attachments) {
				attachment.setParentId(recordId);
				ModuleBaseWithCustomFields parent = new ModuleBaseWithCustomFields();
				parent.setId(recordId);
				attachment.setParent(parent);
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

			attachments = AttachmentsAPI.getAttachments(moduleName, attachmentIds);
			context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachments);
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)) {
				CommonCommandUtil.addEventType(EventType.ADD_TICKET_ATTACHMENTS, (FacilioContext) context);
			}
			JSONArray attachmentNames = new JSONArray();
     		JSONObject attach = new JSONObject();
			List<Object> attachmentActivity = new ArrayList<>();

     		if(moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, WorkOrderActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
     		}
     		else if(moduleName.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS)) {
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
    			CommonCommandUtil.addActivityToContext(parentAttachmentId, currentTime, WorkOrderActivityType.ADD_TASK_ATTACHMENT, attach, (FacilioContext) context);
     		} 
     		else if(moduleName.equals(FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, ItemActivityType.ITEM_ATTACHMENT, attach, (FacilioContext) context);
     		}
			else if(moduleName.equals(FacilioConstants.ContextNames.TOOL_TYPES_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
			else if(moduleName.equals(FacilioConstants.ContextNames.RECEIVABLE_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
			else if(moduleName.equals(FacilioConstants.ContextNames.CONTRACT_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
     		else if(moduleName.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, AssetActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
     		}
			else if(Arrays.asList(FacilioConstants.ContextNames.QUOTE_ATTACHMENTS,FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ATTACHMENTS).contains(moduleName)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
			else if(moduleName.equals(FacilioConstants.ContextNames.STORE_ROOM_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
     		}
			else if(moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
			else if(Arrays.asList(FacilioConstants.ContextNames.VENDOR_ATTACHMENTS,FacilioConstants.ContextNames.TENANT_ATTACHMENTS,FacilioConstants.ContextNames.DELIVERY_ATTACHMENTS).contains(moduleName)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
			}
			else if(moduleName.equals(FacilioConstants.ContextNames.SERVICE_REQUEST_ATTACHMENTS)) {
				setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
				CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
     		}
			else  {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule parentModule = modBean.getModule(parentModuleName);
				if (parentModule.isCustom()) {
					setAttachmentsActivityContext(attachments, attachmentNames, attach, attachmentActivity);
					CommonCommandUtil.addActivityToContext(recordId, currentTime, CommonActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);
				}
			}

		}
		
		return false;
	}

	private void setAttachmentsActivityContext(List<AttachmentContext> attachments, JSONArray attachmentNames, JSONObject attach, List<Object> attachmentActivity) throws Exception {
		for(AttachmentContext attaches : attachments) {
			attachmentNames.add(attaches.getFileName());
			JSONObject info = new JSONObject();
			info.put("Filename", attaches.getFileName());
			info.put("Url", attaches.getPreviewUrl());
			attachmentActivity.add(info);
		}
		attach.put("attachment", attachmentActivity);
	}

	@Override
	public boolean postExecute() throws Exception {
		if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(idsToUpdateChain)) {
			AttachmentsAPI.updateAttachmentCount(idsToUpdateChain, moduleName);
		}
		return false;
	}
	
}