package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTaskContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SOSTAutoCreateAfterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<FacilioField> serviceAppointmentFields = modBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(serviceAppointmentFields);
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        List<FacilioField> serviceTaskFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        Map<String, FacilioField> serviceTasksFieldMap = FieldFactory.getAsMap(serviceTaskFields);

        for (ServiceTaskContext task : serviceTasks) {
                    if (task.getServiceOrder() != null && task.getServiceOrder().getId() > 0) {
                        ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, task.getServiceOrder().getId());
                        if (serviceOrderInfo.isAutoCreateSa()) {
                            List<MultiLookupField> lookUpfields = new ArrayList<>();
                            lookUpfields.add((MultiLookupField) fieldMap.get("serviceTasks"));
                            //fetching all the service appointments which are mapped to the particular service order
                            SelectRecordsBuilder<ServiceAppointmentContext> selectAppointmentsBuilder = new SelectRecordsBuilder<ServiceAppointmentContext>();
                            selectAppointmentsBuilder.select(serviceAppointmentFields)
                                    .module(serviceAppointmentModule)
                                    .beanClass(ServiceAppointmentContext.class)
                                    .fetchSupplements(lookUpfields)
                                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("serviceOrder"), String.valueOf(serviceOrderInfo.getId()), StringOperators.IS));
                            ServiceAppointmentContext selectAppointments = selectAppointmentsBuilder.fetchFirst();

                            task.setServiceAppointment(selectAppointments);

                            SelectRecordsBuilder<ServiceTaskContext> selectTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
                            selectTasksBuilder.select(serviceTaskFields)
                                    .module(serviceTaskModule)
                                    .beanClass(ServiceTaskContext.class)
                                    .andCondition(CriteriaAPI.getCondition(serviceTasksFieldMap.get("serviceOrder"), String.valueOf(serviceOrderInfo.getId()), StringOperators.IS));
                            List<ServiceTaskContext> selectTasks = selectTasksBuilder.get();
                            selectTasks.add(task);
                            if (selectAppointments != null) {
                                List<ServiceAppointmentTaskContext> data = selectAppointments.getServiceTasks();
                                for (ServiceTaskContext taskItems : serviceTasks) {
                                    ServiceAppointmentTaskContext appointmentTasks = new ServiceAppointmentTaskContext();
                                    appointmentTasks.setId(taskItems.getId());
                                    data.add(appointmentTasks);

                                }
                                selectAppointments.setServiceTasks(data);

                                V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, selectAppointments.getId(), FieldUtil.getAsJSON(selectAppointments), null, null, null, null, null, null, null, null);
                            }
                        }
                        }
                    }



        return false;
    }
}
