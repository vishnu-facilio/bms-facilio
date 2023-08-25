package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.identity.client.dto.AppDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateScopingAndPermissionCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> pplList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(pplList)) {
            for(V3PeopleContext ppl : pplList) {
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                    Long scopingId = ppl.getScopingId();
                    userScopeBean.updatePeopleScoping(ppl.getId(),scopingId);
                }else{
                    Map<String,Long> rolemaps = ppl.getRolesMap();
                    if(rolemaps != null && !rolemaps.isEmpty()){
                        for (Map.Entry<String,Long> entryset : rolemaps.entrySet()){
                            String appLinkName = entryset.getKey();
                            Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                            Long orgId = AccountUtil.getCurrentOrg().getOrgId();
                            AppDomain appDomainObj;
                            if(appId != null && appId > 0){
                                appDomainObj = ApplicationApi.getAppDomainForApp(appId);
                                if(appDomainObj != null){
                                    User user = IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, ppl.getEmail(), appDomainObj.getIdentifier());
                                    long orgUserId = getOrgUserIdForIamOrgUserID(user.getOuid());
                                    ScopingContext defaultScoping = ApplicationApi.getDefaultScopingForApp(appId);
                                    if(defaultScoping != null && orgUserId > 0){
                                        ApplicationApi.updateScopingForUser(defaultScoping.getId(), appId,orgUserId);
                                    }
                                }
                            }
                        }
                    }
                }
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                    List<Long> permissionSetIds = ppl.getPermissionSets();
                    if(permissionSetIds != null) {
                        permissionSetBean.updateUserPermissionSets(ppl.getId(), permissionSetIds);
                    }
                }
            }
        }
        return false;
    }

    private static Long getOrgUserIdForIamOrgUserID(long iamOrgUserId) throws Exception{
        if(iamOrgUserId > 0){
            FacilioModule moduleName = ModuleFactory.getOrgUserModule();
            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getField("ouid","ORG_USERID",moduleName, FieldType.NUMBER));

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(moduleName.getTableName())
                    .andCondition(CriteriaAPI.getCondition("IAM_ORG_USERID","iamOrgUserId",iamOrgUserId+"", NumberOperators.EQUALS));

            List<Map<String,Object>> props = selectRecordBuilder.get();
            if(CollectionUtils.isNotEmpty(props)){
                return (Long) props.get(0).get("ouid");
            }
        }
        return null;
    }
}
