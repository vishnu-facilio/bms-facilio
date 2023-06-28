package com.facilio.backgroundactivity.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.context.BackgroundActivityLiveMessageContext;
import com.facilio.backgroundactivity.factory.BackgroundActivityChainFactory;
import com.facilio.backgroundactivity.factory.Constants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.message.WebMessage;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
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
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey("__background_activity__/"+ backgroundActivity.getId() +"/process")
                    .setOrgId(orgId)
                    .setContent(json)
            );
        }
    }

    public static void refreshActivity(long parentActivity) throws Exception {
        if(isActivityLicenseEnabled()) {
            BackgroundActivityLiveMessageContext liveMessageContext = new BackgroundActivityLiveMessageContext();
            liveMessageContext.setActivityId(parentActivity);
            liveMessageContext.setRefresh(true);
            sendLiveMessage(liveMessageContext);
        }
    }
    public static void sendLiveMessage(BackgroundActivity backgroundActivity, String message) throws Exception {
        if(isActivityLicenseEnabled()) {
            BackgroundActivityLiveMessageContext liveMessageContext = new BackgroundActivityLiveMessageContext();
            liveMessageContext.setActivityId(backgroundActivity.getId());
            liveMessageContext.setMessage(message);
            liveMessageContext.setPercentage(backgroundActivity.getPercentage());
            liveMessageContext.setActivity(backgroundActivity);
            sendLiveMessage(liveMessageContext);
        }
    }
    public static void sendLiveMessage(BackgroundActivityInterface backgroundActivity, String message,Long totalRecords,Long processedRecords) throws Exception {
        if(isActivityLicenseEnabled()) {
            BackgroundActivityLiveMessageContext liveMessageContext = new BackgroundActivityLiveMessageContext();
            liveMessageContext.setActivityId(backgroundActivity.getActivityId());
            liveMessageContext.setMessage(message);
            liveMessageContext.setTotalRecords(totalRecords);
            liveMessageContext.setProcessedRecords(processedRecords);
            liveMessageContext.setPercentage(null);
            liveMessageContext.setActivity(null);
            sendLiveMessage(liveMessageContext);
        }
    }

    //note that live messages won't be stored anywhere
    private static void sendLiveMessage(BackgroundActivityLiveMessageContext liveMessageContext) throws Exception {
        if (liveMessageContext == null) {
            LOGGER.error("Live message object is null");
            return;
        }
        long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
        if (orgId > 0L) {
            BackgroundActivity activity = liveMessageContext.getActivity();
            if(activity == null) {
                activity = BackgroundActivityAPI.getBackgroundActivity(liveMessageContext.getActivityId());
            }
            boolean sendMessage = false;
            if(liveMessageContext.isRefresh()) {
                sendMessage = true;
                liveMessageContext.setActivityList(fetchAllChildActivities(liveMessageContext.getActivityId()));
            }
            else if(liveMessageContext.getPercentage() != null && liveMessageContext.getPercentage() >= activity.getPercentage()) {
                sendMessage = true;
                activity.setPercentage(liveMessageContext.getPercentage());
                activity.setStatusColorCode(BackgroundActivityUtil.getStatusColorCode(activity.getRecordType(), activity.getPercentage()));
                activity.setMessage(liveMessageContext.getMessage());
                liveMessageContext.setActivity(activity);
            }
            if(sendMessage) {
                JSONObject json = FieldUtil.getAsJSON(liveMessageContext);
                WebMessage msg = new WebMessage();
                msg.setTopic("__background__activity__live__/" + liveMessageContext.getActivityId() + "/send");
                msg.setOrgId(orgId);
                msg.setContent(json);
                Broadcaster.getBroadcaster().sendMessage(msg);
            }
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

    public static List<BackgroundActivity> fetchAllChildActivities(long parentActivityId) throws Exception {
        if(isActivityLicenseEnabled()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .moduleName(Constants.BACKGROUND_ACTIVITY_MODULE)
                    .beanClass(BackgroundActivity.class)
                    .select(modBean.getAllFields(Constants.BACKGROUND_ACTIVITY_MODULE))
                    .andCondition(CriteriaAPI.getCondition("PARENT_ACTIVITY", "parentActivity",String.valueOf(parentActivityId), NumberOperators.EQUALS));
            return builder.get();
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
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey("__background_activity_rollup__/"+ activityId +"/rollup")
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

    private static boolean isValidChildActivity(Long parentActivityId) throws Exception {
        if (parentActivityId == null) {
            LOGGER.error("Parent Activity Id is null");
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
            BackgroundActivityAPI.refreshActivity(parentActivityId);
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
        return NewTransactionService.newTransactionWithReturn(() -> V3RecordAPI.getRecord(Constants.BACKGROUND_ACTIVITY_MODULE,activityId,BackgroundActivity.class));
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
        return true;
    }

    public static void deleteChildActivity(Long parentId) throws Exception {
        if (parentId == null) {
            LOGGER.error("Background Activity id is null");
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.moduleName(Constants.BACKGROUND_ACTIVITY_MODULE);
        selectRecordsBuilder.beanClass(BackgroundActivity.class);
        selectRecordsBuilder.select(modBean.getAllFields(Constants.BACKGROUND_ACTIVITY_MODULE));
        selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("PARENT_ACTIVITY","parentActivity", String.valueOf(parentId), NumberOperators.EQUALS));
        List<BackgroundActivity> backgroundActivities = selectRecordsBuilder.get();
        List<Long> ids = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(backgroundActivities)) {
            for(BackgroundActivity backgroundActivity : backgroundActivities) {
                ids.add(backgroundActivity.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(ids)) {
            DeleteRecordBuilder deleteRecordBuilder = new DeleteRecordBuilder();
            deleteRecordBuilder.moduleName(Constants.BACKGROUND_ACTIVITY_MODULE);
            deleteRecordBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids,","), NumberOperators.EQUALS));
            deleteRecordBuilder.delete();
        }
        BackgroundActivityAPI.refreshActivity(parentId);
    }
}
