package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ChangeWeekDayAvailabilityMetaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("applyToDaySlots")) {
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<SlotContext> slots = recordMap.get(moduleName);

            //check if query param apply for all similar slots query param is there
            if(CollectionUtils.isNotEmpty(slots)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);
                List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);

                for(SlotContext slot : slots) {
                    FacilityContext facility = new FacilityContext();
                    facility.setId(slot.getFacilityId());

                    List<WeekDayAvailability> newWkList = new ArrayList<>();

                    Long dateTime = slot.getSlotStartTime();
                    ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(slot.getSlotStartTime());
                    LocalTime localStartTime = zdt.toLocalTime();

                    ZonedDateTime zdtEnd = DateTimeUtil.getZonedDateTime(slot.getSlotEndTime());
                    LocalTime localEndTime = zdtEnd.toLocalTime();

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(dateTime);
                    int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    List<WeekDayAvailability> weekDayList = FacilityAPI.getWeekDayAvailabilityForDay(slot.getFacilityId(), day);
                    if (CollectionUtils.isNotEmpty(weekDayList)) {
                        for (WeekDayAvailability wk : weekDayList) {
                            if (localStartTime.isAfter(wk.getActualStartTimeAsLocalTime()) && localEndTime.isBefore(wk.getActualEndTimeAsLocalTime())) {
                                //partition & delete the future slots
                                WeekDayAvailability newWkDay = new WeekDayAvailability();
                                newWkDay.setActualStartTime(localStartTime);
                                newWkDay.setActualEndTime(localEndTime);
                                newWkDay.setDayOfWeek(day);
                                newWkDay.setCost(slot.getSlotCost());
                                newWkDay.setFacility(facility);

                                WeekDayAvailability newWkDayAfter = new WeekDayAvailability();
                                newWkDayAfter.setActualStartTime(localEndTime);
                                newWkDayAfter.setActualEndTime(wk.getActualEndTimeAsLocalTime());
                                newWkDayAfter.setDayOfWeek(day);
                                newWkDayAfter.setCost(wk.getCost());
                                newWkDayAfter.setFacility(facility);

                                newWkList.add(newWkDay);
                                newWkList.add(newWkDayAfter);
                                V3RecordAPI.addRecord(false, newWkList, module, fields);

                                //update the first partitioned record
                                wk.setActualEndTime(localStartTime);
                                V3RecordAPI.updateRecord(wk, module, fields);

                                break;

                            }
                        }
                    }

                }
            }
        }

        return false;
    }
}
