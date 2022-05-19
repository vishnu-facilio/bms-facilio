package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.bmsconsoleV3.util.LicensingInfoUtil;

import com.facilio.command.FacilioCommand;

import com.facilio.constants.FacilioConstants;

import org.apache.commons.chain.Context;


public class UpdateOnDeletionLicenseInfoCommand extends FacilioCommand {

    @Override

    public boolean executeCommand(Context context) throws Exception {

        String name = (String) context.get(FacilioConstants.ContextNames.LICENSE_NAME);

        String type = (String) context.get(FacilioConstants.ContextNames.LICENSE_TYPE);

        LicensingInfoUtil.updateActualCountOnDeletion(name,type);

        return false;

    }

}