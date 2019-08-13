package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.timeseries.TimeSeriesAPI;

public class PublishIOTMessageControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		for(ControlActionCommandContext command :commands) {
			
			if(command.getControlActionMode() == ReadingDataMeta.ControlActionMode.SANDBOX.getValue()) {
				continue;
			}
			Map<String, Object> instance = TimeSeriesAPI.getMappedInstance(command.getResource().getId(),command.getFieldId());
			
			if (instance != null && AccountUtil.getCurrentOrg()!= null) {
				
				instance.put("value", command.getValue());
				instance.put("fieldId", command.getFieldId());
				PublishData data = IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IotCommandType.SET);
//				setResult("data", data);		// handle response here
				
			}
		}
		return false;
	}

}
