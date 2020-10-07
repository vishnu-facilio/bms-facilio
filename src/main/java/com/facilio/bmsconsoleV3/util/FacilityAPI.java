package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementSharingInfoContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityAmenitiesContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilitySpecialAvailabilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.WeekDayAvailability;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FacilityAPI {

    public static void setFacilityAmenities(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityAmenityModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_AMENITIES;
        List<FacilioField> fields = modBean.getAllFields(facilityAmenityModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilityAmenitiesContext> builder = new SelectRecordsBuilder<FacilityAmenitiesContext>()
                .moduleName(facilityAmenityModName)
                .select(fields)
                .beanClass(FacilityAmenitiesContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<FacilityAmenitiesContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setAmenities(list);
        }
    }

    public static void setFacilityWeekDayAvailability(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilityWeekDayModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilityWeekDayModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WeekDayAvailability> builder = new SelectRecordsBuilder<WeekDayAvailability>()
                .moduleName(facilityWeekDayModName)
                .select(fields)
                .beanClass(WeekDayAvailability.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<WeekDayAvailability> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setWeekDayAvailabilities(list);
        }
    }

    public static void setFacilitySpecialAvailability(FacilityContext facility) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String facilitySpecialAvailabilityModName = FacilioConstants.ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY;
        List<FacilioField> fields = modBean.getAllFields(facilitySpecialAvailabilityModName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<FacilitySpecialAvailabilityContext> builder = new SelectRecordsBuilder<FacilitySpecialAvailabilityContext>()
                .moduleName(facilitySpecialAvailabilityModName)
                .select(fields)
                .beanClass(FacilitySpecialAvailabilityContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("facility"), String.valueOf(facility.getId()), NumberOperators.EQUALS));
        List<FacilitySpecialAvailabilityContext> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            facility.setFacilitySpecialAvailabilities(list);
        }
    }

}
