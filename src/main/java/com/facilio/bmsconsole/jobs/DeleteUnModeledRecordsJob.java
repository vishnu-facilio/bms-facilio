package com.facilio.bmsconsole.jobs;


import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class DeleteUnModeledRecordsJob extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            FacilioModule unModeledDataModule = ModuleFactory.getUnmodeledDataModule();

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getUnmodeledDataFields());
            fieldMap.put(AgentConstants.ID, FieldFactory.getIdField(unModeledDataModule));

            GenericSelectRecordBuilder.GenericBatchResult batches = getGenericBatchResult(jobContext, fieldMap, unModeledDataModule);
            deleteUnModeledData(batches, unModeledDataModule);
        } catch (Exception e) {
            LOGGER.error("DeleteUnModeledRecordsJob :: Error occurred during deletion of un modeled record", e);
        }
    }

    private static void deleteUnModeledData(GenericSelectRecordBuilder.GenericBatchResult batches, FacilioModule unModeledDataModule) throws Exception {
        int i = 0;
        while (batches.hasNext()) {
            List<Map<String, Object>> props = batches.get();
            LOGGER.info("DeleteUnModeledRecordsJob :: Batch ID == " + i++ + "Count of Records Fetched for this batch == " + props.size());
            List<Long> pointIds = props.stream().map(prop -> Long.parseLong(prop.get(AgentConstants.ID).toString())).collect(Collectors.toList());
            deleteRecords(pointIds, unModeledDataModule);
        }
    }

    private static GenericSelectRecordBuilder.GenericBatchResult getGenericBatchResult(JobContext jobContext, Map<String, FacilioField> fieldMap, FacilioModule unModeledDataModule) {
        FacilioField startTimeField = fieldMap.get(AgentConstants.TTIME);

        long lastMonth = DateTimeUtil.getDateTime(jobContext.getExecutionTime(), true).minusDays(30).toInstant().toEpochMilli();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(unModeledDataModule.getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(unModeledDataModule)))
                .andCondition(CriteriaAPI.getCondition(startTimeField, String.valueOf(lastMonth), DateOperators.IS_BEFORE));

        return selectBuilder.getInBatches("Unmodeled_Data.ID", 5000);
    }

    private static void deleteRecords(List<Long> unModeledDataPointIdsToBeDeleted, FacilioModule unModeledDataModule) throws Exception {
        LOGGER.debug("DeleteUnModeledRecordsJob :: Un Modeled Points Id's to be deleted === " + unModeledDataPointIdsToBeDeleted);
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(unModeledDataModule.getTableName());
        int countOfDeletedRecords = deleteBuilder.batchDeleteById(unModeledDataPointIdsToBeDeleted);
        LOGGER.info("DeleteUnModeledRecordsJob :: Count of Deleted UN MODELED DATA records : " + countOfDeletedRecords);
    }
}