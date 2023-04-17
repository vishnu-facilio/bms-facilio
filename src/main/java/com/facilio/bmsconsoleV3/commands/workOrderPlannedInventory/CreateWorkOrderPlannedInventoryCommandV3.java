package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.*;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class CreateWorkOrderPlannedInventoryCommandV3 extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(CreateWorkOrderPlannedInventoryCommandV3.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3WorkOrderContext> workOrders = recordMap.get(moduleName);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (V3WorkOrderContext workOrder : workOrders) {
                if (workOrder.getJobPlan() != null && workOrder.getJobPlan().getId() > 0) {

                    V3WorkOrderContext workOrderContext = workOrder;
                    Long jobPlanId = workOrder.getJobPlan().getId();
                    List<WorkOrderPlannedItemsContext> workOrderPlannedItems = getWorkOrderPlannedItems(jobPlanId, workOrderContext);
                    List<WorkOrderPlannedToolsContext> workOrderPlannedTools = getWorkOrderPlannedTools(jobPlanId, workOrderContext);
                    List<WorkOrderPlannedServicesContext> workOrderPlannedServices = getWorkOrderPlannedServices(jobPlanId, workOrderContext);
                    List<V3WorkOrderLabourPlanContext> workOrderPlannedLabour = getWorkOrderPlannedLabour(jobPlanId, workOrderContext);


                    V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_ITEMS), FieldUtil.getAsMapList(workOrderPlannedItems, WorkOrderPlannedItemsContext.class), null, null);
                    V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_TOOLS), FieldUtil.getAsMapList(workOrderPlannedTools, WorkOrderPlannedToolsContext.class), null, null);
                    V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES), FieldUtil.getAsMapList(workOrderPlannedServices, WorkOrderPlannedServicesContext.class), null, null);
                    V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN), FieldUtil.getAsMapList(workOrderPlannedLabour, V3WorkOrderLabourPlanContext.class), null, null);
                }
            }
        }
        catch(Exception e){
            LOGGER.error("Exception during CreateWorkOrderPlannedInventoryCommandV3 --: "  , e);
            }
        return false;
    }
    public List<WorkOrderPlannedItemsContext> getWorkOrderPlannedItems(Long jobPlanId,V3WorkOrderContext workOrder) throws Exception {
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = new ArrayList<>();
        List<JobPlanItemsContext> jobPlanItems = getJobPlanItems(jobPlanId);
        for(JobPlanItemsContext jobPlanItem : jobPlanItems){

            WorkOrderPlannedItemsContext workOrderPlannedItem = new WorkOrderPlannedItemsContext();

            Double unitPrice = jobPlanItem.getItemType().getLastPurchasedPrice()!=null ? jobPlanItem.getItemType().getLastPurchasedPrice() : null;
            Double quantity = jobPlanItem.getQuantity();
            workOrderPlannedItem.setWorkOrder(workOrder);
            workOrderPlannedItem.setItemType(jobPlanItem.getItemType());
            if(jobPlanItem.getItemType().getDescription()!=null){
                workOrderPlannedItem.setDescription(jobPlanItem.getItemType().getDescription());
            }
            workOrderPlannedItem.setQuantity(quantity);
            workOrderPlannedItem.setUnitPrice(unitPrice);
            workOrderPlannedItem.setTotalCost(unitPrice * quantity);
            if(jobPlanItem.getStoreRoom()!=null){
                workOrderPlannedItem.setStoreRoom(jobPlanItem.getStoreRoom());
            }
            workOrderPlannedItems.add(workOrderPlannedItem);
        }

        return workOrderPlannedItems;
    }
    public List<WorkOrderPlannedToolsContext> getWorkOrderPlannedTools(Long jobPlanId,V3WorkOrderContext workOrder) throws Exception {
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = new ArrayList<>();
        List<JobPlanToolsContext> jobPlanTools = getJobPlanTools(jobPlanId);
        for(JobPlanToolsContext jobPlanTool : jobPlanTools){

            WorkOrderPlannedToolsContext workOrderPlannedTool = new WorkOrderPlannedToolsContext();

            Double rate = jobPlanTool.getToolType().getSellingPrice()!=null ? jobPlanTool.getToolType().getSellingPrice() : null;
            Double quantity = jobPlanTool.getQuantity();
            Double duration = jobPlanTool.getDuration();

            workOrderPlannedTool.setWorkOrder(workOrder);
            workOrderPlannedTool.setToolType(jobPlanTool.getToolType());
            if(jobPlanTool.getToolType().getDescription()!=null){
                workOrderPlannedTool.setDescription(jobPlanTool.getToolType().getDescription());
            }
            workOrderPlannedTool.setQuantity(quantity);
            workOrderPlannedTool.setRate(rate);
            workOrderPlannedTool.setDuration(duration);
            workOrderPlannedTool.setTotalCost(rate * quantity * duration);
            if(jobPlanTool.getStoreRoom()!=null){
                workOrderPlannedTool.setStoreRoom(jobPlanTool.getStoreRoom());
            }
            workOrderPlannedTools.add(workOrderPlannedTool);
        }

        return workOrderPlannedTools;
    }
    public List<WorkOrderPlannedServicesContext> getWorkOrderPlannedServices(Long jobPlanId,V3WorkOrderContext workOrder) throws Exception {
        List<WorkOrderPlannedServicesContext> workOrderPlannedServices = new ArrayList<>();
        List<JobPlanServicesContext> jobPlanServices = getJobPlanServices(jobPlanId);
        for(JobPlanServicesContext jobPlanService : jobPlanServices){

            WorkOrderPlannedServicesContext workOrderPlannedService = new WorkOrderPlannedServicesContext();

            Double unitPrice = jobPlanService.getService().getBuyingPrice()!=null ? jobPlanService.getService().getBuyingPrice() : null;
            Double quantity = jobPlanService.getQuantity();
            Double duration = jobPlanService.getDuration();
            Double totalCost = V3InventoryUtil.getServiceCost(jobPlanService.getService(), duration, quantity);
            workOrderPlannedService.setWorkOrder(workOrder);
            workOrderPlannedService.setService(jobPlanService.getService());
            if(jobPlanService.getService().getDescription()!=null){
                workOrderPlannedService.setDescription(jobPlanService.getService().getDescription());
            }
            workOrderPlannedService.setQuantity(quantity);
            workOrderPlannedService.setUnitPrice(unitPrice);
            workOrderPlannedService.setDuration(duration);
            workOrderPlannedService.setTotalCost(totalCost);

            workOrderPlannedServices.add(workOrderPlannedService);
        }

        return workOrderPlannedServices;
    }

    public List<V3WorkOrderLabourPlanContext> getWorkOrderPlannedLabour(Long jobPlanId, V3WorkOrderContext workOrder) throws Exception {
        List<V3WorkOrderLabourPlanContext> workOrderPlannedLabours = new ArrayList<>();
        List<V3JobPlanLabourContext> jobPlanLabours = getJobPlanLabour(jobPlanId);
        for(V3JobPlanLabourContext jobPlanLabour : jobPlanLabours){

            V3WorkOrderLabourPlanContext workOrderPlannedLabour = new V3WorkOrderLabourPlanContext();

            workOrderPlannedLabour.setParent(workOrder);
            workOrderPlannedLabour.setCraft(jobPlanLabour.getCraft());
            workOrderPlannedLabour.setSkill(jobPlanLabour.getSkill());
            workOrderPlannedLabour.setQuantity(jobPlanLabour.getQuantity());
            workOrderPlannedLabour.setDuration(jobPlanLabour.getDuration());
            workOrderPlannedLabour.setRate(jobPlanLabour.getRate());
           workOrderPlannedLabour.setTotalPrice(jobPlanLabour.getTotalPrice());

            workOrderPlannedLabours.add(workOrderPlannedLabour);
        }

        return workOrderPlannedLabours;
    }

    public List<JobPlanItemsContext> getJobPlanItems(Long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.JOB_PLAN_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<JobPlanItemsContext> builder = new SelectRecordsBuilder<JobPlanItemsContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(JobPlanItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("JOB_PLAN_ID", "jobPlan", String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("storeRoom")));
        List<JobPlanItemsContext> jobPlanItems = builder.get();
        return jobPlanItems;
    }
    public List<JobPlanToolsContext> getJobPlanTools(Long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.JOB_PLAN_TOOLS;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<JobPlanToolsContext> builder = new SelectRecordsBuilder<JobPlanToolsContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(JobPlanToolsContext.class)
                .andCondition(CriteriaAPI.getCondition("JOB_PLAN_ID", "jobPlan", String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("storeRoom")));
        List<JobPlanToolsContext> jobPlanTools = builder.get();
        return jobPlanTools;
    }
    public List<JobPlanServicesContext> getJobPlanServices(Long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.JOB_PLAN_SERVICES;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<JobPlanServicesContext> builder = new SelectRecordsBuilder<JobPlanServicesContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(JobPlanServicesContext.class)
                .andCondition(CriteriaAPI.getCondition("JOB_PLAN_ID", "jobPlan", String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("service")));
        List<JobPlanServicesContext> jobPlanServices = builder.get();
        return jobPlanServices;
    }
    public List<V3JobPlanLabourContext> getJobPlanLabour(Long jobPlanId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.JOB_PLAN_LABOURS;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3JobPlanLabourContext> builder = new SelectRecordsBuilder<V3JobPlanLabourContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(V3JobPlanLabourContext.class)
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(jobPlanId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("craft"), (LookupField) fieldsAsMap.get("skill")));

        List<V3JobPlanLabourContext> jobPlanLabour = builder.get();
        return jobPlanLabour;
    }
}
