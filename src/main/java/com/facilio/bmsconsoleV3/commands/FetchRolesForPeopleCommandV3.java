package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchRolesForPeopleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3PeopleContext> pplList = (List<V3PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if(CollectionUtils.isEmpty(pplList)) {
            pplList = new ArrayList<>();
            V3PeopleContext people = (V3PeopleContext) context.get(FacilioConstants.ContextNames.RECORD);
            if(people != null) {
                pplList.add(people);
            }
        }
        if(CollectionUtils.isNotEmpty(pplList)) {
            List<ApplicationContext> apps = ApplicationApi.getAllApplications();
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
