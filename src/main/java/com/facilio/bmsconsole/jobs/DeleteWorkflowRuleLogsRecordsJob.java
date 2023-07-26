package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteWorkflowRuleLogsRecordsJob extends FacilioJob {
    private static final Logger LOGGER= LogManager.getLogger(DeleteWorkflowRuleLogsRecordsJob.class.getName());
    @Override
    public void execute(JobContext jobContext) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module= moduleBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
        List<FacilioField> fieldsList=moduleBean.getAllFields(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
        Map<String,FacilioField> fieldMap= FieldFactory.getAsMap(fieldsList);
        long lastWeek = DateTimeUtil.getDateTime(jobContext.getExecutionTime(), true).minusMonths(1).toInstant().toEpochMilli();
        List<WorkflowRuleLogContext> recordList=new ArrayList<>();
        do{
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .module(module)
                    .beanClass(WorkflowRuleLogContext.class)
                    .select(fieldsList)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("executedOn"), String.valueOf(lastWeek), DateOperators.IS_BEFORE))
                    .limit(5000);
            recordList = builder.get();
            if(CollectionUtils.isNotEmpty(recordList)){
                List<Long> workflowLogsIdsToBeDeleted= recordList.stream().map(WorkflowRuleLogContext::getId).collect(Collectors.toList());
                LOGGER.info("workflowRuleLog Id's to be deleted === "+workflowLogsIdsToBeDeleted);
                DeleteRecordBuilder deleteRecordBuilder=new DeleteRecordBuilder()
                        .module(module);
                int countOfDeletedRecords= deleteRecordBuilder.batchDeleteById(workflowLogsIdsToBeDeleted);
                LOGGER.info("COUNT OF DELETED WORKFLOW RULE LOG RECORDS ===  "+countOfDeletedRecords);
            }
        }while(CollectionUtils.isNotEmpty(recordList)&&recordList.size()>=5000);
    }
}
