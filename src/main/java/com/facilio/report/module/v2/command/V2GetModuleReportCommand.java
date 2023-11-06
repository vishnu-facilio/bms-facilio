package com.facilio.report.module.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

public class V2GetModuleReportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long reportId = (Long) context.get("reportId");
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
            context.put("v2_report", v2_reportContext);
        }

        return false;
    }
}
