package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchRolesForPeopleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> pplList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(pplList)) {
            List<ApplicationContext> apps = ApplicationApi.getAllApplications(true);
            Map<Long, String> appLinkNames= new HashMap<>();
            if(CollectionUtils.isNotEmpty(apps)) {
                for(ApplicationContext app : apps) {
                    appLinkNames.put(app.getId(), app.getLinkName());
                }
            }
            RoleBean roleBean = AccountUtil.getRoleBean();
            for(V3PeopleContext ppl : pplList) {
                Map<String, Long> roleAppForPeople = new HashMap<>();
                List<Long> ouIds = PeopleAPI.getUserIdForPeople(ppl.getId());
                if(CollectionUtils.isNotEmpty(ouIds)) {
                    for(Long ouId : ouIds) {
                        List<OrgUserApp> rolesApps = roleBean.getRolesAppsMappingForUser(ouId);
                        if (CollectionUtils.isNotEmpty(rolesApps)) {
                            for (OrgUserApp userApp : rolesApps) {
                                roleAppForPeople.put(appLinkNames.get(userApp.getApplicationId()), userApp.getRoleId());
                            }
                        }
                    }
                    ppl.setRolesMap(roleAppForPeople);
                }
            }
        }

        return false;
    }
}
