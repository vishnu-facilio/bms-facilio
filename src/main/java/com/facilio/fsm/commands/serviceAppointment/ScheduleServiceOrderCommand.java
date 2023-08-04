package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.signup.ServiceOrderTicketStatus;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ScheduleServiceOrderCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String,FacilioField> soFieldMap = FieldFactory.getAsMap(serviceOrderFields);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments){
                ServiceOrderContext serviceOrder = serviceAppointment.getServiceOrder();
                if(serviceOrder != null) {
                    ServiceOrderTicketStatusContext scheduledStatus = ServiceOrderAPI.getStatus(FacilioConstants.ServiceAppointment.SCHEDULED);
                    ServiceOrderTicketStatusContext newStatus = ServiceOrderAPI.getStatus("new");
                    if(newStatus != null && scheduledStatus != null) {
                        UpdateRecordBuilder<ServiceOrderContext> updateBuilder = new UpdateRecordBuilder<ServiceOrderContext>()
                                .module(serviceOrderModule)
                                .fields(Collections.singletonList(soFieldMap.get(FacilioConstants.ContextNames.STATUS)))
                                .andCondition(CriteriaAPI.getCondition(soFieldMap.get(FacilioConstants.ContextNames.STATUS), String.valueOf(newStatus.getId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getIdCondition(serviceOrder.getId(), serviceOrderModule));
                        Map<String, Object> updateProps = new HashMap<>();
                        updateProps.put("status", scheduledStatus);
                        updateBuilder.updateViaMap(updateProps);
                    } else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Missing service order states");
                    }
                }
            }
        }
        return false;
    }
}
