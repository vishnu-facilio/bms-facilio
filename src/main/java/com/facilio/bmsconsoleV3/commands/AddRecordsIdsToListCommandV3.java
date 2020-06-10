package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddRecordsIdsToListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        ArrayList<Long> recordsIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)) {
            for(ModuleBaseWithCustomFields rec : records) {
                recordsIds.add(rec.getId());
            }
        }
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordsIds);
        return false;
    }
}
