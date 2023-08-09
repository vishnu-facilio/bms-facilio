package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.context.WorkflowRuleRecordRelationshipContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteScheduledOneTimeRuleExecution extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DeleteScheduledOneTimeRuleExecution.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {
        LocalDateTime sameDayLastMonth = LocalDateTime.now().minusMonths(1);
        Long sameDayLastMonthInSeconds = (Timestamp.valueOf(sameDayLastMonth).getTime()) / 1000;

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("EXECUTION_TIME","executionTime", String.valueOf(sameDayLastMonthInSeconds), DateOperators.IS_BEFORE));

        int rowsDeleted = 0;

        for(int batchDeleteCount = 0; batchDeleteCount <= 5; batchDeleteCount++) {
            if(batchDeleteCount == 5){
                LOGGER.info("Batch delete count has exceeded: "+ batchDeleteCount);
                break;
            }

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getScheduleOneTimeRuleExecutionFields())
                    .table(ModuleFactory.getscheduleRuleRecordRelationModule().getTableName())
                    .andCriteria(criteria);

            List<WorkflowRuleRecordRelationshipContext> relationshipContexts = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), WorkflowRuleRecordRelationshipContext.class);

            if(CollectionUtils.isEmpty(relationshipContexts)){
                break;
            }

            List<Long> ids = relationshipContexts.stream().map(WorkflowRuleRecordRelationshipContext::getId).collect(Collectors.toList());

            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getscheduleRuleRecordRelationModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getscheduleRuleRecordRelationModule()));
            rowsDeleted = rowsDeleted + deleteRecordBuilder.delete();
            FacilioTimer.deleteJobs(ids, "ScheduleOneTimeRuleExecution");
        }

        LOGGER.info("Scheduled Workflow Rules deleted count : "+ rowsDeleted + " on " + sameDayLastMonth);
    }
}
