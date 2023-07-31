package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
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
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) serviceFieldsMap.get("service"));
        List<V3WorkOrderServiceContext> workorderServicelist = new ArrayList<>();
        List<V3WorkOrderServiceContext> woServiceToBeAdded = new ArrayList<>();

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

        if (CollectionUtils.isNotEmpty(workOrderServices)) {
            List<Long> parentIds = workOrderServices.stream().map(V3WorkOrderServiceContext::getParentId).collect(Collectors.toList());
                for (V3WorkOrderServiceContext woService : workOrderServices) {
                    long parentId = woService.getParentId();
                    V3WorkOrderContext workorder =getWorkorder(parentId);
                    long vendorId = -1;
                    if (woService.getVendor() != null) {
                        vendorId = woService.getVendor();
                    }
                    if(woService.getStartTime()!=null && woService.getEndTime()!=null &&  woService.getStartTime() > woService.getEndTime()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Start time cannot be greater than end time");
                    }
                    if(woService.getQuantity() <= 0) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be null");
                    }
                    woService.setCost(getCostForService(vendorId, woService.getService().getId()));
                    woService.setParent(workorder);
                    if (woService.getId() > 0) {
                        List<V3WorkOrderServiceContext> woServiceContext = V3RecordAPI.getRecordsListWithSupplements(workorderServiceModule.getName(), Collections.singletonList(woService.getId()), V3WorkOrderServiceContext.class, lookUpfields);
                        woService = setWorkorderServiceObj(woServiceContext.get(0).getService(), workorder.getId(), workorder, woService, baseCurrency, currencyMap);
                        workorderServicelist.add(woService);
                        woServiceToBeAdded.add(woService);
                    } else {
                        woService = setWorkorderServiceObj(woService.getService(), workorder.getId(), workorder, woService, baseCurrency, currencyMap);
                        woServiceToBeAdded.add(woService);
                        workorderServicelist.add(woService);
                    }
                }

            if (CollectionUtils.isNotEmpty(woServiceToBeAdded)) {
                Map<String,Object> data = workOrderServices.get(0).getData();
                woServiceToBeAdded.get(0).setData(data);
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
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderServicelist);
            context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 4);
            context.put(FacilioConstants.ContextNames.WO_SERVICE_LIST, workOrderServices);
        }

        return false;
    }

    private V3WorkOrderServiceContext setWorkorderServiceObj(V3ServiceContext service, long parentId, V3WorkOrderContext workorder, V3WorkOrderServiceContext workorderService,
                                                             CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) throws Exception {
        V3WorkOrderServiceContext woService = new V3WorkOrderServiceContext();
        Long startTime = workorderService.getStartTime();
        Long endTime = workorderService.getEndTime();
        woService.setStartTime(startTime);
        woService.setEndTime(endTime);
        woService.setId(workorderService.getId());
        woService.setParent(workorderService.getParent());
        CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(woService, baseCurrency, currencyMap, service.getCurrencyCode(), service.getExchangeRate());
        Double duration = workorderService.getDuration();
        if (startTime != null && startTime >0 && endTime != null && endTime >0) {
            duration = V3InventoryUtil.getWorkorderActualsDuration(startTime, endTime, workorder);
        }
        if(duration != null && duration > 0 && startTime != null && startTime > 0 && (endTime == null || endTime <= 0)) {
            endTime = V3InventoryUtil.getReturnTimeFromDurationAndIssueTime(duration, startTime);
            woService.setEndTime(endTime);
        } else if (duration != null && duration > 0 && endTime != null && endTime > 0 && (startTime == null || startTime <= 0)) {
            startTime = V3InventoryUtil.getIssueTimeFromDurationAndReturnTime(duration, endTime);
            woService.setStartTime(startTime);
        }
        woService.setDuration(duration);
        V3ServiceContext serviceUtil = (V3ServiceContext) V3Util.getRecord(FacilioConstants.ContextNames.SERVICE,service.getId(),null);
        CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(woService, baseCurrency, currencyMap, serviceUtil.getCurrencyCode(), serviceUtil.getExchangeRate());
        Double unitPrice = serviceUtil.getBuyingPrice();
        woService.setUnitPrice(unitPrice);
        woService.setParentId(parentId);
        woService.setQuantity(workorderService.getQuantity());
        if(workorderService.getQuantity()==null || workorderService.getQuantity() <= 0) {
            woService.setQuantity(1.0);
        }
        Double costOccurred = 0.0;
        if(duration != null && duration >=0) {
            costOccurred = V3InventoryUtil.getServiceCost(serviceUtil, (duration / 3600), woService.getQuantity());
        }
        woService.setCost(costOccurred);
        woService.setService(serviceUtil);
        woService.setDuration(duration);
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

