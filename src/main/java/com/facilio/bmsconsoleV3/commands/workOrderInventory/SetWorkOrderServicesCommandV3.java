package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class SetWorkOrderServicesCommandV3  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderServiceContext> workOrderServices = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderServiceModule = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);
        List<FacilioField> workorderServiceFields = modBean.getAllFields(FacilioConstants.ContextNames.WO_SERVICE);
        Map<String, FacilioField> serviceFieldsMap = FieldFactory.getAsMap(workorderServiceFields);
//        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);

        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) serviceFieldsMap.get("service"));
        List<V3WorkOrderServiceContext> workorderServicelist = new ArrayList<>();
        List<V3WorkOrderServiceContext> woServiceToBeAdded = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(workOrderServices)) {
            long parentId = workOrderServices.get(0).getParentId();
            V3WorkOrderContext workorder =getWorkorder(parentId);

            for (V3WorkOrderServiceContext woService : workOrderServices) {
                long vendorId= -1;
                if(woService.getVendor()!=null){
                    vendorId = woService.getVendor();
                }
                woService.setCost(getCostForService(vendorId, woService.getService().getId()));
                woService.setParent(workorder);
                if (woService.getId() > 0) {
                    List<V3WorkOrderServiceContext> woServiceContext = V3RecordAPI.getRecordsListWithSupplements(workorderServiceModule.getName(),Collections.singletonList(woService.getId()),V3WorkOrderServiceContext.class,lookUpfields);

                    woService = setWorkorderServiceObj(woServiceContext.get(0).getService(), parentId, workorder, woService);
                    workorderServicelist.add(woService);

//                    if (!eventTypes.contains(EventType.EDIT)) {
//                        eventTypes.add(EventType.EDIT);
//                    }
                }
                else
                {
                    woService = setWorkorderServiceObj(woService.getService(), parentId, workorder, woService);
                    woServiceToBeAdded.add(woService);
                    workorderServicelist.add(woService);
//                    if (!eventTypes.contains(EventType.CREATE)) {
//                        eventTypes.add(EventType.CREATE);
//                    }
                }
            }

            if (CollectionUtils.isNotEmpty(woServiceToBeAdded)) {
                recordMap.put(moduleName, woServiceToBeAdded);
            }
            if(CollectionUtils.isNotEmpty(workorderServicelist)) {
                List<Long> recordIds = new ArrayList<>();
                for(V3WorkOrderServiceContext ws : workorderServicelist){
                    recordIds.add(ws.getId());
                }
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WO_SERVICE);
            }
            context.put(FacilioConstants.ContextNames.PARENT_ID, workOrderServices.get(0).getParentId());
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workOrderServices.get(0).getParentId()));
            context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderServicelist);
            context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 4);
            context.put(FacilioConstants.ContextNames.WO_SERVICE_LIST, workOrderServices);
        }

        return false;
    }

    private V3WorkOrderServiceContext setWorkorderServiceObj(V3ServiceContext service, long parentId, V3WorkOrderContext workorder, V3WorkOrderServiceContext workorderService) {
        V3WorkOrderServiceContext woService = new V3WorkOrderServiceContext();
        woService.setStartTime(workorderService.getStartTime());
        woService.setEndTime(workorderService.getEndTime());
        woService.setDuration(workorderService.getDuration());
        woService.setId(workorderService.getId());
        woService.setParent(workorderService.getParent());
        double duration = 0;
        if (woService.getDuration()==null || woService.getDuration() <= 0) {
//			if (woService.getStartTime() <= 0) {
//				woService.setStartTime(workorder.getScheduledStart());
//			}
//			if (woService.getEndTime() <= 0) {
//				woService.setEndTime(workorder.getEstimatedEnd());
//			}
            if (woService.getStartTime()!=null && woService.getEndTime()!=null && woService.getStartTime() >= 0 && woService.getEndTime() >= 0) {
                duration = getEstimatedWorkDuration(woService.getStartTime(), woService.getEndTime());
            } else {
                if(workorder.getActualWorkDuration()!=null && workorder.getActualWorkDuration() > 0) {
                    double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
                    duration = Math.round(hours*100.0)/100.0;
                }
                else{
                    duration = workorderService.getId() > 0 ? 0 : 1;
                }
            }
        } else {
            duration = woService.getDuration();
            if (woService.getStartTime()!=null && woService.getStartTime() > 0) {
                long durationVal = (long) (woService.getDuration() * 60 * 60 * 1000);
                woService.setEndTime(woService.getStartTime() + durationVal);
            }
        }

        Double unitPrice = service.getBuyingPrice();
        woService.setUnitPrice(unitPrice);
        woService.setParentId(parentId);
        woService.setQuantity(workorderService.getQuantity());
        if(workorderService.getQuantity()==null || workorderService.getQuantity() <= 0) {
            woService.setQuantity(1.0);
        }
        double costOccured = 0;
        if(service.getBuyingPrice()!=null && service.getBuyingPrice() > 0) {
            if (service.getPaymentTypeEnum() == V3ServiceContext.PaymentType.FIXED) {
                costOccured = service.getBuyingPrice() * woService.getQuantity();
            }
            else {
                costOccured = service.getBuyingPrice() * duration * woService.getQuantity();
            }
        }
        woService.setCost(costOccured);
        woService.setService(service);
        woService.setDuration(duration * 3600);
        return woService;
    }

    private double getCostForService(long vendorId, long serviceId) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
        SelectRecordsBuilder<WarrantyContractContext> selectBuilder = new SelectRecordsBuilder<WarrantyContractContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(WarrantyContractContext.class).
                andCondition(CriteriaAPI.getCondition("VENDOR", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(ContractsContext.Status.APPROVED.getValue()), NumberOperators.EQUALS))

                ;
        List<WarrantyContractContext> activeServiceContracts = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(activeServiceContracts)) {
            FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
            List<FacilioField> lineItemFields = modBean.getAllFields(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
            SelectRecordsBuilder<WarrantyContractLineItemContext> lineItemsBuilder = new SelectRecordsBuilder<WarrantyContractLineItemContext>()
                    .select(lineItemFields).table(lineItemModule.getTableName()).moduleName(lineItemModule.getName())
                    .beanClass(WarrantyContractLineItemContext.class).
                    andCondition(CriteriaAPI.getCondition("SERVICE", "service", String.valueOf(serviceId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SERVICE_CONTRACT", "serviceContract", String.valueOf(activeServiceContracts.get(0).getId()), NumberOperators.EQUALS))

                    ;
            List<WarrantyContractLineItemContext> serviceContractLineItems = lineItemsBuilder.get();
            if(CollectionUtils.isNotEmpty(serviceContractLineItems)) {
                return serviceContractLineItems.get(0).getCost();
            }

        }
        return 0;
    }

    public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
        double duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }

        double hours = ((duration / (1000 * 60 * 60)));
        return Math.round(hours*100.0)/100.0;
    }

    public static V3WorkOrderContext getWorkorder(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderContext workOrder = V3RecordAPI.getRecord(module.getName(),id,V3WorkOrderContext.class);

        if (workOrder != null) {
            return workOrder;
        }
        return null;
    }


}

