package com.facilio.bmsconsoleV3.commands.peoplegroup;

import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupMemberContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PeopleGroupUtils {

    public static void setPeopleGroupMembers(List<V3PeopleGroupContext> records) throws Exception {

        if (CollectionUtils.isNotEmpty(records)) {

            List<Long> groupIds = records.stream().map(V3PeopleGroupContext::getId).collect(Collectors.toList());

            Map<Long, List<V3PeopleGroupMemberContext>> groupMemberMap = fetchGroupMembers(groupIds);

            for (V3PeopleGroupContext record : records) {

                record.setMembers(groupMemberMap.get(record.getId()));
            }
        }
    }

    private static Map<Long, List<V3PeopleGroupMemberContext>> fetchGroupMembers(List<Long> groupIds) throws Exception {

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        List<FacilioField> fields = Constants.getModBean().getAllFields(module.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3PeopleGroupMemberContext> builder = new SelectRecordsBuilder<V3PeopleGroupMemberContext>()
                .select(fields)
                .module(module)
                .beanClass(V3PeopleGroupMemberContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.GROUP), StringUtils.join(groupIds,","), NumberOperators.EQUALS));

        List<LookupField> lookupFields = Collections.singletonList((LookupField) fieldMap.get(FacilioConstants.ContextNames.PEOPLE));
        builder.fetchSupplements(lookupFields);

        List<V3PeopleGroupMemberContext> groupMembers = builder.get();

        return CollectionUtils.isEmpty(groupMembers) ? Collections.emptyMap() : groupMembers.stream().collect(Collectors.groupingBy(p -> p.getGroup().getId()));

    }

    public static List<V3PeopleGroupContext> fetchGroups (Criteria criteria) throws Exception {

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);

        SelectRecordsBuilder<V3PeopleGroupContext> builder = new SelectRecordsBuilder<V3PeopleGroupContext>()
                .beanClass(V3PeopleGroupContext.class)
                .moduleName(module.getName())
                .select(Constants.getModBean().getAllFields(module.getName()))
                .andCriteria(criteria);

        List<V3PeopleGroupContext> groups = builder.get();

        return CollectionUtils.isNotEmpty(groups) ? groups : Collections.emptyList();
    }
}
