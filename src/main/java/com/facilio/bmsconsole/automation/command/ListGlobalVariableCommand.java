package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListGlobalVariableCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long groupId = (Long) context.get(FacilioConstants.ContextNames.GROUP_ID);
        List<GlobalVariableContext> list = GlobalVariableUtil.getAllGlobalVariables(groupId);

        if (CollectionUtils.isNotEmpty(list)) {
            Set<Long> userIds = new HashSet<>();
            for (GlobalVariableContext variable : list) {
                userIds.add(variable.getCreatedBy());
                userIds.add(variable.getModifiedBy());
            }
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);
            for (GlobalVariableContext variable : list) {
                variable.setCreatedByUser(usersMap.get(variable.getCreatedBy()));
                variable.setModifiedByUser(usersMap.get(variable.getModifiedBy()));
            }
        }

        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE_LIST, list);
        return false;
    }
}
