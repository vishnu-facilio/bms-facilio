package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

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

        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder()
                .module(module)
                .select(fields);

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        Boolean includeParentCriteria = (Boolean) context.get(Constants.INCLUDE_PARENT_CRITERIA);

        Criteria beforeFetchCriteria = (Criteria) context.get(Constants.BEFORE_FETCH_CRITERIA);

        if (beforeFetchCriteria != null) {
            selectRecordsBuilder.andCriteria(beforeFetchCriteria);
        }

        if (filterCriteria != null) {
            selectRecordsBuilder.andCriteria(filterCriteria);
        }

        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if ((filterCriteria == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
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

        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }
}
