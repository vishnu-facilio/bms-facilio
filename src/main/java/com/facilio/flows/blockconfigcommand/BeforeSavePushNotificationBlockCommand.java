package com.facilio.flows.blockconfigcommand;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public class BeforeSavePushNotificationBlockCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NotificationFlowTransitionContext notificationTransitionContext = (NotificationFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
        Objects.requireNonNull(notificationTransitionContext);

        FacilioUtil.throwIllegalArgumentException(notificationTransitionContext.getApplication() == -1l, "Application cannot be empty for notification block");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(notificationTransitionContext.getSubject()), "Subject cannot be empty for notification block");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(notificationTransitionContext.getMessage()), "Message cannot be empty for notification block");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(notificationTransitionContext.getTo()), "To cannot be empty for notification block");
        FacilioUtil.throwIllegalArgumentException(notificationTransitionContext.getRecordModuleId()==-1l,"RecordModuleId can not be empty");
        FacilioUtil.throwIllegalArgumentException(com.facilio.v3.context.Constants.getModBean().getModule(notificationTransitionContext.getRecordModuleId())==null,"Invalid RecordModuleId");

        Map<String, Object> configMap = BlockFactory.getAsMapFromJsonString(notificationTransitionContext.getConfigData());
        configMap.put(Constants.RECORD_ID, notificationTransitionContext.getRecordId());
        notificationTransitionContext.setConfigData(FieldUtil.getAsJSON(configMap).toJSONString());
        return false;
    }
}
