package com.facilio.fsm.commands;

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
        boolean skipValidation = false;
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
                    if(Optional.ofNullable(serviceAppointment.getScheduledStartTime()).orElse(0L) > 0
                        || Optional.ofNullable(serviceAppointment.getScheduledEndTime()).orElse(0L) > 0 ){
                        Shift currentShift = ShiftAPI.getPeopleShiftForDay(fieldAgent.getId(), DateTimeUtil.getCurrenTime());
                        if(currentShift != null){
                            long shiftStart = currentShift.getStartTime();
                            long shiftEnd = currentShift.getEndTime();
                            if(serviceAppointment.getScheduledStartTime() < shiftStart){
                                errors.append("Service Appointment is scheduled to start before the field agent's working hours. ");
                            }
                            if(serviceAppointment.getScheduledEndTime() > shiftEnd){
                                errors.append("Service Appointment is scheduled to end after the field agent's working hours. ");
                            }
                        }
                    }
                    if(serviceAppointment.getTerritory() != null && fieldAgent.getTerritory() != null){
                        if(serviceAppointment.getTerritory().getId() != fieldAgent.getTerritory().getId()){
                            errors.append("Service Appointment is not located in the field agent's territory. ");
                        }
                    }
                    if(CollectionUtils.isNotEmpty(serviceAppointment.getSkills())){
                        List<Long> skillIds = serviceAppointment.getSkills().stream().map(obj -> obj.getLeft().getId()).collect(Collectors.toList());

                        SelectRecordsBuilder<PeopleSkillLevelContext> skillBuilder = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                                .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                .beanClass(PeopleSkillLevelContext.class)
                                .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                .andCondition(CriteriaAPI.getCondition("PEOPLE_ID","people",String.valueOf(fieldAgent.getId()), NumberOperators.EQUALS));
                        List<PeopleSkillLevelContext> peopleSkills = skillBuilder.get();
                        if(CollectionUtils.isNotEmpty(peopleSkills)){
                            List<Long> peopleSkillIds = peopleSkills.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                            if(!CollectionUtils.isEqualCollection(skillIds,peopleSkillIds)){
                                errors.append("Field agent's skills does not match the service Appointment's requirements. ");
                            }
                        }
                    }
                    if(StringUtils.isNotEmpty(errors) && !skipValidation){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,errors.toString());
                    }
                }
            }
        }
        return false;
    }
}
