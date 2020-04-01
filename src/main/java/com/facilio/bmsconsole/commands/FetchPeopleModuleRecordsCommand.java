package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FetchPeopleModuleRecordsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayListMultimap<String, Long> recordList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        for (String key :recordList.keySet()) {
            if (key.equals(FacilioConstants.ContextNames.TENANT_CONTACT)) {
                List<Long> tenantIdsList = recordList.get(key);
                if (CollectionUtils.isNotEmpty(tenantIdsList)) {
                    List<TenantContactContext> records = PeopleAPI.getTenantContactsList(tenantIdsList);
                    context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
                }
            }
        }
        return false;
    }
}
