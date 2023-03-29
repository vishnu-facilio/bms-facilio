package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeleteImportFileCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        Long importFileId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_FILE_ID);
        validateAndDeleteImportFile(importFileId, importId);
        return false;
    }

    private void validateAndDeleteImportFile(long importFileId, long importId) throws Exception {
        List<ImportFileContext> importFilesList = MultiImportApi.getImportFilesByImportId(importId);
        Map<Long, ImportFileContext> importFileIdvsImportFileMap = importFilesList.stream().collect(Collectors.toMap(ImportFileContext::getId, Function.identity()));
        FacilioUtil.throwIllegalArgumentException(!importFileIdvsImportFileMap.containsKey(importFileId), "ImportFileId does not exists");

        ImportFileContext importFile = importFileIdvsImportFileMap.get(importFileId);
        Long fileId = importFile.getFileId();
        FacilioFactory.getFileStore().deleteFile(fileId);

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getImportFileModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(importFileId, ModuleFactory.getImportFileModule()));

        deleteRecordBuilder.delete();

    }
}
