package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ListCommand extends FacilioCommand {
    private FacilioModule module;

    public ListCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean onlyCount = Constants.getOnlyCount(context);
        if (onlyCount) {
            fetchCount(context);
        } else {
            fetchData(context);
        }
        return false;
    }

    private void fetchCount(Context context) throws Exception {
        SelectRecordsBuilder countSelect = getSelectRecordsBuilder(context);

        countSelect.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));

        List<? extends ModuleBaseWithCustomFields> countRecord = countSelect.get();
        context.put(Constants.COUNT, countRecord.get(0).getId());
    }

    private void fetchData(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();

        List<FacilioField> fields =(List<FacilioField>) context.get(FacilioConstants.ContextNames.SELECTABLE_FIELDS);
        if(CollectionUtils.isEmpty(fields)){
            fields=modBean.getAllFields(moduleName);
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(module));
        }

        SelectRecordsBuilder selectRecordsBuilder = getSelectRecordsBuilder(context);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            selectRecordsBuilder.fetchSupplements(supplementFields);
        }
        setOneLevelSelectableFields(context,selectRecordsBuilder);

        selectRecordsBuilder.select(fields);
        boolean isV4 = Constants.isV4(context);

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

        int perPage;
        int page;
        if (pagination != null) {
            page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
            perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
        } else {
            page = 1;
            perPage = 50;
        }
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        selectRecordsBuilder.offset(offset);
        if (isV4) {
            selectRecordsBuilder.limit(perPage+1);
        } else {
            selectRecordsBuilder.limit(perPage);
        }

        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();
        if (isV4 && CollectionUtils.isNotEmpty(records)) {
            if (perPage <= 0) {
                perPage = 50;
            }
            if (records.size() == (perPage + 1)) {
                Constants.setHasMoreRecords(context, true);
                records.remove(records.size() - 1);
            } else {
                Constants.setHasMoreRecords(context, false);
            }
            String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
            if (orderBy != null) {
                JSONObject sortObj = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
                String orderType = (String) sortObj.get("orderType");
                if ("desc".equalsIgnoreCase(orderType)) {
                    Collections.reverse(records);
                }
            }
        }

        Map<String, List<? extends  ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);

        Boolean withCount = (Boolean) context.get(Constants.WITH_COUNT);
        if (withCount != null && withCount) {
            SelectRecordsBuilder countSelect = getSelectRecordsBuilder(context);

            countSelect.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));

            List<? extends ModuleBaseWithCustomFields> countRecord = countSelect.get();
            context.put(Constants.COUNT, countRecord.get(0).getId());
        }

        context.put(Constants.RECORD_MAP, recordMap);
    }
    private void setOneLevelSelectableFields(Context context,SelectRecordsBuilder selectRecordsBuilder) throws Exception {
        Map<LookupField,List<LookupField>> parentLookupFieldVsChildLookupFieldsMap = (Map<LookupField, List<LookupField>>) context.get(FacilioConstants.ContextNames.ONE_LEVEL_LOOKUP_FIELD_MAP);
        if(MapUtils.isEmpty(parentLookupFieldVsChildLookupFieldsMap)){
            return;
        }

        selectRecordsBuilder.fetchSupplements(parentLookupFieldVsChildLookupFieldsMap.keySet());

        for (LookupField parentLookupField:parentLookupFieldVsChildLookupFieldsMap.keySet()){
            Map<BaseLookupField, List<FacilioField>> childLookupFieldVsSelectableFieldsMap = new HashMap<>();
            for (LookupField childLookupField :parentLookupFieldVsChildLookupFieldsMap.get(parentLookupField)){
                    List<FacilioField> oneLevelSelectableFields = new ArrayList<>();
                    String lookupModuleName = childLookupField.getLookupModule().getName();
                    FacilioField primaryField = Constants.getModBean().getPrimaryField(lookupModuleName);
                    if(primaryField == null){
                        oneLevelSelectableFields.addAll(Constants.getModBean().getAllFields(lookupModuleName));
                    }else {
                        oneLevelSelectableFields.add(primaryField);
                    }
                    childLookupFieldVsSelectableFieldsMap.put(childLookupField,oneLevelSelectableFields);
            }
            if(MapUtils.isNotEmpty(childLookupFieldVsSelectableFieldsMap)){
                selectRecordsBuilder.lookupFieldVsOneLevelSelectableFieldsMap(parentLookupField,childLookupFieldVsSelectableFieldsMap);
            }
        }
    }

    private SelectRecordsBuilder getSelectRecordsBuilder(Context context) throws Exception{
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(beanClass);

        List<JoinContext> joins = (List<JoinContext>) context.getOrDefault(Constants.JOINS, null);
        if(CollectionUtils.isNotEmpty(joins)) {
            V3Util.addJoinsToSelectBuilder(selectRecordsBuilder, joins);
        }
        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        boolean excludeParentCriteria = (boolean) context.getOrDefault(Constants.EXCLUDE_PARENT_CRITERIA, false);

        Criteria beforeFetchCriteria = (Criteria) context.get(Constants.BEFORE_FETCH_CRITERIA);

        if (beforeFetchCriteria != null) {
            selectRecordsBuilder.andCriteria(beforeFetchCriteria);
        }

        if (filterCriteria != null) {
            selectRecordsBuilder.andCriteria(filterCriteria);
        }

        Criteria clientCriteria= (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
        if(clientCriteria!=null && !clientCriteria.isEmpty()){
            selectRecordsBuilder.andCriteria(clientCriteria);
        }

        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if (!excludeParentCriteria && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
            selectRecordsBuilder.andCriteria(view.getCriteria());
        }

        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
        if (searchCriteria != null) {
            selectRecordsBuilder.andCriteria(searchCriteria);
        }

        boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
        if (skipModuleCriteria || (view != null && view.isExcludeModuleCriteria())) {
            selectRecordsBuilder.skipModuleCriteria();
        }

        Object serverCriteria = context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        if (serverCriteria != null) {
            if (serverCriteria instanceof Criteria) {
                if (!((Criteria) serverCriteria).isEmpty()) {
                    selectRecordsBuilder.andCriteria((Criteria) serverCriteria);
                }
            } else {
                selectRecordsBuilder.andCondition((Condition) serverCriteria);
            }
        }

        String orderBy = getFinalSortQuery(context);

        selectRecordsBuilder.orderBy(orderBy);

        return selectRecordsBuilder;
    }
    private String getFinalSortQuery(Context context){
        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        String orderType= getOrderType(context);

        //Add id field as final sort column
        if (StringUtils.isNotEmpty(orderBy) && !orderBy.contains(FieldFactory.getIdField(module).getCompleteColumnName())) {
            orderBy += "," + FieldFactory.getIdField(module).getCompleteColumnName() +" "+orderType;
        }

        //default ID sort
        if(StringUtils.isEmpty(orderBy)){
            orderBy = FieldFactory.getIdField(module).getCompleteColumnName() + " "+orderType;
        }

        return orderBy;
    }
    private String getOrderType(Context context){
        String orderType= context.containsKey(FacilioConstants.ContextNames.ORDER_TYPE) ? (String)context.get(FacilioConstants.ContextNames.ORDER_TYPE) : "desc";
        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        if (orderBy != null && !orderBy.isEmpty()) {
            JSONObject sorting = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
            if (sorting != null && !sorting.isEmpty() && sorting.get("orderType")!=null) {
                 orderType = (String) sorting.get("orderType");
            }
        }
        return orderType;
    }

}
