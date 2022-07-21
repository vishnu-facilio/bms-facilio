package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetRecordIdsFromRecordMapCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        List<Long> recordIds = list
                .stream()
                .map(V3WorkOrderContext::getId)
                .filter(id -> id != -1)
                .collect(Collectors.toList());

        if (recordIds.size() > 0) {
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        }

        return false;
    }
}
