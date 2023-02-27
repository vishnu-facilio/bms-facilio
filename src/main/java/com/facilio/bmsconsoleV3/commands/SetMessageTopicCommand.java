package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;

import static com.facilio.wmsv2.constants.Topics.InApp.inApp;

public class SetMessageTopicCommand extends FacilioCommand implements PostTransactionCommand {

    private Context context;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        return false;
    }
    @Override
    public boolean postExecute() throws Exception {
        Map<String, List<UserNotificationContext>> recordMap = (Map<String, List<UserNotificationContext>>) context.get(Constants.RECORD_MAP);
        List<UserNotificationContext> userNotificationList = recordMap.get(FacilioConstants.ContextNames.USER_NOTIFICATION);
        Message message = new Message();
        for (UserNotificationContext userNotification:userNotificationList) {
            message.setTopic(inApp);
            message.setOrgId(userNotification.getOrgId());
            message.setAppId(userNotification.getApplication());
            message.setTo(userNotification.getUser().getId());
            message.setContent(FieldUtil.getAsJSON(userNotification));
            SessionManager.getInstance().sendMessage(message);
        }
        return false;
    }
}
