package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetScopedTeamAndUsersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject resultObject = new JSONObject();
        Long siteId = (Long) context.getOrDefault(FacilioConstants.ContextNames.SITE_ID,-1L);
        boolean fetchAccessibleSpaces = (boolean)  context.getOrDefault(FacilioConstants.ContextNames.ACCESSIBLE_SPACE,false);
        List<Long> siteIds = (List<Long>) context.getOrDefault(FacilioConstants.ContextNames.SITE_IDS, new ArrayList<>());
        if(siteId > 0){
            siteIds = Collections.singletonList(siteId);
        }
        List<Long> appIds = new ArrayList<>();
        ApplicationContext currentApp = AccountUtil.getCurrentApp();
        if(currentApp != null && currentApp.getAppCategoryEnum() != ApplicationContext.AppCategory.PORTALS){
            appIds.add(currentApp.getId());
        } else {
            List<ApplicationContext> allApps = ApplicationApi.getAllApplicationsForDomain(AppDomain.AppDomainType.FACILIO.getIndex());
            if (CollectionUtils.isNotEmpty(allApps)) {
                appIds = allApps.stream().map(ApplicationContext::getId).collect(Collectors.toList());
            }
        }
        if(CollectionUtils.isNotEmpty(appIds)){
            resultObject.put(FacilioConstants.ContextNames.USERS, PeopleAPI.getScopedUsers(appIds, siteIds,fetchAccessibleSpaces));
            resultObject.put(FacilioConstants.ContextNames.GROUPS, PeopleAPI.getScopedTeams(appIds, siteIds,fetchAccessibleSpaces));
        }
        context.put(FacilioConstants.ContextNames.DATA,resultObject);
        return false;
    }
}
