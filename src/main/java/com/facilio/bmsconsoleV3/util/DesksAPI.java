package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import java.util.*;

public class DesksAPI {

    public static void AddorDeleteFacilityForDesks(V3DeskContext desk) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
		FacilioModule facilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
		List<FacilioField> facilityFields = modBean.getAllFields(facilityModule.getName());
		FacilioModule fwkModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);
		List<FacilioField> fwkFields = modBean.getAllFields(fwkModule.getName());
    	List<FacilityContext> existingFacilities = FacilityAPI.getFacilityList(desk.getId(),deskModule.getModuleId());
    	
		if(existingFacilities != null && !existingFacilities.isEmpty()) {
//			List<Long> ids = new ArrayList(existingFacilities)
//			if(desk.getDeskType() == 3) {
//				continue;
//			}
			
		} else if(desk.getDeskType() == 3) {
			List<FacilityContext> facilityprop = new ArrayList<FacilityContext>();
			FacilityContext facility = new FacilityContext();
			
			facility.setName(desk.getName());
			facility.setParentModuleId(deskModule.getModuleId());
			facility.setParentId(desk.getId());
			BaseSpaceContext location = SpaceAPI.getBaseSpace(desk.getId());
			facility.setLocation(location);
			facility.setFacilityType(2);
			facility.setUsageCapacity(1);
			facility.setMaxSlotBookingAllowed(24L);
			facility.setBookingAdvancePeriodInDays(1L);
			facility.setSlotDuration(3600L);
            
            facilityprop.add(facility);
            
            Map<Long, List<UpdateChangeSet>> changes = V3RecordAPI.addRecord(true, facilityprop, facilityModule, facilityFields,true);
            int delay = 0;
            for(Long facilityId : changes.keySet()) {
            	
            	facility.setId(facilityId);
            	
            	List<WeekDayAvailability> days = new ArrayList<WeekDayAvailability>();
				
				WeekDayAvailability newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(1);
                days.add(newWkDay);
                
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(2);
                days.add(newWkDay);
                
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(3);
                days.add(newWkDay);
                
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(4);
                days.add(newWkDay);
                
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(5);
                days.add(newWkDay);
				
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(6);
                days.add(newWkDay);
                
                newWkDay = new WeekDayAvailability();
                newWkDay.setFacility(facility);
                newWkDay.setStartTime("00:00");
                newWkDay.setEndTime("23:45");
                newWkDay.setDayOfWeek(7);
                days.add(newWkDay);
                
            	V3RecordAPI.addRecord(false, days, fwkModule, fwkFields,true);
                BmsJobUtil.scheduleOneTimeJobWithProps(facilityId, "CreateSlotForFacilities", delay, "priority", null);
            }
        
		}
    }
    
}