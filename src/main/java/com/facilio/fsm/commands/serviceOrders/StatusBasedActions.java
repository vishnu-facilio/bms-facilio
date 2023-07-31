package com.facilio.fsm.commands.serviceOrders;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderStatusActions;
import org.apache.commons.chain.Context;

import java.util.*;

public class StatusBasedActions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String status = context.get(FacilioConstants.ContextNames.STATUS).toString();

        Map<String,String> editButton = new HashMap<>();
        editButton.put("label","Edit");
        editButton.put("type","default");
        editButton.put("status","1");

        Map<String,String> deleteButton = new HashMap<>();
        deleteButton.put("label","Delete");
        deleteButton.put("type","default");
        deleteButton.put("status","1");

        Map<String,String> cancelButton = new HashMap<>();
        cancelButton.put("label","Cancel");
        cancelButton.put("type","system");
        cancelButton.put("code",FacilioConstants.ServiceOrder.CANCELLED);
        cancelButton.put("status","1");
        cancelButton.put("api","/updateStatus/serviceOrder/{orderId}/{status}/{skipValidation}");

        Map<String,String> cloneButton = new HashMap<>();
        cloneButton.put("label","Clone");
        cloneButton.put("type","system");
        cloneButton.put("status","1");
        cloneButton.put("code",FacilioConstants.ServiceOrder.CLOSED);
        cloneButton.put("api","/ddd/ddd");

        Map<String,String> associateSPButton = new HashMap<>();
        associateSPButton.put("label","Associate Service Plan");
        associateSPButton.put("type","system");
        associateSPButton.put("status","1");
        cloneButton.put("code",FacilioConstants.ServiceOrder.CANCELLED);
        associateSPButton.put("api","/ddd/ddd");

        Map<String,String> downloadButton = new HashMap<>();
        downloadButton.put("label","Download");
        downloadButton.put("type","default");
        downloadButton.put("status","1");

        Map<String,String> printButton = new HashMap<>();
        printButton.put("label","Print");
        printButton.put("type","default");
        printButton.put("status","1");

        Map<String,String> completeButton = new HashMap<>();
        completeButton.put("label","Complete Work");
        completeButton.put("code",FacilioConstants.ServiceOrder.COMPLETED);
        completeButton.put("type","system");
        completeButton.put("status","1");
        completeButton.put("api","updateStatus/serviceOrder/{orderId}/{status}/{skipValidation}");

        Map<String,String> closeButton = new HashMap<>();
        closeButton.put("label","Close Order");
        closeButton.put("type","system");
        closeButton.put("code",FacilioConstants.ServiceOrder.CLOSED);
        closeButton.put("status","1");
        closeButton.put("api","updateStatus/serviceOrder/{orderId}/{status}/{skipValidation}");

        ServiceOrderStatusActions newStatus = new ServiceOrderStatusActions();
        newStatus.setPrimaryButton(editButton);
        newStatus.setSecondaryButton(deleteButton);
        newStatus.setMoreActions(Arrays.asList(cancelButton,downloadButton,printButton,cloneButton,associateSPButton));

        ServiceOrderStatusActions scheduledStatus = new ServiceOrderStatusActions();
        scheduledStatus.setPrimaryButton(editButton);
        scheduledStatus.setSecondaryButton(deleteButton);
        scheduledStatus.setMoreActions(Arrays.asList(cancelButton,downloadButton,printButton,cloneButton,associateSPButton));

        ServiceOrderStatusActions inProgressStatus = new ServiceOrderStatusActions();
        inProgressStatus.setPrimaryButton(completeButton);
        inProgressStatus.setSecondaryButton(editButton);
        inProgressStatus.setMoreActions(Arrays.asList(deleteButton,cancelButton,downloadButton,printButton,cloneButton,associateSPButton));

        ServiceOrderStatusActions completedStatus = new ServiceOrderStatusActions();
        completedStatus.setPrimaryButton(closeButton);
        completedStatus.setSecondaryButton(deleteButton);
        completedStatus.setMoreActions(Arrays.asList(downloadButton,printButton,cloneButton));

        ServiceOrderStatusActions closedStatus = new ServiceOrderStatusActions();
        closedStatus.setPrimaryButton(cloneButton);
        closedStatus.setMoreActions(Arrays.asList(downloadButton,printButton));

        ServiceOrderStatusActions cancelledStatus = new ServiceOrderStatusActions();
        cancelledStatus.setPrimaryButton(cloneButton);
        cancelledStatus.setMoreActions(Arrays.asList(downloadButton,printButton));

        Map<String,ServiceOrderStatusActions> featureList = new HashMap<>();
        featureList.put(FacilioConstants.ServiceOrder.NEW,newStatus);
        featureList.put(FacilioConstants.ServiceOrder.SCHEDULED,scheduledStatus);
        featureList.put(FacilioConstants.ServiceOrder.IN_PROGRESS,inProgressStatus);
        featureList.put(FacilioConstants.ServiceOrder.COMPLETED,completedStatus);
        featureList.put(FacilioConstants.ServiceOrder.CLOSED,closedStatus);
        featureList.put(FacilioConstants.ServiceOrder.CANCELLED,cancelledStatus);

        context.put("buttons", featureList.get(status));

        return false;
    }
}
