package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListCommand extends FacilioCommand {
    private FacilioModule module;

    public ListCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder selectRecordsBuilder = getSelectRecordsBuilder(context);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            selectRecordsBuilder.fetchSupplements(supplementFields);
        }

        selectRecordsBuilder.select(fields);

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            selectRecordsBuilder.offset(offset);
            selectRecordsBuilder.limit(perPage);
        }

        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();

        if (supplementFields != null) {
            Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
            List<Map<String, Object>> asProps = FieldUtil.getAsMapList(records, beanClass);
            List<FacilioField> supplements = supplementFields.stream().map(SupplementRecord::selectField).collect(Collectors.toList());
            Map<Long, Map<Long, Map<String, Object>>> supplMap = new HashMap<>();
            for (FacilioField supplField: supplements) {
                Map<Long, Map<String, Object>> supplIdMap = new HashMap<>();
                for (Map<String, Object> prop: asProps) {
                    Map<String, Object> supplRecord = (Map<String, Object>) prop.get(supplField.getName());
                    if (supplRecord == null) {
                        continue;
                    }
                    long id = (Long) supplRecord.get("id");
                    if (supplIdMap.get(id) == null) {
                        supplIdMap.put(id, supplRecord);
                    }
                    prop.put(supplField.getName() ,FieldUtil.getAsBeanFromMap(supplRecord, ModuleBaseWithCustomFields.class));
                }
                if (MapUtils.isEmpty(supplMap.get(supplField.getModuleId()))) {
                    supplMap.put(supplField.getModuleId(), supplIdMap);
                }
            }

            if (MapUtils.isNotEmpty(supplMap)) {
                context.put(Constants.SUPPLEMENT_MAP, supplMap);
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

        return false;
    }

    private SelectRecordsBuilder getSelectRecordsBuilder(Context context) {
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(beanClass);

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        boolean excludeParentCriteria = (boolean) context.getOrDefault(Constants.EXCLUDE_PARENT_CRITERIA, false);

        Criteria beforeFetchCriteria = (Criteria) context.get(Constants.BEFORE_FETCH_CRITERIA);

        if (beforeFetchCriteria != null) {
            selectRecordsBuilder.andCriteria(beforeFetchCriteria);
        }

        if (filterCriteria != null) {
            selectRecordsBuilder.andCriteria(filterCriteria);
        }

        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if (!excludeParentCriteria && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
            selectRecordsBuilder.andCriteria(view.getCriteria());
        }

        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
        if (searchCriteria != null) {
            selectRecordsBuilder.andCriteria(searchCriteria);
        }

        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        if (orderBy != null && !orderBy.isEmpty()) {
            selectRecordsBuilder.orderBy(orderBy);
        }


        return selectRecordsBuilder;
    }
}
