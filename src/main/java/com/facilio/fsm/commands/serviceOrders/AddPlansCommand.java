package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fsm.context.*;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddPlansCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)) {
            for (ServiceOrderContext serviceOrder : serviceOrders) {
                if (serviceOrder.getServicePlannedMaintenance() != null && serviceOrder.getServicePlannedMaintenance().getId() > 0) {
                    ServicePlannedMaintenanceContext servicePM = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, serviceOrder.getServicePlannedMaintenance().getId());
                    if (servicePM.getServicePlan() != null && servicePM.getServicePlan().getId() > 0) {
                        createServiceOrderPlannedItems(servicePM.getServicePlan().getId(),serviceOrder);
                        createServiceOrderPlannedTools(servicePM.getServicePlan().getId(),serviceOrder);
                        createServiceOrderPlannedServices(servicePM.getServicePlan().getId(),serviceOrder);
                    }
                }
            }
        }
        return false;
    }
    private void createServiceOrderPlannedItems(Long servicePlanId,ServiceOrderContext serviceOrder)throws Exception{
        List<ServicePlanItemsContext> servicePlanItems = getServicePlanItems(servicePlanId);
        if(CollectionUtils.isNotEmpty(servicePlanItems)){
            List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = new ArrayList<>();
            for(ServicePlanItemsContext servicePlanItem : servicePlanItems){
                ServiceTaskContext serviceTask = getServiceTask(servicePlanItem.getServiceTaskTemplate(),serviceOrder.getId());
                ServiceOrderPlannedItemsContext serviceOrderPlannedItem = new ServiceOrderPlannedItemsContext();
                serviceOrderPlannedItem.setServiceOrder(serviceOrder);
                serviceOrderPlannedItem.setServiceTask(serviceTask);
                serviceOrderPlannedItem.setItemType(servicePlanItem.getItemType());
                serviceOrderPlannedItem.setStoreRoom(servicePlanItem.getStoreRoom());
                serviceOrderPlannedItem.setQuantity(servicePlanItem.getQuantity());
                serviceOrderPlannedItems.add(serviceOrderPlannedItem);
            }
            if(CollectionUtils.isNotEmpty(serviceOrderPlannedItems)){
                V3Util.preCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS, FieldUtil.getAsMapList(serviceOrderPlannedItems,ServiceOrderPlannedItemsContext.class),null,null);
            }
        }
    }
    private void createServiceOrderPlannedTools(Long servicePlanId,ServiceOrderContext serviceOrder)throws Exception{
        List<ServicePlanToolsContext> servicePlanTools = getServicePlanTools(servicePlanId);
        if(CollectionUtils.isNotEmpty(servicePlanTools)){
            List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = new ArrayList<>();
            for(ServicePlanToolsContext servicePlanTool : servicePlanTools){
                ServiceTaskContext serviceTask = getServiceTask(servicePlanTool.getServiceTaskTemplate(),serviceOrder.getId());
                ServiceOrderPlannedToolsContext serviceOrderPlannedTool = new ServiceOrderPlannedToolsContext();
                serviceOrderPlannedTool.setServiceOrder(serviceOrder);
                serviceOrderPlannedTool.setServiceTask(serviceTask);
                serviceOrderPlannedTool.setToolType(servicePlanTool.getToolType());
                serviceOrderPlannedTool.setStoreRoom(servicePlanTool.getStoreRoom());
                serviceOrderPlannedTool.setQuantity(servicePlanTool.getQuantity());
                serviceOrderPlannedTool.setDuration(servicePlanTool.getDuration());
                serviceOrderPlannedTools.add(serviceOrderPlannedTool);
            }
            if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools)){
                V3Util.preCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS, FieldUtil.getAsMapList(serviceOrderPlannedTools,ServiceOrderPlannedToolsContext.class),null,null);
            }
        }
    }
    private void createServiceOrderPlannedServices(Long servicePlanId,ServiceOrderContext serviceOrder)throws Exception{
        List<ServicePlanServicesContext> servicePlanServices = getServicePlanServices(servicePlanId);
        if(CollectionUtils.isNotEmpty(servicePlanServices)){
            List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = new ArrayList<>();
            for(ServicePlanServicesContext servicePlanService : servicePlanServices){
                ServiceTaskContext serviceTask = getServiceTask(servicePlanService.getServiceTaskTemplate(),serviceOrder.getId());
                ServiceOrderPlannedServicesContext serviceOrderPlannedService = new ServiceOrderPlannedServicesContext();
                serviceOrderPlannedService.setServiceOrder(serviceOrder);
                serviceOrderPlannedService.setServiceTask(serviceTask);
                serviceOrderPlannedService.setService(servicePlanService.getService());
                serviceOrderPlannedService.setQuantity(servicePlanService.getQuantity());
                serviceOrderPlannedService.setDuration(servicePlanService.getDuration());
                serviceOrderPlannedServices.add(serviceOrderPlannedService);
            }
            if(CollectionUtils.isNotEmpty(serviceOrderPlannedServices)){
                V3Util.preCreateRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES, FieldUtil.getAsMapList(serviceOrderPlannedServices,ServiceOrderPlannedServicesContext.class),null,null);
            }
        }
    }
    private List<ServicePlanItemsContext> getServicePlanItems(Long servicePlanId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS);
        SelectRecordsBuilder<ServicePlanItemsContext> recordsBuilder = new SelectRecordsBuilder<ServicePlanItemsContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServicePlanItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PLAN","servicePlan",String.valueOf(servicePlanId), NumberOperators.EQUALS));
        List<ServicePlanItemsContext>  servicePlanItems = recordsBuilder.get();
        return servicePlanItems;
    }
    private List<ServicePlanToolsContext> getServicePlanTools(Long servicePlanId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_TOOLS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_TOOLS);
        SelectRecordsBuilder<ServicePlanToolsContext> recordsBuilder = new SelectRecordsBuilder<ServicePlanToolsContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServicePlanToolsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PLAN","servicePlan",String.valueOf(servicePlanId), NumberOperators.EQUALS));
        List<ServicePlanToolsContext>  servicePlanTools = recordsBuilder.get();
        return servicePlanTools;
    }
    private List<ServicePlanServicesContext> getServicePlanServices(Long servicePlanId)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES);
        SelectRecordsBuilder<ServicePlanServicesContext> recordsBuilder = new SelectRecordsBuilder<ServicePlanServicesContext>()
                .module(module)
                .select(fields)
                .table(module.getTableName())
                .beanClass(ServicePlanServicesContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_PLAN","servicePlan",String.valueOf(servicePlanId), NumberOperators.EQUALS));
        List<ServicePlanServicesContext>  servicePlanServices = recordsBuilder.get();
        return servicePlanServices;
    }
    private ServiceTaskContext getServiceTask(ServiceTaskTemplateContext serviceTaskTemplate,Long serviceOrderId)throws Exception{
        if(serviceTaskTemplate!=null && serviceTaskTemplate.getId()>0){
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
            ServiceTaskTemplateContext serviceTaskTemplateRec = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE,serviceTaskTemplate.getId(),ServiceTaskTemplateContext.class);
            SelectRecordsBuilder<ServiceTaskContext> recordsBuilder = new SelectRecordsBuilder<ServiceTaskContext>()
                    .module(module)
                    .select(fields)
                    .table(module.getTableName())
                    .beanClass(ServiceTaskContext.class)
                    .andCondition(CriteriaAPI.getCondition("NAME","name",serviceTaskTemplateRec.getName(), StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder",String.valueOf(serviceOrderId), NumberOperators.EQUALS));
            ServiceTaskContext serviceTask = recordsBuilder.fetchFirst();
            return serviceTask;
        }
        return null;
    }
}
