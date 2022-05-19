package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.accounts.util.AccountConstants;

import com.facilio.bmsconsoleV3.util.LicensingInfoUtil;
import com.facilio.command.FacilioCommand;

import com.facilio.db.builder.GenericInsertRecordBuilder;

import com.facilio.db.builder.GenericSelectRecordBuilder;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import org.apache.commons.chain.Context;

import org.apache.commons.collections4.CollectionUtils;


import java.util.List;

import java.util.Map;

import java.util.logging.Logger;
import com.facilio.bmsconsoleV3.context.licensinginfo.LicenseInfoContext;


public class AddLicensingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        LicenseInfoContext licenseObj = (LicenseInfoContext) context.get("licensingInfo");
        String licenseTypeEnum = String.valueOf(licenseObj.getLicensingTypeEnum());
        List<Map<String, Object>> props = getSelectBuilderData(licenseObj.getName(),licenseTypeEnum);

        if(props.size() > 0){
            throw new IllegalArgumentException("License already exists");
        }
        else{
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getLicensingInfoModule().getTableName())
                    .fields(AccountConstants.getLicensingInfoFields());
            insertBuilder.addRecord(FieldUtil.getAsProperties(licenseObj));
            insertBuilder.save();
            props = getSelectBuilderData(licenseObj.getName(),licenseTypeEnum);
            context.put("LicenseId",props.get(0).get("id"));
        }
        return false;
    }
    public static List<Map<String, Object>> getSelectBuilderData(String name,String type) throws Exception {
        GenericSelectRecordBuilder selectBuilder = LicensingInfoUtil.getSelectBuilder(name,type);
         return selectBuilder.get();
    }
}