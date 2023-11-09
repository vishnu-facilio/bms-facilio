package com.facilio.bmsconsole.jobs;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.agentv2.AgentConstants.CONTROLLER_OFFLINE_ALARM;

public class ControllerOfflineAlarmJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(ControllerOfflineAlarmJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            long jobId = jc.getJobId();
            FacilioAgent agent = AgentConstants.getAgentBean().getAgent(jobId);

            List<Controller> offlineControllers = getOfflineControllers(agent);
            if (!offlineControllers.isEmpty()) {
                LOGGER.info(CONTROLLER_OFFLINE_ALARM + "Processing Raw Controller Offline Alarm for agent : " + agent.getDisplayName() + " Controllers size : " + offlineControllers.size());
                List<Long> controllerIds = offlineControllers.stream()
                        .map(Controller::getId)
                        .collect(Collectors.toList());
                ControllerUtilV2.makeControllersInActive(agent, controllerIds);
                ControllerUtilV2.processControllerRawAlarm(offlineControllers, FacilioConstants.Alarm.CRITICAL_SEVERITY);
            }
        } catch (Exception e) {
            LOGGER.error(CONTROLLER_OFFLINE_ALARM + "Exception occurred in ControllerOfflineAlarmJob ", e);
            CommonCommandUtil.emailException(PointsDataMissingAlarmJob.class.getName(), "Error occurred in ControllerOfflineAlarmJob for job id : " + jc.getJobId(), e);
        }
    }

    private List<Controller> getOfflineControllers(FacilioAgent agent) throws Exception {
        GetControllerRequest controllerRequest = new GetControllerRequest()
                .withAgentId(agent.getId())
                .withCriteria(getOfflineControllersCriteria(agent));

        return controllerRequest.getControllers();
    }

    private Criteria getOfflineControllersCriteria(FacilioAgent agent) {
        List<FacilioField> fields = FieldFactory.getControllersField();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        long currentTime = System.currentTimeMillis();
        String diffInMinutes = "(" + currentTime + "-" + fieldMap.get(AgentConstants.LAST_DATA_RECEIVED_TIME).getCompleteColumnName() + ") / (" + 60 * 1000 + ")";
        FacilioField difference = FieldFactory.getField("difference", diffInMinutes, FieldType.NUMBER);

        String interval = agent.getControllerAlarmIntervalInMins().toString();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.LAST_DATA_RECEIVED_TIME), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(difference, interval, NumberOperators.GREATER_THAN));
        return criteria;
    }

}
