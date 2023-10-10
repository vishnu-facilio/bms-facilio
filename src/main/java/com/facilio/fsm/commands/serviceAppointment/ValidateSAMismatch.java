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
import com.facilio.fsm.context.*;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.TimeOffUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.logging.Logger;
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
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        MultiLookupMeta territories = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("territories"));
        fetchLookupsList.add(territories);

        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        List<FSMErrorCode> mismatchException = new ArrayList<>();
        mismatchException.add(FSMErrorCode.SA_MISMATCH);

        List<Integer> mismatchType = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                if(serviceAppointment.getFieldAgent() != null){

                    long peopleId = serviceAppointment.getFieldAgent().getId();
                    Map<Long,V3PeopleContext> peopleMap = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.PEOPLE,Collections.singletonList(peopleId),V3PeopleContext.class,null,fetchLookupsList);
                    V3PeopleContext fieldAgent = peopleMap.get(peopleId);

                    if(serviceAppointment.getTerritory() != null && CollectionUtils.isNotEmpty(fieldAgent.getTerritories())){
                        TerritoryContext appointmentTerritory = serviceAppointment.getTerritory();
                        boolean territoryMismatch = true;
                        for(TerritoryContext territory : fieldAgent.getTerritories()){
                            if(appointmentTerritory.getId() == territory.getId()){
                                territoryMismatch = false;
                                break;
                            }
                        }
                        if(territoryMismatch){
                            mismatchException.add(FSMErrorCode.SA_TERRITORY_MISMATCH);
                            mismatchType.add(1);
                        }
                    }

                    boolean timeMismatch = false;
                    timeMismatch = checkForTimeMismatch(serviceAppointment,peopleId);
                    if(timeMismatch){
                        mismatchException.add(FSMErrorCode.SA_TIME_MISMATCH);
                        mismatchType.add(2);
                    }

                    if(CollectionUtils.isNotEmpty(serviceAppointment.getSkills())){
                        List<Long> skillIds = serviceAppointment.getSkills().stream().map(obj -> obj.getId()).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(skillIds)) {
                            SelectRecordsBuilder<PeopleSkillLevelContext> skillBuilder = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                                    .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                    .beanClass(PeopleSkillLevelContext.class)
                                    .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", String.valueOf(fieldAgent.getId()), NumberOperators.EQUALS));
                            List<PeopleSkillLevelContext> peopleSkills = skillBuilder.get();
                            if (CollectionUtils.isNotEmpty(peopleSkills)) {
                                List<Long> peopleSkillIds = peopleSkills.stream().map(obj -> obj.getSkill().getId()).collect(Collectors.toList());
                                if (!peopleSkillIds.containsAll(skillIds)) {
                                    mismatchException.add(FSMErrorCode.SA_SKILL_MISMATCH);
                                    mismatchType.add(3);
                                }
                            }
                        }
                    }
                    if(mismatchException.size() > 1){
                        if(!skipValidation) {
                            FSMException exception = null;
                            for(FSMErrorCode mismatch :mismatchException){
                                if(exception != null){
                                    exception.addAdditionalException(new FSMException(mismatch));
                                } else {
                                    exception = new FSMException(mismatch).setRelatedData(FieldUtil.getAsJSON(serviceAppointment));
                                }
                            }
                            throw exception;
                        } else {
                            serviceAppointment.setMismatch(true);
                            serviceAppointment.setMismatchType(mismatchType);
                        }
                    } else {
                        serviceAppointment.setMismatch(false);
                        serviceAppointment.setMismatchType(new ArrayList<>());
                    }
                }
            }
        }
        return false;
    }

    private boolean checkForTimeMismatch(ServiceAppointmentContext serviceAppointment, long peopleId) throws Exception {
        if(Optional.ofNullable(serviceAppointment.getScheduledStartTime()).orElse(0L) > 0 &&
                Optional.ofNullable(serviceAppointment.getScheduledEndTime()).orElse(0L) > 0) {
            List<TimeOffContext> timeOffList = TimeOffUtil.getTimeOffForPeople(serviceAppointment.getScheduledStartTime(), serviceAppointment.getScheduledEndTime(), peopleId);
            if(CollectionUtils.isNotEmpty(timeOffList)){
                return true;
            }
            Map<String,Object> currentShift = ShiftAPI.getPeopleShiftForGivenTime(peopleId,serviceAppointment.getScheduledStartTime());
            if (currentShift != null) {
                long shiftStart = (long) currentShift.getOrDefault("startTime",0);
                long shiftEnd = (long) currentShift.getOrDefault("endTime",0);
                if( shiftStart > 0 && shiftEnd > 0) {
                    long startTimeDayStart = DateTimeUtil.getDayStartTimeOf(serviceAppointment.getScheduledStartTime());
                    //    Need to check Night and morning shift overlap case
                    if (shiftEnd < shiftStart) {
                        shiftEnd += 86400000;
                    }
                    if ((serviceAppointment.getScheduledStartTime() < (shiftStart + startTimeDayStart)) || (serviceAppointment.getScheduledStartTime() > (shiftEnd + startTimeDayStart))) {
                        return true;
                    }
                    if ((serviceAppointment.getScheduledEndTime() > (shiftEnd + startTimeDayStart)) || (serviceAppointment.getScheduledEndTime() < (shiftStart + startTimeDayStart))) {
                        return true;
                    }
                } else {
                    throw new FSMException(FSMErrorCode.UNKNOWN_ERROR);
                }
            }
        } else {
            throw new FSMException(FSMErrorCode.SA_DETAILS_REQUIED);
        }
        return false;
    }
}
