package com.facilio.flows.context;

import com.facilio.flowengine.context.Constants.NotificationBlockConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class NotificationFlowTransitionContext extends FlowTransitionContext {
    private List<NotificationSentToContext> to;
    private long recordModuleId=-1l;
    private Object recordId;
    private String subject;
    private String message;
    private Boolean isSendPushNotification;
    private long application=-1l;

    @Override
    public void updateConfig() throws Exception{
        super.updateConfig();
        addConfigData(NotificationBlockConstants.TO, FieldUtil.getAsJSONArray(to,NotificationSentToContext.class));
        addConfigData(NotificationBlockConstants.SUBJECT,subject);
        addConfigData(NotificationBlockConstants.MESSAGE,message);
        addConfigData(NotificationBlockConstants.IS_SEND_PUSH_NOTIFICATION,isSendPushNotification);
        addConfigData(NotificationBlockConstants.APPLICATION,application);
        addConfigData(NotificationBlockConstants.RECORD_MODULE_ID,recordModuleId);
    }
}
