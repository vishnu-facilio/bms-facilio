package com.facilio.bmsconsoleV3.commands.decommission;

import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.stream.Collectors;

public class DecommissionWmsCommand extends FacilioCommand implements PostTransactionCommand {
    private Context context;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        return false;
    }
    @Override
    public boolean postExecute() throws Exception {
        DecommissionContext decommissionContext = (DecommissionContext) context.get(FacilioConstants.ContextNames.DECOMMISSION);
        List<V3ResourceContext> dependentResourcesData = (List<V3ResourceContext>) context.get(FacilioConstants.ContextNames.DEPENDENT_RESOURCES_DATA);

        List<Long> dependentResourceIds = dependentResourcesData.stream().map(V3ResourceContext::getId).collect(Collectors.toList());
        Boolean decommission = decommissionContext.getDecommission();
        long decommissionTime = (long) context.get(FacilioConstants.ContextNames.COMMISSION_TIME);

        DecommissionUtil.sendResourcesToWms(dependentResourceIds , decommission);

        //ADDING LOGS FOR PARTICULAR RESOURCES
        DecommissionUtil.sendDecommissionLogsToWms(dependentResourcesData,decommissionContext , decommissionTime);

        return false;
    }
}
