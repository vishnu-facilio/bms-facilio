package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import javax.xml.ws.Service;
import java.util.List;
import java.util.Map;

public class CreatePlansCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks =  recordMap.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask: serviceTasks){
                        ServiceTaskContext serviceTaskContext = new ServiceTaskContext();
                        serviceTaskContext.setId(serviceTask.getId());

                        ServiceOrderContext serviceOrderContext = new ServiceOrderContext();
                        serviceOrderContext.setId(serviceTask.getServiceOrder().getId());

                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedItems())){
                            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS);
                            List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = serviceTask.getServiceOrderPlannedItems();
                            for(ServiceOrderPlannedItemsContext plannedItem: serviceOrderPlannedItems){
                                plannedItem.setServiceTask(serviceTaskContext);
                                plannedItem.setServiceOrder(serviceOrderContext);
                            }
                            V3Util.createRecordList(module, FieldUtil.getAsMapList(serviceOrderPlannedItems, ServiceOrderPlannedItemsContext.class),null,null);
                        }
                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedTools())){
                            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS);
                            List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = serviceTask.getServiceOrderPlannedTools();
                            for(ServiceOrderPlannedToolsContext plannedTool: serviceOrderPlannedTools){
                                plannedTool.setServiceTask(serviceTaskContext);
                                plannedTool.setServiceOrder(serviceOrderContext);
                            }
                            V3Util.createRecordList(module, FieldUtil.getAsMapList(serviceOrderPlannedTools, ServiceOrderPlannedToolsContext.class),null,null);
                        }
                        if(CollectionUtils.isNotEmpty(serviceTask.getServiceOrderPlannedServices())){
                            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES);
                            List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = serviceTask.getServiceOrderPlannedServices();
                            for(ServiceOrderPlannedServicesContext serviceOrderPlannedService : serviceOrderPlannedServices){
                                serviceOrderPlannedService.setServiceTask(serviceTaskContext);
                                serviceOrderPlannedService.setServiceOrder(serviceOrderContext);
                            }
                            V3Util.createRecordList(module, FieldUtil.getAsMapList(serviceOrderPlannedServices, ServiceOrderPlannedServicesContext.class),null,null);

                        }
                    }
        }

        return false;
    }
}
