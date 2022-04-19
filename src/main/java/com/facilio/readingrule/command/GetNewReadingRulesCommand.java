package com.facilio.readingrule.command;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class GetNewReadingRulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String count = (String) context.get(FacilioConstants.ContextNames.RULE_COUNT);

//        JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
        JSONObject serachQuery = (JSONObject) context.get(FacilioConstants.ContextNames.SEARCH);
//        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String query = null;
        if ((serachQuery != null)) {
            query = (String) serachQuery.get("query");
        }
        Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
        Criteria criteria = new Criteria();
//        if (filterCriteria != null) {
//            criteria.andCriteria(filterCriteria);
//        }
//        if ((filters == null || includeParentCriteria) && view != null) {
//            Criteria viewCriteria = view.getCriteria();
//            if (viewCriteria != null) {
//                criteria.andCriteria(viewCriteria);
//            }
//        }
        List<FacilioField> fields = FieldFactory.getNewReadingRuleFields();
        FacilioField ruleNameField = FieldFactory.getAsMap(fields).get("name");

        FacilioModule module = ModuleFactory.getNewReadingRuleModule();
        List<FacilioField> queryFields = null;
        if (count != null) {
            FacilioField countFld = new FacilioField();
            countFld.setName("count");
            countFld.setColumnName("COUNT(New_Reading_Rule.ID)");
            countFld.setDataType(FieldType.NUMBER);
            queryFields = Collections.singletonList(countFld);
        } else {
            queryFields = fields;
        }
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(queryFields);
//				.innerJoin(eventModule.getTableName())
//				.on(module.getTableName()+".EVENT_ID = "+ eventModule.getTableName() +".ID");
//				.innerJoin(readingRuleModule.getTableName())
//				.on(module.getTableName()+ ".ID =" + readingRuleModule.getTableName() + ".ID")
        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        if (orderBy != null && !orderBy.isEmpty()) {
            builder.orderBy(orderBy);
        }


        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }
        if (query != null) {
            builder.andCondition(CriteriaAPI.getCondition(ruleNameField, query, StringOperators.CONTAINS));
        }
        if (criteria != null && !criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        if (count != null) {
            context.put(FacilioConstants.ContextNames.RULE_COUNT, builder.get().get(0).get("count"));
        } else {
            context.put(FacilioConstants.ContextNames.NEW_READING_RULE, NewReadingRuleAPI.getReadingRulesFromMap(builder.get(), true));
        }
        return false;
    }
}
