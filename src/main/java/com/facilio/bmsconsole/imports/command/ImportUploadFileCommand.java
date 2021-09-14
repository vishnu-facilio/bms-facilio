package com.facilio.bmsconsole.imports.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import java.io.File;

public class ImportUploadFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        String fileUploadFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
        File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
        String fileUploadContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);

        if (fileUpload == null) {
            throw new IllegalArgumentException("Invalid file to upload");
        }

        Workbook workbook = WorkbookFactory.create(fileUpload);
        if (workbook.getNumberOfSheets() > 1) {
            throw new IllegalArgumentException("Uploaded File contains more than one Sheet");
        }

        FileStore fs = FacilioFactory.getFileStore();
        long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);

        Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);

        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT);
        if (siteId != null && siteId > 0) {
            importProcessContext.setSiteId(siteId);
        }
        importProcessContext.setFileId(fileId);
        ImportAPI.getColumnHeadings(workbook, importProcessContext);

        JSONObject firstRow = ImportAPI.getFirstRow(workbook);
        workbook.close();
        importProcessContext.setfirstRow(firstRow);
        importProcessContext.setFirstRowString(firstRow.toString());

        // import mode is automatically taken using module type
        // TODO check whether this is correct or not
        if (module.getTypeEnum() == FacilioModule.ModuleType.READING) {
            importProcessContext.setImportMode(ImportProcessContext.ImportMode.READING.getValue());
        } else {
            importProcessContext.setImportMode(ImportProcessContext.ImportMode.NORMAL.getValue());
        }

        if (module.getModuleId() > 0) {
            importProcessContext.setModuleId(module.getModuleId());
        } else {
            importProcessContext.setModuleName(module.getName());
        }
        importProcessContext.setStatus(ImportProcessContext.ImportStatus.UPLOAD_COMPLETE.getValue());
        importProcessContext.setImportTime(DateTimeUtil.getCurrenTime());
        importProcessContext.setImportType(ImportProcessContext.ImportType.EXCEL.getValue());
        importProcessContext.setUploadedBy(AccountUtil.getCurrentUser().getOuid());

        if (importProcessContext.getImportSetting() == null) {
            importProcessContext.setImportSetting(ImportProcessContext.ImportSetting.INSERT.getValue());
        }

        // todo omitted assetId, templateId, and moduleMeta tags
        ImportAPI.addImportProcess(importProcessContext);

        ImportAPI.getFieldMapping(importProcessContext);    // this may not get saved in DB

        return false;
    }
}
