package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.constants.FacilioConstants;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateEmailSettingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EmailSettingContext emailSetting = (EmailSettingContext) context.get(FacilioConstants.ContextNames.EMAIL_SETTING);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		System.out.println(mapper.convertValue(emailSetting, Map.class));
		
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
