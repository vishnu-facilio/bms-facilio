package com.facilio.backgroundactivity.handlers;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BackgroundActivityRollupTopicHandler extends ImsHandler {

    private static final Logger LOGGER = LogManager.getLogger(BackgroundActivityRollupTopicHandler.class.getName());


    @Override
    public void processMessage(Message message) {
        long activityId = getActivityId(message);
        if(activityId > 0) {
            try {
                rollupCurrentPercentage(activityId);
            } catch (Exception e) {
                LOGGER.error("Error while rolling up current percentage for activity id: " + activityId, e);
            }
        }
    }


    private static void rollupCurrentPercentage(Long activityId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        BackgroundActivity currentActivity = BackgroundActivityAPI.getBackgroundActivity(activityId);
        if(currentActivity != null) {
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(Constants.BACKGROUND_ACTIVITY_MODULE));
            List<BackgroundActivity> childActivities = getChildActivities(activityId);
            if (CollectionUtils.isNotEmpty(childActivities)) {
                Long totalPercentage = 0L;
                Long size = Long.valueOf(childActivities.size());
                Integer parentPercentage = -2;
                boolean childActivityFailed = false;
                Long numberOfChildActivityFailed = 0l;
                for (BackgroundActivity childActivity : childActivities) {
                    if(childActivity.getPercentage() == null || childActivity.getPercentage() == -2) {
                        childActivityFailed = true;
                        totalPercentage = -2l;
                        numberOfChildActivityFailed++;
                    } else if(!childActivityFailed){
                        totalPercentage += childActivity.getPercentage();
                    }
                }
                if (totalPercentage > 0) {
                    parentPercentage = Math.toIntExact(totalPercentage / size);
                }
                FacilioModule module = modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE);
                BackgroundActivity updateActivity = new BackgroundActivity();
                if(numberOfChildActivityFailed > 0) {
                    if(numberOfChildActivityFailed > 1) {
                        updateActivity.setMessage("Child Activities Failed");
                    }
                    else {
                        updateActivity.setMessage("Child Activity Failed");
                    }
                }
                updateActivity.setPercentage(parentPercentage);
                updateActivity.setSystemStatus(BackgroundActivityAPI.getStatusForPercentage(currentActivity.getRecordType(),parentPercentage));
                UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder();
                updateRecordBuilder.module(module);
                List<FacilioField> fields = Arrays.asList(fieldsMap.get("percentage"),fieldsMap.get("message"),fieldsMap.get("systemStatus"));
                updateRecordBuilder.fields(fields);
                updateRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),String.valueOf(activityId), NumberOperators.EQUALS));
                updateActivity.setId(activityId);
                BackgroundActivity liveMessageActivity = FieldUtil.cloneBean(updateActivity,BackgroundActivity.class);
                liveMessageActivity.setName(currentActivity.getName());
                liveMessageActivity.setRecordType(currentActivity.getRecordType());
                liveMessageActivity.setStartTime(currentActivity.getStartTime());
                liveMessageActivity.setCompletedTime(currentActivity.getCompletedTime());
                BackgroundActivityAPI.sendLiveMessage(liveMessageActivity,updateActivity.getMessage());
                updateRecordBuilder.update(liveMessageActivity);
                if(currentActivity != null && currentActivity.getParentActivity() != null) {
                    BackgroundActivityAPI.rollupActivity(currentActivity.getParentActivity());
                }
            }
        }
    }
    private static List<BackgroundActivity> getChildActivities(Long activityId) throws Exception {
        if(activityId != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ACTIVITY","parentActivity",String.valueOf(activityId), NumberOperators.EQUALS));
            List<BackgroundActivity> activities = V3RecordAPI.getRecordsListWithSupplements(Constants.BACKGROUND_ACTIVITY_MODULE, null, BackgroundActivity.class, criteria, null, null,null);
            return activities;
        }
        return null;
    }

    private long getActivityId(Message message) {
        String partitionKey = message.getKey();
        String[] split = StringUtils.split(partitionKey, "/");
        long activityId = Long.parseLong(split[1]);
        return activityId;
    }
}
