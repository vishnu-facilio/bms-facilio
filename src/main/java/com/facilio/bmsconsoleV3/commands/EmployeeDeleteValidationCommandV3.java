package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class EmployeeDeleteValidationCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> empIds = (List<Long>) context.get(Constants.RECORD_ID_LIST);
        for(Long empId : empIds){
            List<Long> ouIds = PeopleAPI.getUserIdForPeople(empId);
            if(CollectionUtils.isNotEmpty(ouIds)) {
                for(Long ouId : ouIds) {
                    List<OrgUserApp> rolesApps = AccountUtil.getRoleBean().getRolesAppsMappingForUser(ouId);
                    if (CollectionUtils.isNotEmpty(rolesApps)) {
                        if(empIds.size() > 1) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Deletion restricted - One or more employees are active app users");
                        }
                        else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Deletion restricted - Employee is an active app user");
                        }
                    }
                }
            }
        }
        return false;
    }
}
