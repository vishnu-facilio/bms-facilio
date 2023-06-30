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

public class updateServiceOrderStatus extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String serviceAppointmentId = (String) context.get("serviceAppointmentId");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);

        if(StringUtils.isNotEmpty(serviceAppointmentId)){
            SelectRecordsBuilder<ServiceAppointmentContext> selectRecordsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
            selectRecordsBuilder.select(serviceAppointmentFields)
                    .module(serviceAppointmentModule)
                    .beanClass(ServiceAppointmentContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(serviceAppointmentId, serviceAppointmentModule));
            ServiceAppointmentContext serviceAppointment = selectRecordsBuilder.fetchFirst();
            ServiceOrderContext serviceOrderInfo= serviceAppointment.getServiceOrder();


            SelectRecordsBuilder<ServiceAppointmentContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
            selectAppointmentsBuilder.select(serviceAppointmentFields)
                    .module(serviceAppointmentModule)
                    .beanClass(ServiceAppointmentContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"),String.valueOf(serviceOrderInfo.getId()), StringOperators.IS));
            List<ServiceAppointmentContext> selectAppointments = selectAppointmentsBuilder.get();
            Long count = 0L;
            if(CollectionUtils.isNotEmpty(selectAppointments)){
                FacilioStatus closedStatus = TicketAPI.getStatus(serviceAppointmentModule,"completed");
                for(ServiceAppointmentContext sa : selectAppointments){
                    if(sa.getModuleState() == closedStatus.getModuleState()){
                        count++;
                    }
                }
                if(selectAppointments.size() == count){
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
