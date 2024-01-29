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
            List<String> reqFields = Arrays.asList("dimension","lookupModuleMap","moduleMap","moduleType","parentlookupFileds");
            JSONObject reportFields =  ReportFactoryFields.getReportFields(moduleName);
            JSONObject reqReportFields = new JSONObject();
            for(Object key : reportFields.keySet()){
                if(!reqFields.contains(key)){
                    reqReportFields.put(key,reportFields.get(key));
                }
            }
            Map<String, List<FacilioField>> dimensionFieldMap = (Map<String, List<FacilioField>>) reportFields.get("newDimension");
            if(dimensionFieldMap != null) {
                for(String alias: dimensionFieldMap.keySet()) {
                    List<FacilioField> dimensionFields = dimensionFieldMap.get(alias);
                    List<FacilioField> uniqueFields = SetUniqueList.decorate(dimensionFields);
                    dimensionFieldMap.put(alias, uniqueFields);
                }
                reqReportFields.put("newDimension",dimensionFieldMap);
            }
            context.put("reportFields", reqReportFields);
        }
        return false;
    }
}
