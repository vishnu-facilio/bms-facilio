package com.facilio.bmsconsole.imports.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseImportFileCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        if (MapUtils.isEmpty(importProcessContext.getFieldMapping())) {
            throw new IllegalArgumentException("Field mapping not found");
        }

        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
        HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();

        FileStore fs = FacilioFactory.getFileStore();
        InputStream is = fs.readFile(importProcessContext.getFileId());
        HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
        Workbook workbook = WorkbookFactory.create(is);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ArrayList<FacilioField> requiredFields = (ArrayList<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = getRequiredFields(moduleName);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fieldsList = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);


        workbook.close();

        return false;
    }

    private ArrayList<FacilioField> getRequiredFields(String moduleName) throws Exception{
        ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        for (FacilioField field : allFields) {
            if (field.isRequired()) {
                allFields.add(field);
            }
        }
        return fields;
    }
}
