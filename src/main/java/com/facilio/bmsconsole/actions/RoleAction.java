package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ModuleGroupFactory.ModuleGroupPermissionFactory;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ims.handler.AuditLogHandler;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.facilio.bmsconsole.util.AuditLogUtil.sendAuditLogs;

public class RoleAction extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private SetupLayout setup;
    private List<RoleApp> roleApp;
    private Map<Long, List<RoleApp>> roleAppList;
    private long appId;
    private List<WebTabContext> webTabs;

    public List<WebTabContext> getWebTabs() {
        return webTabs;
    }

    public void setWebTabs(List<WebTabContext> webTabs) {
        this.webTabs = webTabs;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public List<RoleApp> getRoleApp() {
        return roleApp;
    }

    public void setRoleApp(List<RoleApp> roleApp) {
        this.roleApp = roleApp;
    }

    public Map<Long, List<RoleApp>> getRoleAppList() {
        return roleAppList;
    }

    public void setRoleAppList(Map<Long, List<RoleApp>> roleAppList) {
        this.roleAppList = roleAppList;
    }

    public SetupLayout getSetup() {
        return this.setup;
    }

    public void setSetup(SetupLayout setup) {
        this.setup = setup;
    }

    private List<Role> roles = null;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    private String linkName;

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    private List<String> linkNames;

    public List<String> getLinkNames() {
        return linkNames;
    }

    public void setLinkNames(List<String> linkNames) {
        this.linkNames = linkNames;
    }

    private List<Long> appIds;

    public List<Long> getAppIds() {
        return appIds;
    }

    public void setAppIds(List<Long> appIds) {
        this.appIds = appIds;
    }

    public String roleList() throws Exception {

        setSetup(SetupLayout.getRolesListLayout());
        if(appIds == null) {
            appIds = new ArrayList<>();
        }
        if(appId > 0){
            appIds.add(appId);
        }
        else if (appId <= 0 && StringUtils.isNotEmpty(linkName)) {
            appIds.add(ApplicationApi.getApplicationIdForLinkName(linkName));
        } else if (appId <= 0 && StringUtils.isEmpty(linkName) && CollectionUtils.isNotEmpty(linkNames)) {
            for(String linkName : linkNames) {
                appIds.add(ApplicationApi.getApplicationIdForLinkName(linkName));
            }
        }
        List<Role> rolesList = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(appIds);
        setRoles(rolesList);
//	x	setGroups(AccountUtil.getGroupBean().getAllOrgGroups(AccountUtil.getCurrentOrg().getOrgId()));
        ActionContext.getContext().getValueStack().set("roles", getRoles());
        return SUCCESS;
    }

    private Map roleresponse = new HashMap();
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private long roleId;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String addRole() throws Exception {
        // setting necessary fields
        role.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        if (role.getName() == null) {
            setRoleResponse("message", "Role Name Cannot be Null");
            return ERROR;
        }
        FacilioContext context = new FacilioContext();
        role.setCreatedTime(System.currentTimeMillis());
        context.put(FacilioConstants.ContextNames.ROLE, getRole());
        context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());
        context.put(FacilioConstants.ContextNames.ROLES_APPS, getRoleApp());

        Command addRole = FacilioChainFactory.getAddRoleCommand();
        addRole.execute(context);
        setRoleId(role.getRoleId());
        addAuditLogs(roleId,"added");
        return SUCCESS;
    }

    public Map<String, Object> getRoleResponse() {
        return roleresponse;
    }

    public void setRoleResponse(String key, Object roleresponse) {
        this.roleresponse.put(key, roleresponse);
    }

    public String deleteRole() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE_ID, getRoleId());
        Command deleteRole = FacilioChainFactory.getDeleteRoleCommand();
        addAuditLogs(roleId,"deleted");
        deleteRole.execute(context);

        return SUCCESS;
    }

    public String updateRole() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE, getRole());
        context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());
        context.put(FacilioConstants.ContextNames.ROLES_APPS, getRoleApp());


        Command updateRole = FacilioChainFactory.getUpdateRoleCommand();
        updateRole.execute(context);
        setRoleId(role.getRoleId());
        addAuditLogs(roleId,"updated");
        return SUCCESS;
    }

    List<ModuleGroupPermissionFactory> modulePerms;

    public List<ModuleGroupPermissionFactory> getModulePerms() {
        return modulePerms;
    }

    public void setModulePerms(List<ModuleGroupPermissionFactory> modulePerms) {
        this.modulePerms = modulePerms;
    }

    public String getAllPermissions() {
        modulePerms = ModuleGroupPermissionFactory.getModuleGroupPermissions();
        return SUCCESS;
    }

    private List<Permissions> permissions;

    public List<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permissions> permissions) {
        this.permissions = permissions;
    }

    private List<NewPermission> newPermissions;

    public List<NewPermission> getNewPermissions() {
        return newPermissions;
    }

    public void setNewPermissions(List<NewPermission> newPermissions) {
        this.newPermissions = newPermissions;
    }


    public String addWebTabRole() throws Exception {
        // setting necessary fields
        role.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        if (role.getName() == null) {
            setRoleResponse("message", "Role Name Cannot be Null");
            return ERROR;
        }
        FacilioContext context = new FacilioContext();
        role.setCreatedTime(System.currentTimeMillis());
        context.put(FacilioConstants.ContextNames.ROLE, getRole());
        context.put(FacilioConstants.ContextNames.PERMISSIONS, getNewPermissions());
        context.put(FacilioConstants.ContextNames.ROLES_APPS, getRoleApp());
        context.put(FacilioConstants.ContextNames.WEB_TABS, getWebTabs());


        Command addRole = FacilioChainFactory.getAddWebTabRoleCommmand();
        addRole.execute(context);
        setRoleId(role.getRoleId());
        addAuditLogs(roleId,"added");

        return SUCCESS;
    }
	public String updateWebTabRole() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ROLE, getRole());
		context.put(FacilioConstants.ContextNames.PERMISSIONS, getNewPermissions());
        context.put(FacilioConstants.ContextNames.ROLES_APPS, getRoleApp());
        context.put(FacilioConstants.ContextNames.WEB_TABS, getWebTabs());
        context.put(FacilioConstants.ContextNames.IS_WEBTAB_PERMISSION,true);

		Command updateRole = FacilioChainFactory.getUpdateWebTabRoleCommand();
		updateRole.execute(context);

        setRoleId(role.getRoleId());
        addAuditLogs(roleId,"updated");

        return SUCCESS;
	}
    private void addAuditLogs(long roleId , String action) throws Exception {
        Role role1 =  AccountUtil.getRoleBean().getRole(roleId);
       AuditLogHandler.ActionType actionType = null;
        if (action.equals("added")) {
            actionType = AuditLogHandler.ActionType.ADD;
        } else if (action.equals("updated")) {
            actionType = AuditLogHandler.ActionType.UPDATE;
        } else if (action.equals("deleted")) {
            actionType = AuditLogHandler.ActionType.DELETE;
        }
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Role  {%s}  has been %s.", role1.getName(), action),
                String.format("Role  %s  has been %s.", role1.getName(),  action),
                AuditLogHandler.RecordType.SETTING,
                "Role", role1.getRoleId())
                .setActionType(actionType)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", role1.getRoleId());
                     json.put("name", role1.getName());
                    json.put("navigateTo", "Roles");
                    array.add(json);
                    return array.toJSONString();
                }).apply(null))
        );
    }

}