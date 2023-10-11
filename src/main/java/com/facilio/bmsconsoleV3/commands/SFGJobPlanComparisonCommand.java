package com.facilio.bmsconsoleV3.commands;

import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20ScheduleContext;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI.getSFGTypeJPMap;

public class SFGJobPlanComparisonCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(SFG20ConnectCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SFG20SyncHistoryContext historyContext = (SFG20SyncHistoryContext) context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY);
        Long backgroundActivityId = BackgroundActivityAPI.parentActivityForRecordIdAndType(historyContext.getId(), "sfg_sync");
        try {
            FacilioModule sfg20JobPlanModule = ModuleFactory.getSFG20JobPlanModule();
            List<Map<String, Object>> schedulesList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SFG20.SCHEDULES_LIST);
            String accessToken = (String) context.get(FacilioConstants.ContextNames.SFG20.SFG_ACCESS_TOKEN);
            Map<Integer, JobPlanContext> sfgTypeJPList = getSFGTypeJPMap();
            List<Map<String, Object>> sfgJPCreationList = new ArrayList<>();
            List<Map<String, Object>> sfgJPUpdationList = new ArrayList<>();
            List<Map<String, Object>> sfgJPRevisonList = new ArrayList<>();
            for(Map<String, Object> sfgSchedule : schedulesList)
            {
                Integer scheduleId = (Integer) sfgSchedule.get("scheduleId");
                if(sfgTypeJPList.containsKey(scheduleId) && sfgTypeJPList.get(scheduleId) != null && !sfgTypeJPList.get(scheduleId).getSfgVersion().equals(sfgSchedule.get("version")))
                {
                    JobPlanContext jobPlanContext = sfgTypeJPList.get(scheduleId);
                    sfgSchedule.put("oldJobPlan",jobPlanContext);
                    sfgSchedule.put("oldVersion",jobPlanContext.getSfgVersion());
                    sfgSchedule.put("flowType", SFG20ScheduleContext.Type.Update.getIndex());
                   if(jobPlanContext.getJpStatus().equals(JobPlanContext.JPStatus.ACTIVE.getVal()))
                   {
                       sfgJPRevisonList.add(sfgSchedule);
                   }
                   else{
                       sfgJPUpdationList.add(sfgSchedule);
                   }
                }
                else if(!sfgTypeJPList.containsKey(scheduleId)){
                    sfgSchedule.put("flowType", SFG20ScheduleContext.Type.Create.getIndex());
                    sfgJPCreationList.add(sfgSchedule);
                }
            }
            if(!sfgJPCreationList.isEmpty()) {
                SFG20JobPlanAPI.getSFG20DetailsForSchedulesAPI(accessToken, sfgJPCreationList, historyContext.getId());
            }
            if(!sfgJPUpdationList.isEmpty()) {
                SFG20JobPlanAPI.getSFG20DetailsForSchedulesAPI(accessToken, sfgJPUpdationList, historyContext.getId());
            }
            if (!sfgJPRevisonList.isEmpty()) {
                SFG20JobPlanAPI.getSFG20DetailsForSchedulesAPI(accessToken, sfgJPRevisonList, historyContext.getId());
            }

            List<Map<String, Object>> sfgJPAllData = new ArrayList<>();
            sfgJPAllData.addAll(sfgJPCreationList);
            sfgJPAllData.addAll(sfgJPUpdationList);
            sfgJPAllData.addAll(sfgJPRevisonList);
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(sfg20JobPlanModule.getTableName())
                    .fields(FieldFactory.getSFG20JobPlansFields());
            builder.addRecords(sfgJPAllData);
            builder.save();
            context.put("sfgJPCreationList",sfgJPCreationList);
            context.put("sfgJPUpdationList",sfgJPUpdationList);
            context.put("sfgJPRevisonList",sfgJPRevisonList);
            BackgroundActivityAPI.updateBackgroundActivity(backgroundActivityId,50,"SFG 20 Schedules Imported");
            return false;
        } catch (Exception e) {
            LOGGER.severe("Error Occured in SFG20 Sync  -- syncId: " + historyContext.getId() + "  Exception:" + e);
            SFG20JobPlanAPI.updateSyncHistoryStatus(historyContext, SFG20SyncHistoryContext.Status.ERROR.getIndex());
            BackgroundActivityAPI.failBackgroundActivity(backgroundActivityId,"Error Occured in SFG20 Sync  -- syncId: " + historyContext.getId() + "  Exception:" + e);
            return true;
        }

    }
}
