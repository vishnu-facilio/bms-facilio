package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarSlotsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateControlActionFromTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionTemplateId = (Long) context.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ID);
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);
        if(controlActionTemplateContext == null){
            return false;
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule controlActionModule = moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<ModuleBaseWithCustomFields> records = new ArrayList<>();

        if(controlActionTemplateContext.getCalendar() != null) {
            Long startTime = (Long) context.get("startTime");
            Long endTime = (Long) context.get("endTime");
            List<V3CalendarSlotsContext> calendarSlotsContextList = CalendarApi.getCalendarSlots(controlActionTemplateContext.getCalendar().getId(), startTime, endTime);
            if (CollectionUtils.isNotEmpty(calendarSlotsContextList)) {
                List<PeopleContext> firstLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId, FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
                List<PeopleContext> secondLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId, FacilioConstants.Control_Action.CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME);
                for (V3CalendarSlotsContext calendarSlotsContext : calendarSlotsContextList) {
                    Map<String, Object> asProperties = FieldUtil.getAsProperties(controlActionTemplateContext);
                    V3ControlActionContext controlActionRecord = FieldUtil.getAsBeanFromMap(asProperties, V3ControlActionContext.class);
                    if (CollectionUtils.isNotEmpty(firstLevelApprovalList)) {
                        controlActionRecord.setFirstLevelApproval(firstLevelApprovalList);
                    }
                    if (CollectionUtils.isNotEmpty(secondLevelApprovalList)) {
                        controlActionRecord.setSecondLevelApproval(secondLevelApprovalList);
                    }
                    controlActionRecord.setControlActionSourceType(V3ControlActionContext.ControlActionSourceTypeEnum.CONTROL_ACTION_TEMPLATE.getVal());
                    controlActionRecord.setScheduledActionDateTime(convertDateTimeToMillis(calendarSlotsContext.getCalendarYear(), calendarSlotsContext.getCalendarMonth(), calendarSlotsContext.getCalendarDate(), calendarSlotsContext.getSlotStartTime()));
                    controlActionRecord.setRevertActionDateTime(convertDateTimeToMillis(calendarSlotsContext.getCalendarYear(), calendarSlotsContext.getCalendarMonth(), calendarSlotsContext.getCalendarDate(), calendarSlotsContext.getSlotEndTime()));
                    controlActionRecord.setControlActionTemplate(controlActionTemplateContext);
                    records.add(controlActionRecord);
                }
            }
        }
        else{
            //Todo create control Action template without calendar
            Map<String, Object> asProperties = FieldUtil.getAsProperties(controlActionTemplateContext);
            V3ControlActionContext controlActionRecord = FieldUtil.getAsBeanFromMap(asProperties, V3ControlActionContext.class);
            List<PeopleContext> firstLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId,FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
            List<PeopleContext> secondLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId,FacilioConstants.Control_Action.CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME);
            if(CollectionUtils.isNotEmpty(firstLevelApprovalList)){
                controlActionRecord.setFirstLevelApproval(firstLevelApprovalList);
            }
            if(CollectionUtils.isNotEmpty(secondLevelApprovalList)){
                controlActionRecord.setSecondLevelApproval(secondLevelApprovalList);
            }
            records.add(controlActionRecord);
            controlActionRecord.setControlActionTemplate(controlActionTemplateContext);
        }
        if(CollectionUtils.isNotEmpty(records)){
            V3Util.createRecord(controlActionModule,records);
        }
        return false;
    }
    public long convertDateTimeToMillis(int year,int month,int date,int time){
        int hour = time/60;
        int min = time%60;
        ZonedDateTime zonedDateTime = ZonedDateTime.of(year,month,date,hour,min,0,0,ZoneId.systemDefault());
        return zonedDateTime.toEpochSecond()*1000;
    }
}
