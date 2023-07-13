package com.facilio.componentpackage.implementation;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPermissionPackageBeanImpl implements PackageBean<NewPermission> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getNewPermissionIdVsRoleId();
    }

    @Override
    public Map<Long, NewPermission> fetchComponents(List<Long> ids) throws Exception {
        List<Map<String,Object>> newPermissionList  = getNewPermissionForIds(ids);
        Map<Long, NewPermission> newPermissionIdVsNewPermissionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(newPermissionList)) {
            newPermissionList.forEach(newPermission -> newPermissionIdVsNewPermissionMap.put((Long)newPermission.get("id"), FieldUtil.getAsBeanFromMap(newPermission,NewPermission.class)));
        }
        return newPermissionIdVsNewPermissionMap;
    }

    @Override
    public void convertToXMLComponent(NewPermission component, XMLBuilder element) throws Exception {
        Map<Long,String> appIdVsLinkName;
        List<Map<String, Object>> props = getProps(component);
        if(!props.isEmpty()) {
            element.element(PackageConstants.NewPermissionConstants.ROLE_NAME).text(String.valueOf(props.get(0).get("name")));
            element.element(PackageConstants.NewPermissionConstants.ROUTE_NAME).text(String.valueOf(props.get(0).get("route")));
            element.element(PackageConstants.NewPermissionConstants.PERMISSION).text(String.valueOf(component.getPermission()));
            if (props.get(0).get("appId") != null) {
                List<Long> appIds = new ArrayList<>();
                appIds.add((Long) props.get(0).get("appId"));
                appIdVsLinkName = ApplicationApi.getAppLinkNamesForIds(appIds);
                for (String appLinkName : appIdVsLinkName.values()) {
                    element.element(PackageConstants.NewPermissionConstants.APP_LINK_NAME).text(appLinkName);
                }
            }
        }
    }
    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<Map<String, Object>> tabProp = getTabProp();
        List<Map<String, Object>> roleProp = getRoleProp();
        for(Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder newPermissionElement = idVsData.getValue();
            String roleName = newPermissionElement.getElement(PackageConstants.NewPermissionConstants.ROLE_NAME).getText();
            for (Map<String, Object> role : roleProp) {
                if (role.get("name").equals(roleName)) {
                    deleteNewPermission((Long) role.get("roleId"));
                    break;
                }
            }
        }
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder newPermissionElement = idVsData.getValue();
            NewPermission newPermission = constructNewPermissionFromBuilder(newPermissionElement,tabProp,roleProp);
            long permissionId = addNewPermission(newPermission);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), permissionId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents, boolean isReUpdate) throws Exception {
        List<Map<String, Object>> tabProp = getTabProp();
        List<Map<String, Object>> roleProp = getRoleProp();
        for(Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()){
                XMLBuilder newPermissionElement = idVsData.getValue();
                String roleName = newPermissionElement.getElement(PackageConstants.NewPermissionConstants.ROLE_NAME).getText();
                for (Map<String, Object> role : roleProp) {
                    if (role.get("name").equals(roleName)) {
                        deleteNewPermission((Long) role.get("roleId"));
                        break;
                    }
                }
        }
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder newPermissionElement = idVsData.getValue();
            NewPermission newPermission = constructNewPermissionFromBuilder(newPermissionElement,tabProp,roleProp);
            if(newPermission!=null) {
                addNewPermission(newPermission);
            }
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private Map<Long, Long> getNewPermissionIdVsRoleId() throws Exception {
        Map<Long, Long> newPermissionIdVsRoleId = new HashMap<>();
        List<Map<String, Object>> props = getIdVSRoleId();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                newPermissionIdVsRoleId.put((Long) prop.get("id"),(Long) prop.get("roleId"));
            }
        }
        return newPermissionIdVsRoleId;
    }

    private List<Map<String, Object>> getIdVSRoleId() throws Exception {
        FacilioModule module = ModuleFactory.getNewPermissionModule();
        List<FacilioField> allNewPermissionFields = FieldFactory.getNewPermissionFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(allNewPermissionFields)
                .table(module.getTableName());
        List<Map<String, Object>> props = selectBuilder.get();
        return props;
    }
    private List<Map<String,Object>> getNewPermissionForIds(List<Long> ids) throws Exception {
        FacilioModule newPermissionModule = ModuleFactory.getNewPermissionModule();
        List<FacilioField> allNewPermissionFields = FieldFactory.getNewPermissionFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(allNewPermissionFields)
                .table(newPermissionModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(ids,newPermissionModule));
        List<Map<String,Object>> props = selectBuilder.get();
        return props;
    }
    private long addNewPermission(NewPermission newPermission) throws Exception {
        RoleBean roleBean = AccountUtil.getRoleBean();
        long permissionId = roleBean.addSingleNewPermission(newPermission.getRoleId(), newPermission);
        return permissionId;
    }

    private void deleteNewPermission(long roleId) throws Exception {
        RoleBean roleBean = AccountUtil.getRoleBean();
        roleBean.deleteSingleNewPermission(roleId);
    }
    private List<Map<String, Object>> getRoleProp() throws Exception {
        FacilioModule roleModule = AccountConstants.getRoleModule();
        List<FacilioField> roleFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("roleId", "ROLE_ID", roleModule));
            add(FieldFactory.getStringField("name","NAME",roleModule));
        }};
        GenericSelectRecordBuilder roleBuilder = new GenericSelectRecordBuilder()
                .select(roleFields)
                .table(roleModule.getTableName());
        List<Map<String,Object>> roleProp = roleBuilder.get();
        return roleProp;
    }
    private List<Map<String, Object>> getTabProp() throws Exception {
        FacilioModule tabModule = ModuleFactory.getWebTabModule();
        FacilioModule appModule = ModuleFactory.getApplicationModule();
        List<FacilioField> tabFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("tabId", "ID", tabModule));
            add(FieldFactory.getStringField("route","ROUTE",tabModule));
            add(FieldFactory.getStringField("linkName","LINK_NAME",appModule));
        }};
        GenericSelectRecordBuilder tabBuilder = new GenericSelectRecordBuilder()
                .select(tabFields)
                .table(tabModule.getTableName())
                .innerJoin(appModule.getTableName()).on( "WebTab.APPLICATION_ID = APPLICATION.ID");
        List<Map<String,Object>> tabProp = tabBuilder.get();
        return tabProp;
    }

    private List<Map<String, Object>> getProps(NewPermission permission) throws Exception {
        long roleId = permission.getRoleId();
        long tabId = permission.getTabId();
        FacilioModule roleModule = AccountConstants.getRoleModule();
        FacilioModule tabModule = ModuleFactory.getWebTabModule();
        FacilioModule permissionModule = ModuleFactory.getNewPermissionModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getStringField("name", "NAME", roleModule));
            add(FieldFactory.getStringField("route", "ROUTE", tabModule));
            add(FieldFactory.getNumberField("appId", "APPLICATION_ID", tabModule));
        }};
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(roleModule.getTableName())
                .innerJoin(permissionModule.getTableName()).on("Role.ROLE_ID = NewPermission.ROLE_ID")
                .innerJoin(tabModule.getTableName()).on("WebTab.ID = NewPermission.TAB_ID")
                .andCondition(CriteriaAPI.getCondition("Role.ROLE_ID","roleId", String.valueOf(roleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("WebTab.ID","tabId", String.valueOf(tabId), NumberOperators.EQUALS));
        return builder.get();
    }

    private NewPermission constructNewPermissionFromBuilder(XMLBuilder newPermissionElement,List<Map<String, Object>> tabProp,List<Map<String, Object>> roleProp ) throws Exception {
        String roleName = newPermissionElement.getElement(PackageConstants.NewPermissionConstants.ROLE_NAME).getText();
        String routeName = newPermissionElement.getElement(PackageConstants.NewPermissionConstants.ROUTE_NAME).getText();
        int permission = Integer.parseInt(newPermissionElement.getElement(PackageConstants.NewPermissionConstants.PERMISSION).getText());
        String appLinkName = newPermissionElement.getElement(PackageConstants.NewPermissionConstants.APP_LINK_NAME).getText();
        NewPermission newPermission = new NewPermission();
        if(!(tabProp.isEmpty()&&roleProp.isEmpty())) {
            for (Map<String, Object> role : roleProp) {
                if (role.get("name").equals(roleName)) {
                    newPermission.setRoleId((Long) role.get("roleId"));
                    break;
                }
            }
            for (Map<String, Object> tab : tabProp) {
                if (tab.get("route").equals(routeName)) {
                    if (tab.get("linkName").equals(appLinkName)) {
                        newPermission.setTabId((Long) tab.get("tabId"));
                        break;
                    }
                }
            }
        }
        newPermission.setPermission(permission);
        return newPermission;
    }
}
