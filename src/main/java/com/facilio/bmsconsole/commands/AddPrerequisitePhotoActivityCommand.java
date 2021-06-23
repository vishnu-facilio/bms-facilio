package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AddPrerequisitePhotoActivityCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<String> fileFileName = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME);
		List<String> fileContentType = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE);
		WorkOrderActivityType woActivity = (WorkOrderActivityType) context.get(FacilioConstants.ContextNames.CURRENT_WO_ACTIVITY);
		if (FacilioConstants.ContextNames.PREREQUISITE_PHOTOS.equalsIgnoreCase(moduleName)) {
			WorkOrderContext wo = WorkOrderAPI.getWorkOrder(parentId);
			long parentAttachmentId = wo.getId();
			List<Object> attachmentActivity = new ArrayList<>();
			JSONObject attach = new JSONObject();
			JSONObject info = new JSONObject();
			info.put("subject", wo.getSubject());
			info.put("Filename", fileFileName);
			info.put("type", fileContentType);
			attachmentActivity.add(info);
			attach.put("taskattachment", attachmentActivity);
			CommonCommandUtil.addActivityToContext(parentAttachmentId, -1, woActivity,
					attach, (FacilioContext) context);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		}
		return false;
	}

}
