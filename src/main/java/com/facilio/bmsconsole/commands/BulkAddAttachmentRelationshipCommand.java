package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;

public class BulkAddAttachmentRelationshipCommand extends FacilioCommand implements PostTransactionCommand {

	private static Logger LOGGER = Logger.getLogger(BulkAddAttachmentRelationshipCommand.class.getName());
	private List<Long> idsToUpdateChain;
	private String moduleName;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenanceAPI.logIf(92L, "Entering BulkAddAttachmentRelationshipCommand");
		moduleName = (String) context.get(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME);
		if(moduleName == null ) {
			moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		}

		BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);
		List<WorkOrderContext> workOrders = bulkWorkOrderContext.getWorkOrderContexts();
		List<List<AttachmentContext>> attachmentList = bulkWorkOrderContext.getAttachments();

		if (workOrders.isEmpty() || attachmentList.isEmpty()) {
			return false;
		}

		List<AttachmentContext> attachments = new ArrayList<>();

		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TICKET_ATTACHMENTS);

		for (int k = 0; k < workOrders.size(); k++) {
			WorkOrderContext wo = workOrders.get(k);
			if (attachmentList.get(k) == null || attachmentList.isEmpty()) {
				continue;
			}

			for (int j = 0; j < attachmentList.get(k).size(); j++) {
				attachmentList.get(k).get(j).setParentId(wo.getId());
			}

			JSONArray attachmentNames = new JSONArray();
			JSONObject attach = new JSONObject();
			List<Object> attachmentActivity = new ArrayList<>();

			for(AttachmentContext attaches : attachmentList.get(k)) {
				attachmentNames.add(attaches.getFileName());
				JSONObject info = new JSONObject();
				info.put("Filename", attaches.getFileName());
				info.put("Url", attaches.getPreviewUrl());
				attachmentActivity.add(info);
			}
			attach.put("attachment", attachmentActivity);

			CommonCommandUtil.addActivityToContext(wo.getId(), -1, WorkOrderActivityType.ADD_ATTACHMENT, attach, (FacilioContext) context);

			attachments.addAll(attachmentList.get(k));
		}

		AttachmentsAPI.addAttachments(attachments, moduleName);

		idsToUpdateChain = workOrders.stream().map(i -> i.getId()).collect(Collectors.toList());

		PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddAttachmentRelationshipCommand");
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(idsToUpdateChain)) {
			AttachmentsAPI.updateAttachmentCount(idsToUpdateChain, moduleName);
		}
		return false;
	}
}