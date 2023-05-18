package com.facilio.backgroundactivity.commands;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FetchParentBackgroundActivitiesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long activityId = (Long) context.get(Constants.ACTIVITY_ID);
        List<BackgroundActivity> backgroundActivityList = new ArrayList<>();
        Map<Long,BackgroundActivity> parentActivityMap = new LinkedHashMap<>();
        if(activityId != null) {
            BackgroundActivity activity = BackgroundActivityAPI.getBackgroundActivity(activityId);
            if(activity != null) {
                backgroundActivityList.add(activity);
                while(activity != null && activity.getParentActivity() != null) {
                    activity = BackgroundActivityAPI.getBackgroundActivity(activity.getParentActivity());
                    backgroundActivityList.add(activity);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(backgroundActivityList)) {
            Collections.reverse(backgroundActivityList);
            long sequence = 0;
            for(BackgroundActivity activity : backgroundActivityList) {
                sequence++;
                parentActivityMap.put(sequence,activity);
            }
        }
        context.put(Constants.PARENT_ACTIVITY_LIST,parentActivityMap);
        return false;
    }
}
