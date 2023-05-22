package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsoleV3.commands.peoplegroup.PeopleGroupUtils;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class PeopleAfterSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<V3PeopleContext> pplList = Constants.getRecordList((FacilioContext) context);
        for (V3PeopleContext ppl : pplList) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                List<Long> permissionSetIds = ppl.getPermissionSets();
                permissionSetBean.updateUserPermissionSets(ppl.getId(), permissionSetIds);
            }
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                Long scopingId = ppl.getScopingId();
                userScopeBean.updatePeopleScoping(ppl.getId(),scopingId);
            }
        }
        return false;
    }
}
