package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsFolderResponseContext;
import com.facilio.analytics.v2.context.V2AnalyticsReportResponseContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FetchReportDataCommand;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFactory;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
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
        context.put("reportFolders",this.getAnalyticsFolders(context , folderId, pagination, orderBy, withCount));
        return false;
    }
    private List<V2AnalyticsFolderResponseContext> getAnalyticsFolders(Context context, Long folderId, JSONObject pagination, String orderBy, Boolean withCount) throws Exception
    {
        List<V2AnalyticsFolderResponseContext>  folder_list = null;
        Long appId = AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1l;
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getReport1FolderFields())
                .table(ModuleFactory.getReportFolderModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", appId+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", module.getModuleId()+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", ReportFolderContext.FolderType.PIVOT.getValue()+"", NumberOperators.NOT_EQUALS));
        if(folderId != null && folderId > 0){
            select.andCondition(CriteriaAPI.getCondition("ID", "id", folderId+"", NumberOperators.EQUALS));
        }

        List<Map<String, Object>> props = select.get();
        if(props != null && props.size() > 0)
        {
            folder_list = FieldUtil.getAsBeanListFromMapList(props, V2AnalyticsFolderResponseContext.class);
            this.getAnalyticsReports(context, folderId, folder_list, pagination, orderBy, withCount);
        }
        return folder_list;
    }
    private void getAnalyticsReports(Context context, Long folderId, List<V2AnalyticsFolderResponseContext> folders, JSONObject pagination , String orderBy, Boolean withCount) throws Exception
    {
        Long appId = AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1l;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
        GenericSelectRecordBuilder select = this.getAnalyticReportSelectQuery(appId, module.getModuleId(), ReportContext.ReportType.READING_REPORT, V2AnalyticsOldUtil.getFieldsForReportList());

        if(folderId != null && folderId > 0){
            select.andCondition(CriteriaAPI.getCondition("REPORT_FOLDER_ID", "reportFolderId", folderId+"", NumberOperators.EQUALS));
        }
        select = V2AnalyticsOldUtil.getPaginatedSelectBuilder(select, pagination, ModuleFactory.getReportModule(), orderBy);
        if(withCount != null && withCount)
        {
            GenericSelectRecordBuilder countSelect = this.getAnalyticReportSelectQuery(appId, module.getModuleId(), ReportContext.ReportType.READING_REPORT, Collections.EMPTY_LIST);
            if(folderId != null && folderId > 0){
                select.andCondition(CriteriaAPI.getCondition("REPORT_FOLDER_ID", "reportFolderId", folderId+"", NumberOperators.EQUALS));
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

            Map<Long, List<V2AnalyticsReportResponseContext>> grouped_reports = filteredReports.stream()
                    .collect(Collectors.groupingBy(V2AnalyticsReportResponseContext::getReportFolderId));

            for(V2AnalyticsFolderResponseContext folder: folders)
            {
                if(grouped_reports.containsKey(folder.getId()))
                {
                    folder.setReports(grouped_reports.get(folder.getId()));
                }
            }
        }
    }

    private GenericSelectRecordBuilder getAnalyticReportSelectQuery(Long appId, Long moduleId, ReportContext.ReportType reportType, List<FacilioField> fields)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getReportModule().getTableName())
                .innerJoin(ModuleFactory.getV2ReportModule().getTableName())
                .on(new StringBuilder().append(ModuleFactory.getReportModule().getTableName()).append(".ID = ").append(ModuleFactory.getV2ReportModule().getTableName()).append(".REPORT_ID").toString())
                .andCondition(CriteriaAPI.getCondition(new StringBuilder(ModuleFactory.getReportModule().getTableName()).append(".APP_ID").toString(), "appId", appId+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(new StringBuilder(ModuleFactory.getReportModule().getTableName()).append(".MODULEID").toString(), "moduleId", moduleId+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(new StringBuilder(ModuleFactory.getReportModule().getTableName()).append(".REPORT_TYPE").toString(), "type", reportType.getValue()+"", NumberOperators.EQUALS));

        return select;
    }

}
