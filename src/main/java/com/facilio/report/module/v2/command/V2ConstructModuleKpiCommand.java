package com.facilio.report.module.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.module.v2.context.*;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V2ConstructModuleKpiCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V2ModuleReportContext v2_report = (V2ModuleReportContext)  context.get("v2_report");
        ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        String moduleName = v2_report.getModuleName();
        reportContext = reportContext == null ? new ReportContext() : reportContext;
        reportContext.setType(ReportContext.ReportType.WORKORDER_REPORT);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        context.put("ModuleDisplayName", module.getDisplayName());
        context.put(FacilioConstants.ContextNames.MODULE, module);
        reportContext.setId(v2_report.getId() > 0 ? v2_report.getId() : -1l);
        reportContext.setAppId(v2_report.getAppId() > 0 ? v2_report.getAppId() : AccountUtil.getCurrentApp().getId());
        reportContext.setModuleId(module.getModuleId());
        reportContext.setName(v2_report.getName());
        reportContext.setReportFolderId(v2_report.getFolderId());
        reportContext.setDescription(v2_report.getDescription());
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);

        return false;
    }
}
