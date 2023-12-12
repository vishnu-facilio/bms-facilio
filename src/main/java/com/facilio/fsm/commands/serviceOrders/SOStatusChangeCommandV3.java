package com.facilio.fsm.commands.serviceOrders;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SOStatusChangeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);

        List<ServiceOrderContext> dataList = (List<ServiceOrderContext>) recordMap.get(moduleName);
        List<ServiceOrderContext> serviceOrdersNew = new ArrayList<>();
        for(ServiceOrderContext order : dataList) {
            if(order.getStatus() ==null || order.getStatus().getTypeCode() < 0){

                if(order.getVendor() != null && order.getVendor().getId() > 0 && order.getFieldAgent() != null && order.getFieldAgent().getId() > 0){
                    V3PeopleContext people = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,order.getFieldAgent().getId(),V3PeopleContext.class);
                    if(people.getPeopleType() == V3PeopleContext.PeopleType.EMPLOYEE.getIndex()){
                        throw new FSMException(FSMErrorCode.SO_VENDOR_INTERNAL_FIELD_AGENT);
                    }
                }
                if(order.isAutoCreateSa()){

                    if(order.getPreferredStartTime() == null || order.getPreferredStartTime() < 0 ){
                        throw new FSMException(FSMErrorCode.SO_AUTOCREATE_SCHEDULEDSTART_TIME);
                    }

                    if(order.getPreferredEndTime() == null || order.getPreferredEndTime() < 0){
                        throw new FSMException(FSMErrorCode.SO_AUTOCREATE_SCHEDULEDEND_TIME);
                    }

                    if(order.getRelations() == null || order.getRelations().get("serviceTask") == null || order.getRelations().get("serviceTask").get(0) == null){
                        throw new FSMException(FSMErrorCode.SO_AUTOCREATE_TASKS_AVAILABILIY);
                    }

                    order.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED));
                    User user = AccountUtil.getCurrentAccount().getUser();
                    if(user != null){
                        V3PeopleContext scheduledBy = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,user.getPeopleId(),V3PeopleContext.class);
                        order.setScheduledBy(scheduledBy);
                    }
                    order.setScheduledTime(DateTimeUtil.getCurrenTime());
                }
                else {
                    order.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW));
                }
            }
            serviceOrdersNew.add(order);
        }
        recordMap.put(moduleName,serviceOrdersNew);
        context.put(Constants.RECORD_MAP,recordMap);
        return false;
    }
}
