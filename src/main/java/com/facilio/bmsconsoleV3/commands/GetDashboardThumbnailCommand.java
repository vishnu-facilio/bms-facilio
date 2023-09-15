package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.DashboardUpdateHandler;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;


public class GetDashboardThumbnailCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Message message = new Message();
        JSONObject dashboardObj = new JSONObject();
        DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
        dashboardObj.put("dashboardId",dashboard.getId());
        dashboardObj.put("appLink", AccountUtil.getCurrentApp().getLinkName());
        message.setKey(DashboardUpdateHandler.KEY);
        message.setContent(dashboardObj);
        Messenger.getMessenger().sendMessage(message);
        return false;
    }
}
