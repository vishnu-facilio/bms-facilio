package com.facilio.multiImport.command;

import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.List;

public class OneLevelImportInitCommand extends BaseImportInItCommand{

    @Override
    protected List<ImportRowContext> getBatchRows(Context context) throws Exception {
        return (List<ImportRowContext>) context.get(MultiImportApi.ImportProcessConstants.BATCH_ROWS);
    }

    @Override
    protected String getModuleName(Context context) throws Exception {
        return (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
    }

    @Override
    protected boolean isOneLevel() {
        return true;
    }
}
