package com.facilio.backgroundactivity.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;


@Log4j
public class AddBackgroundActivityCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(Constants.RECORD_ID);
        String recordType = (String) context.get(Constants.RECORD_TYPE);
        String name = (String) context.get(Constants.NAME);
        String status = (String) context.get(Constants.STATUS);
        String message = (String) context.get(Constants.MESSAGE);
        Long initiatedBy = (Long) context.get(Constants.INITIATED_BY);
        Long parentActivityId = (Long) context.get(Constants.PARENT_ACTIVITY_ID);

        Long activityId = null;
        try {
            if (recordId == null || recordType == null) {
                LOGGER.error("Record Id or Record Type is null");
            }
            if (initiatedBy == null && AccountUtil.getCurrentUser() != null) {
                initiatedBy = AccountUtil.getCurrentUser().getPeopleId();
            }
            Long currentTimeInMillis = DateTimeUtil.getCurrenTime(false);
            BackgroundActivity backgroundActivity = new BackgroundActivity();
            backgroundActivity.setName(name);
            backgroundActivity.setRecordType(recordType);
            backgroundActivity.setRecordId(recordId);
            backgroundActivity.setParentActivity(null);
            backgroundActivity.setMessage(message);
            backgroundActivity.setInitiatedBy(initiatedBy);
            backgroundActivity.setPercentage(BackgroundActivityAPI.INITIAL_PERCENTAGE);
            backgroundActivity.setSystemStatus(BackgroundActivityAPI.getStatusForPercentage(backgroundActivity.getRecordType(),backgroundActivity.getPercentage()));
            backgroundActivity.setStartTime(currentTimeInMillis);
            backgroundActivity.setCompletedTime(null);
            backgroundActivity.setParentActivity(parentActivityId);
            activityId = BackgroundActivityAPI.insertBackgroundActivity(backgroundActivity);
            context.put(Constants.ACTIVITY_ID,activityId);
        } catch (Exception e) {
            LOGGER.error("Background Activity addActivity => ", e);
        }
        return false;
    }
}
