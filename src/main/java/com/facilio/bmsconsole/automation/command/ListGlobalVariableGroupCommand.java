package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ListGlobalVariableGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<GlobalVariableGroupContext> list = GlobalVariableUtil.getAllGlobalVariableGroups();

        if (CollectionUtils.isNotEmpty(list)) {
            Set<Long> userIds = new HashSet<>();
            for (GlobalVariableGroupContext group : list) {
                userIds.add(group.getCreatedBy());
                userIds.add(group.getModifiedBy());
            }
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);
            for (GlobalVariableGroupContext group : list) {
                group.setCreatedByUser(usersMap.get(group.getCreatedBy()));
                group.setModifiedByUser(usersMap.get(group.getModifiedBy()));
            }
        }

        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP_LIST, list);
        return false;
    }
}
