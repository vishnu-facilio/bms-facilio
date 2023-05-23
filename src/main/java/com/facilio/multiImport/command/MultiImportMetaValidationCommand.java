package com.facilio.multiImport.command;

import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class MultiImportMetaValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        FacilioUtil.throwIllegalArgumentException(importId == -1L, "Import Id is Empty");
        ImportDataDetails importDataDetails = MultiImportApi.getImportData(importId);
        FacilioUtil.throwIllegalArgumentException(importDataDetails == null, "Import doesn't exists");

        context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);
        return false;
    }
}

