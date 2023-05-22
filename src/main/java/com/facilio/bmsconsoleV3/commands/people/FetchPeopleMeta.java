package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class FetchPeopleMeta extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3PeopleContext> peoples = Constants.getRecordList((FacilioContext) context);
        long currentUserPeopleId = -1,superAdminPeopleId = -1;
        User currentUser = AccountUtil.getCurrentUser();
        if(currentUser != null && currentUser.getPeopleId() > 0){
            currentUserPeopleId = currentUser.getPeopleId();
        }
        Organization org = AccountUtil.getCurrentOrg();
        if(org != null){
            User SuperAdmin = AccountUtil.getOrgBean().getSuperAdmin(org.getOrgId());
            if(SuperAdmin != null && SuperAdmin.getPeopleId() > 0){
                superAdminPeopleId = SuperAdmin.getPeopleId();
            }
        }
        if(CollectionUtils.isNotEmpty(peoples)){
            for (V3PeopleContext people : peoples) {
                if (currentUserPeopleId > 0 && people.getId() == currentUserPeopleId) {
                    people.setCurrentUser(true);
                }
                if (superAdminPeopleId > 0 && people.getId() == superAdminPeopleId) {
                    people.setSuperAdmin(true);
                }
            }
        }
        return false;
    }
}
