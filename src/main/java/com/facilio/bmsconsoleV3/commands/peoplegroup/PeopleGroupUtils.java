package com.facilio.bmsconsoleV3.commands.peoplegroup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupMemberContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PeopleGroupUtils {

    public static void setPeopleGroupMembers(List<V3PeopleGroupContext> records) throws Exception {

        if (CollectionUtils.isNotEmpty(records)) {

            List<Long> groupIds = records.stream().map(V3PeopleGroupContext::getId).collect(Collectors.toList());

            Map<Long, List<V3PeopleGroupMemberContext>> groupMemberMap = fetchGroupMembers(groupIds);

            for (V3PeopleGroupContext record : records) {
                if (!groupMemberMap.isEmpty()) {
                    record.setMembers(groupMemberMap.get(record.getId()));
                }
            }
        }
    }

    public static void markAsDeletePeopleGroupMember(List<Long> peopleIds) throws Exception {

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);

        DeleteRecordBuilder<V3PeopleGroupMemberContext> builder = new DeleteRecordBuilder<V3PeopleGroupMemberContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", StringUtils.join(peopleIds, ","), NumberOperators.EQUALS));

        builder.markAsDelete();
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

    public static List<Long> fetchPeopleGroupIds(long peopleId) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        List<FacilioField> fields = Constants.getModBean().getAllFields(module.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3PeopleGroupMemberContext> builder = new SelectRecordsBuilder<V3PeopleGroupMemberContext>()
                .select(Collections.singletonList(fieldMap.get(FacilioConstants.ContextNames.GROUP)))
                .module(module)
                .beanClass(V3PeopleGroupMemberContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.PEOPLE), String.valueOf(peopleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.GROUP), CommonOperators.IS_NOT_EMPTY))
                .groupBy(fieldMap.get(FacilioConstants.ContextNames.GROUP).getCompleteColumnName()+" , "+fieldMap.get(FacilioConstants.ContextNames.PEOPLE).getCompleteColumnName());

        List<V3PeopleGroupMemberContext> groupMembers = builder.get();
        if (CollectionUtils.isNotEmpty(groupMembers)) {
            return groupMembers.stream().map(gm -> gm.getGroup().getId()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static void addPeopleToGroups(List<Long> groupIds,Long peopleId, Long ouid) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        List<FacilioField> fields = Constants.getModBean().getAllFields(module.getName());
        List<V3PeopleGroupMemberContext> groupMembers = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(groupIds) && peopleId != null){
            for (Long groupId : groupIds) {
                V3PeopleContext people = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
                V3PeopleGroupContext group = V3RecordAPI.getRecord(FacilioConstants.PeopleGroup.PEOPLE_GROUP, groupId, V3PeopleGroupContext.class);
                if(people != null && group != null) {
                    V3PeopleGroupMemberContext groupMember = new V3PeopleGroupMemberContext();
                    groupMember.setMemberRole(AccountConstants.GroupMemberRole.MEMBER.getMemberRole());
                    groupMember.setPeople(people);
                    if(ouid != null && ouid > 0){
                        groupMember.setOuid(ouid);
                    } else {
                        groupMember.setOuid(Objects.requireNonNull(PeopleAPI.getUserIdForPeople(peopleId)).get(0));
                    }
                    groupMember.setGroup(group);
                    groupMembers.add(groupMember);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(groupMembers)){
            V3RecordAPI.addRecord(false,groupMembers,module,fields);
        }
    }

    public static void updatePeopleInGroups(List<Long> groupIds,Long peopleId, Long ouid) throws Exception {

        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        DeleteRecordBuilder<V3PeopleGroupMemberContext> builder = new DeleteRecordBuilder<V3PeopleGroupMemberContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get(FacilioConstants.ContextNames.PEOPLE), String.valueOf(peopleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get(FacilioConstants.ContextNames.GROUP), StringUtils.join(groupIds,","), NumberOperators.NOT_EQUALS));
        if(ouid != null && ouid > 0){
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get(FacilioConstants.ContextNames.OUID),String.valueOf(ouid),NumberOperators.EQUALS));
        }
        builder.markAsDelete();

        if(groupIds != null){
            List<Long> existingGroupIds = fetchPeopleGroupIds(peopleId);
            List<Long> addGroupIds = (List<Long>) CollectionUtils.subtract(groupIds,existingGroupIds);
            addPeopleToGroups(addGroupIds,peopleId,ouid);
        }
    }
}
