package com.facilio.fsm.actions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

@Setter @Getter
@Log4j
public class ServiceOrderAction extends V3Action {
    private String moduleName;
    private Long orderId;
    private String status;
    private String identifier;
    private String buttonAction;
    private boolean validate;
    private Long preferredStartTime;
    private Long servicePMId;
    private Boolean rescheduleSubsequent;

    private static Map<String,String> statusMap = new HashMap<String, String>() {{
        put("cancelSO", FacilioConstants.ServiceOrder.CANCELLED);
        put("cloneSO", "Clone");
        put("associateSOSP", "Associate");
        put("completeWork", FacilioConstants.ServiceOrder.COMPLETED);
        put("closeSO", FacilioConstants.ServiceOrder.CLOSED);
        put("reschedule", "Reschedule");
    }};


    public String updateServiceOrders()throws Exception{
        if(orderId == null){
            throw new FSMException(FSMErrorCode.SO_ID_NOT_EMPTY);
        }
        if(statusMap.get(identifier).equals("Clone") || statusMap.get(identifier).equals("Associate") ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, statusMap.get(identifier) +" feature is not enabled");
        }
        try {
            HashMap<String, String> successMsg = new HashMap<>();
            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);
            ServiceOrderTicketStatusContext inProgressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
            ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);
            ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);
            ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);
            String statusData = statusMap.get(identifier);
            ServiceOrderTicketStatusContext newStatus =  ServiceOrderAPI.getStatus(statusData);
            if(newStatus != null && newStatus.getId() == cancelledState.getId()){
                if(serviceOrderInfo.getStatus().getId() ==  cancelledState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(serviceOrderInfo.getStatus().getId() == completedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(serviceOrderInfo.getStatus().getId() == closedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(validate){
                    successMsg.put("title","Cancel Work Order");
                    successMsg.put("message","Cancelling Work Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                    setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);
                    return V3Action.SUCCESS;
                }
                serviceOrderInfo.setStatus(newStatus);
                User user = AccountUtil.getCurrentAccount().getUser();
                if(user != null){
                    V3PeopleContext cancelledBy = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,user.getPeopleId(),V3PeopleContext.class);
                    serviceOrderInfo.setCancelledBy(cancelledBy);
                }
                serviceOrderInfo.setCancelledTime(DateTimeUtil.getCurrenTime());
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Work Order Cancelled Successfully");
            }

            if(newStatus != null && newStatus.getId() == completedState.getId()){
                if(serviceOrderInfo.getStatus().getId() != inProgressState.getId()){
                    throw new FSMException(FSMErrorCode.SO_COMPLETE_WARNING);
                }
                if(validate){
                    successMsg.put("title","Complete Work Order");
                    successMsg.put("message","Proceeding with completing the Work Order will also mark all associated Appointments and Tasks as completed. Are you sure you want to proceed?");
                    setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);
                    return V3Action.SUCCESS;
                }

                serviceOrderInfo.setStatus(newStatus);
                Long startDuration = serviceOrderInfo.getActualStartTime();
                Long endDuration = System.currentTimeMillis();
                serviceOrderInfo.setActualEndTime(endDuration);
                serviceOrderInfo.setActualDuration(endDuration - startDuration);
                User user = AccountUtil.getCurrentAccount().getUser();
                if(user != null){
                    V3PeopleContext completedBy = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,user.getPeopleId(),V3PeopleContext.class);
                    serviceOrderInfo.setCompletedBy(completedBy);
                }
                serviceOrderInfo.setCompletedTime(endDuration);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Work Order Completed Successfully");
            }

            if(newStatus !=null && newStatus.getId() == closedState.getId()){
                if(serviceOrderInfo.getStatus() != null && serviceOrderInfo.getStatus().getId() != completedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CLOSE_WARNING);
                }
                serviceOrderInfo.setStatus(closedState);
                User user = AccountUtil.getCurrentAccount().getUser();
                if(user != null){
                    V3PeopleContext closedBy = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,user.getPeopleId(),V3PeopleContext.class);
                    serviceOrderInfo.setClosedBy(closedBy);
                }
                serviceOrderInfo.setClosedTime(DateTimeUtil.getCurrenTime());
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Work Order Closed Successfully");
            }
            if(identifier.equals("reschedule")){
                if(serviceOrderInfo.getStatus()!=null && serviceOrderInfo.getStatus().getId() == cancelledState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_EDIT_FAILED);
                }
                ModuleBean modBean = Constants.getModBean();
                FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ServiceOrder.SERVICE_ORDER);
                ServicePlannedMaintenanceContext servicePM = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,servicePMId,ServicePlannedMaintenanceContext.class);
                Integer leadTime = servicePM.getLeadTime();
                Long estimatedDuration = servicePM.getEstimatedDuration();
                List<ServiceOrderContext> serviceOrders = new ArrayList<>();
                serviceOrders.add(serviceOrderInfo);
                if(rescheduleSubsequent){
                    List<ServiceOrderContext> subsequentServiceOrders = ServicePlannedMaintenanceAPI.getSubsequentServiceOrders(servicePMId,serviceOrderInfo.getPreferredStartTime());
                    if(CollectionUtils.isNotEmpty(subsequentServiceOrders)){
                        serviceOrders.addAll(subsequentServiceOrders);
                    }
                }
                List<ModuleBaseWithCustomFields> oldServiceOrders = new ArrayList<>();
                List<ServiceOrderContext> updatedServiceOrders = new ArrayList<>();
                for(ServiceOrderContext serviceOrder : serviceOrders){
                    if(isTimeAfterToday(preferredStartTime)){
                        ServiceOrderContext oldServiceOrder = FieldUtil.cloneBean(serviceOrder,ServiceOrderContext.class) ;
                        oldServiceOrders.add(oldServiceOrder);
                        serviceOrder.setPreferredStartTime(preferredStartTime);
                        serviceOrder.setCreatedTime(preferredStartTime);
                        if(leadTime!=null && leadTime>0){
                            Long createdTime = getCreatedTime(preferredStartTime,leadTime);
                            if(isTimeAfterToday(createdTime)){
                                serviceOrder.setCreatedTime(createdTime);
                            }else{
                                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Rescheduling 'Preferred Start Date' updates 'Created Time' based on 'Lead Time'. Please reschedule to ensure that 'Created Time' is not current or past date");
                            }
                        }
                        if(estimatedDuration!=null && estimatedDuration>0){
                            Long preferredEndTime = preferredStartTime + estimatedDuration * 1000;
                            serviceOrder.setPreferredEndTime(preferredEndTime);
                        }
                        updatedServiceOrders.add(serviceOrder);
                    }else{
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"You can only reschedule to a future date. Please select a date that is after the current date.");
                    }
                }
                V3Util.processAndUpdateBulkRecords(serviceOrderModule,oldServiceOrders,FieldUtil.getAsMapList(updatedServiceOrders,ServiceOrderContext.class) , null, null, null, null, null, null, null, null, true,false,null);
                successMsg.put("message","Your Work Order has been rescheduled successfully!");
            }
            setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);

        }catch(Exception e){
            if(e instanceof FSMException){
                throw e;
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,e.getMessage());
            }
        }
        return SUCCESS;
    }
    private Boolean isTimeAfterToday(Long time){
        LocalDate currentDate = LocalDate.now();
        LocalDate nextDate = currentDate.plusDays(1);
        LocalDateTime nextDayMidNight = nextDate.atStartOfDay();
        Long nextDayTime = nextDayMidNight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return time >= nextDayTime;
    }
    private Long getCreatedTime(Long preferredStartTime,Integer leadTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(preferredStartTime);
        calendar.add(Calendar.DAY_OF_MONTH,-leadTime);
        Long createdTime = calendar.getTimeInMillis();
        return createdTime;
    }
}
