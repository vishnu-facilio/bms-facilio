package com.facilio.remotemonitoring.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.handlers.AlarmCriteriaHandler;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class FlaggedEventAutoClosureJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long flaggedEventId = jc.getJobId();
        FlaggedEventUtil.closeFlaggedEvent(flaggedEventId);
//        if(isAllFlaggedEventAlarmsClosed(flaggedEventId)) {
//            FlaggedEventUtil.closeFlaggedEvent(flaggedEventId);
//        }
    }
}