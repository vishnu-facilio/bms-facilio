package com.facilio.bmsconsole.jobs;

import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointsDataMissingAlarmJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(PointsDataMissingAlarmJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            long jobId = jc.getJobId();
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(jobId);

            long dataMissingPoints = updatePointsDataMissing(jobId, agent);

            if (dataMissingPoints > 0) {
                LOGGER.info("Data missing for " + dataMissingPoints + " points in agent " + agent.getName());
                AgentUtilV2.raisePointAlarm(agent, dataMissingPoints);
            }
            else {
                LOGGER.info("Data arriving for all points in agent " + agent.getName());
                long activeAlarms = getActiveAlarms(agent);
                if (activeAlarms > 0) {
                    AgentUtilV2.clearPointAlarm(agent);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred in PointsDataMissingAlarmJob ", e);
            CommonCommandUtil.emailException(PointsDataMissingAlarmJob.class.getName(), "Error occurred in PointsDataMissingAlarmJob for job id : "+jc.getJobId(), e);
        }
    }

    private Criteria getPointsDataMissingCriteria(Map<String, FacilioField> fieldMap, long jobId, FacilioAgent agent) {
        long currentTime = System.currentTimeMillis();

        String diffInMinutes = "(" + currentTime + "-" + fieldMap.get(AgentConstants.LAST_RECORDED_TIME).getCompleteColumnName() + ") / (" + 60 * 1000 + ")";
        FacilioField difference = FieldFactory.getField("difference", diffInMinutes, FieldType.NUMBER);

        String interval = "2 * COALESCE(" + fieldMap.get(AgentConstants.DATA_INTERVAL).getCompleteColumnName() + "," + agent.getInterval() + ")";

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(jobId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(PointEnum.ConfigureStatus.CONFIGURED.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.FIELD_ID), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(difference, interval, NumberOperators.GREATER_THAN));
        return criteria;
    }

    private int getActiveAlarms(FacilioAgent agent) throws Exception {
        List<String> messageKeys = Collections.singletonList("AgentAlarm_" + AgentAlarmContext.AgentAlarmType.POINT.getIndex() + "_" + agent.getId());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseAlarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);

        Criteria severityCriteria = new Criteria();
        severityCriteria.addAndCondition(CriteriaAPI.getCondition("SEVERITY", "severity", FacilioConstants.Alarm.CLEAR_SEVERITY, StringOperators.ISN_T));

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY_ID");
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());
        severityField.setModule(baseAlarmModule);
        severityField.setDataType(FieldType.LOOKUP);

        SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<BaseAlarmContext>()
                .beanClass(BaseAlarmContext.class)
                .module(baseAlarmModule)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.BASE_ALARM))
                .andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", StringUtils.join(messageKeys, ','), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(severityField, severityCriteria, LookupOperator.LOOKUP));

        List<BaseAlarmContext> activeAlarms = selectBuilder.get();

        return activeAlarms.size();

    }

    private long updatePointsDataMissing(long jobId, FacilioAgent agent) throws SQLException {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .fields(FieldFactory.getPointFields())
                .andCriteria(getPointsDataMissingCriteria(fieldMap, jobId, agent));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.DATA_MISSING, true);
        return builder.update(toUpdate);
    }

}