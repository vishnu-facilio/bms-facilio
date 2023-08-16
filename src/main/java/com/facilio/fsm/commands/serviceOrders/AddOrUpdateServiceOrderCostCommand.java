package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.fsm.context.ServiceOrderCostContext.InventorySource;
import com.facilio.fsm.context.ServiceOrderCostContext.InventoryCostType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddOrUpdateServiceOrderCostCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST);
        InventoryCostType inventoryCostType = (InventoryCostType) context.get(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE);
        InventorySource inventorySource = (InventorySource) context.get(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            for(ServiceOrderContext serviceOrder : serviceOrders){
                Double cost = null;
                Long quantity = null;
                if(serviceOrder==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order cannot be empty");
                }
                if(inventorySource.equals(InventorySource.PLANS)){
                    if(inventoryCostType.equals(InventoryCostType.ITEMS)){
                        Map<String, Object> serviceOrderPlannedItem = getPlannedItemDetails(serviceOrder.getId());
                        cost= (Double) serviceOrderPlannedItem.get("totalItemsCost");
                        quantity= (Long) serviceOrderPlannedItem.get("totalItemsCount");
                    }else if(inventoryCostType.equals(InventoryCostType.TOOLS)){
                        Map<String, Object> serviceOrderPlannedTool = getPlannedToolDetails(serviceOrder.getId());
                        cost = (Double) serviceOrderPlannedTool.get("totalToolsCost");
                        quantity = (Long) serviceOrderPlannedTool.get("totalToolsCount");
                    }else if(inventoryCostType.equals(InventoryCostType.SERVICES)){
                        Map<String, Object> serviceOrderPlannedService = getPlannedServiceDetails(serviceOrder.getId());
                        cost = (Double) serviceOrderPlannedService.get("totalServicesCost");
                        quantity= (Long) serviceOrderPlannedService.get("totalServicesCount");
                    }
                }
                else if(inventorySource.equals(InventorySource.ACTUALS)){
                    if(inventoryCostType.equals(InventoryCostType.ITEMS)){
                        Map<String, Object> serviceOrderItem = getItemDetails(serviceOrder.getId());
                        cost= (Double) serviceOrderItem.get("totalItemsCost");
                        quantity= (Long) serviceOrderItem.get("totalItemsCount");
                    }else if(inventoryCostType.equals(InventoryCostType.TOOLS)){
                        Map<String, Object> serviceOrderTool = getToolDetails(serviceOrder.getId());
                        cost = (Double) serviceOrderTool.get("totalToolsCost");
                        quantity = (Long) serviceOrderTool.get("totalToolsCount");

                    }else if(inventoryCostType.equals(InventoryCostType.SERVICES)){
                        Map<String, Object> serviceOrderService = getServiceDetails(serviceOrder.getId());
                        cost = (Double) serviceOrderService.get("totalServicesCost");
                        quantity= (Long) serviceOrderService.get("totalServicesCount");
                    }

                }

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule serviceOrderCostModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_COST);
                List<ServiceOrderCostContext> serviceOrderCosts = getServiceOrderCost(serviceOrder.getId(),inventorySource,inventoryCostType);
                if(CollectionUtils.isNotEmpty(serviceOrderCosts)){
                    ServiceOrderCostContext serviceOrderCost = serviceOrderCosts.get(0);
                    serviceOrderCost.setCost(cost);
                    serviceOrderCost.setQuantity(quantity);
                    V3RecordAPI.updateRecord(serviceOrderCost, serviceOrderCostModule, modBean.getAllFields(serviceOrderCostModule.getName()));
                }else{
                    ServiceOrderCostContext serviceOrderCost = new ServiceOrderCostContext();
                    serviceOrderCost.setServiceOrder(serviceOrder);
                    serviceOrderCost.setCost(cost);
                    serviceOrderCost.setQuantity(quantity);
                    serviceOrderCost.setInventoryCostType(inventoryCostType.getIndex());
                    serviceOrderCost.setInventorySource(inventorySource.getIndex());
                    V3RecordAPI.addRecord(false, Collections.singletonList(serviceOrderCost),serviceOrderCostModule,modBean.getAllFields(serviceOrderCostModule.getName()));
                }

            }
        }
        return false;
    }
    private List<ServiceOrderCostContext> getServiceOrderCost(Long serviceOrderId,InventorySource inventorySource,InventoryCostType inventoryCostType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderCostModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_COST);
        List<FacilioField> serviceOrderCostFields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_COST);

        SelectRecordsBuilder<ServiceOrderCostContext> builder = new SelectRecordsBuilder<ServiceOrderCostContext>()
                .select(serviceOrderCostFields)
                .moduleName(serviceOrderCostModule.getName())
                .beanClass(ServiceOrderCostContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("INVENTORY_COST_TYPE","inventoryCostType", String.valueOf(inventoryCostType.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("INVENTORY_SOURCE","inventorySource", String.valueOf(inventorySource.getIndex()), NumberOperators.EQUALS));

        List<ServiceOrderCostContext> serviceOrderCosts = builder.get();
        return serviceOrderCosts;
    }
    private Map<String, Object> getItemDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalItemsCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalItemsCount", "COUNT(DISTINCT ITEM)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderItemsContext> builder = new SelectRecordsBuilder<ServiceOrderItemsContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderItems = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderItems)) {
            return serviceOrderItems.get(0);
        }
        return null;
    }

    private Map<String, Object> getToolDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalToolsCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalToolsCount", "COUNT(DISTINCT TOOL)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderToolsContext> builder = new SelectRecordsBuilder<ServiceOrderToolsContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderTools = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderTools)) {
            return  serviceOrderTools.get(0);
        }
        return null;
    }

    private Map<String, Object> getServiceDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalServicesCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalServicesCount", "COUNT(DISTINCT SERVICE)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderServiceContext> builder = new SelectRecordsBuilder<ServiceOrderServiceContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderServices = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderServices)) {
            return  serviceOrderServices.get(0);
        }
        return null;
    }
    private Map<String, Object> getPlannedItemDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalItemsCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalItemsCount", "COUNT(DISTINCT ITEM_TYPE)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderPlannedItemsContext> builder = new SelectRecordsBuilder<ServiceOrderPlannedItemsContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderPlannedItems = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderPlannedItems)) {
            return serviceOrderPlannedItems.get(0);
        }
        return null;
    }
    private Map<String, Object> getPlannedToolDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalToolsCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalToolsCount", "COUNT(DISTINCT TOOL_TYPE)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderPlannedToolsContext> builder = new SelectRecordsBuilder<ServiceOrderPlannedToolsContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderPlannedTools = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderPlannedTools)) {
            return serviceOrderPlannedTools.get(0);
        }
        return null;
    }
    private Map<String, Object> getPlannedServiceDetails(Long serviceOrderId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("totalServicesCost", "SUM(TOTAL_COST)", FieldType.DECIMAL));
        fields.add(FieldFactory.getField("totalServicesCount", "COUNT(DISTINCT SERVICE)", FieldType.NUMBER));

        SelectRecordsBuilder<ServiceOrderPlannedServicesContext> builder = new SelectRecordsBuilder<ServiceOrderPlannedServicesContext>()
                .select(fields)
                .moduleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES)
                .andCondition(CriteriaAPI.getCondition("SERVICE_ORDER","serviceOrder", String.valueOf(serviceOrderId), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> serviceOrderPlannedServices = builder.getAsProps();
        if (CollectionUtils.isNotEmpty(serviceOrderPlannedServices)) {
            return  serviceOrderPlannedServices.get(0);
        }
        return null;
    }
}
