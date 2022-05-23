package com.facilio.bmsconsoleV3.commands.licensinginfo;


import com.facilio.accounts.util.AccountConstants;

import com.facilio.command.FacilioCommand;

import com.facilio.constants.FacilioConstants;

import com.facilio.db.builder.GenericSelectRecordBuilder;

import com.facilio.db.criteria.CriteriaAPI;

import com.facilio.db.criteria.operators.NumberOperators;

import com.facilio.modules.ModuleFactory;

import org.apache.commons.chain.Context;

import org.apache.commons.collections4.CollectionUtils;


import java.util.List;

import java.util.Map;

import java.util.logging.Logger;


public class FetchLicensingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
            selectBuilder
                    .table(ModuleFactory.getLicensingInfoModule().getTableName())
                    .select(AccountConstants.getLicensingInfoFields());

            List<Map<String, Object>> props = selectBuilder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                context.put(FacilioConstants.ContextNames.LICENSING_INFO, props);
            }
        return false;
    }
}