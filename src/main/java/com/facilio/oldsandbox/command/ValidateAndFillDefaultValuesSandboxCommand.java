package com.facilio.oldsandbox.command;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.oldsandbox.context.SandboxContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class ValidateAndFillDefaultValuesSandboxCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		SandboxContext sandbox = (SandboxContext) context.get(BundleConstants.Sandbox.SANDBOX);
		
		V3Util.throwRestException(sandbox.getName() == null, ErrorCode.VALIDATION_ERROR, "Sandbox name cannot be null");
		V3Util.throwRestException(sandbox.getDomain() == null, ErrorCode.VALIDATION_ERROR, "Sandbox domain cannot be null");
		V3Util.throwRestException(CollectionUtils.isEmpty(sandbox.getSharing()), ErrorCode.VALIDATION_ERROR, "Sandbox sharing cannot be empty");
		
		fillDefaultValues(sandbox);
		
		return false;
	}

	private void fillDefaultValues(SandboxContext sandbox) {
		
		if(sandbox.getId() < 0) {
			sandbox.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
			sandbox.setCreatedTime(DateTimeUtil.getCurrenTime());
			sandbox.setModifiedTime(sandbox.getCreatedTime());
			if(sandbox.getStatusEnum() == null) {
				sandbox.setStatusEnum(SandboxContext.SandboxStatus.ACTIVE);
			}
		}
		else {
			sandbox.setModifiedTime(DateTimeUtil.getCurrenTime());
		}
		
		sandbox.getSharing().stream().forEach((sharingContext) -> { if (sharingContext.getId() < 0 && sharingContext.getSharedBy() < 0) {sharingContext.setSharedBy(AccountUtil.getCurrentUser().getOuid());}});
	}

}
