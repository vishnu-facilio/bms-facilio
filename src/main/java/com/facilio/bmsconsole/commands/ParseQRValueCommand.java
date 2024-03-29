package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;

public class ParseQRValueCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String qrValue = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		String overrideQR = CommonCommandUtil.getOrgInfo("overrideQR").get("overrideQR");
		boolean override = StringUtils.isEmpty(overrideQR) ? false : Boolean.parseBoolean(overrideQR);
		if (qrValue != null && !qrValue.isEmpty() && !override) {
			if (qrValue.startsWith("facilio")) {
				String[] params = qrValue.split("_");
				if (params != null && params.length >= 3) {
					context.put(FacilioConstants.ContextNames.ID, params[2]);
				}
				else {
					throw new IllegalArgumentException("Malformed QR String generated by Facilio");
				}
			}
		}
		return false;
	}

}
