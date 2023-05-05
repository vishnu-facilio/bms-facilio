package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class V3WorkOderAPI {

    public static void handleSiteRelations(V3WorkOrderContext workOrder) throws Exception {
        if (workOrder.getTenant() != null && workOrder.getTenant().getId() != -1) {
            if (workOrder.getResource() == null || workOrder.getResource().getId() == -1) {
                List<BaseSpaceContext> tenantSpaces = V3TenantsAPI.fetchTenantSpaces(workOrder.getTenant().getId());
                if (CollectionUtils.isNotEmpty(tenantSpaces) && tenantSpaces.size() == 1) {

                    ResourceContext resource = ResourceAPI.getResource(tenantSpaces.get(0).getId());
                    if(resource != null) {
                    	workOrder.setResource(resource);
                    }
                }
                V3TenantContext tenant = V3TenantsAPI.getTenant(workOrder.getTenant().getId());
                if (workOrder.getSiteId() == -1) {
                    workOrder.setSiteId(tenant.getSiteId());
                }
                else if (tenant.getSiteId() > 0 && tenant.getSiteId() != workOrder.getSiteId()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tenant doesn't belong to work order site");
                }
            }
            else {
                V3TicketAPI.associateTenant(workOrder);
            }
        }
        else {
            V3TicketAPI.associateTenant(workOrder);
        }
        if (workOrder.getSiteId() == -1 && workOrder.getResource() != null && workOrder.getResource().getId() != -1) {
            ResourceContext resource = ResourceAPI.getResource(workOrder.getResource().getId());
            workOrder.setSiteId(resource.getSiteId());
        }
        if (workOrder.getSiteId() > 0) {
            workOrder.setClient(RecordAPI.getClientForSite(workOrder.getSiteId()));
        } else {
            workOrder.setClient(null);
        }
    }

    public static V3WorkOrderContext getWorkOrder(long ticketId) throws Exception {
        return getWorkOrder(ticketId, null);
    }

    public static V3WorkOrderContext getWorkOrder(long ticketId, boolean skipPermission) throws Exception {
        return getWorkOrder(ticketId, null, skipPermission);
    }

    public static V3WorkOrderContext getWorkOrder(long ticketId, List<String> lookupFieldList) throws Exception {
        List<V3WorkOrderContext> workorders = getWorkOrders(Collections.singletonList(ticketId), lookupFieldList);
        if (CollectionUtils.isNotEmpty(workorders)) {
            return workorders.get(0);
        }
        return null;
    }

    public static V3WorkOrderContext getWorkOrder(long ticketId, List<String> lookupFieldList, boolean skipPermission) throws Exception {
        List<V3WorkOrderContext> workorders = getWorkOrders(Collections.singletonList(ticketId), lookupFieldList, skipPermission);
        if (CollectionUtils.isNotEmpty(workorders)) {
            return workorders.get(0);
        }
        return null;
    }
    public static List<V3WorkOrderContext> getWorkOrders(List<Long> ticketIds, List<String> lookupFieldList, boolean skipPermission) throws Exception{
        List<V3WorkOrderContext> workorders = getWorkOrders(ticketIds,lookupFieldList,skipPermission,false);
        return workorders;
    }

    public static List<V3WorkOrderContext> getWorkOrders(List<Long> ticketIds, List<String> lookupFieldList) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .module(module)
                .beanClass(V3WorkOrderContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(ticketIds, module))
                ;
        if (CollectionUtils.isNotEmpty(lookupFieldList)) {
            List<LookupField> lookupFields = new ArrayList<>();
            for(String fieldName: lookupFieldList) {
                lookupFields.add((LookupField)fieldMap.get(fieldName));
            }
            builder.fetchSupplements(lookupFields);
        }

        List<V3WorkOrderContext> workOrders = builder.get();
        return workOrders;
    }

    public static List<V3WorkOrderContext> getWorkOrders(List<Long> ticketIds, List<String> lookupFieldList, boolean skipPermission, boolean skipModuleCriteria) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .module(module)
                .beanClass(V3WorkOrderContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(ticketIds, module));

        if (skipPermission){
            builder.skipPermission();
            builder.skipScopeCriteria();
        }
        if(skipModuleCriteria){
            builder.skipModuleCriteria();
        }


        if (CollectionUtils.isNotEmpty(lookupFieldList)) {
            List<LookupField> lookupFields = new ArrayList<>();
            for(String fieldName: lookupFieldList) {
                lookupFields.add((LookupField)fieldMap.get(fieldName));
            }
            builder.fetchSupplements(lookupFields);
        }

        List<V3WorkOrderContext> workOrders = builder.get();
        return workOrders;
    }

}
