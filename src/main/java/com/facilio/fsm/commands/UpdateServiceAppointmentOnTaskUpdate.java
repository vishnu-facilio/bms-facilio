package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateServiceAppointmentOnTaskUpdate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule =modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        FacilioField skillfield = modBean.getField("skills",FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if(eventType == EventType.EDIT) {
            if (CollectionUtils.isNotEmpty(serviceTasks)) {
                for (ServiceTaskContext serviceTask : serviceTasks) {
                    if (serviceTask.getServiceAppointment() != null) {
                        ServiceAppointmentContext serviceAppointment = new ServiceAppointmentContext();
                        serviceAppointment.setId(serviceTask.getServiceAppointment().getId());

                        //fetching serviceTask Ids mapped to service appointment

                        FacilioField taskField = modBean.getField("right", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TASK);
//
                        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                                .select(Collections.singleton(taskField))
                                .table("SERVICE_APPOINTMENT_TASK_REL")
                                .andCondition(CriteriaAPI.getCondition("SERVICE_APPOINTMENT_ID", "left", String.valueOf(serviceAppointment.getId()), NumberOperators.EQUALS));
                        List<Map<String, Object>> maps = selectBuilder.get();
                        List<Long> taskIds = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(maps)) {
                            taskIds = maps.stream().map(row -> (long) row.get("right")).collect(Collectors.toList());
                        }
                        FacilioField field = modBean.getField("right", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_SKILLS);
                        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                                .select(Collections.singleton(field))
                                .table("Service_Task_Skills")
                                .andCondition(CriteriaAPI.getCondition("LEFT_ID", "left", StringUtils.join(taskIds, ","), NumberOperators.EQUALS));
                        List<Map<String, Object>> props = selectRecordBuilder.get();
                        List<Long> skillIds = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(props)) {
                            skillIds = props.stream().map(prop -> (long) prop.get("right")).collect(Collectors.toList());
                            List<ServiceAppointmentSkillContext> skills = new ArrayList<>();
                            if (CollectionUtils.isNotEmpty(skillIds)) {
                                Set<Long> skillSet = new HashSet<>(skillIds);
                                for (Long skillId : skillSet) {
                                    ServiceAppointmentSkillContext skill = new ServiceAppointmentSkillContext();
                                    skill.setId(skillId);
                                    skills.add(skill);
                                }

                                Map<String, Object> map = new HashMap<>();
                                map.put("skills", skillSet);
                                serviceAppointment.setData(map);

                                serviceAppointment.setSkills(skills);
                                V3RecordAPI.updateRecord(serviceAppointment, serviceAppointmentModule, Collections.singletonList(skillfield), false, true);

                            }
                        }
                        }
                    }
                }

            }

            return false;
        }
}
