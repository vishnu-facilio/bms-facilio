package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleMetricContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.util.*;

@Log4j
public class GetDataPointFromAlarmCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long alarmId = (long) context.get("alarmId");
        String fields = (String) context.get("fields");
        JSONObject responseJson = new JSONObject();
        if (alarmId > 0 && fields == null) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
                getDataPointFromNewAlarm(context, responseJson);
            } else {
                getDataPointFromAlarm(context, responseJson);
            }
        }
        Iterator<String> keys = responseJson.keySet().iterator();
        while(keys.hasNext()){
            String keyName = keys.next();
            context.put(keyName, responseJson.get(keyName));
        }
        return false;
    }

    private void getDataPointFromNewAlarm(Context context, JSONObject setResponseData) throws Exception {
        AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence((Long) context.get("alarmId"));

        List<ReadingRuleContext> readingRules = new ArrayList<>();
        if ((boolean)context.get("isWithPrerequsite")) {
            ReadingAlarm readingAlarmContext = (ReadingAlarm) alarmOccurrence.getAlarm();
            AlarmRuleContext alarmRuleContext = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(readingAlarmContext.getRule().getId()));
            readingRules.add((ReadingRuleContext) alarmRuleContext.getAlarmTriggerRule());
            readingRules.add(alarmRuleContext.getPreRequsite());
        } else if ((Long) context.get("readingRuleId") > 0) {
            ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule((Long) context.get("readingRuleId"));
            readingRules.add(readingruleContext);
        } else {
            long ruleId = -1;
            if (alarmOccurrence.getAlarm() instanceof ReadingAlarm) {
                ruleId = ((ReadingAlarm) alarmOccurrence.getAlarm()).getRule().getId();
            }
            else if (alarmOccurrence.getAlarm() instanceof MLAnomalyAlarm) {
                MLAnomalyAlarm mlAnomalyAlarm = (MLAnomalyAlarm) alarmOccurrence.getAlarm();
            } else if (alarmOccurrence.getAlarm() instanceof OperationAlarmContext) {
                OperationAlarmContext opAlarm = (OperationAlarmContext) alarmOccurrence.getAlarm();
            } else if (alarmOccurrence.getAlarm() instanceof SensorRollUpAlarmContext) {
                SensorRollUpAlarmContext sensorAlarm = (SensorRollUpAlarmContext) alarmOccurrence.getAlarm();
            }
            if (ruleId > 0) {
                ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
                readingRules.add(readingruleContext);
            }
        }

        setResponseData.put("startTime", (long)context.get("startTime"));
        setResponseData.put("endTime", (long)context.get("endTime"));

        ResourceContext resource = ResourceAPI.getResource(alarmOccurrence.getResource().getId());
        setResponseData.put("alarmResource", resource );
        setResponseData.put("alarmType", alarmOccurrence.getAlarm().getType() );
        JSONArray dataPoints = new JSONArray();
        if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {

            Set readingMap = new HashSet();
            for (ReadingRuleContext readingRule : readingRules) {
                if (readingRule != null) {
                    dataPoints.addAll(getDataPointsJSONFromRule(readingRule, resource, alarmOccurrence, readingMap, setResponseData));
                }
            }

            if (readingRules.get(0).getBaselineId() != -1) {
                JSONArray baselineArray = new JSONArray();
                JSONObject baselineJson = new JSONObject();
                baselineJson.put("baseLineId", readingRules.get(0).getBaselineId());
                baselineArray.add(baselineJson);
//                baseLines = baselineArray.toJSONString();
                setResponseData.put("baseLines", baselineArray.toJSONString());
            }

        } else if (alarmOccurrence.getAlarm() instanceof MLAnomalyAlarm) {
            MLAnomalyAlarm mlAnomalyAlarm = (MLAnomalyAlarm) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForMLAnomalyAlarm(mlAnomalyAlarm, resource));

        } else if (alarmOccurrence.getAlarm() instanceof OperationAlarmContext) {
            OperationAlarmContext opAlarm = (OperationAlarmContext) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForOpAlarm(opAlarm, resource));

        } else if (alarmOccurrence.getAlarm() instanceof SensorRollUpAlarmContext) {
            SensorRollUpAlarmContext sensorAlarm = (SensorRollUpAlarmContext) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForSensorAlarm(sensorAlarm, resource, setResponseData));

        }

        if ((boolean) context.get("newFormat")) {
            long baselineId = -1l;
            if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
                baselineId = readingRules.get(0).getBaselineId();
            }
            ReportUtil.setAliasForDataPoints(dataPoints, baselineId);
        }


        if ((long)context.get("startTime") <= 0 && (long) context.get("endTime") <= 0) {
            long modifiedTime = alarmOccurrence.getCreatedTime();
            if (alarmOccurrence.getLastOccurredTime() > 0) {
                modifiedTime = alarmOccurrence.getLastOccurredTime();
            }

            DateRange range = DateOperators.CURRENT_N_DAY.getRange("" + modifiedTime);
            setResponseData.put("startTime", range.getStartTime());
            setResponseData.put("endTime", range.getEndTime());
        }

        // for ML, aggr value is hourly
        if ((AggregateOperator) context.get("xAggr") != null && (AggregateOperator) context.get("xAggr") != BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY) {
//            setxAggr(0);
            setResponseData.put("xAggr", 0);
        } else {
            for (int i = 0; i < dataPoints.size(); i++) {
                JSONObject json = (JSONObject) dataPoints.get(i);
                JSONObject yAxisJson = (JSONObject) json.get("yAxis");
                yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
            }
        }
        List<Object> fieldId = new ArrayList<>();
        // removing duplicate field Id in alarm report
        for (int i = 0; i < dataPoints.size(); i++) {
            JSONObject json = (JSONObject) dataPoints.get(i);
            JSONObject yAxisJson = (JSONObject) json.get("yAxis");
            if (fieldId.contains(yAxisJson.get("fieldId"))) {
                dataPoints.remove(i);
            } else {
                fieldId.add(yAxisJson.get("fieldId"));
            }
        }
        setResponseData.put("fields", dataPoints.toJSONString());
        context.put("response", setResponseData);
    }

    private JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext, ResourceContext resource, AlarmOccurrenceContext alarm, Set readingMap, JSONObject responseJson) throws Exception {
        JSONArray dataPoints = new JSONArray();
        ResourceContext currentResource = resource;

        if (readingruleContext.getRuleMetrics() != null && !readingruleContext.getRuleMetrics().isEmpty()) {

            for (ReadingRuleMetricContext ruleMetric : readingruleContext.getRuleMetrics()) {
                long resourceId = resource.getId();
                if (ruleMetric.getResourceId() > 0) {
                    resourceId = ruleMetric.getResourceId();
                }
                JSONObject dataPoint = new JSONObject();

                JSONObject yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", ruleMetric.getFieldId());
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resourceId));

                dataPoints.add(dataPoint);
            }

            return dataPoints;
        }

        if (readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {


            if (readingruleContext.getWorkflowId() > 0) {

                WorkflowContext workflow = new WorkflowContext();
                FacilioModule module = ModuleFactory.getWorkflowModule();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(FieldFactory.getWorkflowFields())
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(readingruleContext.getWorkflowId(), module));

                List<Map<String, Object>> props = selectBuilder.get();

                WorkflowContext workflowContext = null;
                if (props != null && !props.isEmpty() && props.get(0) != null) {
                    Map<String, Object> prop = props.get(0);
                    boolean isWithExpParsed = true;

                    workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowContext.class);
                    if (workflowContext.isV2Script()) {
                        if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.XML.getValue()) {
                            workflowContext = WorkflowUtil.getWorkflowContextFromString(workflowContext.getWorkflowString(), workflowContext);
                            if (isWithExpParsed) {
                                WorkflowUtil.parseExpression(workflowContext);
                            }
                        } else if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.GUI.getValue()) {
                            workflowContext.parseScript();
                        }
                        workflow = workflowContext;
                    } else {
                        workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);
                    }
                }

                for (WorkflowExpression workflowExp : workflow.getExpressions()) {

                    if (!(workflowExp instanceof com.facilio.workflows.context.ExpressionContext)) {
                        continue;
                    }
                    com.facilio.workflows.context.ExpressionContext exp = (ExpressionContext) workflowExp;
                    if (exp.getModuleName() != null) {

                        JSONObject dataPoint = new JSONObject();

                        FacilioField readingField = null;
                        if (exp.getFieldName() != null) {
                            readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());

                            updateTimeRangeAsPerFieldType(readingField.getFieldId(), responseJson);

                            JSONObject yAxisJson = new JSONObject();
                            yAxisJson.put("fieldId", readingField.getFieldId());
                            yAxisJson.put("aggr", 0);

                            dataPoint.put("yAxis", yAxisJson);

                        }
                        if (exp.getCriteria() != null) {
                            Map<String, Condition> conditions = exp.getCriteria().getConditions();

                            for (String key : conditions.keySet()) {

                                Condition condition = conditions.get(key);

                                if (condition.getFieldName().equals("parentId")) {
                                    resource = condition.getValue().equals("${resourceId}") ? currentResource : ResourceAPI.getResource(Long.parseLong(condition.getValue()));

                                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                                    break;
                                }
                            }
                        }
                        dataPoint.put("type", 1);
                        if (!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
                            readingMap.add(resource.getId() + "_" + readingField.getFieldId());
                            dataPoints.add(dataPoint);
                        }
                    }
                }
            }
        } else if (readingruleContext.getReadingFieldId() > 0 && (readingruleContext.getWorkflow() != null || readingruleContext.getCriteria() != null)) {
            JSONObject dataPoint = new JSONObject();

            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

            JSONObject yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
            updateTimeRangeAsPerFieldType(readingruleContext.getReadingFieldId(), responseJson);
            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);
            dataPoints.add(dataPoint);
        }
        return dataPoints;
    }

    private void updateTimeRangeAsPerFieldType(long fieldId, JSONObject responseJson) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField readingField = modBean.getField(fieldId);
        if (readingField != null) {
            FormulaFieldContext formulaField = FormulaFieldAPI.getFormulaField(readingField);

            if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.DAILY) {
                responseJson.put("startTime", DateTimeUtil.addDays((Long)responseJson.get("endTime"), -10));
            } else if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.MONTHLY) {
                responseJson.put("startTime", DateTimeUtil.addDays((Long)responseJson.get("endTime"), -10));
            } else if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.WEEKLY) {
                responseJson.put("startTime", DateTimeUtil.addDays((Long)responseJson.get("endTime"), -10));
            }
        }
        LOGGER.error("1.this.startTime --- " + (long)responseJson.get("startTime"));
        LOGGER.error("2.this.endTime --- " + (long)responseJson.get("endTime"));
    }

    private JSONArray getDataPointsJSONForMLAnomalyAlarm(MLAnomalyAlarm mlAlarm, ResourceContext resource) throws Exception {
        JSONArray dataPoints = new JSONArray();
        JSONObject dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        JSONObject yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", mlAlarm.getEnergyDataFieldid());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);
        dataPoints.add(dataPoint);

        dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", mlAlarm.getUpperAnomalyFieldid());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);

        dataPoints.add(dataPoint);

        return dataPoints;

    }

    private JSONArray getDataPointsJSONForOpAlarm(OperationAlarmContext opAlarm, ResourceContext resource) throws Exception {
        JSONArray dataPoints = new JSONArray();
        JSONObject dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        JSONObject yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", opAlarm.getReadingFieldId());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);
        dataPoints.add(dataPoint);

        return dataPoints;
    }
    private Collection getDataPointsJSONForSensorAlarm(SensorRollUpAlarmContext sensorAlarm, ResourceContext resource, JSONObject responseJson) throws Exception {
        JSONArray dataPoints = new JSONArray();

        List<SensorAlarmContext> sensorAlarms = AlarmAPI.getSensorChildAlarms(sensorAlarm, (long)responseJson.get("startTime"), (long) responseJson.get("endTime"));
        if (sensorAlarms != null && !sensorAlarms.isEmpty()) {
            List<Long> duplication = new ArrayList<>();
            for (SensorAlarmContext senAlarm : sensorAlarms) {
                JSONObject dataPoint = new JSONObject();

                if (duplication.isEmpty() || !duplication.contains(senAlarm.getReadingFieldId())) {

                    ResourceContext sensorResource = senAlarm.getResource();

                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(sensorResource.getId()));

                    JSONObject yAxisJson = new JSONObject();
                    yAxisJson.put("fieldId", senAlarm.getReadingFieldId());
                    duplication.add(senAlarm.getReadingFieldId());

                    yAxisJson.put("aggr", 0);

                    dataPoint.put("yAxis", yAxisJson);

                    dataPoint.put("type", 1);
                    dataPoints.add(dataPoint);
                }

            }
        } else if (sensorAlarm.getReadingFieldId() > 0 && (dataPoints == null || dataPoints.isEmpty())) {
            ResourceContext sensorResource = sensorAlarm.getResource();

            JSONObject dataPoint = new JSONObject();
            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(sensorResource.getId()));

            JSONObject yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", sensorAlarm.getReadingFieldId());

            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);
            dataPoints.add(dataPoint);

        }

        return dataPoints;
    }

    private void getDataPointFromAlarm(Context context, JSONObject responseJson) throws Exception {

        long alarmId = (long) context.get("alarmId");
        boolean isWithPrerequsite = (boolean) context.get("isWithPrerequsite");
        long readingRuleId = (long) context.get("readingRuleId");
        AlarmContext alarmContext = AlarmAPI.getReadingAlarmContext(alarmId);

        if (alarmContext == null) {
            alarmContext = AlarmAPI.getMLAlarmContext(alarmId);
        }

        List<ReadingRuleContext> readingRules = new ArrayList<>();
        boolean isAnomalyAlarm = false;
        if (isWithPrerequsite) {                            // new 1st

            ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarmContext;
            AlarmRuleContext alarmRuleContext = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(readingAlarmContext.getRuleId()));
            readingRules.add((ReadingRuleContext) alarmRuleContext.getAlarmTriggerRule());
            readingRules.add(alarmRuleContext.getPreRequsite());

        } else if (readingRuleId > 0) {                    // new 2nd

            ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId);
            readingRules.add(readingruleContext);
        } else {                                            // old
            long ruleId = -1;

            if (alarmContext instanceof ReadingAlarmContext) {
                ruleId = ((ReadingAlarmContext) alarmContext).getRuleId();
            } else if (alarmContext instanceof MLAlarmContext) {
                ruleId = ((MLAlarmContext) alarmContext).getRuleId();
            }
            if (ruleId > 0) {
                ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
                readingRules.add(readingruleContext);
            } else {
                isAnomalyAlarm = true;
            }
        }
        responseJson.put("startTime", (long) context.get("startTime"));
        responseJson.put("endTime", (long) context.get("endTime"));

        ResourceContext resource = ResourceAPI.getResource(alarmContext.getResource().getId());
        responseJson.put("alarmResource", resource);

        JSONArray dataPoints = new JSONArray();

        if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {

            for (ReadingRuleContext readingRule : readingRules) {
                if (readingRule != null) {
                    dataPoints.addAll(getDataPointsJSONFromRule(readingRule, resource, alarmContext, responseJson));
                }
            }

            if (readingRules.get(0).getBaselineId() != -1) {
                JSONArray baselineArray = new JSONArray();
                JSONObject baselineJson = new JSONObject();
                baselineJson.put("baseLineId", readingRules.get(0).getBaselineId());
                baselineArray.add(baselineJson);
                responseJson.put("baseLines", baselineArray.toJSONString());
            }

        } else if (isAnomalyAlarm) {

            JSONObject dataPoint = new JSONObject();

            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

            JSONObject yAxisJson = new JSONObject();
            ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarmContext;
            yAxisJson.put("fieldId", readingAlarmContext.getReadingFieldId());
            updateTimeRangeAsPerFieldType(readingAlarmContext.getReadingFieldId(), responseJson);
            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);

            dataPoints.add(dataPoint);

        }
        String additionalDataPointString = "anomalyreadings";
        if (alarmContext != null && alarmContext.getAdditionInfo() != null && alarmContext.getAdditionInfo().containsKey(additionalDataPointString)) {

            responseJson.put("startTime", DateTimeUtil.getDayStartTimeOf(alarmContext.getCreatedTime()));        // specific handling for
            dataPoints = new JSONArray();                                                        // anomaly alarms

            JSONArray points = FacilioUtil.parseJsonArray(alarmContext.getAdditionInfo().get(additionalDataPointString).toString());

            for (int i = 0; i < points.size(); i++) {
                long fieldId = Long.parseLong(points.get(i).toString());

                JSONObject dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                JSONObject yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", fieldId);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);
            }

        }
        if (alarmId == 890083l) {
            LOGGER.error("new data point json -- " + dataPoints);
        }
        if ((boolean) context.get("newFormat")) {
            long baselineId = -1l;
            if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
                baselineId = readingRules.get(0).getBaselineId();
            }
            ReportUtil.setAliasForDataPoints(dataPoints, baselineId);
        }


        if ((long) responseJson.get("startTime") <= 0 && (long) responseJson.get("endTime") <= 0) {
            long modifiedTime = alarmContext.getCreatedTime();
            if (alarmContext.getModifiedTime() > 0) {
                modifiedTime = alarmContext.getModifiedTime();
            }

            DateRange range = DateOperators.CURRENT_N_DAY.getRange("" + modifiedTime);

            responseJson.put("startTime", range.getStartTime());
            responseJson.put("endTime", range.getEndTime());
        }
        responseJson.put("xAggr", 0);
        responseJson.put("fields", dataPoints.toJSONString());
        context.put("response", responseJson);
    }

    private JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext, ResourceContext resource, AlarmContext alarmContext, JSONObject responseJson) throws Exception {

        JSONArray dataPoints = new JSONArray();
        if (readingruleContext.getReadingRuleTypeEnum() == ReadingRuleContext.ReadingRuleType.ML_RULE) {

            JSONObject dataPoint = new JSONObject();

            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

            JSONObject yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);

            ZonedDateTime zdt = DateTimeUtil.getDateTime(alarmContext.getCreatedTime());
            zdt = zdt.truncatedTo(new SecondsChronoUnit(60 * 60));
            DateTimeUtil.getMillis(zdt, true);

            dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true));

            dataPoints.add(dataPoint);

            dataPoint = new JSONObject();

            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

            yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);

            zdt = DateTimeUtil.getDateTime(alarmContext.getModifiedTime());
            zdt = zdt.truncatedTo(new SecondsChronoUnit(1 * 60 * 60));
            if (alarmContext.getClearedTime() > 0) {
                dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true) - (6 * 3600000));
            } else {
                dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true));
            }
            dataPoints.add(dataPoint);

            if (readingruleContext.getId() == 5085l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253613l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 7504l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 517774l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8165l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253677l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8749l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253686l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8756l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253612l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8761l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253725l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8766l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 351301l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8771l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253738l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            zdt = DateTimeUtil.getDateTime(alarmContext.getCreatedTime());
            zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
            responseJson.put("startTime", DateTimeUtil.getMillis(zdt, true));

            zdt = DateTimeUtil.getDateTime(alarmContext.getModifiedTime() + 432000000);
            zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
            responseJson.put("endTime", DateTimeUtil.getMillis(zdt, true));

            LOGGER.error("dataPoints -- " + dataPoints);
            LOGGER.error("startTime -- " + responseJson.get("startTime"));
            LOGGER.error("startTime -- " + responseJson.get("endTime"));
            return dataPoints;
        }

        ResourceContext currentResource = resource;
        if (readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {

            Set readingMap = new HashSet();
            if (readingruleContext.getWorkflowId() > 0) {
                WorkflowContext workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);

                for (WorkflowExpression workflowExp : workflow.getExpressions()) {

                    if (!(workflowExp instanceof ExpressionContext)) {
                        continue;
                    }
                    ExpressionContext exp = (ExpressionContext) workflowExp;
                    if (exp.getModuleName() != null) {

                        JSONObject dataPoint = new JSONObject();

                        FacilioField readingField = null;
                        if (exp.getFieldName() != null) {
                            readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());

                            updateTimeRangeAsPerFieldType(readingField.getFieldId(), responseJson);

                            JSONObject yAxisJson = new JSONObject();
                            yAxisJson.put("fieldId", readingField.getFieldId());
                            yAxisJson.put("aggr", 0);

                            dataPoint.put("yAxis", yAxisJson);

                        }
                        if (exp.getCriteria() != null) {
                            Map<String, Condition> conditions = exp.getCriteria().getConditions();

                            for (String key : conditions.keySet()) {

                                Condition condition = conditions.get(key);

                                if (condition.getFieldName().equals("parentId")) {
                                    resource = condition.getValue().equals("${resourceId}") ? currentResource : ResourceAPI.getResource(Long.parseLong(condition.getValue()));

                                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                                    break;
                                }
                            }
                        }
                        dataPoint.put("type", 1);
                        if (!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
                            readingMap.add(resource.getId() + "_" + readingField.getFieldId());
                            dataPoints.add(dataPoint);
                        }
                    }
                }
            }
        } else if (readingruleContext.getReadingFieldId() > 0 && (readingruleContext.getWorkflow() != null || readingruleContext.getCriteria() != null)) {
            JSONObject dataPoint = new JSONObject();

            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

            JSONObject yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
            updateTimeRangeAsPerFieldType(readingruleContext.getReadingFieldId(), responseJson);
            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);
            dataPoints.add(dataPoint);
        }
        return dataPoints;
    }
}
