package com.facilio.bmsconsole.commands;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchUserScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<User> users = new ArrayList<>();
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        if(CollectionUtils.isNotEmpty((List<User>)context.get(FacilioConstants.ContextNames.USERS))) {
            users = (List<User>)context.get(FacilioConstants.ContextNames.USERS);
        }
        if((User) context.get(FacilioConstants.ContextNames.USER) != null) {
            users.add((User) context.get(FacilioConstants.ContextNames.USER));
        }
        for(User user : users){
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
                Long scopingId = userScopeBean.getPeopleScoping(user.getPeopleId());
                if(scopingId != null &&  scopingId > -1) {
                    ScopingContext userScoping = ApplicationApi.getScoping(scopingId);
                    if (userScoping != null) {
                        user.setScoping(userScoping);
                        user.setScopingId(userScoping.getId());
                    }
                }
            } else {
                List<OrgUserApp> userApps = ApplicationApi.getScopingsForUser(user.getOuid(), user.getApplicationId());
                if (CollectionUtils.isNotEmpty(userApps)) {
                    OrgUserApp userApp = userApps.get(0);
                    if (userApp != null && userApp.getScopingId() > -1) {
                        ScopingContext userScoping = ApplicationApi.getScoping(userApps.get(0).getScopingId());
                        if (userScoping != null) {
                            user.setScoping(userScoping);
                            user.setScopingId(userScoping.getId());
                        }
                    }
                }
            }
        }
        return false;
    }
}
