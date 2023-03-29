package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;


public class GetMultiImportFileListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        List<ImportFileContext> importFileContextList = MultiImportApi.getImportFilesByImportId(importId);

        checkSheeetMappingDependencies(importFileContextList);

        MultiImportApi.setFileSize(importFileContextList);

        context.put(FacilioConstants.ContextNames.IMPORT_FILE_LIST, importFileContextList);

        return false;
    }

    private void checkSheeetMappingDependencies(List<ImportFileContext> importFileContextList) {
        if (CollectionUtils.isEmpty(importFileContextList)) {
            return;
        }
        for (ImportFileContext importFile : importFileContextList) {
            List<ImportFileSheetsContext> importFileSheetsContextList = importFile.getImportFileSheetsContext();
            boolean hasSheetMappingDependencies = false;
            for (ImportFileSheetsContext sheetsContext : importFileSheetsContextList) {
                if (sheetsContext.getModuleId() != -1) {
                    hasSheetMappingDependencies = true;
                    break;
                }
            }
            importFile.setHasSheetMappingDependencies(hasSheetMappingDependencies);
        }
    }

}
