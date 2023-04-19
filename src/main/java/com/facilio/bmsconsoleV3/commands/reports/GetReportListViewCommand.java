package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;


public class GetReportListViewCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception{
        boolean isCustomModule = (boolean) context.get("isCustomModule");
        boolean isPivot = (boolean) context.get("isPivot");
        String name = AccountUtil.getCurrentApp().getLinkName();
        boolean isMainApp =  name.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        long folderId = (long) context.get("folderId");
        List<FacilioField> selectableFields = FetchAllReportField();
        List<ReportContext> reports = new ArrayList<>();
        if (!isCustomModule) {
            reports = ReportUtil.getReportsFromFolderIdNew(folderId, selectableFields, isMainApp, context);
        }
        else {
            reports = ReportUtil.getReportsFromFolderIdNew(folderId);
        }
        context.put("reportsList", reports);
        return false;
    }
    private List<FacilioField> FetchAllReportField() throws Exception {
        FacilioModule reportModule = ModuleFactory.getReportModule();
        List<FacilioField> SelectFields = new ArrayList<>();

        List<FacilioField> fields = FieldFactory.getReport1Fields();
        SelectFields.add(FieldFactory.getIdField(reportModule));
        SelectFields.add(FieldFactory.getSiteIdField(reportModule));
        SelectFields.add(FieldFactory.getField("reportFolderId", "REPORT_FOLDER_ID", reportModule, FieldType.LOOKUP));
        SelectFields.add(FieldFactory.getField("name", "NAME", reportModule, FieldType.STRING));
        SelectFields.add(FieldFactory.getField("description", "DESCRIPTION", reportModule, FieldType.STRING));
        SelectFields.add(FieldFactory.getField("type", "REPORT_TYPE", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getField("analyticsType", "ANALYTICS_TYPE", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getModuleIdField(reportModule));
        SelectFields.add(FieldFactory.getField("createdTime", "CREATED_TIME", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getField("modifiedTime", "MODIFIED_TIME", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getField("createdBy", "CREATED_BY", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getField("modifiedBy", "MODIFIED_BY", reportModule, FieldType.NUMBER));
        SelectFields.add(FieldFactory.getField("appId", "APP_ID", reportModule, FieldType.NUMBER));

        return SelectFields;
    }
}
