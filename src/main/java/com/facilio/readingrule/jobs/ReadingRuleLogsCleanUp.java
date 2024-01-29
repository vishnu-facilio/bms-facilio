package com.facilio.readingrule.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.ReadingRuleLogsContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class ReadingRuleLogsCleanUp extends FacilioJob {

    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            ModuleBean modBean = Constants.getModBean();
            FacilioModule readingRuleLogsModule = modBean.getModule(FacilioConstants.ReadingRules.READING_RULE_LOGS_MODULE);
            FacilioField idField = FieldFactory.getIdField(readingRuleLogsModule);

            List<Long> ids;
            long startTime = System.currentTimeMillis();
            int deletedCount = 0;
            do {
                SelectRecordsBuilder<ReadingRuleLogsContext> selectBuilder = new SelectRecordsBuilder<ReadingRuleLogsContext>()
                        .module(readingRuleLogsModule)
                        .select(Collections.singletonList(idField))
                        .andCondition(CriteriaAPI.getCondition("SYS_CREATED_TIME", "sysCreatedTime", Long.toString(System.currentTimeMillis() - 5259600000L), NumberOperators.LESS_THAN))
                        .orderBy(idField.getCompleteColumnName())
                        .limit(500);

                List<Map<String, Object>> props = selectBuilder.getAsProps();
                ids = props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(ids)) {
                    DeleteRecordBuilder<ReadingRuleLogsContext> deleteBuilder = new DeleteRecordBuilder<ReadingRuleLogsContext>()
                            .module(readingRuleLogsModule);
                    int currentDeletedCount = deleteBuilder.batchDeleteById(ids);
                    deletedCount += currentDeletedCount;
                } else {
                    LOGGER.info("deleted " + deletedCount + " reading rule logs");
                    deletedCount = 0;
                }
            } while (CollectionUtils.isNotEmpty(ids));
            LOGGER.info("Time taken to complete ReadingRuleLogsCleanUp job : " + (System.currentTimeMillis() - startTime));
        }
        catch (Exception ex) {
            LOGGER.info("ReadingRuleLogsCleanUp job failed", ex);
        }
    }
}
