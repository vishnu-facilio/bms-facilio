package com.facilio.report.module.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.report.module.v2.context.V2ModuleContextForDashboardFilter;
import com.facilio.report.module.v2.context.V2ModuleFilterContext;
import com.facilio.report.module.v2.context.V2ModuleGroupByContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class V2GetModuleReportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long reportId = (Long) context.get("reportId");
        V2ModuleContextForDashboardFilter db_filter = (V2ModuleContextForDashboardFilter) context.get("db_filter");
        ReportContext report = ReportUtil.getReport(reportId);
        context.put(FacilioConstants.ContextNames.REPORT, report);
        V2ModuleReportContext v2_reportContext = V2AnalyticsOldUtil.getV2ModuleReport(reportId);
        if(v2_reportContext != null){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = null;
            FacilioModule module = modBean.getModule(report.getModuleId());
            if(module != null){
                v2_reportContext.setModuleName(module.getName());
                moduleName = module.getName();
            }
            String lookupModuleName = v2_reportContext.getDimensions()!=null ? v2_reportContext.getDimensions().getLookupModuleName() : null;
            if(moduleName!=null && lookupModuleName!=null){
                JSONObject reportFields = ReportFactoryFields.getReportFields(moduleName);
                Map<String,String> lookupModuleMap = reportFields!=null ?(Map<String, String>) reportFields.get("lookupModuleMap") : new HashMap<>();
                lookupModuleName = lookupModuleName.equals("Reading Occurrence") ? "Fault Occurrence" : lookupModuleName;
                if(!lookupModuleName.contains("__")){
                    if(lookupModuleMap!=null && lookupModuleMap.containsKey(lookupModuleName)){
                        lookupModuleName = lookupModuleMap.get(lookupModuleName);
                    }
                    v2_reportContext.getDimensions().setLookupModuleName(lookupModuleName);
                }
                V2ModuleGroupByContext groupBy = v2_reportContext.getGroupBy();
                if(groupBy!=null){
                    String groupByModuleName = groupBy.getModuleName();
                    String groupByFieldName = groupBy.getFieldName();
                    String groupByLookupModuleName = groupBy.getLookupModuleName();
                    if(groupByLookupModuleName==null && groupByFieldName!=null && groupByModuleName!=null){
                        Map<String, List<FacilioField>> dimensions = (Map<String, List<FacilioField>>) reportFields.get("newDimension");
                        for(Map.Entry<String,List<FacilioField>> dimension : dimensions.entrySet()){
                            List<FacilioField> fieldList = dimensions.get(dimension.getKey());
                            List<FacilioField> matchedFields = fieldList.stream().filter(field-> field.getName().equals(groupByFieldName) && field.getModule().getName().equals(groupByModuleName)).collect(Collectors.toList());
                            if(matchedFields!=null && matchedFields.size()>0){
                                groupBy.setLookupModuleName(dimension.getKey());
                                break;
                            }
                        }
                    }
                }
            }

            if(report.getBaseLines() != null) {
                JSONArray baseLines = new JSONArray();
                for(ReportBaseLineContext baseLine : report.getBaseLines()) {
                    JSONObject baseLineObj = new JSONObject();
                    baseLineObj.put("baseLineId",baseLine.getBaseLineId());
                    baseLines.add(baseLineObj);
                }
                v2_reportContext.setBaseLines(baseLines.toJSONString());
            }
            if(report.getReportSettings() != null) {
                v2_reportContext.setReportSettings(report.getReportSettings());
            }
            v2_reportContext.setAppId(report.getAppId());
            v2_reportContext.setName(report.getName());
            v2_reportContext.setFolderId(report.getReportFolderId());
            v2_reportContext.setDescription(report.getDescription());
            if(v2_reportContext.getCriteriaId() != null && v2_reportContext.getCriteriaId() > 0) {
                Criteria globalCriteria = CriteriaAPI.getCriteria(v2_reportContext.getCriteriaId());
                V2ModuleFilterContext filters = new V2ModuleFilterContext();
                filters.setGlobalCriteria(globalCriteria);
                v2_reportContext.setFilters(filters);
                report.setCriteria(globalCriteria);
            }
            if(db_filter != null)
            {
                if(db_filter.getTimeFilter() != null && db_filter.getTimeFilter().getStartTime() > 0 && db_filter.getTimeFilter().getEndTime() > 0) {
                    report.setDateRange(new DateRange(db_filter.getTimeFilter().getStartTime(), db_filter.getTimeFilter().getEndTime()));
                    report.setDateOperator(DateOperators.BETWEEN);
                }
                if(db_filter.getDb_user_filter() != null && !db_filter.getDb_user_filter().equals("")){
                    context.put(FacilioConstants.ContextNames.MODULE_NAME,report.getModule().getName());
                    JSONParser parser = new JSONParser();
                    JSONObject filter = (JSONObject) parser.parse(db_filter.getDb_user_filter());
                    context.put(FacilioConstants.ContextNames.FILTERS, filter);
                }
            }
            context.put("v2_report", v2_reportContext);
        }

        return false;
    }
}
