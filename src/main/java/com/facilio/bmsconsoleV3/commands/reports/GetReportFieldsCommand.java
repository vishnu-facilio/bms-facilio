package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportFactoryFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.list.SetUniqueList;
import org.json.simple.JSONObject;

import java.util.*;

public class GetReportFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        String moduleName = (String) context.get("moduleName");
        if(moduleName != null)
        {
            JSONObject reportFields =  ReportFactoryFields.getReportFields(moduleName);
            Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>) reportFields.get("dimension");
            if(dimensionFieldMap != null) {
                for(String alias: dimensionFieldMap.keySet()) {
                    List<FacilioField> dimensionFields = dimensionFieldMap.get(alias);
                    List<FacilioField> uniqueFields = SetUniqueList.decorate(dimensionFields);
                    dimensionFieldMap.put(alias, uniqueFields);
                }
                reportFields.put("dimension",dimensionFieldMap);
            }
            context.put("reportFields", reportFields);
        }
        return false;
    }
}
