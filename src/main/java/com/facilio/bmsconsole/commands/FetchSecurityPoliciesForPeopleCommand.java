package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchSecurityPoliciesForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PeopleContext> pplList = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if(CollectionUtils.isEmpty(pplList)) {
            pplList = new ArrayList<>();
            PeopleContext people = (PeopleContext) context.get(FacilioConstants.ContextNames.RECORD);
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

            for(PeopleContext ppl : pplList) {
                Map<String, Long> securityPolForPeople = new HashMap<>();
                List<Long> ouIds = PeopleAPI.getUserIdForPeople(ppl.getId());
                if(CollectionUtils.isNotEmpty(ouIds)) {
                    for (long ouId: ouIds) {
                        List<Map<String, Object>> orgUserApps = AccountUtil.getOrgBean(AccountUtil.getCurrentOrg().getOrgId()).getOrgUserApps(ouId);
                        User user = AccountUtil.getUserBean(AccountUtil.getCurrentOrg().getOrgId()).getUser(ouId, true);
                        if (user != null) {
                            for (Map<String, Object> orgUserApp: orgUserApps) {
                                long applicationId = (long) orgUserApp.get("applicationId");
                                if (user.getSecurityPolicyId() > 0) {
                                    securityPolForPeople.put(appLinkNames.get(applicationId), user.getSecurityPolicyId());
                                }
                            }
                        }
                    }
                }
                ppl.setSecurityPolicyMap(securityPolForPeople);
            }
        }
        return false;
    }
}
