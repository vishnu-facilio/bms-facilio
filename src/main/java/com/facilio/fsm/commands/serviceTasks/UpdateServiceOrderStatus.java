package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateServiceOrderStatus extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //this is handled for only one service appointment
        //if multiple service appointments needs to be handled then we should set the serviceAppointmentId as a list of string in previous command
        String serviceAppointmentId = (String) context.get("serviceAppointmentId");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);

        if(StringUtils.isNotEmpty(serviceAppointmentId)){
            //fetching the service appointment details based on id set in context in previous command
            SelectRecordsBuilder<ServiceAppointmentContext> selectRecordsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
            selectRecordsBuilder.select(serviceAppointmentFields)
                    .module(serviceAppointmentModule)
                    .beanClass(ServiceAppointmentContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(serviceAppointmentId, serviceAppointmentModule));
            ServiceAppointmentContext serviceAppointment = selectRecordsBuilder.fetchFirst();
            ServiceOrderContext serviceOrderInfo= serviceAppointment.getServiceOrder();

            //fetching all the service appointments which are mapped to the particular service order
            SelectRecordsBuilder<ServiceAppointmentContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
            selectAppointmentsBuilder.select(serviceAppointmentFields)
                    .module(serviceAppointmentModule)
                    .beanClass(ServiceAppointmentContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderInfo.getId()), StringOperators.IS));
            List<ServiceAppointmentContext> selectAppointments = selectAppointmentsBuilder.get();
            Long count = 0L;
            if(CollectionUtils.isNotEmpty(selectAppointments)){
                FacilioStatus closedStatus = TicketAPI.getStatus(serviceAppointmentModule,"completed");
                //looping and verifying whether all the service appointments are in completed status
                for(ServiceAppointmentContext sa : selectAppointments){
                    if(sa.getModuleState() == closedStatus.getModuleState()){
                        count++;
                    }
                }
                if(selectAppointments.size() == count){
                    //if all service appointments are closed we are updating the column isAllSAClosed against the Service Order
                    FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
                    List<FacilioField> serviceOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
                    Map<String, FacilioField> soFieldMap = FieldFactory.getAsMap(serviceOrderFields);
                    Map<String, Object> valMap = new HashMap<>();
                    valMap.put("isAllSAClosed", true);
                    UpdateRecordBuilder<ServiceOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
                                                updateRecordBuilder.module(serviceOrderModule)
                                                .fields(Collections.singletonList(soFieldMap.get("isAllSAClosed")))
                                                .andCondition(CriteriaAPI.getIdCondition(String.valueOf(serviceOrderInfo.getId()), serviceOrderModule))
                                                .updateViaMap(valMap);
                }else{
                    return false;
                }
            }
        }
        return false;
    }
}
