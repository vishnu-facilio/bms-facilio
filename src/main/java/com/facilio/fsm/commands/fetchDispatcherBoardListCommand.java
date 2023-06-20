package com.facilio.fsm.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fetchDispatcherBoardListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User currentUser = AccountUtil.getCurrentUser();
        if(currentUser!=null){
            long peopleId = currentUser.getPeopleId();
            long roleId = currentUser.getRoleId();
            long groupId = 0;
            if(peopleId>0) {
                groupId = getFacilioGroupMember(peopleId);
            }
            List<Long> dispatcherBoardIds = getDispatcherSharingList(peopleId,roleId,groupId);
            FacilioModule dispatcherModule = ModuleFactory.getDispatcherModule();
            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.addAll(FieldFactory.getDispatcherFields(dispatcherModule));
            selectFields.add(FieldFactory.getIdField(dispatcherModule));
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(selectFields)
                    .table("Dispatcher")
                    .andCondition(CriteriaAPI.getIdCondition(dispatcherBoardIds,dispatcherModule));

            List<Map<String, Object>> props = selectBuilder.get();
            List<DispatcherSettingsContext> dispatcherSettingsContextList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(props)) {
                dispatcherSettingsContextList = FieldUtil.getAsBeanListFromMapList(props, DispatcherSettingsContext.class);
            }
            dispatcherSettingsContextList.addAll(getDispatcherList());
            context.put(FacilioConstants.Dispatcher.DISPATCHER_LIST,dispatcherSettingsContextList);

        }

        return false;
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
    private static List<DispatcherSettingsContext> getDispatcherList()throws Exception{
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
