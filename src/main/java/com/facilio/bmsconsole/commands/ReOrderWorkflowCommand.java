package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReOrderWorkflowCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WorkflowRuleContext> workflowList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
        List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if (workflowList != null && ids != null) {
            if (workflowList.size() != ids.size()) {
                throw new IllegalArgumentException("Size mismatch");
            }

            Map<Long, WorkflowRuleContext> ruleAsMap = workflowList.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, Function.identity()));
            int counter = 1;
            List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
            for (Long id : ids) {
                WorkflowRuleContext ruleContext = ruleAsMap.get(id);
                ruleContext.setExecutionOrder(counter++);

                GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                batchValue.setWhereId(id);
                batchValue.addUpdateValue("executionOrder", ruleContext.getExecutionOrder());
                batchUpdates.add(batchValue);
            }

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getWorkflowRuleModule().getTableName())
                    .fields(Collections.singletonList(FieldFactory.getField("executionOrder", "EXECUTION_ORDER", ModuleFactory.getWorkflowRuleModule(), FieldType.NUMBER)));
            builder.batchUpdateById(batchUpdates);
        }
        return false;
    }
}
