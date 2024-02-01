package com.facilio.readingkpi.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.context.ReadingKpiLogsContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class ReadingKpiLogsCleanUp extends FacilioJob {

    public static final long LAST_THREE_MONTHS_IN_MILLIS = 7889400000L;

    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            ModuleBean modBean = Constants.getModBean();
            FacilioModule readingKpiLogsModule = modBean.getModule(FacilioConstants.ReadingKpi.READING_KPI_LOGS_MODULE);
            FacilioField idField = FieldFactory.getIdField(readingKpiLogsModule);

            List<Long> ids;
            long startTime = System.currentTimeMillis();
            int deletedCount = 0;
            do {
                SelectRecordsBuilder<ReadingKpiLogsContext> selectBuilder = new SelectRecordsBuilder<ReadingKpiLogsContext>()
                        .module(readingKpiLogsModule)
                        .select(Collections.singletonList(idField))
                        .andCondition(CriteriaAPI.getCondition("SYS_CREATED_TIME", "sysCreatedTime", Long.toString(DateTimeUtil.getCurrenTime() - LAST_THREE_MONTHS_IN_MILLIS), NumberOperators.LESS_THAN))
                        .orderBy(idField.getCompleteColumnName())
                        .limit(500);

                List<Map<String, Object>> props = selectBuilder.getAsProps();
                ids = props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(ids)) {
                    DeleteRecordBuilder<ReadingKpiLogsContext> deleteBuilder = new DeleteRecordBuilder<ReadingKpiLogsContext>()
                            .module(readingKpiLogsModule);
                    int currentDeletedCount = deleteBuilder.batchDeleteById(ids);
                    deletedCount += currentDeletedCount;
                }
            } while (CollectionUtils.isNotEmpty(ids));
            LOGGER.info("deleted " + deletedCount + " reading kpi logs");
            LOGGER.info("Time taken to complete ReadingKpiLogsCleanUp job : " + (System.currentTimeMillis() - startTime));
        }
        catch (Exception ex) {
            LOGGER.info("ReadingKpiLogsCleanUp job failed", ex);
        }
    }
}
