package com.facilio.remotemonitoring.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.ControllerAlarmInfoContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.FilteredAlarmContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.handlers.AlarmCriteriaHandler;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.ControllerAlarmInfoModule;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class FlaggedEventScheduledJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long flaggedEventRuleId = jc.getJobId();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(flaggedEventRuleId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SENT_TO_PROCESSING","sentToProcessing",String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        List<FilteredAlarmContext> filteredAlarms = V3RecordAPI.getRecordsListWithSupplements(FilteredAlarmModule.MODULE_NAME, null, FilteredAlarmContext.class, criteria, null);
        if(CollectionUtils.isNotEmpty(filteredAlarms)) {
            for(FilteredAlarmContext filterAlarm : filteredAlarms) {
                FlaggedEventUtil.pushToStormFlaggedEventQueue(filterAlarm);
            }
        }
    }
}