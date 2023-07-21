package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.DataLogContextV3;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class DeleteDataLogsRecordsJob extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule agentDataModule = modbean.getModule(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modbean.getAllFields(FacilioConstants.ContextNames.AGENT_DATA_LOGGER));
            FacilioField startTimeField = fieldMap.get("startTime");
            long lastMonth = DateTimeUtil.getDateTime(jobContext.getExecutionTime(), true).minusDays(30).toInstant().toEpochMilli();

            SelectRecordsBuilder<DataLogContextV3> selectBuilder = new SelectRecordsBuilder<DataLogContextV3>()
                    .module(agentDataModule)
                    .select(modbean.getAllFields(FacilioConstants.ContextNames.AGENT_DATA_LOGGER))
                    .beanClass(DataLogContextV3.class)
                    .andCondition(CriteriaAPI.getCondition(startTimeField, String.valueOf(lastMonth), DateOperators.IS_BEFORE))
                    .skipModuleCriteria();

            SelectRecordsBuilder.BatchResult<DataLogContextV3> batches = selectBuilder.getInBatches("AgentData_Logger.ID", 5000);

            int i=0;
            List<List<Long>> dataLogIdsToBeDeleted = new ArrayList<>();

            while (batches.hasNext()) {
                List<DataLogContextV3> props = batches.get();
                LOGGER.info("Batch ID == "+ i++ +"Count of Records Fetched for this batch == "+props.size());
                dataLogIdsToBeDeleted.add(props.stream().map(DataLogContextV3::getId).collect(Collectors.toList()));
            }
            for(List<Long> ids:dataLogIdsToBeDeleted){
                LOGGER.info("dataLog Id's to be deleted === "+ids);
                DeleteRecordBuilder<DataLogContextV3> deleteBuilder1 = new DeleteRecordBuilder<DataLogContextV3>()
                        .module(agentDataModule)
                        .skipModuleCriteria();
                int countOfDeletedRecords= deleteBuilder1.batchDeleteById(ids);
                LOGGER.info("COUNT OF DELETED DATA LOG RECORDS ===  "+countOfDeletedRecords);
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occurred during deletion of DataLog record",e);
        }
    }
}
