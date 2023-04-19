package com.facilio.bmsconsole.jobs;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class PointsDataMissingAlarmJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(PointsDataMissingAlarmJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            long jobId = jc.getJobId();
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(jobId);

            Collection<Long> missingPointsIds = AgentUtilV2.getPointsDataMissing(agent);
            long pointsMissingCount = missingPointsIds.size();

            if (pointsMissingCount > 0) {
                LOGGER.info("Data missing for " + pointsMissingCount + " commissioned points in agent " + agent.getDisplayName());
                updatePointsDataMissing(jobId, missingPointsIds);
                AgentUtilV2.raisePointAlarm(agent, pointsMissingCount);
            }
            else {
                LOGGER.info("Data arriving for all the commissioned points in agent " + agent.getDisplayName());
                long activeAlarms = AgentUtilV2.getActiveAlarms(agent);
                if (activeAlarms > 0) {
                    AgentUtilV2.clearPointAlarm(agent);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred in PointsDataMissingAlarmJob ", e);
            CommonCommandUtil.emailException(PointsDataMissingAlarmJob.class.getName(), "Error occurred in PointsDataMissingAlarmJob for job id : "+jc.getJobId(), e);
        }
    }

    private void updatePointsDataMissing(long agentId, Collection<Long> pointIds) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
        Map<String, FacilioField> fieldMap = new HashMap<>();
        if (pointModule == null) {
            pointModule = ModuleFactory.getPointModule();
            fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
        }
        else {
            fieldMap = FieldFactory.getAsMap(moduleBean.getAllFields(AgentConstants.POINT));
        }
        /*GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .fields(FieldFactory.getPointFields())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getIdCondition(pointIds, ModuleFactory.getPointModule()));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.DATA_MISSING, true);
        builder.update(toUpdate);*/

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

        for(long pointId: pointIds) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addUpdateValue(AgentConstants.DATA_MISSING, true);
            updateVal.addWhereValue(AgentConstants.ID, pointId);
            updateVal.addWhereValue(AgentConstants.AGENT_ID, agentId);
            batchUpdateList.add(updateVal);
        }

        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(fieldMap.get(AgentConstants.ID));
        whereFields.add(fieldMap.get(AgentConstants.AGENT_ID));

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(pointModule.getTableName())
                .fields(Collections.singletonList(fieldMap.get(AgentConstants.DATA_MISSING)));

        updateBuilder.batchUpdate(whereFields, batchUpdateList);
    }

}