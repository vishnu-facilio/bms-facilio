package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.MultiImportField;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetMultiImportFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        List<MultiImportField> multiImportField = MultiImportApi.getMultiImportFieldsList(moduleName);

        context.put(FacilioConstants.ContextNames.FIELDS,multiImportField);

        return false;
    }

}
