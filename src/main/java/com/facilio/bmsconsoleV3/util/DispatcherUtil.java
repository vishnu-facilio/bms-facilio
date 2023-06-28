package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.db.criteria.CriteriaAPI.updateConditionField;

public class DispatcherUtil {

    public static void addDispatcher(DispatcherSettingsContext dispatcherConfig)throws Exception{
        addOrUpdateResouceCriteria(dispatcherConfig);
        addDispatcherConfig(dispatcherConfig);
        addOrUpdateDispatcherSharing(dispatcherConfig);
    }

    private static void addOrUpdateResouceCriteria(DispatcherSettingsContext dispatcherConfig)throws Exception{
        if(dispatcherConfig != null && dispatcherConfig.getResourceCriteria() != null){
            Organization org = AccountUtil.getCurrentOrg();
            if(org != null) {
                long orgId = org.getOrgId();
                Criteria criteria = dispatcherConfig.getResourceCriteria();
                updateConditionField(FacilioConstants.ContextNames.PEOPLE, criteria);
                long criteriaId = CriteriaAPI.addCriteria(criteria, orgId);
                dispatcherConfig.setResourceCriteriaId(criteriaId);
            }
        }
    }

    private static void addOrUpdateDispatcherSharing(DispatcherSettingsContext dispatcherConfig)throws Exception{
        if(dispatcherConfig != null) {
            FacilioModule module = ModuleFactory.getDispatcherBoardSharingModule();
            SharingContext<SingleSharingContext> dispatcherSharing = dispatcherConfig.getDispatcherSharing();
            SharingAPI.deleteSharingForParent(Collections.singletonList(dispatcherConfig.getId()), module);

            if (dispatcherSharing == null) {
                dispatcherSharing = new SharingContext<>();
            }

            List<Long> peopleIds = dispatcherSharing.stream().filter(value -> value.getTypeEnum() == SingleSharingContext.SharingType.PEOPLE)
                    .map(val -> val.getPeopleId()).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(peopleIds) && !peopleIds.contains(AccountUtil.getCurrentUser().getPeopleId())) {
                SingleSharingContext newDispatcherSharing = new SingleSharingContext();
                newDispatcherSharing.setPeopleId(AccountUtil.getCurrentUser().getPeopleId());
                newDispatcherSharing.setType(SingleSharingContext.SharingType.PEOPLE);
                dispatcherSharing.add(newDispatcherSharing);
            }
            List<FacilioField> dispatcherSharingFields = FieldFactory.getDispatcherBoardSharingFields(module);
            SharingAPI.addSharing(dispatcherSharing, dispatcherSharingFields, dispatcherConfig.getId(), module);
        }

    }

    private static void addDispatcherConfig(DispatcherSettingsContext dispatcherConfig)throws Exception {
        if (dispatcherConfig != null) {

            FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(dispatcherModule.getTableName())
                    .fields(FieldFactory.getDispatcherFields(dispatcherModule));

            Map<String, Object> props = FieldUtil.getAsProperties(dispatcherConfig);
            insertBuilder.addRecord(props);
            insertBuilder.save();
            dispatcherConfig.setId((Long) props.get("id"));
        }
    }

    public static void updateDispatcher(DispatcherSettingsContext dispatcherConfig)throws Exception{
        addOrUpdateResouceCriteria(dispatcherConfig);
        updateDispatcherConfig(dispatcherConfig);
        addOrUpdateDispatcherSharing(dispatcherConfig);
    }

    private static void updateDispatcherConfig(DispatcherSettingsContext dispatcherConfig)throws Exception{
        if(dispatcherConfig != null) {
            Map<String, Object> prop = FieldUtil.getAsProperties(dispatcherConfig);
            FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(dispatcherModule.getTableName())
                    .fields(FieldFactory.getDispatcherFields(dispatcherModule))
                    .andCondition(CriteriaAPI.getIdCondition(dispatcherConfig.getId(), dispatcherModule));
            updateBuilder.update(prop);
        }

    }

    public static void deleteDispatcher(long dispatcherId)throws Exception{
        if(dispatcherId >0) {
            deleteDispatcherBoard(dispatcherId);
            deleteDispatcherBoardSharing(dispatcherId);
        }
    }

    private static void deleteDispatcherBoard(long id) throws Exception{
        FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(dispatcherModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, dispatcherModule));
        deleteBuilder.delete();
    }

    private static void deleteDispatcherBoardSharing(long id)throws Exception{
        FacilioModule dispatcherBoardSharingModule = ModuleFactory.getDispatcherBoardSharingModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(dispatcherBoardSharingModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("Dispatcher_Board_Sharing.PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }

    public static DispatcherSettingsContext getDispatcher(long id)throws Exception{
        DispatcherSettingsContext dispatcher = getDispatcherConfig(id);
        return dispatcher;
    }
    private static DispatcherSettingsContext getDispatcherConfig(long id)throws Exception{
        Organization org = AccountUtil.getCurrentOrg();

        if(id>0 && org != null) {

            long orgId = org.getOrgId();
            DispatcherSettingsContext dispatcherConfig = new DispatcherSettingsContext();
            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.addAll(FieldFactory.getDispatcherFields(ModuleFactory.getDispatcherModule()));
            selectFields.add(FieldFactory.getIdField(ModuleFactory.getDispatcherModule()));
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(selectFields)
                    .table(ModuleFactory.getDispatcherModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getDispatcherModule()));
            List<Map<String, Object>> props = builder.get();

            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    dispatcherConfig = FieldUtil.getAsBeanFromMap(prop, DispatcherSettingsContext.class);
                }

                if (dispatcherConfig.getResourceCriteriaId() != -1) {
                    Criteria criteria = CriteriaAPI.getCriteria(orgId, dispatcherConfig.getResourceCriteriaId());
                    dispatcherConfig.setResourceCriteria(criteria);
                }
                dispatcherConfig.setDispatcherSharing(getDispatcherSharing(id));
                return dispatcherConfig;
            }
        }
        return null;
    }

    private static SharingContext<SingleSharingContext> getDispatcherSharing(long id)throws Exception {
        if(id > 0 ) {
            FacilioModule dispatcherBoardSharingModule = ModuleFactory.getDispatcherBoardSharingModule();
            List<FacilioField> dispatcherBoardSharingFields = FieldFactory.getDispatcherBoardSharingFields(dispatcherBoardSharingModule);

            SharingContext<SingleSharingContext> dispatcherBoardSharing = SharingAPI.getSharing(id, dispatcherBoardSharingModule, SingleSharingContext.class, dispatcherBoardSharingFields);
            return dispatcherBoardSharing;
        }
        return null;
    }

    public static List<DispatcherSettingsContext> getDispatcherList()throws Exception{
        User currentUser = AccountUtil.getCurrentUser();
        if(currentUser!=null){
            long peopleId = currentUser.getPeopleId();
            long roleId = currentUser.getRoleId();
            long groupId = 0;
            if(peopleId>0) {
                groupId = getFacilioGroupMember(peopleId);
            }
            List<DispatcherSettingsContext> dispatcherSettingsContextList = new ArrayList<>();
            dispatcherSettingsContextList.addAll(getSharedDispatcherList()); //fetching dispatcher board shared to everyone

            List<Long> dispatcherBoardIds = getDispatcherSharingList(peopleId,roleId,groupId);
            if(CollectionUtils.isNotEmpty(dispatcherBoardIds)) {
                FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
                List<FacilioField> selectFields = new ArrayList<>();
                selectFields.addAll(FieldFactory.getDispatcherFields(dispatcherModule));
                selectFields.add(FieldFactory.getIdField(dispatcherModule));
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(selectFields)
                        .table("Dispatcher")
                        .andCondition(CriteriaAPI.getIdCondition(dispatcherBoardIds, dispatcherModule));

                List<Map<String, Object>> props = selectBuilder.get();

                if (CollectionUtils.isNotEmpty(props)) {
                    dispatcherSettingsContextList = FieldUtil.getAsBeanListFromMapList(props, DispatcherSettingsContext.class);
                }
            }

            return dispatcherSettingsContextList;
        }
        return null;
    }

    private static long getFacilioGroupMember(long peopleId)throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getGroupMemberFields())
                .table("FacilioGroupMembers")
                .andCondition(CriteriaAPI.getCondition("FacilioGroupMembers.PEOPLE_ID", "people", String.valueOf(peopleId), NumberOperators.EQUALS));


        List<Map<String , Object>> props = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                return (long) prop.get("groupId");
            }
        }
        return -1;
    }
    private static List<Long> getDispatcherSharingList(long peopleId,long roleId,long groupId)throws Exception{
        FacilioModule dispatcherBoardSharingModule = ModuleFactory.getDispatcherBoardSharingModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDispatcherBoardSharingFields(dispatcherBoardSharingModule))
                .table("Dispatcher_Board_Sharing")
                .andCondition(CriteriaAPI.getCondition("Dispatcher_Board_Sharing.PEOPLE_ID","peopleId", String.valueOf(peopleId),NumberOperators.EQUALS));

        if(roleId>0){
            selectRecordBuilder.orCondition(CriteriaAPI.getCondition("Dispatcher_Board_Sharing.ROLE_ID","roleId", String.valueOf(roleId), NumberOperators.EQUALS));
        }
        if(groupId>0){
            selectRecordBuilder.orCondition(CriteriaAPI.getCondition("Dispatcher_Board_Sharing.GROUP_ID","groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        }
        List<Map<String , Object>> props = selectRecordBuilder.get();
        List<Long> dispatcherBoardIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(props)) {
            for(Map<String, Object> prop : props) {
                dispatcherBoardIds.add((Long) prop.get("parentId"));
            }
        }
        return dispatcherBoardIds;
    }
    private static List<DispatcherSettingsContext> getSharedDispatcherList()throws Exception{
        FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.addAll(FieldFactory.getDispatcherFields(dispatcherModule));
        selectFields.add(FieldFactory.getIdField(dispatcherModule));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(selectFields)
                .table("Dispatcher")
                .leftJoin("Dispatcher_Board_Sharing")
                .on("Dispatcher.ID=Dispatcher_Board_Sharing.PARENT_ID")
                .andCustomWhere("Dispatcher_Board_Sharing.PARENT_ID IS NULL");

        List<Map<String , Object>> props = selectRecordBuilder.get();
        List<DispatcherSettingsContext> dispatcherSettingsContextList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(props)) {
            dispatcherSettingsContextList = FieldUtil.getAsBeanListFromMapList(props, DispatcherSettingsContext.class);

        }
        return dispatcherSettingsContextList;
    }



}
