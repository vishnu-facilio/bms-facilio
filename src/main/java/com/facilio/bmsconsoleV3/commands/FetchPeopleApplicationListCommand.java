package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.bean.IAMUserBean;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchPeopleApplicationListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject result = new JSONObject();
        Long pplId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<Map<String, Object>> rolesApps = new ArrayList<>();
        Organization org = AccountUtil.getCurrentOrg();
        if(pplId != null && org != null) {
            List<Long> ouIds = PeopleAPI.getUserIdForPeople(pplId);
            if(CollectionUtils.isNotEmpty(ouIds)) {
                List<FacilioField> selectFields = new ArrayList<>(AccountConstants.getOrgUserAppsFields());
                selectFields.add(FieldFactory.getField("uid","USERID",AccountConstants.getAppOrgUserModule(), FieldType.NUMBER));
                selectFields.add(FieldFactory.getField("appName", "APPLICATION_NAME", ModuleFactory.getApplicationModule(), FieldType.STRING));
                selectFields.add(FieldFactory.getField("description", "DESCRIPTION", ModuleFactory.getApplicationModule(), FieldType.STRING));
                selectFields.add(FieldFactory.getField("roleName", "NAME", AccountConstants.getRoleModule(), FieldType.STRING));
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(selectFields)
                        .table(AccountConstants.getOrgUserAppsModule().getTableName())
                        .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                        .on("Application.ID = ORG_User_Apps.APPLICATION_ID")
                        .innerJoin(AccountConstants.getRoleModule().getTableName())
                        .on("Role.ROLE_ID = ORG_User_Apps.ROLE_ID")
                        .innerJoin(AccountConstants.getAppOrgUserModule().getTableName())
                        .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                        .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "orgUserid", StringUtils.join(ouIds, ','), NumberOperators.EQUALS));

                List<Map<String,Object>> props = selectBuilder.get();
                List<Long> uids = props.stream().map(prop -> (Long) prop.get("uid")).collect(Collectors.toList());
                Map<Long,User> userMap = new HashMap<>();
                for(Long uid : uids){
                    if(uid != null) {
                        User user = IdentityClient.getDefaultInstance().getUserBean().getUser(org.getOrgId(), uid);
                        userMap.put(uid, user);
                    }
                }
                for(Map<String, Object> prop : props){
                    if(prop.get("uid") != null){
                        User user = userMap.get((Long) prop.get("uid"));
                        prop.put("user",user);
                        rolesApps.add(prop);
                    }
                }
            }
        }
        result.put(FacilioConstants.ContextNames.RECORD_LIST,rolesApps);
        context.put(FacilioConstants.ContextNames.DATA,result);
        return false;
    }
}
