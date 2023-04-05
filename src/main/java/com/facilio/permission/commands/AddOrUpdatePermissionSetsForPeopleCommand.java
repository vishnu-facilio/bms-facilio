package com.facilio.permission.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddOrUpdatePermissionSetsForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            try {
                User user = (User) context.get(FacilioConstants.ContextNames.USER);
                if (user != null && user.getPeopleId() > -1) {
                    PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
                    List<Long> permissionSetIds = user.getPermissionSets();
                    permissionSetBean.updateUserPermissionSets(user.getPeopleId(), permissionSetIds);
                }
            } catch (Exception e) {
                if(e instanceof InvocationTargetException) {
                    throw new IllegalArgumentException(((InvocationTargetException) e).getTargetException().getMessage());
                }
            }
        }
        return false;
    }
}