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
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BackgroundActivityTopicHandler extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BackgroundActivityTopicHandler.class.getName());

    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public void processOutgoingMessage(Message message) {
        BackgroundActivity backgroundActivity = new BackgroundActivity();
        try {
            JSONObject content = message.getContent();
            backgroundActivity = FieldUtil.getAsBeanFromJson(content, BackgroundActivity.class);
            BackgroundActivity existingBackgroundActivity = BackgroundActivityAPI.getBackgroundActivity(backgroundActivity.getId());
            if(existingBackgroundActivity == null) {
                LOGGER.error("Background activity log not found for id: " + backgroundActivity.getId());
                return;
            }
            if(isChildActivityExists(backgroundActivity.getId())) {
                LOGGER.info("Cannot update parent activity if child activity exists for id: " + backgroundActivity.getId());
                return;
            }
            backgroundActivity.setSystemStatus(BackgroundActivityAPI.getStatusForPercentage(existingBackgroundActivity.getRecordType(),backgroundActivity.getPercentage()));
            if(backgroundActivity.getPercentage() == null || backgroundActivity.getPercentage() == 0) {
                backgroundActivity.setPercentage(1);
            }
            else if(backgroundActivity.getPercentage() < 0) {
                backgroundActivity.setPercentage(-2);
            }
            BackgroundActivityAPI.updateBackgroundActivity(backgroundActivity);
            if(existingBackgroundActivity != null && existingBackgroundActivity.getParentActivity() != null) {
                BackgroundActivityAPI.rollupActivity(existingBackgroundActivity.getParentActivity());
            }
        } catch (Exception e) {
            LOGGER.error("Error in inserting background activity log", e);
            if (backgroundActivity != null) {
                LOGGER.info("Background activity log: " + backgroundActivity.toString());
            }
        }
    }

    private static boolean isChildActivityExists(Long activityId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(Constants.BACKGROUND_ACTIVITY_MODULE));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("parentActivity"),String.valueOf(activityId), NumberOperators.EQUALS));

        FacilioField aggregateField = FieldFactory.getIdField(modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE));
        List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(Constants.BACKGROUND_ACTIVITY_MODULE, null, BackgroundActivity.class,criteria, BmsAggregateOperators.CommonAggregateOperator.COUNT, aggregateField, null);
        if(props != null) {
            Long count = (Long) props.get(0).get(aggregateField.getName());
            if(count != null && count > 0) {
                return true;
            }
        }
        return false;
    }
}