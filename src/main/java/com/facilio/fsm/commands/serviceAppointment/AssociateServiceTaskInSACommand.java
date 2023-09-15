package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTaskContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociateServiceTaskInSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));

        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                Map<String,Object>  tasks = serviceAppointment.getAssociatedTasks();
                    if(tasks != null && !tasks.isEmpty()){
                        List<ServiceTaskContext> allServiceTasks = new ArrayList<>();
                        List<ServiceTaskContext> newTasks = (List<ServiceTaskContext>) tasks.get("newTasks");
                        List<ServiceTaskContext> associatedTasks = (List<ServiceTaskContext>) tasks.get("associatedTasks");
                        if(CollectionUtils.isNotEmpty(newTasks)) {
                            FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
                            FacilioContext taskList = V3Util.createRecordList(serviceTaskModule, FieldUtil.getAsMapList(newTasks, ServiceTaskContext.class), null, null);
                            List<Map<String, Object>> serviceTaskList = new ArrayList<>();
                            if (taskList != null) {
                                Map<String, List<ModuleBaseWithCustomFields>> record = Constants.getRecordMap(taskList);
                                List<ModuleBaseWithCustomFields> serviceTasks = record.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
                                serviceTaskList = FieldUtil.getAsMapList(serviceTasks, ServiceTaskContext.class);
                                allServiceTasks.addAll(FieldUtil.getAsBeanListFromMapList(serviceTaskList,ServiceTaskContext.class));
                            }
                        }
                        allServiceTasks.addAll(associatedTasks);
                        serviceAppointment.setServiceTasks(allServiceTasks);
                        serviceAppointment.setRelations(null);
                    }
            }
        }
        return false;
    }
}
