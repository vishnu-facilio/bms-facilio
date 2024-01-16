package com.facilio.analytics.v2.command;

import com.clickhouse.data.value.UnsignedLong;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;

public class V2ConstructHeatMapResponseCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(V2ConstructHeatMapResponseCommand.class.getName());
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");
    private boolean isClickhouseEnabled=false;
    @Override
    public boolean executeCommand(Context context) throws Exception {

        isClickhouseEnabled = (Boolean) context.get("isClickHouseEnabled");
        List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
        V2ReportContext report_v2 = context.get("report_v2") != null ? (V2ReportContext) context.get("report_v2") : (V2ReportContext) context.get("v2_report");
        ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        boolean isClickhouseAggrTableEnabled = (Boolean) context.get("clickhouse");
        if(isClickhouseAggrTableEnabled && reportContext.getDataPoints() != null && reportContext.getDataPoints().size() == 1 && report_v2.getMeasures().get(0).getHmAggr() != null)
        {
            for (ReportDataContext data : reportData)
            {
                Map<String, List<Map<String, Object>>> reportProps = data.getProps();
                if (reportProps != null && !reportProps.isEmpty())
                {
                    for (ReportDataPointContext dataPoint : data.getDataPoints())
                    {
                        for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet())
                        {
                            List<Map<String, Object>> props = entry.getValue();
                            if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
                                constructHeatMapData(context,reportContext,report_v2, dataPoint, props);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private void constructHeatMapData(Context context, ReportContext report,V2ReportContext v2_report, ReportDataPointContext dataPoint, List<Map<String, Object>> props)throws Exception
    {
        if(props != null && !props.isEmpty())
        {
            List<HashMap<String, Object>>  temp_map = new ArrayList<>();
            for (Map<String, Object> prop : props)
            {
                Object heatmap_ttime = prop.get(dataPoint.getxAxis().getField().getName());
                Object heatmap_y = prop.get("heatmap_y");
                if (heatmap_ttime != null) {
                    String hmAggr = v2_report.getMeasures().get(0).getHmAggr();
                    Object ttime_val=null;
                    if(hmAggr.equals("hours")){
                        ttime_val = formatVal(dataPoint.getxAxis(), BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY, heatmap_ttime);
                    }else if(hmAggr.equals("days")){
                        ttime_val = formatVal(dataPoint.getxAxis(), BmsAggregateOperators.DateAggregateOperator.FULLDATE, heatmap_ttime);
                    }else if(hmAggr.equals("weeks")){
                        ttime_val = formatVal(dataPoint.getxAxis(), BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR, heatmap_ttime);
                    }
                    Object heatmap_y_val = formatVal(dataPoint.getxAxis(), report.getxAggrEnum(), heatmap_y, true);
                    Object yVal = prop.get(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
                    if (yVal != null) {
                        yVal = dataPoint.getDynamicKpi() != null ? DECIMAL_FORMAT.format(yVal) : formatVal(dataPoint.getyAxis(), dataPoint.getyAxis().getAggrEnum(), yVal);
                    }
                    HashMap<String, Object> result_Map = new HashMap<>();
                    temp_map.add(fillMap(ttime_val, heatmap_y_val, yVal, dataPoint.getAliases().get("actual")));
                }
            }
            context.put("heatMapData",constructReportDataJson(dataPoint.getAliases().get("actual"), report, v2_report,temp_map));
        }
    }
    private HashMap<String, Object> fillMap(Object ttime_val, Object heatmap_y, Object val, String alias)
    {
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("X", ttime_val);
        metaData.put(alias, val);
        metaData.put("Y", heatmap_y);
        return metaData;
    }
    private List<Map<String, Object>> constructReportDataJson(String alias, ReportContext report, V2ReportContext v2_report, List<HashMap<String, Object>> data)throws Exception
    {
        List<Map<String, Object>> heatMapData = new ArrayList<>();
        if(v2_report != null)
        {
            long startTime = report.getDateRange().getStartTime();
            long endTime = report.getDateRange().getEndTime();

            String hmAggr = v2_report.getMeasures().get(0).getHmAggr();
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy MM dd HH");
            SimpleDateFormat hourly_sdf = new java.text.SimpleDateFormat("MM HH");
            SimpleDateFormat weekly_sdf = new java.text.SimpleDateFormat("ww HH");
            SimpleDateFormat year_hourly_sdf = new java.text.SimpleDateFormat("yyyy HH");
            SimpleDateFormat quarterly_sdf = new java.text.SimpleDateFormat("HH");
            SimpleDateFormat yearAndDay_sdf = new java.text.SimpleDateFormat("yyyy dd");
            SimpleDateFormat quarterAndDay_sdf = new java.text.SimpleDateFormat("dd");
            SimpleDateFormat year_week_Day_sdf = new java.text.SimpleDateFormat("ww dd");

            HashMap reportDataMap= new HashMap();
            HashMap monthAndHourlyReportDataMap= new HashMap();
            HashMap yearAndHourlyReportDataMap= new HashMap();
            HashMap quarterlyReportDataMap= new HashMap();
            HashMap weeklyReportDataMap= new HashMap();
            HashMap yearAndDayReportDataMap= new HashMap();
            HashMap quarterAndDayReportDataMap= new HashMap();
            HashMap yearWeekAndDayReportDataMap= new HashMap();
            for(Map<String, Object> record : data)
            {
                Date recordDate = new java.util.Date(((long) record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS)));
                reportDataMap.put(sdf.format(recordDate), record);
                monthAndHourlyReportDataMap.put(hourly_sdf.format(recordDate), record);
                yearAndHourlyReportDataMap.put(year_hourly_sdf.format(recordDate), record);
                weeklyReportDataMap.put(weekly_sdf.format(recordDate), record);
                LocalDateTime myLocal = recordDate.toInstant().atZone(DBConf.getInstance().getCurrentZoneId()).toLocalDateTime();
                int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
                quarterlyReportDataMap.put(quarter + " "+ quarterly_sdf.format(recordDate), record);
                //day related map
                yearAndDayReportDataMap.put(yearAndDay_sdf.format(recordDate), record);
                LocalDate yearAndDayLocal = recordDate.toInstant().atZone(DBConf.getInstance().getCurrentZoneId()).toLocalDate();
                int yearAndDayQuarter = yearAndDayLocal.get(IsoFields.QUARTER_OF_YEAR);
                quarterAndDayReportDataMap.put(yearAndDayQuarter + " "+ quarterAndDay_sdf.format(recordDate), record);
                yearWeekAndDayReportDataMap.put(year_week_Day_sdf.format(recordDate), record);
            }

            ZonedDateTime startDateTime = DateTimeUtil.getZonedDateTime(startTime);
            ZonedDateTime endDateTime = DateTimeUtil.getZonedDateTime(endTime);

            while(!startDateTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
            {
                if(hmAggr.equals("hours") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.FULLDATE)
                {
                    long hourStartTime = DateTimeUtil.getHourStartTimeOf(startDateTime.toEpochSecond() *1000);
                    HashMap mapData= new HashMap();
                    mapData.put("X", hourStartTime);
                    mapData.put("Y",hourStartTime);

                    if(reportDataMap.containsKey(sdf.format(hourStartTime)))
                    {
                        mapData = (HashMap) reportDataMap.get(sdf.format(hourStartTime));
                    }
                    heatMapData.add(mapData);
                    startDateTime = startDateTime.plusHours(1);
                }
                else if(hmAggr.equals("hours") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR)
                {
                    long weekStartTime = DateTimeUtil.getDayStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long weekEndTime = DateTimeUtil.getDayEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDateTime().isAfter(weekEndZonedTime.toLocalDateTime()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long hourStartTime = DateTimeUtil.getHourStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData = new HashMap();
                        mapData.put("X", hourStartTime);
                        mapData.put("Y", hourStartTime);

                        if (weeklyReportDataMap.containsKey(weekly_sdf.format(hourStartTime))) {
                            mapData = (HashMap) weeklyReportDataMap.get(weekly_sdf.format(hourStartTime));
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusHours(1);
                    }
                    startDateTime = startDateTime.plusWeeks(1);
                }
                else if(hmAggr.equals("hours") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR)
                {
                    long monthStartTime = DateTimeUtil.getDayStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long monthEndTime = DateTimeUtil.getDayEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime monthStartZonedTime = DateTimeUtil.getZonedDateTime(monthStartTime);
                    ZonedDateTime monthEndZonedTime = DateTimeUtil.getZonedDateTime(monthEndTime);
                    while(!monthStartZonedTime.toLocalDateTime().isAfter(monthEndZonedTime.toLocalDateTime()))
                    {
                        long hourStartTime = DateTimeUtil.getHourStartTimeOf(monthStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData = new HashMap();
                        mapData.put("X", hourStartTime);
                        mapData.put("Y", hourStartTime);

                        if (monthAndHourlyReportDataMap.containsKey(hourly_sdf.format(hourStartTime))) {
                            mapData = (HashMap) monthAndHourlyReportDataMap.get(hourly_sdf.format(hourStartTime));
                        }
                        heatMapData.add(mapData);
                        monthStartZonedTime = monthStartZonedTime.plusHours(1);
                    }
                    startDateTime = startDateTime.plusMonths(1);
                }
                else if(hmAggr.equals("hours") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.QUARTERLY)
                {
                    long monthStartTime = DateTimeUtil.getDayStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long monthEndTime = DateTimeUtil.getDayEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime monthStartZonedTime = DateTimeUtil.getZonedDateTime(monthStartTime);
                    ZonedDateTime monthEndZonedTime = DateTimeUtil.getZonedDateTime(monthEndTime);
                    while(!monthStartZonedTime.toLocalDateTime().isAfter(monthEndZonedTime.toLocalDateTime()))
                    {
                        long hourStartTime = DateTimeUtil.getHourStartTimeOf(monthStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData = new HashMap();
                        mapData.put("X", hourStartTime);
                        mapData.put("Y", hourStartTime);
                        Date recordDate = new Date(hourStartTime);
                        LocalDateTime myLocal = recordDate.toInstant().atZone(DBConf.getInstance().getCurrentZoneId()).toLocalDateTime();
                        int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
                        String key = new StringBuilder().append(quarter+"").append(" ").append(quarterly_sdf.format(recordDate)).toString();
                        if (quarterlyReportDataMap.containsKey(key)) {
                            mapData = (HashMap) quarterlyReportDataMap.get(key);
                            if(!isClickhouseEnabled) {
                                mapData.put("Y", hourStartTime);
                            }
                        }
                        heatMapData.add(mapData);
                        monthStartZonedTime = monthStartZonedTime.plusHours(1);
                    }
                    startDateTime = DateTimeUtil.getQuarterStartTimeOf(startDateTime.plusMonths(3));
                }
                else if(hmAggr.equals("hours") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.YEAR)
                {
                    long monthStartTime = DateTimeUtil.getDayStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long monthEndTime = DateTimeUtil.getDayEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime monthStartZonedTime = DateTimeUtil.getZonedDateTime(monthStartTime);
                    ZonedDateTime monthEndZonedTime = DateTimeUtil.getZonedDateTime(monthEndTime);
                    while(!monthStartZonedTime.toLocalDateTime().isAfter(monthEndZonedTime.toLocalDateTime()))
                    {
                        long hourStartTime = DateTimeUtil.getHourStartTimeOf(monthStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData = new HashMap();
                        mapData.put("X", hourStartTime);
                        mapData.put("Y", hourStartTime);

                        if (yearAndHourlyReportDataMap.containsKey(year_hourly_sdf.format(hourStartTime))) {
                            mapData = (HashMap) yearAndHourlyReportDataMap.get(year_hourly_sdf.format(hourStartTime));
                        }
                        heatMapData.add(mapData);
                        monthStartZonedTime = monthStartZonedTime.plusHours(1);
                    }
                    startDateTime = startDateTime.plusYears(1);
                }
                else if(hmAggr.equals("days") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR)
                {
                    long weekStartTime = DateTimeUtil.getWeekStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long weekEndTime = DateTimeUtil.getWeekEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDate().isAfter(weekEndZonedTime.toLocalDate()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long weekDayStartTime = DateTimeUtil.getDayStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData= new HashMap();
                        mapData.put("X", weekDayStartTime);
                        mapData.put("Y",weekDayStartTime);

                        if (reportDataMap.containsKey(sdf.format(weekDayStartTime))) {
                            mapData = (HashMap) reportDataMap.get(sdf.format(weekDayStartTime));
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusDays(1);
                    }
                    startDateTime = startDateTime.plusWeeks(1);
                }
                else if(hmAggr.equals("days") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR)
                {
                    long weekStartTime = DateTimeUtil.getMonthStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long weekEndTime = DateTimeUtil.getMonthEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDate().isAfter(weekEndZonedTime.toLocalDate()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long weekDayStartTime = DateTimeUtil.getDayStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData= new HashMap();
                        mapData.put("X", weekDayStartTime);
                        mapData.put("Y",weekDayStartTime);

                        if (reportDataMap.containsKey(sdf.format(weekDayStartTime))) {
                            mapData = (HashMap) reportDataMap.get(sdf.format(weekDayStartTime));
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusDays(1);
                    }
                    startDateTime = startDateTime.plusMonths(1);
                }
                else if(hmAggr.equals("days") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.QUARTERLY)
                {
                    long weekStartTime = DateTimeUtil.getQuarterStartTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime quarterStartOfMonthZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    long weekEndTime = DateTimeUtil.getMonthEndTimeOf(quarterStartOfMonthZonedTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDate().isAfter(weekEndZonedTime.toLocalDate()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long weekDayStartTime = DateTimeUtil.getDayStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData= new HashMap();
                        mapData.put("X", weekDayStartTime);
                        mapData.put("Y",weekDayStartTime);
                        Date recordDate = new Date(weekDayStartTime);
                        LocalDate yearAndDayLocal = recordDate.toInstant().atZone(DBConf.getInstance().getCurrentZoneId()).toLocalDate();
                        int yearAndDayQuarter = yearAndDayLocal.get(IsoFields.QUARTER_OF_YEAR);
                        String key = new StringBuilder().append(yearAndDayQuarter+" ").append(quarterAndDay_sdf.format(weekDayStartTime)).toString();
                        if (quarterAndDayReportDataMap.containsKey(key)) {
                            mapData = (HashMap) quarterAndDayReportDataMap.get(key);
                            if(!isClickhouseEnabled) {
                                mapData.put("Y", weekDayStartTime);
                            }
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusDays(1);
                    }
                    int year = quarterStartOfMonthZonedTime.getYear();
                    int month = quarterStartOfMonthZonedTime.getMonthValue();
                    YearMonth yearMonth = YearMonth.of(year, month);
                    int daysInMonth = yearMonth.lengthOfMonth();
                    if(daysInMonth == 30)
                    {
                        Date recordDate = new Date(weekStartTime);
                        LocalDate yearAndDayLocal = recordDate.toInstant().atZone(DBConf.getInstance().getCurrentZoneId()).toLocalDate();
                        int yearAndDayQuarter = yearAndDayLocal.get(IsoFields.QUARTER_OF_YEAR);
                        String key = new StringBuilder().append(yearAndDayQuarter+" ").append("31").toString();
                        HashMap mapData=new HashMap();
                        mapData.put("X", DateTimeUtil.getMonthEndTimeOf(weekStartZonedTime.toEpochSecond() * 1000));
                        mapData.put("Y", DateTimeUtil.getMonthEndTimeOf(weekStartZonedTime.toEpochSecond() * 1000));
                        if (quarterAndDayReportDataMap.containsKey(key)) {
                            mapData = (HashMap) quarterAndDayReportDataMap.get(key);
                        }
                        heatMapData.add(mapData);
                    }
                    startDateTime = startDateTime.plusMonths(3);
                }
                else if(hmAggr.equals("days") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.YEAR)
                {
                    long weekStartTime = DateTimeUtil.getMonthStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long weekEndTime = DateTimeUtil.getMonthEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDate().isAfter(weekEndZonedTime.toLocalDate()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long weekDayStartTime = DateTimeUtil.getDayStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData= new HashMap();
                        mapData.put("X", weekDayStartTime);
                        mapData.put("Y",weekDayStartTime);

                        if (yearAndDayReportDataMap.containsKey(yearAndDay_sdf.format(weekDayStartTime))) {
                            mapData = (HashMap) yearAndDayReportDataMap.get(yearAndDay_sdf.format(weekDayStartTime));
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusDays(1);
                    }
                    startDateTime = startDateTime.plusYears(1);
                }
                else if(hmAggr.equals("weeks") && report.getxAggrEnum() == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR)
                {
                    long weekStartTime = DateTimeUtil.getWeekStartTimeOf(startDateTime.toEpochSecond() *1000);
                    long weekEndTime = DateTimeUtil.getWeekEndTimeOf(startDateTime.toEpochSecond() *1000);
                    ZonedDateTime weekStartZonedTime = DateTimeUtil.getZonedDateTime(weekStartTime);
                    ZonedDateTime weekEndZonedTime = DateTimeUtil.getZonedDateTime(weekEndTime);
                    while(!weekStartZonedTime.toLocalDate().isAfter(weekEndZonedTime.toLocalDate()) && !weekStartZonedTime.toLocalDate().isAfter(endDateTime.toLocalDate()))
                    {
                        long weekDayStartTime = DateTimeUtil.getDayStartTimeOf(weekStartZonedTime.toEpochSecond() * 1000);
                        HashMap mapData= new HashMap();
                        mapData.put("X", weekDayStartTime);
                        mapData.put("Y",weekDayStartTime);
                        if (yearWeekAndDayReportDataMap.containsKey(year_week_Day_sdf.format(weekDayStartTime))) {
                            mapData = (HashMap) yearWeekAndDayReportDataMap.get(year_week_Day_sdf.format(weekDayStartTime));
                        }
                        heatMapData.add(mapData);
                        weekStartZonedTime = weekStartZonedTime.plusDays(1);
                    }
                    startDateTime = startDateTime.plusWeeks(1);
                }
                else
                {
                    LOGGER.debug("provided aggr and xaggr combination is not supported in heatmap"+ hmAggr + " AND "+ report.getxAggrEnum());
                    break;
                }
            }
        }
        return heatMapData;
    }

    private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val) throws Exception {
        return formatVal(reportFieldContext, aggr, val, false);
    }
    private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val, boolean isHeatMapYFormat) throws Exception {
        FacilioField field = reportFieldContext.getField();
        if (val == null) {
            return "";
        }
        if(isHeatMapYFormat && val instanceof String)
        {
            SimpleDateFormat format = null;
            if(aggr == BmsAggregateOperators.DateAggregateOperator.FULLDATE) {
                format = new SimpleDateFormat("yyyy MM dd");
            }else if(aggr == BmsAggregateOperators.DateAggregateOperator.WEEKANDYEAR){
                format = new SimpleDateFormat("yyyy ww");
            }else if(aggr == BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR){
                format = new SimpleDateFormat("yyyy MM");
            }
            else if(aggr == BmsAggregateOperators.DateAggregateOperator.YEAR)
            {
                format = new SimpleDateFormat("yyyy");
            }
            Date date = format.parse((String) val);
            val = date.getTime();
            System.out.print(date);
        }else if(isHeatMapYFormat && aggr == BmsAggregateOperators.DateAggregateOperator.QUARTERLY){
            return val;
        }
        switch (field.getDataTypeEnum()) {
            case DECIMAL:
                val = DECIMAL_FORMAT.format(val);
                break;
            case DATE:
            case DATE_TIME:
                if (aggr != null && aggr instanceof BmsAggregateOperators.DateAggregateOperator) {
                    if(val instanceof UnsignedLong){
                        val = ((UnsignedLong) val).longValue();
                    }
                    val = ((BmsAggregateOperators.DateAggregateOperator) aggr).getAdjustedTimestamp((long) val);
                }
                break;
            default:
                break;
        }
        return val;
    }
}

