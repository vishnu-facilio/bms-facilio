package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class SendPushNotificationBlock extends BaseBlock {
    private JSONArray toList;
    private String to;
    private String subject;
    private String message;
    private Boolean isSendPushNotification;
    private long application=-1l;
    private long recordModuleId=-1l;
    private Object recordId;
    public SendPushNotificationBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try {
            init();
            Object recordIdOb = FlowEngineUtil.replacePlaceHolder(recordId,memory);
            if(recordIdOb == null){
                throw new FlowException("recordId cannot be empty for SendPushNotificationBlock");
            }
            if(!NumberUtils.isNumber(recordIdOb.toString())){
                throw new FlowException("recordId is not a number for SendPushNotificationBlock");
            }

            long recId = (long)Double.parseDouble(recordIdOb.toString());

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(recordModuleId);

            Object record = V3Util.getRecord(module.getName(),recId,null);

            JSONObject notificationJSON = getNotificationJSON(module.getName(),recId);

            JSONObject replaceJSON = (JSONObject) FlowEngineUtil.replacePlaceHolder(notificationJSON,memory);

            String ids = (String) replaceJSON.get("id");
            List<Long> idLongList = getToAsIdList(ids);
            Boolean isPushNotification = (replaceJSON!=null )?(Boolean) replaceJSON.getOrDefault("isSendNotification",false):false;
            NotificationAPI.pushNotification(replaceJSON,idLongList,isPushNotification,record,module,null);

        } catch (Exception exception) {
            LOGGER.debug("Exception in SendPushNotificationBlock:"+exception.getMessage());
            flowEngineInterFace.log(exception.getMessage());
            FlowException flowException = exception instanceof FlowException?(FlowException)exception:new FlowException(exception.getMessage());
            flowEngineInterFace.emitBlockError(this,memory,flowException);
        }

    }
    private String getTo(){
        StringBuilder toStringBuilder = new StringBuilder();
        for(int i=0;i<toList.size();i++){

            Map sentToObject = (Map) toList.get(i);

            Long userId = (Long) sentToObject.get("userId");
            String placeHolder = (String) sentToObject.get("placeHolder");

            if(userId!=null && userId!=-1l){
                toStringBuilder.append(userId);
            }
            else if(StringUtils.isNotEmpty(placeHolder)){
                toStringBuilder.append(placeHolder);
            }

            if(i!=toList.size()-1){
                toStringBuilder.append(",");
            }
        }
        return toStringBuilder.toString();
    }
    private List<Long> getToAsIdList(String ids){
        List<Long> idLongList =  new ArrayList<>();
        String idsArray[] = ids.split(",");
        for(int i =0; i<idsArray.length;i++){
            if(NumberUtils.isNumber(idsArray[i])){
                idLongList.add(((Double)Double.parseDouble(idsArray[i])).longValue());
            }
            else{
                LOGGER.debug("Invalid toUserID:"+idsArray[i] +" in SendPushNotificationBlock");
            }
        }
        return idLongList;
    }
    private void init() {
        this.recordId = config.get(Constants.RECORD_ID);
        this.recordModuleId = (long) config.get(Constants.NotificationBlockConstants.RECORD_MODULE_ID);
        this.toList = (JSONArray)config.get(Constants.NotificationBlockConstants.TO);
        this.to = getTo();
        this.subject = (String) config.get(Constants.NotificationBlockConstants.SUBJECT);
        this.message = (String) config.get(Constants.NotificationBlockConstants.MESSAGE);
        this.isSendPushNotification = (Boolean) config.get(Constants.NotificationBlockConstants.IS_SEND_PUSH_NOTIFICATION);
        this.application = (Long) config.get(Constants.NotificationBlockConstants.APPLICATION);
    }
    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object recordId = config.get(Constants.RECORD_ID);
        Object recordModuleId = config.get(Constants.NotificationBlockConstants.RECORD_MODULE_ID);
        Object toList = config.get(Constants.NotificationBlockConstants.TO);
        Object subject = config.get(Constants.NotificationBlockConstants.SUBJECT);
        Object message = config.get(Constants.NotificationBlockConstants.MESSAGE);
        Object isSendPushNotification = config.get(Constants.NotificationBlockConstants.IS_SEND_PUSH_NOTIFICATION);
        Object application = config.get(Constants.NotificationBlockConstants.APPLICATION);

        if(recordId==null){
            throw new FlowException("recordId can not be empty");
        }
        if(!(recordId instanceof String || recordId instanceof Number)){
            throw new FlowException("recordID should be either a number or placeholder");
        }
        if(recordModuleId == null){
            throw new FlowException("recordModuleId can not be empty");
        }
        if(!(recordModuleId instanceof Long)){
             throw new FlowException("recordModuleId is not a number");
        }
        if(!(subject instanceof String)){
            throw new FlowException("subject is not a string");
        }
        if(((String) subject).length()>500){
            throw new FlowException("subject length should be less than 500");
        }
        if(!(message instanceof String)){
            throw new FlowException("message is not a string");
        }
        if(((String) message).length()>2040){
            throw new FlowException("message length should be less than 2040");
        }
        if(!(isSendPushNotification instanceof Boolean)){
            throw new FlowException("isSendPushNotification is not a boolean");
        }
        if(!(application instanceof Long)){
            throw new FlowException("application is not a number");
        }
        if(!(toList instanceof JSONArray)){
            throw new FlowException("to is not a JSONArray");
        }
        if(CollectionUtils.isEmpty((JSONArray)toList)){
            throw new FlowException("to cannot be empty");
        }
        if(!(toList instanceof JSONArray)){
            throw new FlowException("to is not a JSONArray");
        }
    }
    public JSONObject getNotificationJSON(String moduleName, long recordId) {
        JSONObject res = new JSONObject();
        try{
            JSONObject notification = new JSONObject();

            notification.put("content_available", true);
            notification.put("summary_id", recordId);
            notification.put("sound", "default");
            notification.put("module_name", moduleName);
            notification.put("priority", "high");
            notification.put("click_action", moduleName.toUpperCase() + "_SUMMARY");
            notification.put("title", subject);
            notification.put("text", message);

            JSONObject data = new JSONObject();

            data.put("content_available", true);
            notification.put("summary_id", recordId);
            data.put("sound", "default");
            notification.put("module_name", moduleName);
            data.put("priority", "high");
            notification.put("click_action", moduleName.toUpperCase() + "_SUMMARY");
            data.put("title", subject);
            data.put("text", message);


            res.put("name", moduleName.toUpperCase() + "_PUSH_NOTIFICATION");
            res.put("notification", notification);
            res.put("data", data);
            res.put("id", to);
            res.put("isSendNotification", isSendPushNotification);
            res.put("application", application);
        }catch (Exception e){
            LOGGER.info("Exception in NotificationFlowTransitionContext:"+e);
        }
        return res;
    }
}
