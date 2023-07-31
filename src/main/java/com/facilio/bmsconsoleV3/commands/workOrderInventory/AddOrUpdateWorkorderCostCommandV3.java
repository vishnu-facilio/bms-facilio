package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderCostContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddOrUpdateWorkorderCostCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
        Map<Long, V3WorkOrderContext> workOrderRecordMap = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORK_ORDER, parentIds, V3WorkOrderContext.class);

        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);
        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);

        if (parentIds != null && !parentIds.isEmpty()) {
            for (long parentId : parentIds) {
                int costType = (int) context.get(FacilioConstants.ContextNames.WORKORDER_COST_TYPE);
                V3WorkorderCostContext.CostType cos = V3WorkorderCostContext.CostType.valueOf(costType);
                double cost = 0;
                long qty = 0;
                if (costType == 1) {
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_ITEMS);
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    cost = getTotalItemCost(parentId, module, fieldsMap);
                    qty = getTotalNoOfItem(parentId, module, fieldsMap);
                } else if (costType == 2) {
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_TOOLS);
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    cost = getTotalToolCost(parentId, module, fieldsMap);
                    qty = getTotalNoOfTool(parentId, module, fieldsMap);
                } else if (costType == 3) {
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WO_LABOUR);
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    cost = getTotalLabourCost(parentId, module, fieldsMap);
                    qty = getTotalNoOfLabour(parentId, module, fieldsMap);
                }
                else if (costType == 4) {
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);
                    List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WO_SERVICE);
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    cost = getTotalServiceCost(parentId, module, fieldsMap);
                    qty = getTotalNoOfServices(parentId, module, fieldsMap);
                }

                FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);

                List<FacilioField> workorderCostsFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
                CurrencyUtil.addMultiCurrencyFieldsToFields(workorderCostsFields, workorderCostsModule);
                List<FacilioField> workOrderCostMultiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(workorderCostsFields);

                Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

                SelectRecordsBuilder<V3WorkorderCostContext> workorderCostSetlectBuilder = new SelectRecordsBuilder<V3WorkorderCostContext>()
                        .select(workorderCostsFields).table(workorderCostsModule.getTableName())
                        .moduleName(workorderCostsModule.getName()).beanClass(V3WorkorderCostContext.class)
                        .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
                                String.valueOf(parentId), PickListOperators.IS))
                        .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("costType"),
                                String.valueOf(cos.getValue()), EnumOperators.IS));

                List<V3WorkorderCostContext> workorderCosts = workorderCostSetlectBuilder.get();
                V3WorkorderCostContext workorderCost = new V3WorkorderCostContext();

                V3WorkOrderContext workOrderContext = workOrderRecordMap.get(parentId);
                if (workorderCosts != null && !workorderCosts.isEmpty()) {
                    workorderCost = workorderCosts.get(0);

                    Map<String, Object> recordAsMap = FieldUtil.getAsProperties(workorderCost);
                    recordAsMap.put("cost", cost);
                    recordAsMap.put("quantity", qty);
                    recordAsMap.put("modifiedTime", System.currentTimeMillis());
                    recordAsMap.put("currencyCode", workOrderContext.getCurrencyCode());
                    CurrencyUtil.checkAndUpdateCurrencyProps(recordAsMap, workorderCost, baseCurrency, currencyMap, null, workOrderCostMultiCurrencyFields);
                    workorderCost = FieldUtil.getAsBeanFromMap(recordAsMap, V3WorkorderCostContext.class);

                    V3RecordAPI.updateRecord(workorderCost, workorderCostsModule, workorderCostsFields);
                } else {
                    workorderCost.setCost(cost);
                    V3WorkOrderContext wo = new V3WorkOrderContext();
                    wo.setId(parentId);
                    workorderCost.setParentId(wo);
                    workorderCost.setCostType(costType);
                    workorderCost.setQuantity(qty);
                    workorderCost.setTtime(System.currentTimeMillis());
                    workorderCost.setCurrencyCode(workOrderContext.getCurrencyCode());
                    workorderCost.setModifiedTime(System.currentTimeMillis());

                    List<ModuleBaseWithCustomFields> multiCurrencyData = CurrencyUtil.addMultiCurrencyData(workorderCostsModule.getName(), workOrderCostMultiCurrencyFields, Collections.singletonList(workorderCost), V3WorkorderCostContext.class, baseCurrency, currencyMap);
                    workorderCost = (V3WorkorderCostContext) multiCurrencyData.get(0);

                    V3RecordAPI.addRecord(false,Collections.singletonList(workorderCost), workorderCostsModule, workorderCostsFields);
                }
                context.put(FacilioConstants.ContextNames.TOTAL_COST, workorderCost.getCost());
                context.put(FacilioConstants.ContextNames.TOTAL_QUANTITY, qty);
                context.put(FacilioConstants.ContextNames.RECORD, workorderCost);
            }
        }
        return false;
    }

    public static double getTotalItemCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
            throws Exception {

        if (id <= 0) {
            return 0d;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

        SelectRecordsBuilder<WorkorderItemContext> builder = new SelectRecordsBuilder<WorkorderItemContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            double returnTotal = 0;
            double issueTotal = 0;
            if (rs.get(0).get("totalItemsCost") != null) {
                return (double) rs.get(0).get("totalItemsCost");
            }
            return 0d;
        }
        return 0d;
    }

    public static double getTotalToolCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
            throws Exception {

        if (id <= 0) {
            return 0d;
        }
        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

        SelectRecordsBuilder<WorkorderToolsContext> builder = new SelectRecordsBuilder<WorkorderToolsContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            double returnTotal = 0;
            double issueTotal = 0;
            if (rs.get(0).get("totalItemsCost") != null) {
                return (double) rs.get(0).get("totalItemsCost");
            }
            return 0d;
        }
        return 0d;
    }

    public static double getTotalLabourCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
            throws Exception {

        if (id <= 0) {
            return 0d;
        }
        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalLabourCost", "sum(COST)", FieldType.DECIMAL));

        SelectRecordsBuilder<WorkOrderLabourContext> builder = new SelectRecordsBuilder<WorkOrderLabourContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalLabourCost") != null) {
                return (double) rs.get(0).get("totalLabourCost");
            }
            return 0d;
        }
        return 0d;
    }

    public static double getTotalServiceCost(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
            throws Exception {

        if (id <= 0) {
            return 0d;
        }
        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalServiceCost", "sum(COST)", FieldType.DECIMAL));

        SelectRecordsBuilder<WorkOrderServiceContext> builder = new SelectRecordsBuilder<WorkOrderServiceContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalServiceCost") != null) {
                return (double) rs.get(0).get("totalServiceCost");
            }
            return 0d;
        }
        return 0d;
    }

    private long getTotalNoOfTool(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
        if (id <= 0) {
            return 0;
        }

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalTools", "COUNT(DISTINCT TOOL)", FieldType.NUMBER));

        SelectRecordsBuilder<WorkorderToolsContext> builder = new SelectRecordsBuilder<WorkorderToolsContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalTools") != null) {
                return (long) rs.get(0).get("totalTools");
            }
            return 0;
        }
        return 0;
    }

    private long getTotalNoOfItem(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
        if (id <= 0) {
            return 0;
        }

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalItems", "COUNT(DISTINCT ITEM_ID)", FieldType.NUMBER));

        SelectRecordsBuilder<WorkorderItemContext> builder = new SelectRecordsBuilder<WorkorderItemContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS))
                .andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalItems") != null) {
                return (long) rs.get(0).get("totalItems");
            }
            return 0;
        }
        return 0;
    }

    private long getTotalNoOfLabour(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
        if (id <= 0) {
            return 0;
        }

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalLabours", "COUNT(DISTINCT LABOUR)", FieldType.NUMBER));

        SelectRecordsBuilder<WorkOrderLabourContext> builder = new SelectRecordsBuilder<WorkOrderLabourContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS)).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalLabours") != null) {
                return (long) rs.get(0).get("totalLabours");
            }
            return 0;
        }
        return 0;
    }

    private long getTotalNoOfServices(long id, FacilioModule module, Map<String, FacilioField> fieldsMap) throws Exception {
        if (id <= 0) {
            return 0;
        }

        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("totalServices", "COUNT(DISTINCT SERVICE)", FieldType.NUMBER));

        SelectRecordsBuilder<WorkOrderServiceContext> builder = new SelectRecordsBuilder<WorkOrderServiceContext>()
                .select(field).moduleName(module.getName())
                .andCondition(
                        CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(id), NumberOperators.EQUALS)).setAggregation();

        List<Map<String, Object>> rs = builder.getAsProps();
        if (rs != null && rs.size() > 0) {
            if (rs.get(0).get("totalServices") != null) {
                return (long) rs.get(0).get("totalServices");
            }
            return 0;
        }
        return 0;
    }

}

