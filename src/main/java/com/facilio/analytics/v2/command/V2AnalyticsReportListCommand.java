package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsFolderResponseContext;
import com.facilio.analytics.v2.context.V2AnalyticsReportResponseContext;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class V2AnalyticsReportListCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(V2AnalyticsReportListCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String orderBy = (String) context.get(FacilioConstants.ContextNames.ORDER_TYPE);
        Boolean withCount = (Boolean) context.get(FacilioConstants.ContextNames.WITH_COUNT);
        Long folderId = (Long) context.get("folderId");
        int reportType = (int) context.get("reportType");
        ReportContext.ReportType reportType1 = null;
        if(reportType > 0){
            reportType1 = ReportContext.ReportType.valueOf(reportType);
        }
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        context.put("reports",this.getAnalyticsReports(context , folderId, pagination, orderBy, withCount, appId, reportType1));
        return false;
    }

    private Object getAnalyticsReports(Context context, Long folderId, JSONObject pagination , String orderBy, Boolean withCount, Long appId, ReportContext.ReportType reportType) throws Exception

    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GenericSelectRecordBuilder select = this.getAnalyticReportSelectQuery(appId, reportType, V2AnalyticsOldUtil.getFieldsForReportList());

        if(folderId != null && folderId > 0){
            select.andCondition(CriteriaAPI.getCondition("REPORT_FOLDER_ID", "reportFolderId", folderId+"", NumberOperators.EQUALS));
        }
        select = V2AnalyticsOldUtil.getPaginatedSelectBuilder(select, pagination, ModuleFactory.getReportModule(), orderBy);
        if(withCount != null && withCount)
        {
            GenericSelectRecordBuilder countSelect = this.getAnalyticReportSelectQuery(appId, reportType, Collections.EMPTY_LIST);
            if(folderId != null && folderId > 0){
                countSelect.andCondition(CriteriaAPI.getCondition("REPORT_FOLDER_ID", "reportFolderId", folderId+"", NumberOperators.EQUALS));
            }
            countSelect.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(ModuleFactory.getReportModule()));
            List<Map<String, Object>> countRecord = countSelect.get();
            context.put(Constants.COUNT, countRecord.get(0).get("id"));
        }
        Boolean isUserPrevileged = AccountUtil.getCurrentUser().getRole().getIsPrevileged();
        Boolean isShareEnabled = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.REPORT_SHARE);
        List<Map<String, Object>> props = select.get();
        if(props != null && props.size() > 0)
        {
            List<V2AnalyticsReportResponseContext> response_report_list = FieldUtil.getAsBeanListFromMapList(props, V2AnalyticsReportResponseContext.class);
            List<V2AnalyticsReportResponseContext> filteredReports = response_report_list.stream()
                    .filter(report -> {
                        FacilioModule reportModule = null;
                        try {
                            reportModule = modBean.getModule(report.getModuleId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        report.setModuleName( reportModule.getName());
                        report.setModuleDisplayName( reportModule.getDisplayName());
                        if ((isUserPrevileged == null && isShareEnabled)) {
                            try {
                                return V2AnalyticsOldUtil.getAnalyticsReportSharing(report.getId());
                            } catch (Exception e) {
                                LOGGER.info("issue while setting module in report response object");
                            }
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            return filteredReports;
        }
        return null;
    }

    private GenericSelectRecordBuilder getAnalyticReportSelectQuery(Long appId, ReportContext.ReportType reportType, List<FacilioField> fields)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getReportModule().getTableName())
                .innerJoin(ModuleFactory.getV2ReportModule().getTableName())
                .on(new StringBuilder().append(ModuleFactory.getReportModule().getTableName()).append(".ID = ").append(ModuleFactory.getV2ReportModule().getTableName()).append(".REPORT_ID").toString())
                .andCondition(CriteriaAPI.getCondition(new StringBuilder(ModuleFactory.getReportModule().getTableName()).append(".APP_ID").toString(), "appId", appId+"", NumberOperators.EQUALS));
                if(reportType != null){
                    select.andCondition(CriteriaAPI.getCondition(new StringBuilder(ModuleFactory.getReportModule().getTableName()).append(".REPORT_TYPE").toString(), "type", reportType.getValue()+"", NumberOperators.EQUALS));
                }
        return select;
    }

}
