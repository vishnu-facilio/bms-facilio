package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.UniqueCheckUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class EmailFromAddressValidateCommand extends FacilioCommand {
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailFromAddress> emailFromAddreses = Constants.getRecordList((FacilioContext) context);
		
		for(EmailFromAddress emailFromAddress : emailFromAddreses) {
			
			if(emailFromAddress.getEmail() != null && !VALID_EMAIL_ADDRESS_REGEX.matcher(emailFromAddress.getEmail()).find()) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "Not a valid email - "+ emailFromAddress.getEmail());
			}

			if(emailFromAddress.getSourceType().equals(EmailFromAddress.SourceType.SUPPORT.getIndex())
					&& emailFromAddress.getSiteId() <= 0) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "Site is mandatory");
			}
			if(!UniqueCheckUtil.checkIfUnique(emailFromAddress.getEmail(), FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME, "email")) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email "+emailFromAddress.getEmail()+" already exist");
			}
		}
		
		return false;
	}

}
