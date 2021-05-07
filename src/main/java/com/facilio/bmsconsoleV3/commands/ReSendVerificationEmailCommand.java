package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class ReSendVerificationEmailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EmailFromAddress fromAddress = (EmailFromAddress) context.get(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
		
		if(fromAddress != null && fromAddress.getEmail() != null) {
			
			if(!FacilioProperties.isProduction()) {
				return false;
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Map<String, Boolean> verificationStatus = AwsUtil.getVerificationMailStatus(Collections.singletonList(fromAddress.getEmail()));
			
			if(verificationStatus.get(fromAddress.getEmail())) {
				
				fromAddress.setVerificationStatus(Boolean.TRUE);
				
				V3RecordAPI.updateRecord(fromAddress, modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME), modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));
			}
			else {
				AwsUtil.sendVerificationMailForFromAddressConfig(fromAddress.getEmail());
			}
		}
		else {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "From Address object cannot be empty");
		}
		return false;
	}

}
