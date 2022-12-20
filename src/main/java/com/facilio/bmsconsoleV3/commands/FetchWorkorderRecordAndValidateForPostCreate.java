package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class FetchWorkorderRecordAndValidateForPostCreate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
         Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
         List<V3WorkOrderContext> wos = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
         
         if (wos == null || wos.isEmpty()) {
             return true;
         }
         
         V3WorkOrderContext workorder = wos.get(0);
         
         PlannedMaintenance plannedmaintenance = (PlannedMaintenance) V3Util.getRecord("plannedmaintenance", workorder.getPmV2(), null);

         if (plannedmaintenance.getPmStatusEnum() != PlannedMaintenance.PMStatus.ACTIVE) {
             return true;
         }
         
         context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
         
		return false;
	}

}
