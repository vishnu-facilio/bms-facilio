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
    private static final Map<String, Long> customTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.CUSTOM.getIndex());
    private static final Map<String, Long> approvalTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.APPROVAL.getIndex());
    private static final Map<String, Long> dispatchConsoleTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.DISPATCHER_CONSOLE.getIndex());
    private static final Map<String, Long> attendanceTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.ATTENDANCE.getIndex());
    private static final Map<String, Long> myAttendanceTabPermissions = NewPermissionUtil.getPermissions(WebTabContext.Type.MY_ATTENDANCE.getIndex());



    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String , Long> dispatcherRolePermissionMap  = getDefaultPermissionForDispatcherRole();
        Map<String , Long> fieldAgentRolePermissionMap = getDefaultPermissionForFieldAgentRole();
        Map<String , Long> assistantFieldAgentRolePermissionMap = getDefaultPermissionForAssistantFieldAgentRole();
        Map<String , Long> storeRoomManagerRolePermissionMap = getDefaultPermissionForStoreRoomManagerRole();

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


            List<RoleApp> roleApps = new ArrayList<>();
            roleApps.add(roleApp);

            //Adding Default Permission for Dispatcher Role
            List<NewPermission> dispatcherDefaultPermission = new ArrayList<>();
//            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("dashboard"), dispatcherRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("dispatch"), dispatcherRolePermissionMap.get(FacilioConstants.ServiceAppointment.DISPATCH)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("approval"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.APPROVAL)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("portfolio"), dispatcherRolePermissionMap.get(FacilioConstants.Portfolio.PORTFOLIO)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("assets"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.ASSET)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("quote"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.QUOTE)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("employees"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.EMPLOYEE)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("attendance"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.ATTENDANCE)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("myAttendance"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.MY_ATTENDANCE)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("timeOff"), dispatcherRolePermissionMap.get(FacilioConstants.TimeOff.TIME_OFF)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("inventoryrequests"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("serviceAppointment"), dispatcherRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("serviceOrder"), dispatcherRolePermissionMap.get(FacilioConstants.ContextNames.SERVICE_ORDER)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("trip"),dispatcherRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            dispatcherDefaultPermission.add(new NewPermission(routeVsId.get("timeSheet"),dispatcherRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));



            addDefaultTabPermissionForRoles(dispatcher, dispatcherDefaultPermission, Collections.singletonList(roleApp));


            //Adding Default Permission for Field Agent Role
            List<NewPermission> fieldAgentDefaultPermission = new ArrayList<>();

            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("quote"), fieldAgentRolePermissionMap.get(FacilioConstants.ContextNames.QUOTE)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("myAttendance"), fieldAgentRolePermissionMap.get(FacilioConstants.ContextNames.MY_ATTENDANCE)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("timeOff"), fieldAgentRolePermissionMap.get(FacilioConstants.TimeOff.TIME_OFF)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("inventoryrequests"), fieldAgentRolePermissionMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("serviceAppointment"), fieldAgentRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("trip"),fieldAgentRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            fieldAgentDefaultPermission.add(new NewPermission(routeVsId.get("timeSheet"),fieldAgentRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));

            addDefaultTabPermissionForRoles(fieldAgent, fieldAgentDefaultPermission, Collections.singletonList(roleApp));

            //Adding Default Permission for Assistant Field Agent Role
            List<NewPermission> assistantFieldAgentPermissions = new ArrayList<>();

            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("myAttendance"), assistantFieldAgentRolePermissionMap.get(FacilioConstants.ContextNames.MY_ATTENDANCE)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("timeOff"), assistantFieldAgentRolePermissionMap.get(FacilioConstants.TimeOff.TIME_OFF)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("inventoryrequests"), assistantFieldAgentRolePermissionMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("serviceAppointment"), assistantFieldAgentRolePermissionMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("trip"),assistantFieldAgentRolePermissionMap.get(FacilioConstants.Trip.TRIP)));
            assistantFieldAgentPermissions.add(new NewPermission(routeVsId.get("timeSheet"),assistantFieldAgentRolePermissionMap.get(FacilioConstants.TimeSheet.TIME_SHEET)));

            addDefaultTabPermissionForRoles(assistantFieldAgent, assistantFieldAgentPermissions, Collections.singletonList(roleApp));

            //Adding Default Permission for Store Room Manager Role
            List<NewPermission> storeRoomManagerPermissions = new ArrayList<>();

            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("portfolio"), storeRoomManagerRolePermissionMap.get(FacilioConstants.Portfolio.PORTFOLIO)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("assets"), storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.ASSET)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("inventoryrequests"), storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("items"), storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.ITEMS)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("tools"),storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.TOOLS)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("service"),storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.SERVICES)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("storeroom"), storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.STORE_ROOM)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("itemtypes"),storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.ITEM_TYPES)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("tooltypes"),storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.TOOL_TYPES)));
            storeRoomManagerPermissions.add(new NewPermission(routeVsId.get("transferrequests"),storeRoomManagerRolePermissionMap.get(FacilioConstants.ContextNames.TRANSFER_REQUEST)));

            addDefaultTabPermissionForRoles(storeRoomManager, storeRoomManagerPermissions, Collections.singletonList(roleApp));

        }

        return false;
    }

    public static List<Map<String,Object>> getWebtabIdVsRoute(long appId) throws Exception{

        List<String> routeList = new ArrayList<>(Arrays.asList("serviceOrder","serviceAppointment","timeSheet","trip","dashboard","dispatch","approval","portfolio","assets","quote","employees","shift","shiftplanner","attendance","myAttendance","timeOff","vendors","vendorcontact","client","clientcontact","items","tools","service","storeroom","itemtypes","tooltypes","inventoryrequests","transferrequests"));


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

        permission = 0L; // TODO: Reinitialize
        permission += dispatchConsoleTabPermissions.get("CAN_ASSIGN")
                + dispatchConsoleTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.ServiceAppointment.DISPATCH, permission);

        permission = 0L; // TODO: Reinitialize
        permission += approvalTabPermissions.get("CAN_APPROVE");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.APPROVAL, permission);

        permission = 0L; // TODO: Reinitialize
        permission += customTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.Portfolio.PORTFOLIO, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.ASSET, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.QUOTE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.EMPLOYEE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += attendanceTabPermissions.get("READ");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.ATTENDANCE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += myAttendanceTabPermissions.get("READ")
                    +myAttendanceTabPermissions.get("UPDATE");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.MY_ATTENDANCE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        dispatcherRolePermissions.put(FacilioConstants.TimeOff.TIME_OFF, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        dispatcherRolePermissions.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, permission);

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

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("READ");
        fieldAgentRolePermissions.put(FacilioConstants.ContextNames.QUOTE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += myAttendanceTabPermissions.get("READ")
                +myAttendanceTabPermissions.get("UPDATE");
        fieldAgentRolePermissions.put(FacilioConstants.ContextNames.MY_ATTENDANCE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        fieldAgentRolePermissions.put(FacilioConstants.TimeOff.TIME_OFF, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        fieldAgentRolePermissions.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, permission);

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

        permission = 0L; // TODO: Reinitialize
        permission += myAttendanceTabPermissions.get("READ")
                +myAttendanceTabPermissions.get("UPDATE");
        assistantFieldAgentRolePermissions.put(FacilioConstants.ContextNames.MY_ATTENDANCE, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        assistantFieldAgentRolePermissions.put(FacilioConstants.TimeOff.TIME_OFF, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        assistantFieldAgentRolePermissions.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, permission);

        return assistantFieldAgentRolePermissions;
    }

    /**
     * STORE ROOM MANAGER Role Default permission declaration goes here.
     * Map<String, Long> storeRoomManagerRolePermissions - where key is moduleName, value is permission value.
     *
     * TODO: Reinitialize permission to 0L, before declaring permission for another module/tab
     * @return
     */
    private Map<String, Long> getDefaultPermissionForStoreRoomManagerRole(){

        Map<String, Long> storeRoomManagerRolePermissions = new HashMap<>();

        Long permission = 0L;
        permission += customTabPermissions.get("READ");
        storeRoomManagerRolePermissions.put(FacilioConstants.Portfolio.PORTFOLIO, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.ASSET, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.ITEMS, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.TOOLS, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.SERVICES, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.STORE_ROOM, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.ITEM_TYPES, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.TOOL_TYPES, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, permission);

        permission = 0L; // TODO: Reinitialize
        permission += moduleTabPermissions.get("CREATE")
                +moduleTabPermissions.get("READ")
                +moduleTabPermissions.get("UPDATE")
                +moduleTabPermissions.get("DELETE");
        storeRoomManagerRolePermissions.put(FacilioConstants.ContextNames.TRANSFER_REQUEST, permission);

        return storeRoomManagerRolePermissions;
    }


}
