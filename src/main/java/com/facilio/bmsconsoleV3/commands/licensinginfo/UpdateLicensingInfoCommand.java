package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.accounts.util.AccountConstants;

import com.facilio.bmsconsoleV3.context.licensinginfo.LicenseInfoContext;
import com.facilio.command.FacilioCommand;

import com.facilio.db.builder.GenericUpdateRecordBuilder;

import com.facilio.db.criteria.CriteriaAPI;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import org.apache.commons.chain.Context;


import java.util.Map;


public class UpdateLicensingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LicenseInfoContext licenseObj = (LicenseInfoContext) context.get("licensingInfo");
        if(licenseObj.getId() > 0){
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getLicensingInfoModule().getTableName())
                    .fields(AccountConstants.getLicensingInfoFields())
                    .andCondition(CriteriaAPI.getIdCondition(licenseObj.getId(), ModuleFactory.getLicensingInfoModule()));
            int res = builder.update(FieldUtil.getAsProperties(licenseObj));
            if(res > 0) {
                context.put("licensingInfo",licenseObj.getId());
            }
            else{
                context.put("licensingInfo",0);
            }
        }
        return false;
    }
}
