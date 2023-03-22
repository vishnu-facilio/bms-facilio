package com.facilio.bmsconsole.imports.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class V3AddOrUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isEmpty(moduleName)) {
            moduleName = importProcessContext.getModuleName();
        }
        if (StringUtils.isEmpty(moduleName)) {
            throw new IllegalArgumentException("Module name is empty");
        }
        FacilioModule module = modBean.getModule(moduleName);

        List<Map<String, Object>> insertRecordList = (List<Map<String, Object>>) context.get(ImportAPI.ImportProcessConstants.INSERT_RECORDS);
        if (CollectionUtils.isNotEmpty(insertRecordList)) {
            V3Util.createRecordList(module, insertRecordList, null, null);
        }
        List<Map<String, Object>> updateRecordList = (List<Map<String, Object>>) context.get(ImportAPI.ImportProcessConstants.UPDATE_RECORDS);
        if (CollectionUtils.isNotEmpty(updateRecordList)) {
            List<ModuleBaseWithCustomFields> oldRecords =
                    (List<ModuleBaseWithCustomFields>) context.get(ImportAPI.ImportProcessConstants.OLD_RECORDS);
            V3Util.processAndUpdateBulkRecords(module, oldRecords, updateRecordList, null, null, null, null, null, null, null, true);
        }
        return false;
    }
}
