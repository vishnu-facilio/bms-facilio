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
import com.facilio.report.module.v2.context.V2ModuleMeasureContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;

public class V2GetKpiValueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V2ModuleReportContext v2_report = (V2ModuleReportContext) context.get("v2_report");
        KPIContext kpi = new KPIContext();
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
                kpi.setCriteria(criteriaObj);
            }
            kpi.setMetric(modBean.getField(measure.getFieldName(),measure.getModuleName()));
        }
        if(v2_report.getTimeFilter() != null && v2_report.getTimeFilter().getFieldName() != null){
            DateOperators dateOperator = DateOperators.CURRENT_MONTH;
            if(v2_report.getTimeFilter().getOperatorId() > 0){
                dateOperator = (DateOperators) Operator.getOperator(Integer.parseInt(String.valueOf(v2_report.getTimeFilter().getOperatorId())));
            }
            FacilioField dateField = modBean.getField(v2_report.getTimeFilter().getFieldName(),v2_report.getTimeFilter().getModuleName());
            kpi.setDateOperator(dateOperator);
            kpi.setDateFieldName(v2_report.getTimeFilter().getFieldName());
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
}
