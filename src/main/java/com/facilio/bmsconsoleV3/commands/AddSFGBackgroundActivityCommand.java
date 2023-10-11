package com.facilio.bmsconsoleV3.commands;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.logging.Logger;

public class AddSFGBackgroundActivityCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddSFGBackgroundActivityCommand.class.getName());
    BackgroundActivityService backgroundActivityService = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SFG20SyncHistoryContext syncHistoryContext = (SFG20SyncHistoryContext) context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY);
        try {
            backgroundActivityService = new BackgroundActivityService(syncHistoryContext.getId(), "sfg_sync", "Sfg20 Sync Process", "SFG20 Sync History Id : #" + syncHistoryContext.getId());
            return false;
        }
        catch (Exception e)
        {
            LOGGER.severe("Error Occured in SFG20 Sync  -- syncId: " + syncHistoryContext.getId() + "  Exception:" + e);
            SFG20JobPlanAPI.updateSyncHistoryStatus(syncHistoryContext, SFG20SyncHistoryContext.Status.ERROR.getIndex());
            return true;
        }
    }
}