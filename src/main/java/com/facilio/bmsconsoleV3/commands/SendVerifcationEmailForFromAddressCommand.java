package com.facilio.bmsconsoleV3.commands;


import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;

public class SendVerifcationEmailForFromAddressCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		List<EmailFromAddress> emailFromAddreses = Constants.getRecordList((FacilioContext) context);
		
		for(EmailFromAddress emailFromAddress : emailFromAddreses) {
			
			if(!emailFromAddress.isVerificationStatus()) {
				if(FacilioProperties.isProduction()) {
					AwsUtil.sendVerificationMailForFromAddressConfig(emailFromAddress.getEmail());
				}
			}
		}
		
		return false;
	}

}
