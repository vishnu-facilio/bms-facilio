package com.facilio.bmsconsoleV3.util;

import com.amazonaws.util.StringUtils;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.homepage.homepagewidgetdata.HomepageWidgetData;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.time.DateTimeUtil;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tika.utils.SystemUtils;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Log4j
public class HomepageWidgteApi {
    private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

    public static List<HomepageWidgetData> getMyVisitorActionCards()throws Exception {
        List<HomepageWidgetData> widgets = new ArrayList<>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> map = FieldFactory.getAsMap(fields);
        Long daystartTime = DateTimeUtil.getDayStartTime();
        Long dayEndTime = DateTimeUtil.getDayEndTimeOf(daystartTime);

        FacilioStatus InviteRequestedStatus = getTicketfromName(module.getName(), "InviteRequested");

        if(InviteRequestedStatus != null && InviteRequestedStatus.getId() > 0) {

            Long peopleId = AccountUtil.getCurrentUser().getPeopleId();

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(map.get("moduleState"),String.valueOf(InviteRequestedStatus.getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(map.get("host"), String.valueOf(peopleId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(map.get("expectedCheckInTime"), String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));
            criteria.addAndCondition(CriteriaAPI.getCondition(map.get("expectedCheckInTime"), String.valueOf(dayEndTime), NumberOperators.LESS_THAN_EQUAL));

            List<SupplementRecord> supplementRecords = new ArrayList<>();
            supplementRecords.add((SupplementRecord)map.get("moduleState"));
            supplementRecords.add((SupplementRecord)map.get("host"));
            supplementRecords.add((SupplementRecord)map.get("visitedSpace"));


            List<InviteVisitorContextV3> visitors= V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, InviteVisitorContextV3.class, criteria, supplementRecords);

            if(visitors != null) {
                visitors.forEach(visitor -> {
                    HomepageWidgetData widget = new HomepageWidgetData();
                    JSONObject params = new JSONObject();
                    String visitedSpace = "---";
                    if(visitor.getVisitedSpace() != null && visitor.getVisitedSpace().getName() != null) {
                        visitedSpace = visitor.getVisitedSpace().getName();
                    }
                    String primaryText = visitor.getVisitorName()+ " is waiting to meet you";
                    widget.setIcon(4);
                    widget.setTitle(visitor.getModuleState().getDisplayName());
                    widget.setPrimaryText(primaryText);
//                    widget.setPrimaryText(visitor.getHost().getName());
                    widget.setSecondaryText(visitedSpace);
                    widget.setModuleName(module.getName());
                    widget.setSecondaryText2("Today");
                    params.put("record", visitor);
                    widget.setParams(params);
                    widget.setRecordId(visitor.getId());
                    widgets.add(widget);

                });
            }


        }


        return widgets;
    }

    public static FacilioStatus getTicketfromName(String moduleName, String status) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(moduleName);

        FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

        SelectRecordsBuilder<FacilioStatus> selectBuilder = new SelectRecordsBuilder<FacilioStatus>()
                .moduleName(ticketModule.getName())
                .select(modBean.getAllFields(ticketModule.getName()))
                .beanClass(FacilioStatus.class)
                .andCondition(CriteriaAPI.getCondition(ticketModule.getTableName() + ".PARENT_MODULEID", "parentModuleId", String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(ticketModule.getTableName() + ".STATUS", "status", status, StringOperators.CONTAINS));

        List<FacilioStatus> ticketStatusList = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(ticketStatusList)) {
            return ticketStatusList.get(0);
        }
        return null;
    }
    public static List<HomepageWidgetData> getMyDeliveriesActionCards()throws Exception {
        List<HomepageWidgetData> widgets = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.DELIVERIES);
        FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
        List<FacilioField> deliveryFileds = modBean.getAllFields(module.getName());

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));


        if (peopleId != null) {

            SelectRecordsBuilder<V3DeliveriesContext> selectBuilder = new SelectRecordsBuilder<V3DeliveriesContext>()
                    .moduleName(module.getName())
                    .select(deliveryFileds)
                    .beanClass(V3DeliveriesContext.class)
                    .innerJoin(ticketModule.getTableName())
                    .on(ticketModule.getTableName()+".ID = "+module.getTableName()+".MODULE_STATE")
                    .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".EMPLOYEE_ID", "employee", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(ticketModule.getTableName() + ".STATUS", "status", "pending", StringOperators.CONTAINS));



            selectBuilder.fetchSupplement((LookupField) fieldMap.get("deliveryArea"));
            selectBuilder.fetchSupplement((LookupField) fieldMap.get("moduleState"));


            List<V3DeliveriesContext> deliveriesList = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(deliveriesList))
            {
                deliveriesList.forEach(deliveries -> {
                    HomepageWidgetData widget = new HomepageWidgetData();

                    JSONObject params = new JSONObject();

                    widget.setSecondaryText("");
                    widget.setSecondaryText2("");
                    FacilioField carrierField;
                    try {
                        carrierField = modBean.getField("carrier", module.getName());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    EnumField enums = (EnumField) carrierField;
                    String carrierName= (String) enums.getEnumMap().get(deliveries.getCarrier());
                    String secondaryText = "Package from " + carrierName + " is waiting for you";
                    if (deliveries.getDeliveryArea() != null && deliveries.getDeliveryArea().getName() != null) {
                        widget.setSecondaryText(deliveries.getDeliveryArea().getName());
                        widget.setSecondaryText2(secondaryText);
                    }

//                    String primaryText = "Package " + deliveries.getTrackingNumber() + " arrived";

                    String primaryText = "Your Package is arrived";

                    if (deliveries.getModuleState() != null && deliveries.getModuleState().getDisplayName() != null) {
                        widget.setTitle(deliveries.getModuleState().getDisplayName());
                    }
                    widget.setIcon(4);
                    widget.setPrimaryText(primaryText);
                    widget.setModuleName(module.getName());
                    params.put("record", deliveries);
                    widget.setParams(params);
                    widget.setRecordId(deliveries.getId());
                    widgets.add(widget);
                });

            }
        }

        return widgets;
    }

        public static  List<HomepageWidgetData> getMyDeskBookingActionCards() throws Exception {
            List <HomepageWidgetData> widgets = new ArrayList<>();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
            List<V3FacilityBookingContext> bookingList = getMyActionBookingList(module);
            if(CollectionUtils.isNotEmpty(bookingList)) {
                bookingList.forEach(booking -> {
                    try {
                        HomepageWidgetData widget = getWidgetFromBookingData(booking, module);
                        if (widget != null) {
                            widgets.add(widget);

                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return widgets;
        }

    public static  List<HomepageWidgetData> getMyParkingBookingActionCards() throws Exception {
        List <HomepageWidgetData> widgets = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
        List<V3FacilityBookingContext> bookingList = getMyActionBookingList(module);
        if(CollectionUtils.isNotEmpty(bookingList)) {
            bookingList.forEach(booking -> {
                try {
                    HomepageWidgetData widget = getWidgetFromBookingData(booking, module);
                    if (widget != null) {
                        widgets.add(widget);

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return widgets;
    }
    public static  List<HomepageWidgetData> getMySpaceBookingActionCards() throws Exception {
        List <HomepageWidgetData> widgets = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        List<V3FacilityBookingContext> bookingList = getMyActionBookingList(module);
        if(CollectionUtils.isNotEmpty(bookingList)) {
            bookingList.forEach(booking -> {
                try {
                    HomepageWidgetData widget = getWidgetFromBookingData(booking, module);
                    if (widget != null) {
                        widgets.add(widget);

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return widgets;
    }

        public static HomepageWidgetData getWidgetFromBookingData(V3FacilityBookingContext booking, FacilioModule module) throws Exception {
            HomepageWidgetData widget = new HomepageWidgetData();
            JSONObject params = new JSONObject();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule FacilityBookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);


            try {
                FacilioStatus moduleState = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TICKET_STATUS, booking.getModuleState().getId());
                booking.setModuleState(moduleState);
                widget.setPrimaryText(" Your upcoming " + module.getDisplayName() + " booking");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            widget.setTitle(booking.getModuleState().getDisplayName());
            widget.setRecordId(booking.getId());
            widget.setModuleName(FacilityBookingModule.getName());
            params.put("record", booking);
            widget.setParams(params);
            widget.setSecondaryText(booking.getFacility().getName());
            widget.setModuleName(FacilityBookingModule.getName());


            List<BookingSlotsContext> bookingSlostList = null;
            List<SlotContext> slotList = null;
            try {
                bookingSlostList = FacilityAPI.getBookingSlots(booking.getId());
                slotList = bookingSlostList.stream().map(bookingslot -> bookingslot.getSlot()).collect(Collectors.toList());;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Long durationstartTime = null;
            Long durationEndTime = null;

            String secondaryText2 = "";
            String date = "";

            if (CollectionUtils.isNotEmpty(slotList)) {

                if (slotList.size() == 1) {
                    SlotContext slot = slotList.get(0);
                    durationstartTime = slot.getSlotStartTime();
                    durationEndTime = slot.getSlotEndTime();
                }
                else {
                    SlotContext firstSlot = slotList.get(0);
                    SlotContext lastSlot = slotList.get(slotList.size() - 1);
                    durationstartTime = firstSlot.getSlotStartTime();
                    durationEndTime = lastSlot.getSlotEndTime();
                }

                if(durationstartTime != null && durationEndTime != null) {
                    date = new SimpleDateFormat("dd MMM yyyy").format(durationstartTime);
                    secondaryText2 += new SimpleDateFormat("hh:mm a").format(durationstartTime);
                    secondaryText2 += " to " + new SimpleDateFormat("hh:mm a").format(durationEndTime);
                    secondaryText2 = secondaryText2.replace("am", "AM").replace("pm","PM");
                }
                widget.setDate(date);
                widget.setSecondaryText2(secondaryText2);
                return widget;
            }
            else {
                return null;
            }

        }

        public static  List<V3FacilityBookingContext> getMyActionBookingList(FacilioModule parentModule) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<V3FacilityBookingContext> bookingList = new ArrayList<>();
        FacilioModule FacilityBookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        FacilioModule StateFlowModule = ModuleFactory.getStateRuleTransitionModule();

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();
        Long dayEndTime = DateTimeUtil.getDayEndTimeOf(daystartTime);

        Map<String, FacilioField> facilityBookingFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilityBookingModule.getName()));

        if(peopleId != null ) {

            FacilioStatus activeStatus = TicketAPI.getStatus(FacilityBookingModule, "Active");

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getStateRuleTransitionFields());


            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(StateFlowModule.getTableName())
                    .select(Collections.singletonList(fieldMap.get("id")))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("fromStateId"), String.valueOf(activeStatus.getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("buttonType"), String.valueOf(0), NumberOperators.GREATER_THAN));


            List<Map<String, Object>> props = builder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                List<Long> ids = props.stream().map(prop -> (long) prop.get("id")).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(ids)) {
                    Long id = ids.get(0);
                    if (id != null) {

                        SelectRecordsBuilder<V3FacilityBookingContext> selectBuilder = new SelectRecordsBuilder<V3FacilityBookingContext>()
                                .moduleName(FacilityBookingModule.getName())
                                .select(modBean.getAllFields(FacilityBookingModule.getName()))
                                .beanClass(V3FacilityBookingContext.class);

                        selectBuilder
                                .innerJoin("Facility")
                                .on("FacilityBooking.FACILITY_ID = Facility.ID")
                                .andCondition(CriteriaAPI.getCondition("FacilityBooking.RESERVED_FOR", "reservedFor", String.valueOf(peopleId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("FacilityBooking.BOOKING_DATE", "bookingDate", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL))
                                .andCondition(CriteriaAPI.getCondition("Facility.PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("FacilityBooking.MODULE_STATE", "moduleState", String.valueOf(activeStatus.getId()), NumberOperators.EQUALS))
                                .fetchSupplement((LookupField) facilityBookingFieldMap.get("facility"));

                        bookingList = selectBuilder.get();
                    }

                }


            }
        }

        return bookingList;
    }

    public static JSONObject getRecentlyReservedSpace() throws Exception {
        JSONObject widgetData = new JSONObject();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        List<FacilityContext> facilityList = getMyPreviousBookedFacilityList(module);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        List<SupplementRecord> supplementRecords = new ArrayList<SupplementRecord>();
        supplementRecords.add((SupplementRecord) fieldMap.get("spaceCategory"));

        List<Long> spaceIds = new ArrayList<Long>();
        facilityList.forEach(facility -> {
            spaceIds.add(facility.getLocation().getId());
        });



        List<V3SpaceContext> spaceList =  V3RecordAPI.getRecordsListWithSupplements(module.getName(), spaceIds, V3SpaceContext.class, null, supplementRecords);

        widgetData.put("reservedSpaces",spaceList);
        return widgetData;
    }

    public static List<HomepageWidgetData> getMyDeliveriesActionCard() throws Exception {
        List <HomepageWidgetData> list = new ArrayList<>();

        return list;
    }

    public static List<HomepageWidgetData> getLatestMyBookingActionCards()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FACILITY_BOOKING);
        List<V3FacilityBookingContext> bookingList = getMyBookingList(module);


        List<HomepageWidgetData> widgetDataList = new ArrayList<>();

        HomepageWidgetData widgetData1 = new HomepageWidgetData();
        Long startTime = System.currentTimeMillis();


        bookingList.forEach(booking -> {

            HomepageWidgetData widgetData = new HomepageWidgetData();
            JSONObject params = new JSONObject();
            try {
                FacilioModule parentModule = modBean.getModule(booking.getFacility().getParentModuleId());

                FacilioStatus moduleState = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TICKET_STATUS, booking.getModuleState().getId());
                booking.setModuleState(moduleState);
                widgetData.setPrimaryText("Upcoming " + parentModule.getDisplayName() + " booking");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            widgetData.setTitle(booking.getModuleState().getDisplayName());
            widgetData.setRecordId(booking.getId());
            widgetData.setModuleName(module.getName());
            params.put("record", booking);
            widgetData.setParams(params);
            widgetData.setSecondaryText(booking.getFacility().getName());
            widgetData.setModuleName(module.getName());

            List<Long> facilityIds = new ArrayList<Long>();
            facilityIds.add(booking.getFacility().getId());



            String secondaryText2 = "";
            String date = "";


            Long endTime = DateTimeUtil.addDays(startTime, 7);

            List<BookingSlotsContext> bookingSlostList = null;
            List<SlotContext> slotList = null;
            try {
                bookingSlostList = FacilityAPI.getBookingSlots(booking.getId());
                slotList = bookingSlostList.stream().map(bookingslot -> bookingslot.getSlot()).collect(Collectors.toList());;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Long durationstartTime = null;
            Long durationEndTime = null;


            if (CollectionUtils.isNotEmpty(slotList)) {
                if (slotList.size() == 1) {
                    SlotContext slot = slotList.get(0);
                    durationstartTime = slot.getSlotStartTime();
                    durationEndTime = slot.getSlotEndTime();
                }
                else {
                    SlotContext firstSlot = slotList.get(0);
                    SlotContext lastSlot = slotList.get(slotList.size() - 1);
                    durationstartTime = firstSlot.getSlotStartTime();
                    durationEndTime = lastSlot.getSlotEndTime();
                }

                if(durationstartTime != null && durationEndTime != null) {
                    date= new SimpleDateFormat("dd MMM yyyy").format(durationstartTime);
                    secondaryText2 +=new SimpleDateFormat("hh:mm a").format(durationstartTime);
                    secondaryText2 += " to " + new SimpleDateFormat("hh:mm a").format(durationEndTime);
                    secondaryText2 = secondaryText2.replace("am", "AM").replace("pm","PM");

                }
                widgetData.setDate(date);
                widgetData.setSecondaryText2(secondaryText2);
            }
            widgetDataList.add(widgetData);

        });

        return widgetDataList;
    }

    public static  List<V3FacilityBookingContext>  getMyBookingList(FacilioModule module)throws Exception {
        List<V3FacilityBookingContext> bookinglist = new ArrayList<V3FacilityBookingContext>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();

        if(peopleId != null ) {
            SelectRecordsBuilder<V3FacilityBookingContext> selectBuilder = new SelectRecordsBuilder<V3FacilityBookingContext>()
                    .module(module)
                    .beanClass(V3FacilityBookingContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.RESERVED_FOR", "reservedFor", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.BOOKING_DATE", "bookingDate", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));

            selectBuilder.fetchSupplement((LookupField) fieldMap.get("facility"));


            bookinglist = selectBuilder.get();

        }

    return bookinglist;
    }
    public static  List<FacilityContext>  getMyPreviousBookedFacilityList(FacilioModule module)throws Exception {
        List<FacilityContext> facilityList = new ArrayList<FacilityContext>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule FacilityModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();
        if(peopleId != null ) {


            SelectRecordsBuilder<FacilityContext> selectBuilder = new SelectRecordsBuilder<FacilityContext>()
                    .moduleName(FacilityModule.getName())
                    .select(modBean.getAllFields(FacilityModule.getName()))
                    .beanClass(FacilityContext.class)
                    .limit(4);

            selectBuilder
                    .innerJoin("FacilityBooking")
                    .on("FacilityBooking.FACILITY_ID = Facility.ID")
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.RESERVED_FOR", "reservedFor", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.BOOKING_DATE", "bookingDate", String.valueOf(daystartTime), NumberOperators.LESS_THAN))
                    .orderBy("BOOKING_DATE DESC");

            facilityList = selectBuilder.get();

        }

        return facilityList;
    }


    public static HomepageWidgetData getMyDeliveries()throws Exception {
        V3DeliveriesContext deliveriesContext = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.DELIVERIES);
        FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
        List<FacilioField> deliveryFileds = modBean.getAllFields(module.getName());

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        HomepageWidgetData widget = new HomepageWidgetData();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));


        if (peopleId != null) {

            SelectRecordsBuilder<V3DeliveriesContext> selectBuilder = new SelectRecordsBuilder<V3DeliveriesContext>()
                    .moduleName(module.getName())
                    .select(deliveryFileds)
                    .beanClass(V3DeliveriesContext.class)
                    .innerJoin(ticketModule.getTableName())
                    .on(ticketModule.getTableName()+".ID = "+module.getTableName()+".MODULE_STATE")
                    .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".EMPLOYEE_ID", "employee", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(ticketModule.getTableName() + ".STATUS", "status", "pending", StringOperators.CONTAINS))
                    .limit(1);



            selectBuilder.fetchSupplement((LookupField) fieldMap.get("deliveryArea"));


            List<V3DeliveriesContext> list = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(list))
            {
                deliveriesContext = list.get(0);

                widget.setSecondaryText("");
                widget.setSecondaryText2("");
               if (deliveriesContext.getDeliveryArea() !=null && deliveriesContext.getDeliveryArea().getName() != null) {
                   widget.setSecondaryText(deliveriesContext.getDeliveryArea().getName());
                   widget.setSecondaryText2("package is waiting for pickup");
               }

               String primaryText = "Package " + deliveriesContext.getTrackingNumber() + " arrived";

                widget.setIcon(4);
                widget.setTitle(module.getDisplayName());
                widget.setPrimaryText(primaryText);
                widget.setModuleName(module.getName());
                return widget;
            }
        }

        return null;
    }

    public static HomepageWidgetData getLatestMyDeskBooking()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        HomepageWidgetData assignedWidget = getMyAssignedDesk();
        if (assignedWidget != null) {
            assignedWidget.setIcon(1);
            assignedWidget.setTitle(module.getDisplayName());
            assignedWidget.setPrimaryText("Your desk");
            assignedWidget.setModuleName(module.getName());
            return  assignedWidget;
        }
        else {
            HomepageWidgetData bookingwidget = getLatestMyBooking(module.getModuleId());
            if (bookingwidget != null) {
                bookingwidget.setIcon(1);
                bookingwidget.setTitle(module.getDisplayName());
                bookingwidget.setPrimaryText("Upcoming " + module.getDisplayName() + " booking list");
                bookingwidget.setModuleName(module.getName());
                return bookingwidget;
            }
        }
        return null;
    }

    public static HomepageWidgetData getMyAssignedParking()throws Exception {
        V3ParkingStallContext parking = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        HomepageWidgetData parkingWidget = new HomepageWidgetData();
        if (peopleId != null) {
            SelectRecordsBuilder<V3ParkingStallContext> selectBuilder = new SelectRecordsBuilder<V3ParkingStallContext>()
                    .module(module)
                    .beanClass(V3ParkingStallContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("Parking_Stall.EMPLOYEE_ID", "employee", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .limit(1);


            List<V3ParkingStallContext> parkingList = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(parkingList))
            {
                parking = parkingList.get(0);
                parkingWidget.setIcon(2);
                parkingWidget.setSecondaryText(parking.getName());
                parkingWidget.setSecondaryText2("Permanent");
                parkingWidget.setModuleName(module.getName());
                return parkingWidget;
            }
        }

        return null;
    }
    public static HomepageWidgetData getMyAssignedDesk()throws Exception {
        V3DeskContext mydesk = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        HomepageWidgetData deskWidget = new HomepageWidgetData();
        if (peopleId != null) {
            SelectRecordsBuilder<V3DeskContext> selectBuilder = new SelectRecordsBuilder<V3DeskContext>()
                    .module(module)
                    .beanClass(V3DeskContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("Desks.EMPLOYEE_ID", "employee", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .limit(1);


            List<V3DeskContext> deskList = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(deskList))
            {
                mydesk = deskList.get(0);
                deskWidget.setIcon(1);
                deskWidget.setSecondaryText(mydesk.getName());
                deskWidget.setSecondaryText2("Permanent");
                deskWidget.setModuleName(module.getName());
                return deskWidget;
            }
        }

        return null;
    }
    public static HomepageWidgetData getLatestMyParkingBooking()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PARKING_STALL);

        HomepageWidgetData assignedWidget = getMyAssignedParking();

        if (assignedWidget != null) {
            assignedWidget.setIcon(2);
            assignedWidget.setTitle(module.getDisplayName());
            assignedWidget.setPrimaryText("Your parking");
            assignedWidget.setModuleName(module.getName());
            return assignedWidget;
        }
        else {
            HomepageWidgetData widget = getLatestMyBooking(module.getModuleId());
            if (widget != null) {
                widget.setIcon(2);
                widget.setTitle(module.getDisplayName());
                widget.setPrimaryText("Upcoming " + module.getDisplayName() + " booking list");
                widget.setModuleName(module.getName());
                return widget;
            }
        }
        return null;
    }
    public static HomepageWidgetData getLatestMySpaceBooking()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        HomepageWidgetData widget = getLatestMyBooking(module.getModuleId());
        if (widget != null) {
            widget.setIcon(3);
            widget.setTitle(module.getDisplayName());
            widget.setPrimaryText("Upcoming " + module.getDisplayName() + " booking list");
            widget.setModuleName(module.getName());
        }
        return widget;
    }

    public static List<Long> getFacilityIdsByParentModuleId(Long parentId)throws Exception{
        List<Long> facilityIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        SelectRecordsBuilder<FacilityContext> selectBuilder = new SelectRecordsBuilder<FacilityContext>()
                .module(module)
                .beanClass(FacilityContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition("Facility.PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentId), NumberOperators.EQUALS));

        List<FacilityContext> facilityList = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(facilityList)) {

            facilityList.forEach(facility -> {
                facilityIds.add(facility.getId());
            });
        }

        return facilityIds;
    }
    public static HomepageWidgetData getLatestMyBooking(Long parentId)throws Exception{
        HomepageWidgetData widget = new HomepageWidgetData();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FACILITY_BOOKING);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long startTime = System.currentTimeMillis();
        Long daystartTime = DateTimeUtil.getDayStartTime();

        if(peopleId != null ) {
            SelectRecordsBuilder<V3FacilityBookingContext> selectBuilder = new SelectRecordsBuilder<V3FacilityBookingContext>()
                    .module(module)
                    .beanClass(V3FacilityBookingContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.RESERVED_FOR", "reservedFor", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("FacilityBooking.BOOKING_DATE", "bookingDate", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));


            if(parentId != null) {
                List <Long> facilityIds = getFacilityIdsByParentModuleId(parentId);
                if(CollectionUtils.isNotEmpty(facilityIds)) {
                    StringJoiner idString = new StringJoiner(",");
                    for(Long facilityId : facilityIds) {
                        idString.add(String.valueOf(facilityId));
                    }
                    selectBuilder.andCondition(CriteriaAPI.getCondition("FacilityBooking.FACILITY_ID", "facility", idString.toString(), NumberOperators.EQUALS));
                }
                else {
                    return null;
                }
            }
            selectBuilder.limit(1);

            selectBuilder.fetchSupplement((LookupField) fieldMap.get("facility"));
        //    selectBuilder.fetchSupplement((LookupField) fieldMap.get("slotList"));



            List<V3FacilityBookingContext> facilityList = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(facilityList))
            {
                V3FacilityBookingContext latestBooking = facilityList.get(0);
                List<Long> facilityIds = new ArrayList<Long>();
                facilityIds.add(latestBooking.getFacility().getId());



                String secondaryText2 = "";
                String date = "";


                Long endTime = DateTimeUtil.addDays(startTime, 7);

                List<SlotContext> slotList = FacilityAPI.getFacilityBookedSlotsForTimeRange(facilityIds, daystartTime, endTime);

                Long durationstartTime = null;
                Long durationEndTime = null;

                if (CollectionUtils.isNotEmpty(slotList)) {
                    if (slotList.size() == 1) {
                        SlotContext slot = slotList.get(0);
                        durationstartTime = slot.getSlotStartTime();
                        durationEndTime = slot.getSlotEndTime();
                    }
                    else {
                        SlotContext firstSlot = slotList.get(0);
                        SlotContext lastSlot = slotList.get(slotList.size() - 1);
                        durationstartTime = firstSlot.getSlotStartTime();
                        durationEndTime = lastSlot.getSlotEndTime();
                    }

                    if(durationstartTime != null && durationEndTime != null) {
                        date = new SimpleDateFormat("dd MMM yyyy").format(durationstartTime);
                        secondaryText2 +=new SimpleDateFormat("hh:mm a").format(durationstartTime);
                        secondaryText2 += " to " + new SimpleDateFormat("hh:mm a").format(durationEndTime);
                        secondaryText2 = secondaryText2.replace("am", "AM").replace("pm","PM");

                    }
                    widget.setSecondaryText(latestBooking.getFacility().getName());
                    widget.setDate(date);
                    widget.setSecondaryText2(secondaryText2);
                    widget.setModuleName(module.getName());
                    return widget;
                }
                else {
                    return null;
                }
            }

        }


        return null;
    }
}
