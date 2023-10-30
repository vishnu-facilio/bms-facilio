package com.facilio.multiImport.command;

import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.List;

public class ImportInItCommand extends BaseImportInItCommand {
    @Override
    protected List<ImportRowContext> getBatchRows(Context context) throws Exception {
        Long lastRowIdTaken  = importSheet.getLastRowIdTaken();
        int chunkLimit =(Integer)context.get(FacilioConstants.ContextNames.CHUNK_LIMIT);
        List<ImportRowContext> batchRows = MultiImportApi.getRowsByBatch(importSheet.getId(), lastRowIdTaken, chunkLimit);
        return batchRows;
    }

    @Override
    protected String getModuleName(Context context) throws Exception{
        return importSheet.getModuleName();
    }

    @Override
    protected boolean isOneLevel() {
        return false;
    }
}
