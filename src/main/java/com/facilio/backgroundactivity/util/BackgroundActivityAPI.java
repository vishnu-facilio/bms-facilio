package com.facilio.backgroundactivity.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.factory.BackgroundActivityChainFactory;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class BackgroundActivityAPI {

    public static final Integer INITIAL_PERCENTAGE = 0;
    public static final Integer COMPLETED_PERCENTAGE = 100;


    private static void sendActivity(BackgroundActivity backgroundActivity) throws Exception {
        if (backgroundActivity == null) {
            LOGGER.error("Background Activity is null");
            return;
        }
        long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
        if (orgId > 0L) {
            JSONObject json = FieldUtil.getAsJSON(backgroundActivity);
            WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                    .setTopic("__background_activity__/"+ backgroundActivity.getId() +"/process")
                    .setOrgId(orgId)
                    .setContent(json)
            );
        }
    }

    public static Long parentActivityForRecordIdAndType(Long recordId,String recordType) throws Exception {
        if(isActivityLicenseEnabled() && recordId != null && StringUtils.isNotEmpty(recordType)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .moduleName(Constants.BACKGROUND_ACTIVITY_MODULE)
                    .beanClass(BackgroundActivity.class)
                    .select(Collections.singletonList(FieldFactory.getIdField(modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE))))
                    .andCondition(CriteriaAPI.getCondition("RECORD_TYPE", "recordType",recordType, StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId",String.valueOf(recordId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("PARENT_ACTIVITY", "parentActivity",StringUtils.EMPTY, CommonOperators.IS_EMPTY));
            BackgroundActivity activity = (BackgroundActivity) builder.fetchFirst();
            return activity.getId();
        }
        return null;
    }

    public static Long getChildActivity(Long parentActivity, Long recordId,String recordType) throws Exception {
        if(isActivityLicenseEnabled() && recordId != null && StringUtils.isNotEmpty(recordType)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .moduleName(Constants.BACKGROUND_ACTIVITY_MODULE)
                    .beanClass(BackgroundActivity.class)
                    .select(Collections.singletonList(FieldFactory.getIdField(modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE))))
                    .andCondition(CriteriaAPI.getCondition("RECORD_TYPE", "recordType",recordType, StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId",String.valueOf(recordId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("PARENT_ACTIVITY", "parentActivity",String.valueOf(parentActivity), NumberOperators.EQUALS));
            BackgroundActivity activity = (BackgroundActivity) builder.fetchFirst();
            if(activity != null) {
                return activity.getId();
            }
        }
        return null;
    }
    public static void rollupActivity(Long activityId) throws Exception {
        if (activityId == null) {
            LOGGER.error("Background Activity is null");
            return;
        }
        long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
        if (orgId > 0L) {
            WmsBroadcaster.getBroadcaster().sendMessage(new Message()
                    .setTopic("__background_activity_rollup__/"+ activityId +"/rollup")
                    .setOrgId(orgId)
                    .setContent(null)
            );
        }
    }

    private static boolean isValidPercentage(Integer percentage) {
        if (percentage == null) {
            LOGGER.error("Percentage is null");
            return false;
        }
        if (percentage < INITIAL_PERCENTAGE || percentage > COMPLETED_PERCENTAGE) {
            LOGGER.error("Percentage should be between 0 and 100");
            return false;
        }
        return true;
    }

    public static Long insertBackgroundActivity(BackgroundActivity backgroundActivity) throws Exception {
        if (backgroundActivity == null) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE);
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()));
        return builder.insert(backgroundActivity);
    }

    public static void updateBackgroundActivity(BackgroundActivity backgroundActivity) throws Exception {
        if (backgroundActivity == null) {
            LOGGER.error("Background Activity is null");
        }
        if (backgroundActivity.getId() <= 0) {
            LOGGER.error("Background Activity id is null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE);
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder();
        updateRecordBuilder.module(module);
        updateRecordBuilder.fields(modBean.getAllFields(module.getName()));
        updateRecordBuilder.andCondition(CriteriaAPI.getIdCondition(backgroundActivity.getId(), module));
        updateRecordBuilder.updateViaMap(FieldUtil.getAsProperties(backgroundActivity));
    }

    public static String getStatusForPercentage(String activity,Integer percentage) {
        if (percentage == null) {
            LOGGER.error("Percentage is null");
            return null;
        }
        if(activity == null || !BackgroundActivityUtil.isActivityAvailable(activity)) {
            LOGGER.error("Activity is null or not available");
        }
        return BackgroundActivityUtil.getStatus(activity,percentage);
    }
    private static boolean anyNeighbourStarted(Long parentActivityId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(Constants.BACKGROUND_ACTIVITY_MODULE));

        Criteria criteriaNeighbour = new Criteria();
        criteriaNeighbour.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("parentActivity"), String.valueOf(parentActivityId), NumberOperators.EQUALS));
        criteriaNeighbour.addOrCondition(CriteriaAPI.getIdCondition(String.valueOf(parentActivityId),modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE)));

        Criteria criteriaParent = new Criteria();
        criteriaParent.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("percentage"), String.valueOf(BackgroundActivityAPI.INITIAL_PERCENTAGE),NumberOperators.GREATER_THAN));

        Criteria criteria = new Criteria();
        criteria.andCriteria(criteriaNeighbour);
        criteria.andCriteria(criteriaParent);

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

    private static boolean isValidChildActivity(Long parentActivityId) throws Exception {
        if (parentActivityId == null) {
            LOGGER.error("Parent Activity Id is null");
            return false;
        }
        if(anyNeighbourStarted(parentActivityId)) {
            LOGGER.error("Some neighbour activity is already started");
            return false;
        }
        return true;
    }

    public static Long addChildBackgroundActivity(Long recordId,String recordType,String name,String message,Long initiatedBy, Long parentActivityId) throws Exception {
        if(!isValidChildActivity(parentActivityId)) {
            LOGGER.error("Invalid child activity");
            return null;
        }
        return NewTransactionService.newTransactionWithReturn(() -> addBackgroundActivity(recordId,recordType,name,message,initiatedBy,parentActivityId));
    }

    public static Long addBackgroundActivity(Long recordId,String recordType,String name,String message,Long initiatedBy) throws Exception {
        return NewTransactionService.newTransactionWithReturn(() -> addBackgroundActivity(recordId,recordType,name,message,initiatedBy,null));
    }
    private static Long addBackgroundActivity(Long recordId,String recordType,String name,String message,Long initiatedBy, Long parentActivityId) {
        FacilioChain chain = BackgroundActivityChainFactory.addBackgroundActivityChain();
        FacilioContext context = chain.getContext();
        try {
            context.put(Constants.RECORD_ID,recordId);
            context.put(Constants.RECORD_TYPE,recordType);
            context.put(Constants.NAME,name);
            context.put(Constants.INITIATED_BY,initiatedBy);
            context.put(Constants.MESSAGE,message);
            context.put(Constants.PARENT_ACTIVITY_ID,parentActivityId);
            chain.execute();
            return (Long) context.get(Constants.ACTIVITY_ID);
        } catch (Exception e) {
            LOGGER.error("Error while adding background activity in new transaction",e);
            return null;
        }
    }

    public static void updateBackgroundActivity(Long activityId, Integer percentage, String message) throws Exception {
        if(!isValidPercentage(percentage)) {
            LOGGER.error("Invalid percentage");
            return;
        }
        BackgroundActivity backgroundActivity = new BackgroundActivity();
        backgroundActivity.setId(activityId);
        backgroundActivity.setPercentage(percentage);
        backgroundActivity.setMessage(message);
        sendActivity(backgroundActivity);
    }

    public static void completeBackgroundActivity(Long activityId, String message) throws Exception {
        BackgroundActivity backgroundActivity = new BackgroundActivity();
        backgroundActivity.setId(activityId);
        backgroundActivity.setPercentage(COMPLETED_PERCENTAGE);
        backgroundActivity.setMessage(message);
        backgroundActivity.setCompletedTime(System.currentTimeMillis());
        sendActivity(backgroundActivity);
    }

    public static void failBackgroundActivity(Long activityId, String message) throws Exception {
        BackgroundActivity backgroundActivity = new BackgroundActivity();
        backgroundActivity.setId(activityId);
        backgroundActivity.setMessage(message);
        backgroundActivity.setPercentage(-2);
        sendActivity(backgroundActivity);
    }

    public static BackgroundActivity getBackgroundActivity(Long activityId) throws Exception {
        BackgroundActivity backgroundActivity = V3RecordAPI.getRecord(Constants.BACKGROUND_ACTIVITY_MODULE,activityId,BackgroundActivity.class);
        return backgroundActivity;
    }

    public static void updateBackgroundActivityMessage(Long activityId, String message) throws Exception {
        if (activityId == null) {
            LOGGER.error("Background Activity is null");
        }
        if (activityId <= 0) {
            LOGGER.error("Background Activity id is null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(Constants.BACKGROUND_ACTIVITY_MODULE);
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder();
        updateRecordBuilder.module(module);
        updateRecordBuilder.fields(Collections.singletonList(fieldsMap.get("message")));
        updateRecordBuilder.andCondition(CriteriaAPI.getIdCondition(activityId, module));
        Map<String,Object> prop = new HashMap<>();
        prop.put("message",message);
        updateRecordBuilder.updateViaMap(prop);
    }

    public static boolean isActivityLicenseEnabled() {
        try {
            return AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.BACKGROUND_ACTIVITY);
        } catch (Exception e) {
            LOGGER.error("Error while checking background activity license",e);
        }
        return false;
    }
}
