package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import static com.facilio.wmsv2.constants.Topics.DashboardUpdate.dashboardUpdate;

public class GetDashboardThumbnailCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Message message = new Message();
        JSONObject dashboardObj = new JSONObject();
        DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
        dashboardObj.put("dashboardId",dashboard.getId());
        dashboardObj.put("dashboardUrl",context.get("dashboardUrl"));
        message.setTopic(dashboardUpdate);
        message.setContent(dashboardObj);
        message.setAppId(AccountUtil.getCurrentUser().getApplicationId());
        WmsBroadcaster.getBroadcaster().sendMessage(message);
        return false;
    }
}
