package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class AddWMSMessageForVirtualMeterPopulationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		

	    Long templateId = (Long) context.get("vmTemplateId");
		List<Long> resourceIds = (List<Long>) context.get("resourceIds");
		
		JSONObject json= new JSONObject();
		
		json.put(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ID, templateId);
		json.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceIds);
		json.put("methodName", "populateVMData");
		
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(LongRunningTaskHandler.KEY+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(AccountUtil.getCurrentOrg().getId())
                .setContent(json));
	    	
		return false;
	}

}
