package com.facilio.report.module.v2.command;

import org.json.simple.JSONObject;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsMeasureContext;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.module.v2.context.V2ModuleMeasureContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import org.apache.commons.chain.Context;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

public class V2AddNewModuleReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        V2ModuleReportContext v2_report = (V2ModuleReportContext) context.get("v2_report");
        String actionType = (String) context.get("actionType");
        Boolean isKpi = (Boolean) context.get("isKpi");
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        if(actionType != null && actionType.equals(FacilioConstants.ContextNames.CREATE) && report != null && report.getId() > 0 && v2_report != null){
            v2_report.setReportId(report.getId());
            if(isKpi){
                generateAndSetMeasureCriteria(v2_report,actionType);
            }
            addNewReportV2(v2_report,isKpi);
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.UPDATE) && report != null && report.getId() > 0 && v2_report != null){
            v2_report.setReportId(report.getId());
            if(isKpi){
                generateAndSetMeasureCriteria(v2_report,actionType);
            }
            updateNewReportV2(v2_report,isKpi);
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.DELETE)){
            Long reportId = (Long) context.get(FacilioConstants.ContextNames.REPORT_ID);
            deleteNewReportV2(reportId);
        }
        return false;
    }
    private void addNewReportV2(V2ModuleReportContext reportContext, boolean isKpi) throws Exception
    {
        reportContext.setKpi(isKpi);
        if(reportContext.getFilters() != null && reportContext.getFilters().getGlobalCriteria() != null){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = reportContext.getFilters().getGlobalCriteria();
            FacilioModule module = modBean.getModule(reportContext.getModuleName());
                for (String key : criteria.getConditions().keySet())
                {
                    Condition condition = criteria.getConditions().get(key);
                    if (module == null || condition == null || condition.getFieldName() == null) continue;
                    FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
                    if(field == null) continue;
                    condition.setField(field);
                }
            long globalCriteriaId = CriteriaAPI.addCriteria(criteria);
            reportContext.setCriteriaId(globalCriteriaId);
        }
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .fields(FieldFactory.getV2ReportModuleFields());
        Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
        insertBuilder.insert(props);

        List<V2AnalyticsMeasureContext> context_list = V2AnalyticsOldUtil.getV2ModuleMeasureContexts(reportContext.getMeasures(), reportContext.getReportId());
        if(context_list.size() > 0) {
            List<Map<String, Object>> measures_list = FieldUtil.getAsMapList(context_list, V2AnalyticsMeasureContext.class);
            for(Map<String,Object> measure_prop : measures_list) {
                V2AnalyticsOldUtil.addReportMeasures(measure_prop);
            }
        }
    }
    private void updateNewReportV2(V2ModuleReportContext reportContext, boolean isKpi) throws Exception
    {
        reportContext.setKpi(isKpi);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .fields(FieldFactory.getV2ReportModuleFields())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportContext.getReportId()+"", NumberOperators.EQUALS));
        Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
        updateBuilder.update(props);

        if (reportContext.getReportId() > 0){
            V2AnalyticsOldUtil.updateModuleReportMeasures(reportContext);
        }
    }
    private V2ModuleReportContext getV2Report(Long reportId)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getV2ReportModuleFields())
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", String.valueOf(reportId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = select.get();
        if (props != null && !props.isEmpty())
        {
            return FieldUtil.getAsBeanFromMap(props.get(0), V2ModuleReportContext.class);
        }
        return null;
    }
    private void deleteNewReportV2(Long id) throws Exception
    {
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", id+"", NumberOperators.EQUALS));
        deleteBuilder.delete();
    }
    private void generateAndSetMeasureCriteria(V2ModuleReportContext reportContext, String actionType)throws Exception
    {
        for(V2ModuleMeasureContext measure: reportContext.getMeasures())
        {
            if(measure.getCriteria() != null && !measure.equals("")){
                if(actionType != null && actionType.equals("update") && measure.getCriteriaId() > 0)
                {
                    CriteriaAPI.deleteCriteria(measure.getCriteriaId());
                    measure.setCriteriaId(-1l);
                }
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule reportModule = modBean.getModule(measure.getModuleName());
                JSONParser parser = new JSONParser();
                JSONObject filter = (JSONObject) parser.parse(measure.getCriteria());
                Criteria criteriaObj = FieldUtil.getAsBeanFromJson(filter,Criteria.class);
                Map<String, Condition> conditionMap = criteriaObj.getConditions();
                for(Map.Entry<String,Condition> entry: conditionMap.entrySet()){
                    Condition condition = entry.getValue();
                    FacilioField field = modBean.getField(condition.getFieldName(),reportModule.getName());
                    condition.setField(field);
                    condition.setModuleName(field.getModule().getName());
                }
                criteriaObj.setConditions(conditionMap);
                long criteriaId = V2AnalyticsOldUtil.generateCriteriaId(criteriaObj, measure.getModuleName());
                if (criteriaId > 0) {
                    measure.setCriteriaId(criteriaId);
                    measure.setCriteria(null);
                }
            }
        }
    }
}
