package com.facilio.bmsconsoleV3.commands;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFGScheduleContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI.getFacilioJobPlanForSFGData;
import static com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI.updateSFGSchWithJobPlanId;

public class SFGtoFacilioJobplanCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(SFG20ConnectCommand.class.getName());
@Override
public boolean executeCommand(Context context) throws Exception {
        SFG20SyncHistoryContext historyContext = (SFG20SyncHistoryContext) context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY);
        Long backgroundActivityId = BackgroundActivityAPI.parentActivityForRecordIdAndType(historyContext.getId(),"sfg_sync");
        try {
            BackgroundActivityAPI.updateBackgroundActivity(backgroundActivityId,75,"Job plan Conversion started");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule jobPlanModule =modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
            List<Map<String, Object>> schedulesDetailsList = (List<Map<String, Object>>) context.get("sfgJPCreationList");
            List<JobPlanContext> jobPlanContextList = new ArrayList<>();
            long createdCount = 0L;
            long errorCount = 0L;
            for(Map<String, Object> sfgJobPlan : schedulesDetailsList)
            {
                    Map<String, Object> details = (Map<String, Object>) sfgJobPlan.get("scheduleDetail");
                    SFGScheduleContext schedule = FieldUtil.getAsBeanFromMap(details, SFGScheduleContext.class);
                    JobPlanContext jobPlanContext = getFacilioJobPlanForSFGData(schedule);
                    jobPlanContextList.add(jobPlanContext);
                    FacilioContext ctx = V3Util.createRecord(jobPlanModule, FieldUtil.getAsProperties(jobPlanContext));
                    Map<String, List> recordMap = (Map<String, List>) ctx.get(Constants.RECORD_MAP);
                    List<JobPlanContext> jobPlans = recordMap.get(FacilioConstants.ContextNames.JOB_PLAN);
                    updateSFGSchWithJobPlanId(historyContext.getId(),schedule.getScheduleId(),jobPlans.get(0).getId());
                    createdCount++;
                    LOGGER.log(Level.INFO,createdCount+" -Jobplan created");
            }
            historyContext.setJobPlanCreatedCount(createdCount);
            SFG20JobPlanAPI.updateSyncHistoryStatus(historyContext, SFG20SyncHistoryContext.Status.COMPLETED.getIndex());
            BackgroundActivityAPI.completeBackgroundActivity(backgroundActivityId,"Sync Completed");
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
