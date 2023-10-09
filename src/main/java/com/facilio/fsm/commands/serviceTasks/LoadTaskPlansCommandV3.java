package com.facilio.fsm.commands.serviceTasks;

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

public class LoadTaskPlansCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        ServiceTaskContext serviceTask = (ServiceTaskContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, id);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String plannedItemModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_ITEMS;
        List<FacilioField> fields = modBean.getAllFields(plannedItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ServiceOrderPlannedItemsContext> builder = new SelectRecordsBuilder<ServiceOrderPlannedItemsContext>()
                .moduleName(plannedItemModuleName)
                .select(fields)
                .beanClass(ServiceOrderPlannedItemsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType")));
        List<ServiceOrderPlannedItemsContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
            serviceTask.setServiceOrderPlannedItems(list);
        }

        String plannedToolModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOLS;
        fields = modBean.getAllFields(plannedToolModuleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ServiceOrderPlannedToolsContext> toolsBuilder = new SelectRecordsBuilder<ServiceOrderPlannedToolsContext>()
                .moduleName(plannedToolModuleName)
                .select(fields)
                .beanClass(ServiceOrderPlannedToolsContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("toolType")));
        List<ServiceOrderPlannedToolsContext> toolsList = toolsBuilder.get();
        if(CollectionUtils.isNotEmpty(toolsList)) {
            serviceTask.setServiceOrderPlannedTools(toolsList);
        }
        String plannedServiceModuleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES;
        fields = modBean.getAllFields(plannedServiceModuleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ServiceOrderPlannedServicesContext> servicesBuilder = new SelectRecordsBuilder<ServiceOrderPlannedServicesContext>()
                .moduleName(plannedServiceModuleName)
                .select(fields)
                .beanClass(ServiceOrderPlannedServicesContext.class)
                .andCondition(CriteriaAPI.getCondition("SERVICE_TASK", "serviceTask", String.valueOf(id), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("service")));
        List<ServiceOrderPlannedServicesContext> servicesList = servicesBuilder.get();
        if(CollectionUtils.isNotEmpty(servicesList)) {
            serviceTask.setServiceOrderPlannedServices(servicesList);
        }
        return false;
    }
}
