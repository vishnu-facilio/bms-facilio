package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchScopingForPeopleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        boolean permissionSetLicenseEnabled = false;
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            permissionSetLicenseEnabled = true;
        }

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> pplList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(pplList)) {
            List<ApplicationContext> apps = ApplicationApi.getAllApplications();
            Map<Long, String> appLinkNames= new HashMap<>();
            if(CollectionUtils.isNotEmpty(apps)) {
                for(ApplicationContext app : apps) {
                    appLinkNames.put(app.getId(), app.getLinkName());
                }
            }
            for(V3PeopleContext ppl : pplList) {
                if(permissionSetLicenseEnabled) {
                    List<PermissionSetContext> permissionSetList = permissionSetBean.getUserPermissionSetMapping(ppl.getId(),false);
                    if(CollectionUtils.isNotEmpty(permissionSetList)) {
                        List<Long> permissionSetIds = permissionSetList.stream().map(PermissionSetContext::getId).collect(Collectors.toList());
                        ppl.setPermissionSets(permissionSetIds);
                    }
                }
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = userScopeBean.getPeopleScoping(ppl.getId());
                    ppl.setScopingId(scopingId);
                } else {
                    Map<String, Long> scopingAppForPeople = new HashMap<>();
                    List<Long> ouIds = PeopleAPI.getUserIdForPeople(ppl.getId());
                    if (CollectionUtils.isNotEmpty(ouIds)) {
                        for (Long ouId : ouIds) {
                            List<OrgUserApp> scopingsForUsers = ApplicationApi.getScopingsForUser(ouId, null);
                            if (CollectionUtils.isNotEmpty(scopingsForUsers)) {
                                for (OrgUserApp userScopingApp : scopingsForUsers) {
                                    scopingAppForPeople.put(appLinkNames.get(userScopingApp.getApplicationId()), userScopingApp.getScopingId());
                                }
                            }
                        }
                        ppl.setScopingsMap(scopingAppForPeople);
                    }
                }
            }
        }
        return false;
    }
}
