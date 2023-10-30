package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.MultiImportField;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetMultiImportFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Boolean newVersion = (Boolean) context.get("newVersion");

        if(newVersion){
            Map<ImportFieldMappingType,List<MultiImportField>> typeVsFields = MultiImportApi.getMultiImportFieldMap(moduleName);
            context.put(FacilioConstants.ContextNames.FIELDS,typeVsFields);
        }
        else {
            List<MultiImportField> multiImportField = MultiImportApi.getMultiImportFieldsList(moduleName,true);
            context.put(FacilioConstants.ContextNames.FIELDS,multiImportField);
        }

        return false;
    }

}
