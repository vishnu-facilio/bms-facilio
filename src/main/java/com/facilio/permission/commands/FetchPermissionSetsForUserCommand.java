package com.facilio.permission.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FetchPermissionSetsForUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            List<User> users = new ArrayList<>();
            if (CollectionUtils.isNotEmpty((List<User>) context.get(FacilioConstants.ContextNames.USERS))) {
                users = (List<User>) context.get(FacilioConstants.ContextNames.USERS);
            }
            if ((User) context.get(FacilioConstants.ContextNames.USER) != null) {
                users.add((User) context.get(FacilioConstants.ContextNames.USER));
            }
            for (User user : users) {
                PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
                List<PermissionSetContext> permissionSetList = permissionSetBean.getUserPermissionSetMapping(user.getPeopleId(),false);
                if(CollectionUtils.isNotEmpty(permissionSetList)) {
                    List<Long> permissionSetIds = permissionSetList.stream().map(PermissionSetContext::getId).collect(Collectors.toList());
                    user.setPermissionSets(permissionSetIds);
                }
            }
        }
        return false;
    }
}
