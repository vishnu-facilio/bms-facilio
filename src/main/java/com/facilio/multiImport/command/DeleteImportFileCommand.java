package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeleteImportFileCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        Long importFileId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_FILE_ID);
        ImportDataDetails importDataDetails = ImportConstants.getImportDataDetails(context);
        validateAndDeleteImportFile(importFileId, importId,importDataDetails);
        return false;
    }

    private void validateAndDeleteImportFile(long importFileId, long importId,ImportDataDetails importDataDetails) throws Exception {
        List<ImportFileContext> importFilesList = MultiImportApi.getImportFilesByImportId(importId);
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(importFilesList),"No files to delete");
        Map<Long, ImportFileContext> importFileIdvsImportFileMap = importFilesList.stream().collect(Collectors.toMap(ImportFileContext::getId, Function.identity()));
        FacilioUtil.throwIllegalArgumentException(!importFileIdvsImportFileMap.containsKey(importFileId), "ImportFileId does not exists");

        ImportFileContext importFile = importFileIdvsImportFileMap.get(importFileId);
        Long fileId = importFile.getFileId();
        FacilioFactory.getFileStore().deleteFile(fileId);

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getImportFileModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(importFileId, ModuleFactory.getImportFileModule()));

        deleteRecordBuilder.delete();

        if(importFilesList.size()==1){ //if we delete last file means ,need to update import status to UPLOAD_COMPLETED to render add files page in client
            importDataDetails.setStatus(ImportDataStatus.UPLOAD_COMPLETED);
            MultiImportApi.updateImportStatus(importDataDetails);
        }
    }
}
