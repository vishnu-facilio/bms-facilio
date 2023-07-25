package com.facilio.bmsconsoleV3.commands;

import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.bmsconsole.commands.GetDbTimeLineFilterToWidgetMapping;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SFG20ConnectCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(SFG20ConnectCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        SFG20SyncHistoryContext historyContext = (SFG20SyncHistoryContext) context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY);
        Long backgroundActivityId = BackgroundActivityAPI.parentActivityForRecordIdAndType(historyContext.getId(),"sfg_sync");
        try {
            LOGGER.log(Level.INFO,"SFG20 Background Activity started-"+historyContext.getId());
            BackgroundActivityAPI.updateBackgroundActivity(backgroundActivityId,5,"Syncing");
            String accessToken = (String) SFG20JobPlanAPI.getSFG20ClientAccessToken(Objects.requireNonNull(SFG20JobPlanAPI.getSFG20Setting())).get("access_token");
            List<Map<String, Object>> schedulesList = SFG20JobPlanAPI.getSFG20SchedulesAPI(accessToken);
            context.put(FacilioConstants.ContextNames.SFG20.SFG_ACCESS_TOKEN, accessToken);
            context.put(FacilioConstants.ContextNames.SFG20.SCHEDULES_LIST, schedulesList);
            BackgroundActivityAPI.updateBackgroundActivity(backgroundActivityId,25,"Data Dumped");
            LOGGER.log(Level.INFO,"SFG20 API's completed- "+historyContext.getId());
            return false;
        }
        catch (Exception e)
        {
            LOGGER.severe("Error Occured in SFG20 Sync  -- syncId: " + historyContext.getId() + "  Exception:" + e);
            SFG20JobPlanAPI.updateSyncHistoryStatus(historyContext, SFG20SyncHistoryContext.Status.ERROR.getIndex());
            BackgroundActivityAPI.failBackgroundActivity(backgroundActivityId,"Error Occured in SFG20 Sync  -- syncId: " + historyContext.getId() + "  Exception:" + e);
            return true;
        }
    }

}