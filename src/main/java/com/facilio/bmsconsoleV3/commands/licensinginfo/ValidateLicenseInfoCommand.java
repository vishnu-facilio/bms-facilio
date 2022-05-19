package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.bmsconsoleV3.util.LicensingInfoUtil;

import com.facilio.command.FacilioCommand;

import com.facilio.constants.FacilioConstants;

import org.apache.commons.chain.Context;


public class ValidateLicenseInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String name = (String) context.get(FacilioConstants.ContextNames.LICENSE_NAME);
        String type = (String) context.get(FacilioConstants.ContextNames.LICENSE_TYPE);
        Long newCount = (Long) context.get(FacilioConstants.ContextNames.LICENSE_NEW_COUNT);
        if(!LicensingInfoUtil.checkForLicense(name,type,newCount)){
            throw new IllegalArgumentException("Cannot perform operation as the count is greater than the permitted count");
        }
        return false;
    }

}