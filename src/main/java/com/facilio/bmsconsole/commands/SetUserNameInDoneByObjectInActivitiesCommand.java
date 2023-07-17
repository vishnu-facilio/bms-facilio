package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.activity.ActivityContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class SetUserNameInDoneByObjectInActivitiesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ActivityContext> activities =  (List<ActivityContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isEmpty(activities)){
            return false;
        }
        Set<Long> userIds = new HashSet<>();
        for(ActivityContext activity : activities){
            if(activity==null){
                continue;
            }
            User user = activity.getDoneBy();
            if(user == null){
                continue;
            }
            userIds.add(user.getId());
        }

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);


        for (ActivityContext activity : activities) {
            if(activity==null){
                continue;
            }
            User doneBy = activity.getDoneBy();
            if(doneBy == null){
                continue;
            }
            User dpUser = usersMap.get(doneBy.getId());
            if(dpUser == null){
                continue;
            }
            doneBy.setName(dpUser.getName());
        }

        return false;
    }
}
