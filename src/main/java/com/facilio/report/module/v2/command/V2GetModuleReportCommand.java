package com.facilio.report.module.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportContext;
import com.facilio.report.module.v2.context.V2ModuleContextForDashboardFilter;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
            FacilioModule module = modBean.getModule(report.getModuleId());
            if(module != null){
                v2_reportContext.setModuleName(module.getName());
            }
            v2_reportContext.setAppId(report.getAppId());
            v2_reportContext.setName(report.getName());
            v2_reportContext.setFolderId(report.getReportFolderId());
            v2_reportContext.setDescription(report.getDescription());
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
