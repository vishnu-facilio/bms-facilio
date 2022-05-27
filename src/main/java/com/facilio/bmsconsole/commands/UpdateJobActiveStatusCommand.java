package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateJobActiveStatusCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(UpdateJobActiveStatusCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> agentIds = (List<Long>) context.get(AgentConstants.AGENT_IDS);
        List<FacilioAgent> agentList = AgentApiV2.getAgents(agentIds);
        List<String> agentService = new ArrayList<>();
        Boolean isActiveUpdateValue = (Boolean) context.get(AgentConstants.IS_ACTIVE_UPDATE_VALUE);

        if(!agentList.isEmpty()){
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

            Map<String,Object> props = Collections.singletonMap("active", isActiveUpdateValue);
            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

            for (FacilioAgent agent : agentList) {
                if(agent.getAgentTypeEnum().isAgentService()){
                    agentService.add(agent.getName());
                }
                else {
                    GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateContext();
                    batchUpdate.setUpdateValue(props);
                    batchUpdate.addWhereValue("jobId", agent.getId());
                    batchUpdate.addWhereValue("jobName", "CloudAgent");
                    batchUpdateList.add(batchUpdate);
                }
            }
            int rows = builder.batchUpdate(whereFields, batchUpdateList);
            LOGGER.info("Number of rows updated: "+rows);
        }
        if(!agentService.isEmpty()){
            CloudAgentUtil.toggleJob(agentService, isActiveUpdateValue);
        }
        return false;
    }
}
