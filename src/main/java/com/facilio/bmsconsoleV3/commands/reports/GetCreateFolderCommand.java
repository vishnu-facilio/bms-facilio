package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetCreateFolderCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String actionType = (String) context.get("actionType");
        ReportFolderContext reportFolder = (ReportFolderContext)  context.get("reportFolder");
        if(reportFolder != null) {
            if (actionType != null && actionType.equals("ADD")) {
                String moduleName = (String) context.get("moduleName");
                if (moduleName != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(moduleName);
                    reportFolder.setModuleId(module.getModuleId());

                    GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                            .select(FieldFactory.getReport1FolderFields())
                            .table(ModuleFactory.getReportFolderModule().getTableName())
                            .andCustomWhere("NAME = ?", reportFolder.getName());
                    List<Map<String, Object>> props = select.get();
                    if (props != null && props.size() > 0) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, new StringBuilder().append("Folder with name ").append(reportFolder.getName()).append(" already exists. Please choose different name.").toString());
                    }
                    reportFolder = ReportUtil.addReportFolder(reportFolder);
                    context.put("reportFolder", reportFolder);
                }
            } else if (actionType != null && actionType.equals("UPDATE")) {
                ReportUtil.updateReportFolder(reportFolder);
                context.put("reportFolder", reportFolder);
            } else if (actionType != null && actionType.equals("DELETE")) {
                List<Map<String, Object>> reports = ReportUtil.getReportFromFolderId(reportFolder.getId());
                if (reports == null || reports.isEmpty()) {
                    ReportUtil.deleteReportFolder(reportFolder);
                } else {
                    context.put("isReportExists", true);
                }
            }
        }

        return false;
    }
}
