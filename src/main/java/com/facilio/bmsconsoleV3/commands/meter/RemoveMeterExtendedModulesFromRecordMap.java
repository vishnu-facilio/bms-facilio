package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;
import java.util.Set;
public class RemoveMeterExtendedModulesFromRecordMap extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP);
        Set<String> extendedModules = Constants.getExtendedModules(context);
        if (CollectionUtils.isNotEmpty(extendedModules)) {
            for (String extendedModule : extendedModules) {
                recordMap.remove(extendedModule);
            }
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}