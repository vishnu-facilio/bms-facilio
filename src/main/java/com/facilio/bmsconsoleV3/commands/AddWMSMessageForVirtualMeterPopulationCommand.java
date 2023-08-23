package com.facilio.bmsconsoleV3.commands;

import java.time.ZoneId;
import java.util.List;

import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;

public class AddWMSMessageForVirtualMeterPopulationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		

	    Long templateId = (Long) context.get("vmTemplateId");
		
		JSONObject json= new JSONObject();
		
		json.put(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ID, templateId);
		json.put("methodName", "populateVMData");
		
        WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                .setTopic(Topics.Tasks.longRunnningTasks+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(AccountUtil.getCurrentOrg().getId())
                .setContent(json));
	    	
		return false;
	}

}
