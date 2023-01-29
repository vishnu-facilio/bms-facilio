package com.facilio.bmsconsoleV3.commands.userScoping;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j
public class GetUserScopingListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {
            UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
            Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
            String searchQuery = (String) context.get(FacilioConstants.ContextNames.SEARCH_QUERY);
            int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
            int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
            Long count = userScopeBean.getUserScopingCount(appId,searchQuery);
            List<ScopingContext> userScopingList = userScopeBean.getUserScopingList(appId, searchQuery, page, perPage);
            List<Long> ouids = userScopingList.stream().map(userScoping -> {
                Long userId = userScoping.getCreatedBy();
                return userId;
            }).collect(Collectors.toList());
            Set<Long> uniqueIds = new HashSet<>(ouids);
            ouids.clear();
            ouids.addAll(uniqueIds);
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            List<User> createdUsersList = userBean.getUsers(ouids, false);
            context.put(FacilioConstants.ContextNames.CREATED_BY, createdUsersList);
            context.put(FacilioConstants.ContextNames.USER_SCOPING_LIST, userScopingList);
            context.put(FacilioConstants.ContextNames.COUNT, count);
        } catch (Exception e){
            LOGGER.error("Error occurred while getting User Scoping List " + e.getMessage());
            if(e instanceof InvocationTargetException){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,((InvocationTargetException) e).getTargetException().getMessage());
            }
        }

        return false;

    }
}
