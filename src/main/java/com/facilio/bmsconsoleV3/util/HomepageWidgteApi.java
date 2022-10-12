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
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
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

    public static  Map<String, List<HomepageWidgetData>> getMySpaceBookingNutshellData()throws Exception {

        Map<String, List<HomepageWidgetData>> widgetMapData = new HashMap<>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        FacilioModule parkingModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        FacilioModule visitorModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR);



        List<V3SpaceBookingContext> bookingsList = getMySpaceBookings();

       if(bookingsList != null) {
           List<HomepageWidgetData> deskWidgetList = new ArrayList<>();
           List<HomepageWidgetData> parkingWidgteList = new ArrayList<>();
           List<HomepageWidgetData> spaceWidgetList = new ArrayList<>();

           bookingsList.forEach(booking -> {
               try {
                   if(deskModule.getModuleId() == booking.getParentModuleId()) {
                       deskWidgetList.add(getWidgetDataFromSPaceBookingContext(booking, deskModule));
                   }
                   else if(parkingModule.getModuleId() == booking.getParentModuleId()) {
                       parkingWidgteList.add(getWidgetDataFromSPaceBookingContext(booking, parkingModule));
                   }
                   else {
                       spaceWidgetList.add(getWidgetDataFromSPaceBookingContext(booking, spaceModule));
                   }


               } catch (Exception e) {
                   throw new RuntimeException(e);
               }

           });

           widgetMapData.put(deskModule.getName(),deskWidgetList);
           widgetMapData.put(parkingModule.getName(), parkingWidgteList);
           widgetMapData.put(spaceModule.getName(), spaceWidgetList);
           widgetMapData.put(visitorModule.getName(), getMyVisitorActionCards());


       }


        return widgetMapData;
    }

    public static HomepageWidgetData getWidgetDataFromSPaceBookingContext (V3SpaceBookingContext spaceBooking, FacilioModule module) throws  Exception {
        HomepageWidgetData widget = new HomepageWidgetData();

        widget.setTitle(module.getDisplayName());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        FacilioModule parkingModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);

        V3SpaceContext space = spaceBooking.getSpace();


        if(deskModule.getModuleId() == spaceBooking.getParentModuleId()) {
            widget.setPrimaryText("You desk is " + space.getName());
            widget.setSpace(space);
            widget.setDate(spaceBooking.getBookingStartTime());
            String time = "";
            time += new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingStartTime());
            time += " to " + new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingEndTime());
            time = time.replace("am", "AM").replace("pm","PM");
            widget.setTime(time);
        }
        else if(parkingModule.getModuleId() == spaceBooking.getParentModuleId()) {
            widget.setPrimaryText("Slot No " + space.getName());
            widget.setSpace(space);
            widget.setDate(spaceBooking.getBookingStartTime());
            String time = "";
            time += new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingStartTime());
            time += " to " + new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingEndTime());
            time = time.replace("am", "AM").replace("pm","PM");
            widget.setTime(time);
        }
        else {
            widget.setPrimaryText("You space is " + space.getName());
            widget.setSpace(space);
            widget.setDate(spaceBooking.getBookingStartTime());
            String time = "";
            time += new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingStartTime());
            time += " to " + new SimpleDateFormat("hh:mm a").format(spaceBooking.getBookingEndTime());
            time = time.replace("am", "AM").replace("pm","PM");
            widget.setTime(time);
        }

        return widget;
    }
    public static List<V3SpaceBookingContext> getMySpaceBookings()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> map = FieldFactory.getAsMap(fields);

        Long daystartTime = DateTimeUtil.getDayStartTime();
        Long dayEndTime = DateTimeUtil.getDayEndTimeOf(daystartTime);


        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("host"), String.valueOf(peopleId), NumberOperators.EQUALS));
//        criteria.addOrCondition(CriteriaAPI.getCondition(map.get("internalAttendees"), String.valueOf(peopleId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(map.get("bookingStartTime"), String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));

        List<SupplementRecord> supplementRecords = new ArrayList<>();
        supplementRecords.add((SupplementRecord)map.get("host"));
