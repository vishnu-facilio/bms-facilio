package com.facilio.bmsconsoleV3.util;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

public class ParkingAPI {


	public static void AddorDeleteFacilityForParkings(V3ParkingStallContext parking) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parkingModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
		FacilioModule facilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
		List<FacilioField> facilityFields = modBean.getAllFields(facilityModule.getName());
		FacilioModule fwkModule = modBean
				.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY);
		List<FacilioField> fwkFields = modBean.getAllFields(fwkModule.getName());
		List<FacilityContext> existingFacilities = FacilityAPI.getFacilityList(parking.getId(), parkingModule.getModuleId());
		if(existingFacilities != null && !existingFacilities.isEmpty() && parking.getParkingMode() != 2) {
			List<Long> facilityIds = new ArrayList<>();
			existingFacilities.forEach(i -> facilityIds.add(i.getId()));
			List<V3FacilityBookingContext> bookingsList = FacilityAPI.getFacilityBooking(facilityIds);
			if(bookingsList != null && !bookingsList.isEmpty())
			{
				CancelBooking(bookingsList);
			}

		}
		else if (parking.getParkingMode() == 2 && CollectionUtils.isEmpty(existingFacilities)) {
			List<FacilityContext> facilityprop = new ArrayList<FacilityContext>();
			FacilityContext facility = new FacilityContext();

			facility.setName(parking.getName());
			facility.setParentModuleId(parkingModule.getModuleId());
			facility.setParentId(parking.getId());
			facility.setSiteId(parking._getSiteId());
			BaseSpaceContext location = SpaceAPI.getBaseSpace(parking.getId());
			facility.setLocation(location);
			facility.setFacilityType(2); // facility type space
			facility.setUsageCapacity(1);
			facility.setMaxSlotBookingAllowed(24L);
			facility.setBookingAdvancePeriodInDays(30L);
			facility.setSlotDuration(3600L);

			facilityprop.add(facility);

			Map<Long, List<UpdateChangeSet>> changes = V3RecordAPI.addRecord(true, facilityprop, facilityModule,
					facilityFields, true);
			int delay = 0;
			for (Long facilityId : changes.keySet()) {

				facility.setId(facilityId);

				List<WeekDayAvailability> days = new ArrayList<WeekDayAvailability>();

				WeekDayAvailability newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("20:00");
				newWkDay.setDayOfWeek(1);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("20:00");
				newWkDay.setDayOfWeek(2);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("22:00");
				newWkDay.setDayOfWeek(3);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("22:00");
				newWkDay.setDayOfWeek(4);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("22:00");
				newWkDay.setDayOfWeek(5);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("22:00");
				newWkDay.setDayOfWeek(6);
				days.add(newWkDay);

				newWkDay = new WeekDayAvailability();
				newWkDay.setFacility(facility);
				newWkDay.setStartTime("08:00");
				newWkDay.setEndTime("22:00");
				newWkDay.setDayOfWeek(7);
				days.add(newWkDay);

				V3RecordAPI.addRecord(false, days, fwkModule, fwkFields, true);
				BmsJobUtil.scheduleOneTimeJobWithProps(facilityId, "CreateSlotForFacilities", delay, "priority", null);
			}

		}
	}
	public static void CancelBooking(List<V3FacilityBookingContext> bookings) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
         FacilioModule slotModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.SLOTS);
         Collection<JSONObject> jsonList = new ArrayList<>();

         for(V3FacilityBookingContext booking : bookings) {
             Map<String, Object> map = FieldUtil.getAsProperties(booking);
             map.put("isCancelled", true);
             JSONObject json = new JSONObject();
             json.putAll(map);
             jsonList.add(json);

             //delete booking & slots rel
            List<BookingSlotsContext> bookingSlots  = FacilityAPI.getBookingSlots(booking.getId());
            if(CollectionUtils.isNotEmpty(bookingSlots)) {
                for(BookingSlotsContext bookingSlot : bookingSlots) {
                    SlotContext slot = (SlotContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.SLOTS, bookingSlot.getSlot().getId(), SlotContext.class);
                    if (slot != null) {
                        //reverting booking count in slot
                    	if(slot.getBookingCount()!=null && booking.getNoOfAttendees()!=null) 
                    	{
                        slot.setBookingCount(slot.getBookingCount() - booking.getNoOfAttendees());
                        V3RecordAPI.updateRecord(slot, slotModule, modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.SLOTS));
                    	}
                    }
                }
            }
         }

         if(CollectionUtils.isNotEmpty(jsonList)) {
             FacilioChain patchChain = ChainUtil.getBulkPatchChain(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
             FacilioContext patchContext = patchChain.getContext();
             V3Config v3Config = ChainUtil.getV3Config(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
             Class beanClass = ChainUtil.getBeanClass(v3Config, bookingModule);

             //to avoid validations
             JSONObject json = new JSONObject();
             json.put("cancel", true);
             Constants.setBodyParams(patchContext, json);

             Constants.setModuleName(patchContext, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
             Constants.setBulkRawInput(patchContext, (Collection) jsonList);
             patchContext.put(Constants.BEAN_CLASS, beanClass);
             patchContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
             patchContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
             patchChain.execute();
         }
		
	} 

}