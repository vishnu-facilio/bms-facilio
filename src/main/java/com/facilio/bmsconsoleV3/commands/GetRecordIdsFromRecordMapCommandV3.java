package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetRecordIdsFromRecordMapCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = new ArrayList<>();
        List<ModuleBaseWithCustomFields> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)){
            for(ModuleBaseWithCustomFields rec : list){
                recordIds.add(rec.getId());
            }
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        }
        return false;
    }
}
