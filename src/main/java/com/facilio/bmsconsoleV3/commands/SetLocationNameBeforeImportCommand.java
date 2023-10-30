package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class SetLocationNameBeforeImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> locations = insertRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if (CollectionUtils.isEmpty(locations)) {
            return false;
        }

        for(Pair<Long,ModuleBaseWithCustomFields> entry : locations){
            Long logId = entry.getKey();
            ImportRowContext rowContext = logIdVsImportRows.get(logId);
            if(rowContext.isErrorOccurredRow()){
               continue;
            }
            LocationContext locationContext = (LocationContext) entry.getValue();
            if(StringUtils.isEmpty(locationContext.getName())){
                locationContext.setName("import_location_"+logId);
            }
        }

        return false;
    }
}
