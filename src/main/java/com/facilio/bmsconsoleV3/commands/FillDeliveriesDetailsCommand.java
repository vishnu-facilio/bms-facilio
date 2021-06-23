package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3DeliveriesContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;

public class FillDeliveriesDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3DeliveriesContext> deliveries = recordMap.get(moduleName);
        
        if(CollectionUtils.isNotEmpty(deliveries)) {
        	
        for(V3DeliveriesContext delivery : deliveries){
        	if(delivery.getEmployee() != null && delivery.getEmployee().getId() > 0) {
        		V3EmployeeContext employee = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, delivery.getEmployee().getId(), V3EmployeeContext.class);
        		delivery.setName(employee.getName());
			} else {
				delivery.setName("Undefined");
			}
        }
	}
        
        return false;
	}
	
}
