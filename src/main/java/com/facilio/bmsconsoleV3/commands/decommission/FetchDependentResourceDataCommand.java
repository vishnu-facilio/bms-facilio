package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class FetchDependentResourceDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DecommissionContext decommissionContext = (DecommissionContext) context.get(FacilioConstants.ContextNames.DECOMMISSION);
        Long currentResourceId = decommissionContext.getResourceId();
        String currentResourceModuleName = decommissionContext.getModuleName();
        FacilioUtil.throwIllegalArgumentException(currentResourceId == null || currentResourceModuleName == null,"Resource Id or ModuleName cannot be null");

        List<V3ResourceContext> childResourcesData = DecommissionUtil.getChildResources(currentResourceId , currentResourceModuleName , decommissionContext.getDecommission() , false,null);

        context.put(FacilioConstants.ContextNames.DEPENDENT_RESOURCES_DATA,childResourcesData);

        return false;
    }
}
