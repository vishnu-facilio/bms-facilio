package com.facilio.fsm.actions;


import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fsm.commands.FSMReadOnlyChainFactory;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.context.*;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fsm.util.ServiceTaskUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class fsmAction extends V3Action {
    private long recordId;
    private String identifier;
    private String resourceIds;
    private Long appointmentId;
    private Long fieldAgentId;
    private Criteria criteria;
    private Long startTime;
    private Long endTime;
    private Long boardId;
    private boolean skipValidation;
    private Long scheduledStartTime;
    private Long scheduledEndTime;
    private LocationContext startLocation;
    private LocationContext endLocation;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    private String viewName;

    private String moduleName;
    private long appId = -1;
    private String status;

    public String resourceList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchPeopleListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PAGE,getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE,getPerPage());
        context.put(FacilioConstants.ContextNames.START_TIME,getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME,getEndTime());
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        context.put(FacilioConstants.ContextNames.SEARCH,getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS,getFilters());
        context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.PEOPLE);
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.Dispatcher.RESOURCES));
        return SUCCESS;
    }
    public String eventsList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchEventsListChain();
        FacilioContext context = chain.getContext();
        if (StringUtils.isNotEmpty(resourceIds)) {
            String[] ids = FacilioUtil.splitByComma(resourceIds);
            List<Long> defaultIdList = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
            context.put(FacilioConstants.ContextNames.PEOPLE_IDS, defaultIdList);
        }
        context.put(FacilioConstants.ContextNames.START_TIME,getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME,getEndTime());
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.Dispatcher.EVENTS));
        return SUCCESS;
    }
    public String serviceAppointmentList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchServiceAppointmentListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        context.put(FacilioConstants.ContextNames.VIEW_NAME,getViewName());
        context.put(FacilioConstants.ContextNames.PAGE,getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE,getPerPage());
        context.put(FacilioConstants.ContextNames.SEARCH,getSearch());
        if (StringUtils.isNotEmpty(getFilters())) {
            JSONParser parser = new JSONParser();
            JSONObject filterJson = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, filterJson);
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        context.put(FacilioConstants.ContextNames.ORDER_BY,getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,getOrderType());
        chain.execute();
        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }
    public String fetchDispatcherBoardList() throws Exception{
        FacilioContext context = new FacilioContext();
        FacilioChain fetchListChain = ReadOnlyChainFactory.getDispatcherBoardList();
        fetchListChain.execute(context);
        setData(FacilioConstants.Dispatcher.DISPATCHER_LIST,context.get(FacilioConstants.Dispatcher.DISPATCHER_LIST));
        return SUCCESS;
    }
    public String updateServiceAppointmentStatus() throws Exception{
        FacilioContext context = new FacilioContext();
        HashMap<String, String> successMsg = new HashMap<>();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        switch(getIdentifier()){
            case FacilioConstants.ServiceAppointment.DISPATCH:
                if(getRecordId() > 0 && (getFieldAgentId() != null && getFieldAgentId() >0)) {
                    context.put(FacilioConstants.ServiceAppointment.FIELD_AGENT_ID,getFieldAgentId());
                    FacilioChain dispatchChain = FsmTransactionChainFactoryV3.dispatchChain();
                    dispatchChain.execute(context);
                    successMsg.put("message","Service Appointment Dispatched Successfully");
                }
                else {
                    throw new FSMException(FSMErrorCode.SA_DETAILS_REQUIED);
                }
                break;
            case FacilioConstants.ServiceAppointment.START_TRIP:
                context.put(FacilioConstants.Trip.START_LOCATION,getStartLocation());
                FacilioChain startTripChain = FsmTransactionChainFactoryV3.startTripChain();
                startTripChain.execute(context);
                TripContext result = (TripContext) context.get(FacilioConstants.ContextNames.DATA);
                if(result != null){
                    setData(FacilioConstants.Trip.TRIP,result);
                }
                successMsg.put("message","Trip Started Successfully");
                break;
            case FacilioConstants.ServiceAppointment.END_TRIP:
                context.put(FacilioConstants.Trip.END_LOCATION,getEndLocation());
                FacilioChain endTripChain = FsmTransactionChainFactoryV3.endTripChain();
                endTripChain.execute(context);
                successMsg.put("message","Trip Ended Successfully");
                break;
            case FacilioConstants.ServiceAppointment.START_WORK:
                FacilioChain startSAChain = FsmTransactionChainFactoryV3.startSAChain();
                startSAChain.execute(context);
                successMsg.put("message","Service Appointment Started Successfully");
                break;
            case FacilioConstants.ServiceAppointment.COMPLETE:
                FacilioChain completeSAChain = FsmTransactionChainFactoryV3.completeSAChain();
                completeSAChain.execute(context);
                successMsg.put("message","Service Appointment Completed Successfully");
                break;
            case FacilioConstants.ServiceAppointment.CANCEL:
                FacilioChain cancelSAChain = FsmTransactionChainFactoryV3.cancelSAChain();
                cancelSAChain.execute(context);
                successMsg.put("message","Service Appointment Cancelled Successfully");
                break;
        }
        setData(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_STATUS_ACTIONS,successMsg);
        return SUCCESS;
    }
    public String dispatchServiceAppointment() throws Exception {

        Map<String, Object> mapping = new HashMap<>();
        DispatcherEventContext dispatcherSAEvent = null;
        Long fieldAgentId = getFieldAgentId();
        JSONObject bodyParams = new JSONObject();

        if (appointmentId == null || appointmentId < 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR,"appointment id is mandatory");
        }

        if(fieldAgentId != null && fieldAgentId > 0) {
            V3PeopleContext fieldAgent = V3PeopleAPI.getPeopleById(fieldAgentId);
            mapping.put("fieldAgent", fieldAgent);
            ServiceAppointmentTicketStatusContext dispatch = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.DISPATCHED);
            if(dispatch != null) {
                mapping.put("status", dispatch);
                bodyParams.put("updateStatus",true);
            }
        }

        if(scheduledStartTime != null && scheduledStartTime > 0){
            mapping.put("scheduledStartTime",scheduledStartTime);
        }

        if(scheduledEndTime != null && scheduledEndTime > 0){
            mapping.put("scheduledEndTime",scheduledEndTime);
        }

        if(MapUtils.isNotEmpty(mapping)) {
            bodyParams.put("skipValidation",skipValidation);
            FacilioContext context = V3Util.updateBulkRecords(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, mapping, Collections.singletonList(appointmentId), bodyParams,null,false,false);
            HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
            List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
            if(CollectionUtils.isNotEmpty(serviceAppointments)) {
                ServiceAppointmentContext serviceAppointment = serviceAppointments.get(0);
                if(serviceAppointment != null){
                    dispatcherSAEvent = new DispatcherEventContext();
                    dispatcherSAEvent.setServiceAppointmentContext(serviceAppointment);
                    dispatcherSAEvent.setEventType(DispatcherEventContext.EventType.SERVICE_APPOINTMENT.getIndex());
                    dispatcherSAEvent.setAllowResize(true);
                    dispatcherSAEvent.setAllowReschedule(true);
                    dispatcherSAEvent.setStartTime(serviceAppointment.getScheduledStartTime());
                    dispatcherSAEvent.setEndTime(serviceAppointment.getScheduledEndTime());
                    ServiceAppointmentTicketStatusContext saStatus = serviceAppointment.getStatus();
                    dispatcherSAEvent.setBackgroundColor(saStatus.getColor());
                }
            }
        }

        setData(FacilioConstants.Dispatcher.EVENT,dispatcherSAEvent);
        return SUCCESS;

    }

    public String updateServiceTaskStatus() throws Exception{
        FacilioContext context = new FacilioContext();
        HashMap<String, String> successMsg = new HashMap<>();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        switch(getIdentifier()){

            case FacilioConstants.ServiceAppointment.START_WORK:
                FacilioChain startTaskChain = FsmTransactionChainFactoryV3.startTaskChain();
                startTaskChain.execute(context);
                successMsg.put("message","Service Task Started Successfully");
                break;
            case FacilioConstants.ServiceAppointment.PAUSE:
                FacilioChain pauseTaskChain = FsmTransactionChainFactoryV3.pauseTaskChain();
                pauseTaskChain.execute(context);
                successMsg.put("message","Service Task Paused Successfully");
                break;
            case FacilioConstants.ServiceAppointment.RESUME:
                FacilioChain resumeTaskChain = FsmTransactionChainFactoryV3.resumeTaskChain();
                resumeTaskChain.execute(context);
                successMsg.put("message","Service Task Resumed Successfully");
                break;
//            case FacilioConstants.ServiceAppointment.REOPEN:
//                ServiceTaskUtil.moveToInProgress(getRecordId());
//                break;
            case FacilioConstants.ServiceAppointment.COMPLETE:
                FacilioChain completeTaskChain = FsmTransactionChainFactoryV3.completeTaskChain();
                completeTaskChain.execute(context);
                successMsg.put("message","Service Task Completed Successfully");
                break;
            case FacilioConstants.ServiceAppointment.CANCEL:
                FacilioChain cancelTaskChain = FsmTransactionChainFactoryV3.cancelTaskChain();
                cancelTaskChain.execute(context);
                successMsg.put("message","Service Task Cancelled Successfully");

                break;
        }
        setData(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_STATUS_ACTIONS,successMsg);

        return SUCCESS;
    }

    public String updateTimeSheetStatus() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        switch (getIdentifier()){
            case FacilioConstants.TimeSheet.STOP_TIME_SHEET:
                FacilioChain stopTimeSheetChain = FsmTransactionChainFactoryV3.stopTimeSheetChain();
                stopTimeSheetChain.execute(context);
                break;
        }
        return SUCCESS;
    }

}
