package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.PeopleSkillLevelContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentSkillContext;
import com.facilio.fsm.context.ServiceSkillsContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateSAMismatch extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        boolean skipValidation = true;
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.get("skipValidation") != null ) {
            skipValidation = (boolean) bodyParams.get("skipValidation");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        StringBuilder errors=new StringBuilder();

        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                if(serviceAppointment.getFieldAgent() != null){
                    V3PeopleContext fieldAgent = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,serviceAppointment.getFieldAgent().getId(),V3PeopleContext.class);
                    if(Optional.ofNullable(serviceAppointment.getScheduledStartTime()).orElse(0L) > 0){
                        Shift currentShift = ShiftAPI.getPeopleShiftForDay(fieldAgent.getId(), serviceAppointment.getScheduledStartTime());
                        if(currentShift != null){
                            long shiftStart = currentShift.getStartTime();
                            long shiftEnd = currentShift.getEndTime();
                            long dayStartTime = DateTimeUtil.getDayStartTimeOf(serviceAppointment.getScheduledStartTime());
                            if(serviceAppointment.getScheduledStartTime() < (shiftStart + dayStartTime)){
                                errors.append("Service Appointment is scheduled to start before the field agent's working hours. ");
                            } else if(serviceAppointment.getScheduledStartTime() > (shiftEnd + dayStartTime)){
                                errors.append("Service Appointment is scheduled to start after the field agent's working hours. ");
                            }
                        }
                    }
                    if(Optional.ofNullable(serviceAppointment.getScheduledEndTime()).orElse(0L) > 0 ){
                        Shift currentShift = ShiftAPI.getPeopleShiftForDay(fieldAgent.getId(), serviceAppointment.getScheduledEndTime());
                        if(currentShift != null){
                            long shiftStart = currentShift.getStartTime();
                            long shiftEnd = currentShift.getEndTime();
                            long dayStartTime = DateTimeUtil.getDayStartTimeOf(serviceAppointment.getScheduledEndTime());
                            if(serviceAppointment.getScheduledEndTime() > (shiftEnd + dayStartTime)){
                                errors.append("Service Appointment is scheduled to end after the field agent's working hours. ");
                            } else if(serviceAppointment.getScheduledEndTime() < (shiftStart + dayStartTime)){
                                errors.append("Service Appointment is scheduled to end before the field agent's working hours. ");
                            }
                        }
                    }
                    if(CollectionUtils.isNotEmpty(serviceAppointment.getSkills())){
                        List<Long> saSkillIds = serviceAppointment.getSkills().stream().map(obj -> obj.getId()).collect(Collectors.toList());
                        SelectRecordsBuilder<ServiceAppointmentSkillContext> saSkillsBuilder  = new SelectRecordsBuilder<ServiceAppointmentSkillContext>()
                                .module(Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL))
                                .select(Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL))
                                .beanClass(ServiceAppointmentSkillContext.class)
                                .andCondition(CriteriaAPI.getIdCondition(saSkillIds,Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_SKILL)));
                        List<ServiceAppointmentSkillContext> saSkills = saSkillsBuilder.get();
                        if(CollectionUtils.isNotEmpty(saSkills)) {
                            List<Long> skillIds = saSkills.stream().map(obj -> obj.getLeft().getId()).collect(Collectors.toList());
                            SelectRecordsBuilder<PeopleSkillLevelContext> skillBuilder = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                                    .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                    .beanClass(PeopleSkillLevelContext.class)
                                    .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", String.valueOf(fieldAgent.getId()), NumberOperators.EQUALS));
                            List<PeopleSkillLevelContext> peopleSkills = skillBuilder.get();
                            if (CollectionUtils.isNotEmpty(peopleSkills)) {
                                List<Long> peopleSkillIds = peopleSkills.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                                if (!CollectionUtils.isEqualCollection(skillIds, peopleSkillIds)) {
                                    errors.append("Field agent's skills does not match the service Appointment's requirements. ");
                                }
                            }
                        }
                    }
                    if(StringUtils.isNotEmpty(errors)){
                        if(!skipValidation) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, errors.toString());
                        } else {
                            serviceAppointment.setMismatch(true);
                        }
                    } else {
                        serviceAppointment.setMismatch(false);
                    }
                }
            }
        }
        return false;
    }
}
