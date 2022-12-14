package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import com.facilio.modules.FacilioStatus;
import com.facilio.bmsconsole.util.TicketAPI;

public class UpdateCloseAllFromBulkActionCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey(FacilioConstants.ContextNames.CLOSE_ALL_FROM_BULK_ACTION) && (boolean) bodyParams.get(FacilioConstants.ContextNames.CLOSE_ALL_FROM_BULK_ACTION))
        {
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3WorkOrderContext> wos = recordMap.get(moduleName);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioStatus closedState = TicketAPI.getStatus("Closed");

            for (V3WorkOrderContext wo : wos) {
              StateFlowRulesAPI.updateState(wo, module, closedState, false, context);
         }
      }
            return false;
    }
}
