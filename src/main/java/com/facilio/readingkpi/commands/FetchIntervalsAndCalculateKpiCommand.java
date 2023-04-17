package com.facilio.readingkpi.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.ReadingKpiLoggerAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.facilio.readingkpi.context.KpiResourceLoggerContext.KpiLoggerStatus.COMPLETED;

@Log4j
public class FetchIntervalsAndCalculateKpiCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        ReadingKPIContext kpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);
        List<Long> resourceList = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
        Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ReadingKpi.IS_HISTORICAL, false);
        List<ReadingContext> readings = new ArrayList<>();

        Long startTime = null;
        long parentLoggerId;
        if (isHistorical) {
            startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
            parentLoggerId = ReadingKpiLoggerAPI.insertLog(kpi.getId(), KPIType.SCHEDULED.getIndex(), startTime, endTime, false, resourceList.size());
        } else {
            parentLoggerId = ReadingKpiLoggerAPI.insertLog(kpi.getId(), KPIType.SCHEDULED.getIndex(), true, resourceList.size());
        }

        for (Long resourceId : resourceList) {
            if (!isHistorical) {
                ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, kpi.getReadingField());
                startTime = getStartTime(meta.getTtime());
            } else {
                deleteReadings(kpi.getReadingModuleId(), resourceId, startTime, endTime);
            }

            ScheduleInfo schedule = ReadingKpiAPI.getSchedule(kpi.getFrequencyEnum());
            List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);

            if (CollectionUtils.isNotEmpty(intervals)) {
                LOGGER.info("Going to execute scheduled kpi : " + kpi.getId() + "-" + kpi.getName() + " for resource: " + resourceId + " for intervals: " + intervals);
                ReadingKpiLoggerAPI.insertResourceLog(kpi.getId(), parentLoggerId, resourceId, startTime, endTime, isHistorical);
                List<ReadingContext> currentReadings = ReadingKpiAPI.calculateReadingKpi(resourceId, kpi, intervals, startTime);
                if (CollectionUtils.isNotEmpty(currentReadings)) {
                    readings.addAll(currentReadings);
                }
            }
        }
        ReadingKpiLoggerAPI.updateLogWithKpiId(kpi.getId(), COMPLETED.getIndex(), System.currentTimeMillis(), ReadingKpiLoggerAPI.getSuccessCount(parentLoggerId));

        if (!readings.isEmpty()) {
            Unit inputUnit = kpi.getUnit();
            FacilioChain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
            FacilioContext ctx = addReadingChain.getContext();
            ctx.put(FacilioConstants.ContextNames.MODULE_NAME, kpi.getReadingField().getModule().getName());
            ctx.put(FacilioConstants.ContextNames.READINGS, readings);
            ctx.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
            ctx.put(FacilioConstants.ContextNames.FORMULA_INPUT_UNIT_STRING, inputUnit);
            addReadingChain.execute();
        }

        return false;
    }

    private long getStartTime(long lastReadingTime) {
        ZonedDateTime zdt = DateTimeUtil.getDateTime(lastReadingTime).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        return DateTimeUtil.getMillis(zdt, true);
    }

    private void deleteReadings(Long readingModuleId, Long resourceId, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule readingModule = modBean.getModule(readingModuleId);
        List<FacilioField> readingsFields = modBean.getAllFields(readingModule.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(readingsFields);

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(readingModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), Collections.singletonList(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getField("moduleId", "MODULEID", readingModule, FieldType.NUMBER), Collections.singletonList(readingModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("ttime"), startTime + "," + endTime, DateOperators.BETWEEN));
        deleteRecordBuilder.delete();
    }
}
