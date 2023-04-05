package com.facilio.permission.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.GlobalScopeBean;
import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.PermissionSetContext;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FetchPermissionSetsListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        List<PermissionSetContext> permissionSetList = permissionSetBean.getPermissionSetsList(page,perPage,search,false);
        List<Long> ouids = permissionSetList.stream().map(PermissionSetContext::getSysCreatedBy).collect(Collectors.toList());
        Set<Long> uniqueIds = new HashSet<>(ouids);
        ouids = new ArrayList<>(uniqueIds);
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        List<User> createdUsersList = userBean.getUsers(ouids, false);
        context.put(FacilioConstants.ContextNames.CREATED_BY, createdUsersList);
        context.put(FacilioConstants.ContextNames.COUNT,permissionSetBean.getPermissionSetsCount(search));
        context.put(PermissionSetConstants.PERMISSION_SET,permissionSetList);
        return false;
    }
}
