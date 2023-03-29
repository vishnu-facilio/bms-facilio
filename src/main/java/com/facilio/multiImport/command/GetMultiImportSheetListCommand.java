package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetMultiImportSheetListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        List<ImportFileSheetsContext> importFileContextList = MultiImportApi.getSheetsListByImportId(importId, false);

        context.put(FacilioConstants.ContextNames.IMPORT_SHEETS, importFileContextList);
        return false;
    }

}
