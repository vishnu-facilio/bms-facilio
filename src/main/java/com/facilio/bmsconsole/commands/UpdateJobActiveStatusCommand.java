package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateJobActiveStatusCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(UpdateJobActiveStatusCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> agentIds = (List<Long>) context.get(AgentConstants.AGENT_IDS);
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        List<FacilioAgent> agentList = agentBean.getAgents(agentIds);
        context.put(AgentConstants.AGENT_LIST,agentList);
        Boolean isActiveUpdateValue = (Boolean) context.get(AgentConstants.IS_ACTIVE_UPDATE_VALUE);

        List<String> agentService = new ArrayList<>();
        for(FacilioAgent agent : agentList){
            if(agent.getAgentTypeEnum().isAgentService()){
                agentService.add(agent.getName());
                agentIds.remove(agent.getId());
            }
        }
        if(!agentService.isEmpty()) {
            CloudAgentUtil.toggleJob(agentService, isActiveUpdateValue);
        }
        if(!agentIds.isEmpty()){
            FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE,() -> {
                updateActiveStatus(agentIds,isActiveUpdateValue, FacilioConstants.Job.CLOUD_AGENT_JOB_NAME);
                updateActiveStatus(agentIds,isActiveUpdateValue, FacilioConstants.Job.POINTS_DATA_MISSING_ALARM_JOB_NAME);
            });
        }

         return false;
    }

    private void updateActiveStatus(List<Long>idList,Boolean isActiveUpdateValue, String jobName) throws Exception {
        FacilioModule jobsModule = ModuleFactory.getJobsModule();
        FacilioField isActiveField = FieldFactory.getField("active", "IS_ACTIVE", ModuleFactory.getJobsModule(), FieldType.BOOLEAN);
        FacilioField jobidField = FieldFactory.getField("jobId", "JOBID", ModuleFactory.getJobsModule(), FieldType.ID);
        FacilioField jobNameField = FieldFactory.getField("jobName", "JOBNAME", ModuleFactory.getJobsModule(), FieldType.STRING);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(jobsModule.getTableName())
                .fields(Collections.singletonList(isActiveField))
                ;

        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(jobidField);
        whereFields.add(jobNameField);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

        for (Long id : idList) {
                GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateContext();
                batchUpdate.addUpdateValue("active", isActiveUpdateValue);
                batchUpdate.addWhereValue("jobId", id);
                batchUpdate.addWhereValue("jobName", jobName);
                batchUpdateList.add(batchUpdate);
        }
        if(batchUpdateList!= null &&!batchUpdateList.isEmpty()) {
            int rows = builder.batchUpdate(whereFields, batchUpdateList);
            LOGGER.info("Number of rows updated: "+rows);
        }
    }
}
