package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetPortalUsersListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getAppOrgUserFields());
        fields.add(AccountConstants.getRoleIdField());

        String mainAppLinkNames = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP +","+ FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP;

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_Users")
                ;
        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

        fields.add(AccountConstants.getApplicationIdField());
        selectBuilder.innerJoin("ORG_User_Apps")
                .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                .innerJoin("Application")
                .on("Application.ID = ORG_User_Apps.APPLICATION_ID")
        ;
        selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", mainAppLinkNames, StringOperators.ISN_T));


        JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);

       Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);

        if (filterCriteria != null) {
            selectBuilder.andCriteria(filterCriteria);
        }
        if (( filters == null || includeParentCriteria) && view != null) {
            Criteria criteria = view.getCriteria();
            selectBuilder.andCriteria(criteria);
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            List<User> users = new ArrayList<>();
            IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
            for(Map<String, Object> prop : props) {
                User user = UserBeanImpl.createUserFromProps(prop, false, false, null);
                if(status) {
                    if(user.isActive()) {
                        users.add(user);
                    }
                }
                else {
                    users.add(user);
                }
            }
            context.put(FacilioConstants.ContextNames.USERS, users);
        }

        return false;
    }
}
