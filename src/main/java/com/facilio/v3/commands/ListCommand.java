package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();

        Map<String, List<Map<String, Object>>> recordMap = new HashMap<>();
        recordMap.put(moduleName, asProps);

        Boolean withCount = (Boolean) context.get(Constants.WITH_COUNT);
        if (withCount != null && withCount) {
            SelectRecordsBuilder countSelect = getSelectRecordsBuilder(context);

            FacilioField countFld = new FacilioField();
            countFld.setName("count");
            countFld.setColumnName("COUNT(" + module.getTableName() + ".ID)");
            countFld.setDataType(FieldType.NUMBER);

            countSelect.select(Collections.singletonList(countFld));

            List<Map<String, Object>> countProps = countSelect.getAsProps();
            context.put(Constants.COUNT, countProps.get(0).get("count"));
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
