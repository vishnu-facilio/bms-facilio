package com.facilio.report.module.v2.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.module.v2.context.V2ModuleContextForDashboardFilter;
import com.facilio.report.module.v2.context.V2ModuleMeasureContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.module.v2.context.V2ModuleTimeFilterContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;
import java.util.Map;

public class V2GetKpiValueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V2ModuleReportContext v2_report = (V2ModuleReportContext) context.get("v2_report");
        V2ModuleContextForDashboardFilter db_filter = (V2ModuleContextForDashboardFilter) context.get("db_filter");
        KPIContext kpi = new KPIContext();
        Criteria kpiCriteria = new Criteria();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(v2_report.getMeasures() != null && v2_report.getMeasures().size() > 0){
            V2ModuleMeasureContext measure = v2_report.getMeasures().get(0);
            FacilioModule kpiModule = modBean.getModule(measure.getModuleName());
            kpi.setAggr(measure.getAggr() > -1 ? AggregateOperator.getAggregateOperator(measure.getAggr()) : BmsAggregateOperators.CommonAggregateOperator.COUNT);
            if(kpiModule != null){
                kpi.setModule(kpiModule);
                kpi.setModuleId(kpiModule.getModuleId());
            }
            if(measure.getCriteria() != null){
                JSONParser parser = new JSONParser();
                JSONObject filter = (JSONObject) parser.parse(measure.getCriteria());
                Criteria criteriaObj = FieldUtil.getAsBeanFromJson(filter,Criteria.class);
                Map<String,Condition> conditionMap = criteriaObj.getConditions();
                for(Map.Entry<String,Condition> entry: conditionMap.entrySet()){
                    Condition condition = entry.getValue();
                    FacilioField field = modBean.getField(condition.getFieldName(),kpiModule.getName());
                    condition.setField(field);
                    condition.setModuleName(field.getModule().getName());
                }
                criteriaObj.setConditions(conditionMap);
                kpiCriteria = criteriaObj;
            }
            if(db_filter != null && db_filter.getDb_user_filter() != null) {
                JSONParser parser = new JSONParser();
                JSONObject userFilter = (JSONObject) parser.parse(db_filter.getDb_user_filter());
                for(Object field: userFilter.keySet()){
                    JSONObject criteria = (JSONObject) userFilter.get(field);
                    Condition condition = new Condition();
                    condition.setField(modBean.getField((String)field, kpiModule.getName()));
                    condition.setModuleName(kpiModule.getName());
                    condition.setValue(getValue(criteria));
                    condition.setOperatorId(((Long) criteria.get("operatorId")).intValue());
                    kpiCriteria.addAndCondition(condition);
                }
            }
            kpi.setCriteria(kpiCriteria);
            kpi.setMetric(modBean.getField(measure.getFieldName(),measure.getModuleName()));
        }
        if(v2_report.getTimeFilter() != null && v2_report.getTimeFilter().getFieldName() != null){
            DateOperators dateOperator = DateOperators.CURRENT_MONTH;
            V2ModuleTimeFilterContext timeFilter = v2_report.getTimeFilter();
            int operatorId = timeFilter.getOperatorId();
            if(db_filter != null && db_filter.getTimeFilter() != null) {
                dateOperator = db_filter.getTimeFilter().dateOperator;
                kpi.setDateValue(db_filter.getTimeFilter().getDateValueString());
            }
            else if(operatorId == DateOperators.BETWEEN.getOperatorId()){
                long startTime = timeFilter.getStartTime();
                long endTime = timeFilter.getEndTime();
                if(startTime > 0 && endTime > 0){
                    String dateValue = new StringBuilder()
                            .append(startTime)
                            .append(",")
                            .append(endTime).toString();
                    dateOperator = (DateOperators) Operator.getOperator(Integer.parseInt(String.valueOf(operatorId)));
                    kpi.setDateValue(dateValue);
                }
            }
            else if(operatorId > 0){
                dateOperator = (DateOperators) Operator.getOperator(Integer.parseInt(String.valueOf(operatorId)));
            }
            FacilioField dateField = modBean.getField(timeFilter.getFieldName(),timeFilter.getModuleName());
            kpi.setDateOperator(dateOperator);
            kpi.setDateFieldName(timeFilter.getFieldName());
            kpi.setDateField(dateField);
        }
        DateOperators cardPeriod = (DateOperators) context.get(FacilioConstants.ContextNames.CARD_PERIOD);
        String baseLine = (String) context.get(FacilioConstants.ContextNames.BASE_LINE);
        if(cardPeriod!=null){
            kpi.setDateOperator(cardPeriod);
            Object value = KPIUtil.getKPIValue(kpi,baseLine);
            if(value instanceof JSONObject){
                JSONObject cardValue  = (JSONObject) value;
                context.put(FacilioConstants.ContextNames.CARD_VALUE,cardValue.get(FacilioConstants.ContextNames.CARD_VALUE));
                context.put(FacilioConstants.ContextNames.BASE_LINE_VALUE,cardValue.get(FacilioConstants.ContextNames.BASE_LINE_VALUE));
            }
            else{
                context.put(FacilioConstants.ContextNames.CARD_VALUE,value);
            }
        }else{
            Object value;
            value = KPIUtil.getKPIValue(kpi);
            context.put("value",value);
        }
        return false;
    }
    private String getValue(JSONObject fieldJson) {
        JSONArray value = (JSONArray) fieldJson.get("value");
        StringBuilder values = new StringBuilder();
        if(value!=null && value.size()>0) {
            boolean isFirst = true;
            Iterator<String> iterator = value.iterator();
            while (iterator.hasNext()) {
                String obj = iterator.next();
                if (!isFirst) {
                    values.append(",");
                } else {
                    isFirst = false;
                }
                values.append(obj);
            }
        }
        return values.toString();
    }
}
