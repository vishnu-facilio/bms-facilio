package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDefaultRolesFsmApp extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Role fsmDispatcher = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);
        ApplicationApi.addAppRoleMapping(fsmDispatcher.getRoleId(), fsm.getId());

        Role  fieldAgent = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        ApplicationApi.addAppRoleMapping(fieldAgent.getRoleId(), fsm.getId());

        Role  assistantFieldAgent = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);
        ApplicationApi.addAppRoleMapping(assistantFieldAgent.getRoleId(), fsm.getId());

        Role  storeRoomManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.STOREROOM_MANAGER);
        ApplicationApi.addAppRoleMapping(storeRoomManager.getRoleId(), fsm.getId());

        List<Map<String,Object>> map = getWebtabIdVsRoute(fsm.getId());
        Long soId = 0L;
        Long saId = 0L;
        Long timeSheetId = 0L;
        Long tripId = 0L;
        if(CollectionUtils.isNotEmpty(map)) {
            for (Map<String, Object> item : map) {
                if (item.get("route").equals("serviceOrder")) {
                    soId = (Long) item.get("id");
                } else if(item.get("route").equals("serviceAppointment")){
                    saId = (Long) item.get("id");
                } else if(item.get("route").equals("timeSheet")){
                    timeSheetId = (Long) item.get("id");
                } else if (item.get("route").equals("trip")){
                    tripId = (Long) item.get("id");
                }
            }

            RoleApp roleApp = new RoleApp();
            roleApp.setApplicationId(fsm.getId());

            List<RoleApp> roleApps = new ArrayList<>();
            roleApps.add(roleApp);

            //fsmDispatcher
            NewPermission sAPermission = new NewPermission(saId, 256901165);
            NewPermission sOPermission = new NewPermission(soId, 983085);
            NewPermission tripPermission = new NewPermission(tripId,4194317);
            NewPermission timeSheetPermission = new NewPermission(timeSheetId,536870925);
            List<NewPermission> newPermissions = new ArrayList<>();
            newPermissions.add(sAPermission);
            newPermissions.add(sOPermission);
            newPermissions.add(tripPermission);
            newPermissions.add(timeSheetPermission);

            addDefaultTabPermissionForRoles(fsmDispatcher, newPermissions, roleApps);


            //fieldAgent
            List<NewPermission> fieldAgentPermissions = new ArrayList<>();
            fieldAgentPermissions.add(new NewPermission(saId, 211812512));
            fieldAgentPermissions.add(new NewPermission(tripId,276824704));
            fieldAgentPermissions.add(new NewPermission(timeSheetId,1342177920));

            addDefaultTabPermissionForRoles(fieldAgent, fieldAgentPermissions, roleApps);

            //assistantFieldAgent


            List<NewPermission> assistantFieldAgentPermissions = new ArrayList<>();
            assistantFieldAgentPermissions.add(new NewPermission(saId, 10485888));
            assistantFieldAgentPermissions.add(new NewPermission(tripId,276824704));
            assistantFieldAgentPermissions.add(new NewPermission(timeSheetId,1342177920));

            addDefaultTabPermissionForRoles(assistantFieldAgent, assistantFieldAgentPermissions, roleApps);

            //storeRoomManager

//
//        List<NewPermission> newStoreRoomManagerPermissions = new ArrayList<>();
//        newStoreRoomManagerPermissions.add(new NewPermission(saId,10485888));
//
//
//        addDefaultTabPermissionForRoles(storeRoomManager,newStoreRoomManagerPermissions,roleApps);
        }

        return false;
    }

    public static List<Map<String,Object>> getWebtabIdVsRoute(long appId) throws Exception{
        List<String> routeList = new ArrayList<>();
        routeList.add("serviceOrder");
        routeList.add("serviceAppointment");
        routeList.add("timeSheet");
        routeList.add("trip");

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(ModuleFactory.getWebTabModule()));
        fields.add(FieldFactory.getStringField("route","ROUTE",ModuleFactory.getWebTabModule()));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("WebTab")
                .andCondition((CriteriaAPI.getCondition("ROUTE", "route", StringUtils.join(routeList,","), StringOperators.IS)))
                .andCondition((CriteriaAPI.getCondition("APPLICATION_ID","applicationId", String.valueOf(appId),NumberOperators.EQUALS)));
        List<Map<String, Object>> props = selectBuilder.get();
        return props;

    }

    public static void addDefaultTabPermissionForRoles(Role role,List<NewPermission> newPermissions,List<RoleApp> roleApps) throws Exception{

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE, role);
        context.put(FacilioConstants.ContextNames.PERMISSIONS, newPermissions);
        context.put(FacilioConstants.ContextNames.ROLES_APPS, roleApps);
        context.put(FacilioConstants.ContextNames.WEB_TABS, null);
        context.put(FacilioConstants.ContextNames.IS_WEBTAB_PERMISSION,true);


        Command updateRole = FacilioChainFactory.getUpdateWebTabRoleCommand();
        updateRole.execute(context);
    }



}
