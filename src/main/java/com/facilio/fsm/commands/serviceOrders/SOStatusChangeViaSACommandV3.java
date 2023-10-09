package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

public class SOStatusChangeViaSACommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> dataList = (List<ServiceAppointmentContext>) recordMap.get(moduleName);
        for(ServiceAppointmentContext appointment : dataList) {
            //if(appointment.getStatus() == ServiceAppointmentContext) check for scheduled status
//            {
//                ServiceOrderContext so = appointment.getServiceOrder();
//                if(so != null){
//                    Long soid = so.getId();
//                    ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,soid);
//                    if(serviceOrderInfo.getStatus() == ServiceTaskContext.ServiceTaskStatus.NEW.getIndex()){
//                        serviceOrderInfo.setStatus(ServiceOrderContext.ServiceOrderStatus.SCHEDULED);
//                        updateServiceOrder(serviceOrderInfo);
//                    }
//                }else{
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"No Service Order is mapped against the Service Appointment -"+appointment.getId());
//                }
//            }
        }
        return false;
    }
}
