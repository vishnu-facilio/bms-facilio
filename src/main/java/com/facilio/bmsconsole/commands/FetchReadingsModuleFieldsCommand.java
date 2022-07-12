package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class FetchReadingsModuleFieldsCommand extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        List<Long> recordList = records.stream().map(row -> row.getId()).collect(Collectors.toList());

        ModuleBean modBean = Constants.getModBean();
        List<FacilioModule> readingModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.READING, FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA);

        for(FacilioModule subModule : readingModules) {
            List<FacilioField> fields = modBean.getAllFields(subModule.getName());
            subModule.setFields(fields);
        }
        context.put(FacilioConstants.ContextNames.MODULE_LIST, readingModules);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordList);
        return false;
    }
}