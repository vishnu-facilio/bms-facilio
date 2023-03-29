package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

public class AbortMultiImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);

        importDataDetails.setStatus(ImportDataStatus.ABORTED);
        MultiImportApi.updateImportStatus(importDataDetails);
        return false;
    }
}
