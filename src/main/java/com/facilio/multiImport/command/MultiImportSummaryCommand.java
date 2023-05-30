package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.Arrays;

public class MultiImportSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        MultiImportApi.fillImportFilesInfo(importDataDetails);

        context.put(FacilioConstants.ContextNames.IMPORT_LIST, Arrays.asList(importDataDetails));
        return false;
    }
}
