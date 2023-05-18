package com.facilio.backgroundactivity.handlers;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BackgroundActivityRollupTopicHandler extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BackgroundActivityRollupTopicHandler.class.getName());

    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public void processOutgoingMessage(Message message) {
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
                updateRecordBuilder.update(updateActivity);
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
        String topic = message.getTopic();
        String[] split = StringUtils.split(topic, "/");
        long activityId = Long.parseLong(split[1]);
        return activityId;
    }
}
