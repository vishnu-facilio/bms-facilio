package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.command.FacilioCommand;

import com.facilio.constants.FacilioConstants;

import com.facilio.db.builder.GenericDeleteRecordBuilder;

import com.facilio.db.criteria.CriteriaAPI;

import com.facilio.db.criteria.operators.NumberOperators;

import com.facilio.modules.ModuleFactory;

import org.apache.commons.chain.Context;

import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.lang3.StringUtils;


import java.util.List;

import java.util.logging.Logger;


public class DeleteLicensingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> licensingInfoIds = (List<Long>) context.get(FacilioConstants.ContextNames.LICENSING_INFO_IDS);
        if(CollectionUtils.isNotEmpty(licensingInfoIds)){
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getLicensingInfoModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(licensingInfoIds, ","), NumberOperators.EQUALS));
            int res = builder.delete();
            context.put("licensingInfo",res);
        }
        return false;
    }
}