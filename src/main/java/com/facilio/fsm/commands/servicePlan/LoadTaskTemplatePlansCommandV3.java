package com.facilio.fsm.commands.servicePlan;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoadTaskTemplatePlansCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        ServiceTaskTemplateContext serviceTask = (ServiceTaskTemplateContext) CommandUtil.getModuleData(context, FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String plannedItemModuleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(plannedItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ServicePlanItemsContext> builder = new SelectRecordsBuilder<ServicePlanItemsContext>()
                .moduleName(plannedItemModuleName)
                .select(fields)
                .beanClass(ServicePlanItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK_TEMPLATE", "serviceTaskTemplate", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType")));
        List<ServicePlanItemsContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
            serviceTask.setServicePlanItems(list);
        }

        String plannedToolModuleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_TOOLS;
        fields = modBean.getAllFields(plannedToolModuleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ServicePlanToolsContext> toolsBuilder = new SelectRecordsBuilder<ServicePlanToolsContext>()
                .moduleName(plannedToolModuleName)
                .select(fields)
                .beanClass(ServicePlanToolsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK_TEMPLATE", "serviceTaskTemplate", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("toolType")));
        List<ServicePlanToolsContext> toolsList = toolsBuilder.get();
        if(CollectionUtils.isNotEmpty(toolsList)) {
            serviceTask.setServicePlanTools(toolsList);
        }
        String plannedServiceModuleName = FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES;
        fields = modBean.getAllFields(plannedServiceModuleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ServicePlanServicesContext> servicesBuilder = new SelectRecordsBuilder<ServicePlanServicesContext>()
                .moduleName(plannedServiceModuleName)
                .select(fields)
                .beanClass(ServicePlanServicesContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK_TEMPLATE", "serviceTaskTemplate", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("service")));
        List<ServicePlanServicesContext> servicesList = servicesBuilder.get();
        if(CollectionUtils.isNotEmpty(servicesList)) {
            serviceTask.setServicePlanServices(servicesList);
        }
        return false;
    }
}
