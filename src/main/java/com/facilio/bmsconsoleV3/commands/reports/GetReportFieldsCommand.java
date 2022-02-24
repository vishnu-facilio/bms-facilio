package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportFactoryFields;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class GetReportFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String moduleName = (String) context.get("moduleName");
        if(moduleName != null)
        {
            JSONObject reportFields =  ReportFactoryFields.getReportFields(moduleName);
            context.put("reportFields", reportFields);
        }
        return false;
    }
}