//        supplementRecords.add((SupplementRecord)map.get("internalAttendees"));
        supplementRecords.add((SupplementRecord)map.get("space"));

        List<V3SpaceBookingContext> spaceBookingList = new ArrayList<>();

        spaceBookingList= V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, V3SpaceBookingContext.class, criteria, supplementRecords);


        return spaceBookingList;
    }

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

                               try {
                                   FacilioStatus moduleState = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TICKET_STATUS, visitor.getModuleState().getId());
                                   visitor.setModuleState(moduleState);
                               }
                               catch (Exception e) {
                                   throw new RuntimeException(e);
                               }

                    String primaryText = visitor.getVisitorName()+ " will arrive soon";
                    widget.setIcon(4);
                    widget.setTitle(visitor.getModuleState().getDisplayName());
                    widget.setTitle(module.getDisplayName());
                    widget.setPrimaryText(primaryText);
                    widget.setSecondaryText(visitedSpace);
                    widget.setModuleName(module.getName());
                    widget.setSecondaryText2("Today");
                    params.put("record", visitor);
                    widget.setParams(params);
                    widget.setRecordId(visitor.getId());


                    widget.setDate(visitor.getExpectedCheckInTime());

                  String time = "";
                  time += new SimpleDateFormat("hh:mm a").format(visitor.getExpectedCheckInTime());
                  time += " to " + new SimpleDateFormat("hh:mm a").format(visitor.getExpectedCheckOutTime());
                  time = time.replace("am", "AM").replace("pm","PM");
                  widget.setTime(time);
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

                    String primaryText = "Your can now pick up your package";

                    if (deliveries.getModuleState() != null && deliveries.getModuleState().getDisplayName() != null) {
                        widget.setTitle(deliveries.getModuleState().getDisplayName());
                    }
                    widget.setIcon(4);
                    widget.setPrimaryText(primaryText);
                    widget.setModuleName(module.getName());
                    params.put("record", deliveries);
                    widget.setParams(params);
                    widget.setRecordId(deliveries.getId());
    

                    widget.setDate((deliveries.getReceivedTime()));

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
            List<V3SpaceBookingContext> bookingList = getMyActionBookingList(module);
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
        List<V3SpaceBookingContext> bookingList = getMyActionBookingList(module);
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
        List<V3SpaceBookingContext> bookingList = getMyActionBookingList(module);
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

        public static HomepageWidgetData getWidgetFromBookingData(V3SpaceBookingContext booking, FacilioModule module) throws Exception {
            HomepageWidgetData widget = new HomepageWidgetData();
            JSONObject params = new JSONObject();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule SpaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
            FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
            FacilioModule parkingModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.PARKING);
            FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);

            V3SpaceContext space = booking.getSpace();

            try {
                FacilioStatus moduleState = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TICKET_STATUS, booking.getModuleState().getId());
                booking.setModuleState(moduleState);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            HashMap<String, Object> startTimeData = DateTimeUtil.getTimeData(booking.getBookingStartTime());
            int startDay = (int) startTimeData.get("day");
            widget.setTitle(booking.getModuleState().getDisplayName());
            widget.setRecordId(booking.getId());
            widget.setSpace(space);
            widget.setDate(booking.getBookingStartTime());
            
            String time = "";
            time += new SimpleDateFormat("hh:mm a").format(booking.getBookingStartTime());
            time += " to " + new SimpleDateFormat("hh:mm a").format(booking.getBookingEndTime());
            time = time.replace("am", "AM").replace("pm", "PM");
            widget.setTime(time);

             params.put("record", booking);
             widget.setParams(params);
             widget.setSecondaryText(booking.getSpace().getName());

             if(deskModule.getModuleId() == booking.getParentModuleId()) {

                widget.setPrimaryText("You desk is " + space.getName());
                widget.setModuleName(deskModule.getName());

             }
            else if(parkingModule.getModuleId() == booking.getParentModuleId()) {
                widget.setPrimaryText("Slot No " + space.getName());
                 widget.setModuleName(parkingModule.getName());

             }
            else {
                widget.setPrimaryText("You space is " + space.getName());
                 widget.setModuleName(SpaceBookingModule.getName());

             }

                return widget;
            }



    public static  List<V3SpaceBookingContext> getMyActionBookingList(FacilioModule parentModule) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<V3SpaceBookingContext>bookingList = new ArrayList<>();
        FacilioModule SpaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        FacilioModule StateFlowModule = ModuleFactory.getStateRuleTransitionModule();

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();
        Long dayEndTime = DateTimeUtil.getDayEndTimeOf(daystartTime);

        Map<String, FacilioField> spaceBookingFieldMap = FieldFactory.getAsMap(modBean.getAllFields(SpaceBookingModule.getName()));

        if(peopleId != null ) {

            FacilioStatus activeStatus = TicketAPI.getStatus(SpaceBookingModule, "Pending");

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

                        SelectRecordsBuilder<V3SpaceBookingContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingContext>()
                                .moduleName(SpaceBookingModule.getName())
                                .select(modBean.getAllFields(SpaceBookingModule.getName()))
                                .beanClass(V3SpaceBookingContext.class);
//
                        selectBuilder
//                                .innerJoin("Facility")
//                                .on("FacilityBooking.FACILITY_ID = Facility.ID")
                                .andCondition(CriteriaAPI.getCondition("SpaceBooking.HOST", "host", String.valueOf(peopleId), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("SpaceBooking.BOOKING_STARTTIME", "bookingStartTime", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL))
                                .andCondition(CriteriaAPI.getCondition("SpaceBooking.PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS))
                                .andCondition(CriteriaAPI.getCondition("SpaceBooking.MODULE_STATE", "moduleState", String.valueOf(activeStatus.getId()), NumberOperators.EQUALS))
                                .fetchSupplement((LookupField) spaceBookingFieldMap.get("space"));

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
        List<V3SpaceBookingContext> spaceBookingList = getMyPreviousBookedSpaceList(module);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        List<SupplementRecord> supplementRecords = new ArrayList<SupplementRecord>();
        supplementRecords.add((SupplementRecord) fieldMap.get("spaceCategory"));

        List<Long> spaceIds = new ArrayList<Long>();
        spaceBookingList.forEach(spaceBooking -> {
            spaceIds.add(spaceBooking.getSpace().getId());
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
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<V3SpaceBookingContext> bookingList = getMyBookingList(module);


        List<HomepageWidgetData> widgetDataList = new ArrayList<>();

        HomepageWidgetData widgetData1 = new HomepageWidgetData();
        Long startTime = System.currentTimeMillis();


        bookingList.forEach(booking -> {

            HomepageWidgetData widgetData = new HomepageWidgetData();
            JSONObject params = new JSONObject();
            try {
                FacilioModule parentModule = modBean.getModule(booking.getParentModuleId());

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
            widgetData.setSecondaryText(booking.getSpace().getName());
            widgetData.setModuleName(module.getName());

            List<Long> spaceIds = new ArrayList<Long>();
            spaceIds.add(booking.getSpace().getId());


            String secondaryText2 = "";
            String date = "";


            Long endTime = DateTimeUtil.addDays(startTime, 7);

            Long durationstartTime = booking.getBookingStartTime();
            Long durationEndTime = booking.getBookingEndTime();


                if(durationstartTime != null && durationEndTime != null) {
                    date= new SimpleDateFormat("dd MMM yyyy").format(durationstartTime);
                    secondaryText2 +=new SimpleDateFormat("hh:mm a").format(durationstartTime);
                    secondaryText2 += " to " + new SimpleDateFormat("hh:mm a").format(durationEndTime);
                    secondaryText2 = secondaryText2.replace("am", "AM").replace("pm","PM");

                }
                widgetData.setDate(booking.getBookingStartTime());
                widgetData.setSecondaryText2(secondaryText2);

            widgetDataList.add(widgetData);

        });

        return widgetDataList;
    }

    public static  List<V3SpaceBookingContext>  getMyBookingList(FacilioModule module)throws Exception {
        List<V3SpaceBookingContext> bookinglist = new ArrayList<V3SpaceBookingContext>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();

        if(peopleId != null ) {
            SelectRecordsBuilder<V3SpaceBookingContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingContext>()
                    .module(module)
                    .beanClass(V3SpaceBookingContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.HOST", "host", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.BOOKING_STARTTIME", "bookingStartTime", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));

            selectBuilder.fetchSupplement((LookupField) fieldMap.get("space"));


            bookinglist = selectBuilder.get();

        }

    return bookinglist;
    }
    public static  List<V3SpaceBookingContext>  getMyPreviousBookedSpaceList(FacilioModule module)throws Exception {
        List<V3SpaceBookingContext> spaceBookingList = new ArrayList<V3SpaceBookingContext>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);

        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long daystartTime = DateTimeUtil.getDayStartTime();
        if(peopleId != null ) {


            SelectRecordsBuilder<V3SpaceBookingContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingContext>()
                    .moduleName(spaceBookingModule.getName())
                    .select(modBean.getAllFields(spaceBookingModule.getName()))
                    .beanClass(V3SpaceBookingContext.class)
                    .limit(4);

            selectBuilder
//                    .innerJoin("FacilityBooking")
//                    .on("FacilityBooking.FACILITY_ID = Facility.ID")
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.HOST", "host", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.BOOKING_STARTTIME", "bookingStartTime", String.valueOf(daystartTime), NumberOperators.LESS_THAN))
                    .orderBy("BOOKING_STARTTIME DESC");

            spaceBookingList = selectBuilder.get();

        }

        return spaceBookingList;
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
                   widget.setSecondaryText2("You can now pickup your package");
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

    public static List<Long> getSpaceIdsByParentModuleId(Long parentId)throws Exception{
        List<Long> spaceIds = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        SelectRecordsBuilder<V3SpaceBookingContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingContext>()
                .module(module)
                .beanClass(V3SpaceBookingContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition("SpaceBooking.PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentId), NumberOperators.EQUALS));

        List<V3SpaceBookingContext> spaceList = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(spaceList)) {

            spaceList.forEach(space -> {
                spaceIds.add(space.getId());
            });
        }

        return spaceIds;
    }
    public static HomepageWidgetData getLatestMyBooking(Long parentId)throws Exception{
        HomepageWidgetData widget = new HomepageWidgetData();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
        Long startTime = System.currentTimeMillis();
        Long daystartTime = DateTimeUtil.getDayStartTime();

        if(peopleId != null ) {
            SelectRecordsBuilder<V3SpaceBookingContext> selectBuilder = new SelectRecordsBuilder<V3SpaceBookingContext>()
                    .module(module)
                    .beanClass(V3SpaceBookingContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.HOST", "host", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SpaceBooking.BOOKING_STARTTIME", "bookingStartTime", String.valueOf(daystartTime), NumberOperators.GREATER_THAN_EQUAL));


            if(parentId != null) {
                List <Long> spaceIds = getSpaceIdsByParentModuleId(parentId);
                if(CollectionUtils.isNotEmpty(spaceIds)) {
                    StringJoiner idString = new StringJoiner(",");
                    for(Long spaceId : spaceIds) {
                        idString.add(String.valueOf(spaceId));
                    }
                    selectBuilder.andCondition(CriteriaAPI.getCondition("SpaceBooking.SPACE_ID", "space", idString.toString(), NumberOperators.EQUALS));
                }
                else {
                    return null;
                }
            }
            selectBuilder.limit(1);

            selectBuilder.fetchSupplement((LookupField) fieldMap.get("space"));


            List<V3SpaceBookingContext> spaceList = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(spaceList))
            {
                V3SpaceBookingContext latestBooking = spaceList.get(0);
                List<Long> spaceIds = new ArrayList<Long>();
                spaceIds.add(latestBooking.getSpace().getId());



                String secondaryText2 = "";
                String date = "";


                Long endTime = DateTimeUtil.addDays(startTime, 7);


                Long durationstartTime = latestBooking.getBookingStartTime();
                Long durationEndTime = latestBooking.getBookingEndTime();

                    if(durationstartTime != null && durationEndTime != null) {
                        date = new SimpleDateFormat("dd MMM yyyy").format(durationstartTime);
                        secondaryText2 +=new SimpleDateFormat("hh:mm a").format(durationstartTime);
                        secondaryText2 += " to " + new SimpleDateFormat("hh:mm a").format(durationEndTime);
                        secondaryText2 = secondaryText2.replace("am", "AM").replace("pm","PM");

                    }
                    widget.setSecondaryText(latestBooking.getSpace().getName());
                    widget.setDate(durationstartTime);
                    widget.setSecondaryText2(secondaryText2);
                    widget.setModuleName(module.getName());
                    return widget;
            }

        }


        return null;
    }
}
