package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3WorkPermitContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetRecordIdsFromRecordMapCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = new ArrayList<>();
        List<V3WorkPermitContext> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)){
            for(V3WorkPermitContext wp : list){
                recordIds.add(wp.getId());
            }
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        }
        return false;
    }
}
