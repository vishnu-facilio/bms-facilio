package com.facilio.apiv3;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;

public class FetchEmailToModuleDataReccordCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);
		
		for(V3ServiceRequestContext serviceRequest : serviceRequests) {
			
			EmailToModuleDataContext emailToModuleDataRecord = MailMessageUtil.getEmailToModuleContext(serviceRequest.getId(), serviceRequest.getModuleId());
			
			if(emailToModuleDataRecord != null) {
				List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(emailToModuleDataRecord.getId(), MailMessageUtil.EMAIL_TO_MODULE_DATA_ATTACHMENT_MODULE);
				if(attachments != null) {
					emailToModuleDataRecord.setAttachmentsList(FieldUtil.getAsMapList(attachments, AttachmentV3Context.class, false));
				}
				serviceRequest.setDatum("emailToModuleDataRecord", emailToModuleDataRecord);
			}
		}
		
		return false;
	}

}
