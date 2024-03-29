package com.facilio.fsm.commands.serviceTasks;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetPlansCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask : serviceTasks){
                if(serviceTask!=null){
                    Map<String, List<SubFormContext>> relations = new HashMap<>();
                    Map<String, List<Map<String, Object>>> subForm = new HashMap<>();
                    if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedItems())){
                        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = serviceTask.getServiceOrderPlannedItems();
                        for(ServiceOrderPlannedItemsContext plannedItem: serviceOrderPlannedItems){
                            plannedItem.setServiceOrder(serviceTask.getServiceOrder());
                        }
                        SubFormContext<ServiceOrderPlannedItemsContext> plannedItemsContextSubFormContext = getPlannedItemsSubForm(serviceOrderPlannedItems);
                        relations.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS,Collections.singletonList(plannedItemsContextSubFormContext));
                        subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS,FieldUtil.getAsMapList(serviceOrderPlannedItems,ServiceOrderPlannedItemsContext.class));
                        List<Long> deleteIds = Collections.singletonList(4l);
                        //subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS_DELETE,FieldUtil.getAsMapList(deleteIds));

                    }
                    if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedTools())){
                        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = serviceTask.getServiceOrderPlannedTools();
                        for(ServiceOrderPlannedToolsContext plannedTool: serviceOrderPlannedTools){
                            plannedTool.setServiceOrder(serviceTask.getServiceOrder());
                        }
                        subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS,FieldUtil.getAsMapList(serviceOrderPlannedTools,ServiceOrderPlannedToolsContext.class));
                    }
                    if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedServices())){
                        List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = serviceTask.getServiceOrderPlannedServices();
                        for(ServiceOrderPlannedServicesContext serviceOrderPlannedService : serviceOrderPlannedServices){
                            serviceOrderPlannedService.setServiceOrder(serviceTask.getServiceOrder());
                        }
                        subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES,FieldUtil.getAsMapList(serviceOrderPlannedServices,ServiceOrderPlannedServicesContext.class));
                    }
                   // serviceTask.setSubForm(subForm);

                    serviceTask.setRelations(relations);
                }
            }
            recordMap.put(moduleName,serviceTasks);
            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        }
        return false;
    }
    private SubFormContext<ServiceOrderPlannedItemsContext> getPlannedItemsSubForm (List<ServiceOrderPlannedItemsContext> plannedItems) {
        SubFormContext<ServiceOrderPlannedItemsContext> subFormContext = new SubFormContext<>();
        subFormContext.setFieldId(-1);
        subFormContext.setData(plannedItems);
        subFormContext.setDeleteIds(Collections.singletonList(4l));
        return subFormContext;
    }
}
