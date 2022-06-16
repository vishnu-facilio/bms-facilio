package com.facilio.bmsconsoleV3.commands.clientcontact;

import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

public class DeleteClientContactPeopleUsersCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if (moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) {
            List records = recordMap.get(moduleName);
            if (records != null && !records.isEmpty()) {
                for(int  i=0; i< records.size(); i++){
                    V3ClientContactContext cc = (V3ClientContactContext) records.get(i);
                    V3PeopleAPI.deletePeopleUsers(cc.getId());
                }
            }
        }
        return false;
    }
}