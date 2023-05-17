package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.bmsconsoleV3.util.DecommissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;

public class ValidateDecommisionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	DecommissionContext decommissionContext = (DecommissionContext) context.get(FacilioConstants.ContextNames.DECOMMISSION);

        //CHECKING PARENT AND GRAND PARENT OF THE RESOURCE IS DECOMMISSIONED WHILE RECOMMISSIONING
        if(decommissionContext.getDecommission() != null){
            if(!decommissionContext.getModuleName().equals(FacilioConstants.ContextNames.SITE) && !decommissionContext.getDecommission()) {
                boolean isParentDecommissioned = DecommissionUtil.checkParentResourcesIsDecommissioned(decommissionContext.getResourceId(), decommissionContext.getModuleName());
                FacilioUtil.throwIllegalArgumentException(isParentDecommissioned, "Parent of this Resource is Decommissioned , So You can't Recommission it");
            }
        }
    	
		return false;
	}

}
