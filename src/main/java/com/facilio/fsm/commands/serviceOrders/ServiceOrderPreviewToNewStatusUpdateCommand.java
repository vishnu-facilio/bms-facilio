package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getServiceOrderStatus;

public class ServiceOrderPreviewToNewStatusUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            ServiceOrderTicketStatusContext serviceOrderStatus = getServiceOrderStatus(FacilioConstants.ServiceOrder.NEW);
            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
            for(ServiceOrderContext serviceOrder : serviceOrders){
                serviceOrder.setStatus(serviceOrderStatus);
                GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateContext();
                batchUpdate.addWhereValue("id", serviceOrder.getId());
                batchUpdate.addUpdateValue("status", serviceOrderStatus.getId());
                batchUpdateList.add(batchUpdate);
            }
            bulkUpdateServiceOrderStatus(batchUpdateList,moduleName);
            recordMap.put(moduleName, serviceOrders);
        }
        return false;
    }
    private void bulkUpdateServiceOrderStatus(List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList,String moduleName)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceOrderModule = modBean.getModule(moduleName);
        FacilioField serviceOrderStatusField = modBean.getField("status",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        FacilioField id = modBean.getField("id",FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(id);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(serviceOrderModule.getTableName())
                .fields(Collections.singletonList(serviceOrderStatusField));
        updateRecordBuilder.batchUpdate(whereFields,batchUpdateList);
    }
}
