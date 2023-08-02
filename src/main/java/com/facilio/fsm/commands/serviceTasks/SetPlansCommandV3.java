package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetPlansCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask : serviceTasks){
                if(serviceTask!=null){
                    if(serviceTask.getId()>0){
                        DeleteRecordBuilder<ServiceOrderPlannedItemsContext> itemDeleteBuilder = new DeleteRecordBuilder<ServiceOrderPlannedItemsContext>()
                                .module(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS))
                                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(serviceTask.getId()), NumberOperators.EQUALS));
                        itemDeleteBuilder.delete();
                        DeleteRecordBuilder<ServiceOrderPlannedToolsContext> toolDeleteBuilder = new DeleteRecordBuilder<ServiceOrderPlannedToolsContext>()
                                .module(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS))
                                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(serviceTask.getId()), NumberOperators.EQUALS));
                        toolDeleteBuilder.delete();
                        DeleteRecordBuilder<ServiceOrderPlannedServicesContext> serviceDeleteBuilder = new DeleteRecordBuilder<ServiceOrderPlannedServicesContext>()
                                .module(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES))
                                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(serviceTask.getId()), NumberOperators.EQUALS));
                        serviceDeleteBuilder.delete();
                    }
                    Map<String, List<Map<String, Object>>> subForm = new HashMap<>();
                    if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedItems())){
                        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = serviceTask.getServiceOrderPlannedItems();
                        for(ServiceOrderPlannedItemsContext plannedItem: serviceOrderPlannedItems){
                            plannedItem.setServiceOrder(serviceTask.getServiceOrder());
                        }
                        subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS,FieldUtil.getAsMapList(serviceOrderPlannedItems,ServiceOrderPlannedItemsContext.class));
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
                    serviceTask.setSubForm(subForm);
                }
            }
            recordMap.put(moduleName,serviceTasks);
            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        }
        return false;
    }
}
