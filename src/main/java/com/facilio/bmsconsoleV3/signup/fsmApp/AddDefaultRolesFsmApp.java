package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
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

import java.util.*;
import java.util.stream.Collectors;

public class AddDefaultRolesFsmApp extends FacilioCommand {

    private static final Map<String, Long> moduleTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.MODULE.getIndex());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String , Long> dispatcherRolePermissionMap  = getDefaultPermissionForDispatcherRole();
        Map<String , Long> fieldAgentRolePermissionMap = getDefaultPermissionForFieldAgentRole();
        Map<String , Long> assistantFieldAgentRolePermissionMap = getDefaultPermissionForAssistantFieldAgentRole();

        Role dispatcher = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);
        ApplicationApi.addAppRoleMapping(dispatcher.getRoleId(), fsm.getId());

        Role  fieldAgent = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FIELD_AGENT);
        ApplicationApi.addAppRoleMapping(fieldAgent.getRoleId(), fsm.getId());

        Role  assistantFieldAgent = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.ASSISTANT_FIELD_AGENT);
        ApplicationApi.addAppRoleMapping(assistantFieldAgent.getRoleId(), fsm.getId());

        Role  storeRoomManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.STOREROOM_MANAGER);
        ApplicationApi.addAppRoleMapping(storeRoomManager.getRoleId(), fsm.getId());

        List<Map<String,Object>> tabs = getWebtabIdVsRoute(fsm.getId());
        if(CollectionUtils.isNotEmpty(tabs)) {
            Map<String,Long> routeVsId =  tabs.stream().collect(Collectors.toMap(tab -> (String) tab.get("route"),tab -> (Long) tab.get("id")));

            RoleApp roleApp = new RoleApp();
            roleApp.setApplicationId(fsm.getId());

            //Adding Default Permission for Dispatcher Role
            List<NewPermission> dispatcherDefaultPermission = new ArrayList<>();
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("serviceAppointment"), dispatcherRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("serviceOrder"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.SERVICE_ORDER)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("trip"),dispatcherRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("timeSheet"),dispatcherRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));

            addDefaultTabPermissionForRoles(dispatcher, dispatcherDefaultPermission, Collections.singletonList(roleApp));


            //Adding Default Permission for Field Agent Role
            List<NewPermission> fieldAgentDefaultPermission = new ArrayList<>();
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("serviceAppointment"), fieldAgentRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("trip"),fieldAgentRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("timeSheet"),fieldAgentRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));

            addDefaultTabPermissionForRoles(fieldAgent, fieldAgentDefaultPermission, Collections.singletonList(roleApp));

            //Adding Default Permission for Assistant Field Agent Role
            List<NewPermission> assistantFieldAgentPermissions = new ArrayList<>();
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("serviceAppointment"), assistantFieldAgentRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("trip"),assistantFieldAgentRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("timeSheet"),assistantFieldAgentRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));

            addDefaultTabPermissionForRoles(assistantFieldAgent, assistantFieldAgentPermissions, Collections.singletonList(roleApp));

        }

        return false;
    }

    public static List<Map<String,Object>> getWebtabIdVsRoute(long appId) throws Exception{
        List<String> routeList = new ArrayList<>(Arrays.asList("serviceOrder","serviceAppointment","timeSheet","trip"));

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


    /** DEFAULT PERMISSION DECLARATION **/

    /**
     * DISPATCHER Role Default permission declaration goes here.
     * Map<String, Long> dispatcherRolePermissions - where key is moduleName, value is permission value.
     *
     * TODO: Reinitialize permission to 0L, before declaring permission for another module/tab
     * @return
     */
    private Map<String, Long> getDefaultPermissionForDispatcherRole(){

        Map<String, Long> dispatcherRolePermissions = new HashMap<>();

        Long permission = 0L;
        permission += moduleTabPermissions.get("CREATE")
                        + moduleTabPermissions.get("READ")
                        + moduleTabPermissions.get("UPDATE")
                        + moduleTabPermissions.get("EXPORT")
                        + moduleTabPermissions.get("COMPLETE_SERVICE_ORDER")
                        + moduleTabPermissions.get("MANAGE_SERVICE_TASKS")
                        + moduleTabPermissions.get("CLOSE_SERVICE_ORDER")
                        + moduleTabPermissions.get("CANCEL_SERVICE_ORDER")
                        + moduleTabPermissions.get("MANAGE_INVENTORY_AND_SERVICE")
                        + moduleTabPermissions.get("MANAGE_INVENTORY_REQUEST")
                        ;
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.SERVICE_ORDER, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                + moduleTabPermissions.get("READ")
                + moduleTabPermissions.get("UPDATE")
                + moduleTabPermissions.get("CANCEL")
                + moduleTabPermissions.get("DISPATCH")
                + moduleTabPermissions.get("START_WORK_ALL")
                + moduleTabPermissions.get("COMPLETE_ALL")
                + moduleTabPermissions.get("EXPORT")
                + moduleTabPermissions.get("MANAGE_INVENTORY_AND_SERVICE")
                + moduleTabPermissions.get("MANAGE_SERVICE_TASKS")
                + moduleTabPermissions.get("MANAGE_INVENTORY_REQUEST");
        dispatcherRolePermissions.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                + moduleTabPermissions.get("READ")
                + moduleTabPermissions.get("UPDATE")
                + moduleTabPermissions.get("CLOSE_ALL");
        dispatcherRolePermissions.put(FacilioConstants.TimeSheet.TIME_SHEET, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                + moduleTabPermissions.get("READ")
                + moduleTabPermissions.get("UPDATE")
                + moduleTabPermissions.get("COMPLETE_ALL");
        dispatcherRolePermissions.put(FacilioConstants.Trip.TRIP, permission);

        return dispatcherRolePermissions;
    }

    /**
     * FIELD AGENT Role Default permission declaration goes here.
     * Map<String, Long> fieldAgentRolePermissions - where key is moduleName, value is permission value.
     *
     * TODO: Reinitialize permission to 0L, before declaring permission for another module/tab
     * @return
     */
    private Map<String, Long> getDefaultPermissionForFieldAgentRole(){

        Map<String, Long> fieldAgentRolePermissions = new HashMap<>();

        Long permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("READ_OWN")
                + moduleTabPermissions.get("START_WORK_OWN")
                + moduleTabPermissions.get("COMPLETE_OWN")
                + moduleTabPermissions.get("EXPORT")
                + moduleTabPermissions.get("MANAGE_INVENTORY_AND_SERVICE")
                + moduleTabPermissions.get("MANAGE_INVENTORY_REQUEST");
        fieldAgentRolePermissions.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE_OWN")
                + moduleTabPermissions.get("UPDATE_OWN")
                + moduleTabPermissions.get("CLOSE_OWN")
                + moduleTabPermissions.get("READ_OWN");
        fieldAgentRolePermissions.put(FacilioConstants.TimeSheet.TIME_SHEET, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE_OWN")
                + moduleTabPermissions.get("UPDATE_OWN")
                + moduleTabPermissions.get("COMPLETE_OWN")
                + moduleTabPermissions.get("READ_OWN");
        fieldAgentRolePermissions.put(FacilioConstants.Trip.TRIP, permission);

        return fieldAgentRolePermissions;
    }

    /**
     * ASSISTANT FIELD AGENT Role Default permission declaration goes here.
     * Map<String, Long> assistantFieldAgentRolePermissions - where key is moduleName, value is permission value.
     *
     * TODO: Reinitialize permission to 0L, before declaring permission for another module/tab
     * @return
     */
    private Map<String, Long> getDefaultPermissionForAssistantFieldAgentRole(){

        Map<String, Long> assistantFieldAgentRolePermissions = new HashMap<>();

        Long permission = 0L;
        permission += moduleTabPermissions.get("READ_OWN")
                + moduleTabPermissions.get("START_WORK_OWN")
                + moduleTabPermissions.get("COMPLETE_OWN");
        assistantFieldAgentRolePermissions.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE_OWN")
                + moduleTabPermissions.get("UPDATE_OWN")
                + moduleTabPermissions.get("CLOSE_OWN")
                + moduleTabPermissions.get("READ_OWN");
        assistantFieldAgentRolePermissions.put(FacilioConstants.TimeSheet.TIME_SHEET, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE_OWN")
                + moduleTabPermissions.get("UPDATE_OWN")
                + moduleTabPermissions.get("COMPLETE_OWN")
                + moduleTabPermissions.get("READ_OWN");
        assistantFieldAgentRolePermissions.put(FacilioConstants.Trip.TRIP, permission);

        return assistantFieldAgentRolePermissions;
    }

}
