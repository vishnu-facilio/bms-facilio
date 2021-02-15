package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateAgentWorkflowCommand extends FacilioCommand {

    private static final List<FacilioField> FIELDS = FieldFactory.getNewAgentFields();
    private static final FacilioModule MODULE = ModuleFactory.getNewAgentModule();

    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        Long workflow_rule_id = (long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        Long agentId = (long)context.get(AgentConstants.AGENT_ID);
        if(workflow_rule_id ==null || agentId ==null || workflow_rule_id <= 0L || agentId <= 0L){
            throw new IllegalArgumentException("WorkflowId or Agent Id should not be null..");
        }
        Map<String,Object> prop = new HashMap<>();
        prop.put("workflowId",workflow_rule_id);
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(FIELDS)
                .table(MODULE.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(agentId,MODULE));
        builder.update(prop);
        return false;
    }
}
