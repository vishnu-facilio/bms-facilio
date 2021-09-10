package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;


public class FetchFloorplanFacilitiesCommmand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> spaceIds = (List<Long>) context.get(FacilioConstants.ContextNames.SPACE_LIST);
		long startTime =  (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime =  (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		
		
		if(!CollectionUtils.isEmpty(spaceIds)) {
			
    		Map<Long, V3FacilityBookingContext> bookingMap =  new HashMap<>();

		
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        String slots = FacilioConstants.ContextNames.FacilityBooking.FACILITY;
	        List<FacilioField> fields = modBean.getAllFields(slots);
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
	        FacilioModule amenitiesModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
	        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
	        SupplementRecord manager = (SupplementRecord) fieldsAsMap.get("manager");
	        SupplementRecord location = (SupplementRecord) fieldsAsMap.get("location");

	        MultiLookupMeta amenities = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("amenities"));

	        FacilioField nameField = FieldFactory.getField("description", "DESCRIPTION", amenitiesModule, FieldType.STRING);
	        amenities.setSelectFields(Collections.singletonList(nameField));

	        fetchLookupsList.add(manager);
	        fetchLookupsList.add(location);
	        fetchLookupsList.add(amenities);
	
	        SelectRecordsBuilder<FacilityContext> builder = new SelectRecordsBuilder<FacilityContext>()
	                .moduleName(slots)
	                .select(fields)
	                .beanClass(FacilityContext.class)
	                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("parentId"), StringUtils.join(spaceIds, ","), NumberOperators.EQUALS))
	                .fetchSupplements(fetchLookupsList);
	
	        List<FacilityContext> list = builder.get();
	        
	        Map<Long, FacilityContext> FacilitiesMap = new HashMap<>();
	        Map<Long, List<BookingSlotsContext>> BookingsMap = new HashMap<>();
	        Map<Long,List<BookingSlotsContext>> bookedSlotMap = new HashMap<>();
	        
	        List<Long> facilityIds = new ArrayList<>();
	        list.forEach(i -> facilityIds.add(i.getId()));
	        
	        List<SlotContext> slotList = FacilityAPI.getFacilitySlotsForTimeRange(facilityIds, startTime, endTime);
	        
	        if (CollectionUtils.isNotEmpty(slotList)) {
	    		List<Long> slotIds = new ArrayList<>();
	    		slotList.forEach(i -> slotIds.add(i.getId()));
	    		List<BookingSlotsContext> bookingSlots = FacilityAPI.getFacilityBookingListWithSlots(slotIds);
	    		Map<Long,BookingSlotsContext> bookingSlotsAsMap =  bookingSlots.stream()
	            .collect(Collectors.toMap(b -> b.getSlot().getId(), Function.identity()));
	    		
	    		List<Long> bookingIds = new ArrayList<Long>();
	        
	    		bookingSlots.forEach(i -> bookingIds.add(i.getBooking().getId()));
	    		
	    		
	    		// this is to get the facility booking list
	    		if (CollectionUtils.isNotEmpty(bookingIds)) {
	    			
	                FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
	                Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(bookingModule);
	                Collection<SupplementRecord>lookUpfields = new ArrayList<>();
	        		List<FacilioField> bookingFields = modBean.getAllFields(bookingModule.getName());
	        		Map<String, FacilioField> bookingFieldMap = FieldFactory.getAsMap(bookingFields);
	         		lookUpfields.add((LookupField) bookingFieldMap.get(FacilioConstants.ContextNames.FacilityBooking.BOOKING_RESERVEDFOR));
	   			
	    			
	    			List<V3FacilityBookingContext> bookingList = V3RecordAPI.getRecordsListWithSupplements(bookingModule.getName(), bookingIds, beanClassName, lookUpfields);
	    			
	    			bookingMap = bookingList.stream()
	    		            .collect(Collectors.toMap(b -> b.getId(), Function.identity()));
	    			
	    		
	    		}

	        
	        Map<Long,List<SlotContext>> slotMap = new HashMap<>();
	        for (SlotContext slot : slotList) {
	        	if(slot.getFacilityId() != null) {
	        		if(slotMap.get(slot.getFacilityId()) != null) {
	        			slotMap.get(slot.getFacilityId()).add(slot);
	        		} else {
	        			List<SlotContext> slotArray = new ArrayList<>();
	        			slotArray.add(slot);
	        			slotMap.put(slot.getFacilityId(),slotArray);
	        		}
	        		if(bookingSlotsAsMap.get(slot.getId()) != null) {
	        			if(bookedSlotMap.get(slot.getFacilityId()) != null) {
	        			bookedSlotMap.get(slot.getFacilityId()).add(bookingSlotsAsMap.get(slot.getId()));
	        			} else {
		        			List<BookingSlotsContext> bookingslotArray = new ArrayList<>();
		        			BookingSlotsContext bookingSlot = bookingSlotsAsMap.get(slot.getId());
		        			if (bookingSlot.getBooking() != null) {
		        				bookingSlot.setBooking(bookingMap.get(bookingSlot.getBooking().getId()));
		        			}
		        			bookingslotArray.add(bookingSlot);
		        			bookedSlotMap.put(slot.getFacilityId(),bookingslotArray);
		        		}
	        		}
	        	}
	        }
	        
	        for (FacilityContext facility : list) {
	        	if (slotMap.get(facility.getId()) != null) {
	        		facility.setSlots(slotMap.get(facility.getId()));
                }
	        	FacilitiesMap.put(facility.getParentId(), facility);
	        	BookingsMap.put(facility.getParentId(), bookedSlotMap.get(facility.getId()));
	        }
	        }
	        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY, FacilitiesMap);
			
	        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, BookingsMap);
	        
	        context.put("bookingMap", bookingMap);
		}
		
		return false;
	}
	
}