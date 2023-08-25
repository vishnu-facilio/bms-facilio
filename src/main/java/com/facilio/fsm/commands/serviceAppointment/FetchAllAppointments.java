package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.DispatcherEventContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchAllAppointments extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long peopleId= (Long) context.get(FacilioConstants.ContextNames.PEOPLE_ID);
        Long startTime= (Long) context.get(FacilioConstants.ContextNames.START_TIME);
        Long endTime= (Long) context.get(FacilioConstants.ContextNames.END_TIME);

        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module=moduleBean.getModule(serviceAppointmentModuleName);
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        List<ServiceAppointmentContext> saData = new ArrayList<>();

        List<SupplementRecord> saSupplements = new ArrayList<>();
        saSupplements.add((LookupField) saFieldMap.get("site"));
        saSupplements.add((LookupField) saFieldMap.get("status"));

        SelectRecordsBuilder<ServiceAppointmentContext> serviceAppointmentBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>()
                .select(saFields)
                .module(module)
                .beanClass(ServiceAppointmentContext.class)
                .andCondition(CriteriaAPI.getCondition(saFieldMap.get("fieldAgent"), String.valueOf(peopleId), NumberOperators.EQUALS))
                .fetchSupplements(saSupplements);;
        Criteria saTimeCrit = new Criteria();
        saTimeCrit.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get("scheduledStartTime"), startTime+","+endTime, DateOperators.BETWEEN));
        saTimeCrit.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get("scheduledEndTime"), startTime+","+endTime, DateOperators.BETWEEN));
        serviceAppointmentBuilder.andCriteria(saTimeCrit);
        saData = serviceAppointmentBuilder.get();

        context.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, saData);

        return false;
    }
}
