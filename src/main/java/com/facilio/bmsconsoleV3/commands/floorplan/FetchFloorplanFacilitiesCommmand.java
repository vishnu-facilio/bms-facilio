package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
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
	        Map<Long, List<V3FacilityBookingContext>> BookingsMap = new HashMap<>();
	        for (FacilityContext facility : list) {
	        	FacilitiesMap.put(facility.getParentId(), facility);
	        	BookingsMap.put(facility.getParentId(), FacilityAPI.getFacilityBookingListWithSlots(facility.getId(),startTime,endTime));
	        }
	        
	        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY, FacilitiesMap);
			
	        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, BookingsMap);
		}
		
		return false;
	}
	
}