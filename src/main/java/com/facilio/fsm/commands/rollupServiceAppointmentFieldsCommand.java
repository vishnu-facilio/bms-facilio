package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class rollupServiceAppointmentFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
//        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                if (serviceAppointment.getAppointmentType() == ServiceAppointmentContext.AppointmentType.SERVICE_WORK_ORDER.getIndex()) {
                    ServiceOrderContext serviceOrder = serviceAppointment.getServiceOrder();
                    if (serviceOrder != null) {
                        serviceAppointment.setPriority(serviceOrder.getPriority());
                        V3SiteContext site = serviceOrder.getSite();
                        if (site != null) {
                            serviceAppointment.setSite(site);
                            LocationContext location = site.getLocation();
                            if (location != null) {
                                serviceAppointment.setLocation(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(location), LocationContextV3.class));
                            }
                            serviceAppointment.setTerritory(site.getTerritory());
                        }
                    }
                }

                List<ServiceAppointmentTaskContext> serviceAppointmentTaskContextList = serviceAppointment.getServiceTasks();
//                if(eventType == EventType.EDIT) {
//                    //fetching old service task ids from serviceAppointmenttask table
//                    FacilioField taskField = modBean.getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);
//
//                    GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//                            .select(Collections.singleton(taskField))
//                            .table("SERVICE_APPOINTMENT_TASK_REL")
//                            .andCondition(CriteriaAPI.getCondition("LEFT_ID", "left", String.valueOf(serviceAppointment.getId()), NumberOperators.EQUALS));
//                    List<Map<String, Object>> maps = selectBuilder.get();
//                    List<Long> oldTaskIds = new ArrayList<>();
//                    if (CollectionUtils.isNotEmpty(maps)) {
//                        oldTaskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
//                    }
//                    context.put(FacilioConstants.ContextNames.FieldServiceManagement.OLD_SERVICE_TASK_IDS, oldTaskIds);
//                }
                //fetching updated service task ids
//                if (CollectionUtils.isNotEmpty(serviceAppointmentTaskContextList)) {
//                    List<Long> serviceTaskIds = serviceAppointmentTaskContextList
//                            .stream()
//                            .filter(a -> a != null)
//                            .map(a -> a.getId())
//                            .collect(Collectors.toList());
//                    context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_IDS,serviceTaskIds);

                    //fetching skills associated with service tasks

//                    FacilioField field = modBean.getField("right",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_SKILLS);
//                    GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//                            .select(Collections.singleton(field))
//                            .table("Service_Task_Skills")
//                            .andCondition(CriteriaAPI.getCondition("LEFT_ID","left", StringUtils.join(serviceTaskIds,","),NumberOperators.EQUALS ));
//                    List<Map<String, Object>> props = selectRecordBuilder.get();
//                    List<Long> skillIds = new ArrayList<>();
//                    if (CollectionUtils.isNotEmpty(props)) {
//                        skillIds = props.stream().map(prop -> (long) prop.get("right")).collect(Collectors.toList());
//                        List<ServiceAppointmentSkillContext> skills = new ArrayList<>();
//                        if(CollectionUtils.isNotEmpty(skillIds)) {
//                            for (Long skillId : skillIds) {
//                                ServiceAppointmentSkillContext skill = new ServiceAppointmentSkillContext();
//                                skill.setId(skillId);
//                                skills.add(skill);
//                            }
//                        }
//                        serviceAppointment.setSkills(skills);
//                    }
//                }
            }
        }
        return false;
    }
}
