package com.facilio.readingkpi.commands;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.NumberField;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class FetchIntervalsAndCalculateKpiCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
        ReadingKPIContext kpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);
        List<Long> resourceList = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
        Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ReadingKpi.IS_HISTORICAL, false);
        List<ReadingContext> readings = new ArrayList<>();
        for (Long resourceId : resourceList) {
            Long startTime;
            if (isHistorical==false) {
                ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, kpi.getReadingField());
                startTime = getStartTime(meta.getTtime());
                ReadingKpiAPI.insertLog(kpi.getId(), resourceId, startTime, endTime, false);
            } else {
                startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
                bean.deleteReadings(kpi.getOrgId(), kpi.getReadingFieldId(),resourceId,startTime,endTime,kpi.getAssetCategoryId(),kpi.getReadingModuleId());
            }
            ScheduleInfo schedule = ReadingKpiAPI.getSchedule(kpi.getFrequencyEnum());
            List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);
            LOGGER.info("Going to execute scheduled kpi : " + kpi.getName() + "for intervals: " + intervals);
            List<ReadingContext> currentReadings = ReadingKpiAPI.calculateReadingKpi(resourceId, kpi, intervals, isHistorical);
            if (CollectionUtils.isNotEmpty(currentReadings)) {
                readings.addAll(currentReadings);
            }
        }
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
}
