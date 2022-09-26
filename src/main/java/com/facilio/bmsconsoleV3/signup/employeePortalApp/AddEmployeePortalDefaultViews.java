package com.facilio.bmsconsoleV3.signup.employeePortalApp;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsoleV3.commands.AddSignupDataCommandV3;
import com.facilio.bmsconsoleV3.util.V3ModuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddEmployeePortalDefaultViews extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<String> appLinkNames = new ArrayList<>();
//        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        List<ApplicationContext> applicationContexts = ApplicationApi.getApplicationForLinkNames(appLinkNames);

        ApplicationContext employee_portal = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<String> sysModuleNames = V3ModuleAPI.getSystemModuleNamesForApp(employee_portal.getLinkName());
        Role portalAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.EMPLOYEE_ADMIN);
        if(!AddSignupDataCommandV3.createViews) {
            for (String moduleName : sysModuleNames) {
                boolean isEmployeePortal = false;
                List<Map<String, Object>> groupViews = ViewFactory.getGroupVsViews(moduleName);
                for (ApplicationContext applicationContext : applicationContexts) {
                    if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
                        for (Map<String, Object> groupView : groupViews) {
                            ViewGroups viewGroup = new ViewGroups();
                            if (applicationContext.getLinkName().equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
                                viewGroup.setName((String) groupView.get("name") + "_employee_portal");
                                viewGroup.setAppId(applicationContext.getId());
                                isEmployeePortal = true;
                            } else {
                                viewGroup.setName((String) groupView.get("name"));
                                viewGroup.setAppId(applicationContext.getId());
                            }
                            viewGroup.setDisplayName((String) groupView.get("displayName"));
                            long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
                            if (groupView.get("views") != null) {
                                for (String view : (List<String>) groupView.get("views")) {
                                    addViewsAndColumns(moduleName, groupId, view, modBean, applicationContext, isEmployeePortal, portalAdminRole.getId());
                                }
                            }
                        }
                    } else {
                        Map<String, FacilioView> moduleViews = ViewFactory.getModuleViews(moduleName, modBean.getModule(moduleName));
                        ViewGroups viewGroup = new ViewGroups();
                        viewGroup.setName("systemviews");
                        viewGroup.setDisplayName("System Views");
                        if (applicationContext.getLinkName().equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
                            viewGroup.setAppId(applicationContext.getId());
                            isEmployeePortal = true;
                        } else {
                            viewGroup.setAppId(applicationContext.getId());
                        }
                        long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
                        for (String viewName : moduleViews.keySet()) {
                            addViewsAndColumns(moduleName, groupId, viewName, modBean, applicationContext, isEmployeePortal, portalAdminRole.getId());
                        }
                    }
                }
            }
        }
        return false;
    }
    private void addViewsAndColumns(String moduleName,Long groupId,String viewName,ModuleBean modBean,ApplicationContext applicationContext,boolean isEmployeePortal, long portalAdminRoleId) throws Exception {
        List<ViewField> viewColumns = ColumnFactory.getColumns(moduleName, viewName);
        List<ViewField> columns = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(viewColumns)) {
            for (ViewField viewField : viewColumns) {
                FacilioField field = modBean.getField(viewField.getName(), moduleName);
                viewField.setField(field);
                viewField.setFieldId(field.getId());
                columns.add(viewField);
            }
        }
        long viewId = -1;
        FacilioModule module= modBean.getModule(moduleName);
        long moduleId = module.getModuleId();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        FacilioView view = ViewAPI.getView(viewName, moduleId, orgId, applicationContext.getId());
        if(view == null) {
            view = ViewFactory.getView(module, viewName, modBean);
            if(view != null) {
                if (isEmployeePortal){
                    view.setName(view.getName()+ "_employee_portal");
                    view.setViewSharing(null);
                } else {
                    view.setName(view.getName());
                }
                view.setAppId(applicationContext.getId());
                if(view.getTypeEnum() == null){
                    view.setType(FacilioView.ViewType.TABLE_LIST);
                }
                view.setDefault(false);
                view.setModuleId(moduleId);
                if (groupId != null && groupId > 0) {
                    view.setGroupId(groupId);
                }
                viewId = ViewAPI.addView(view, orgId);
                if (columns == null || columns.isEmpty()) {
                    columns = view.getFields();
                    for(ViewField column: columns) {
                        if (StringUtils.isNotEmpty(column.getParentFieldName())) {
                            LookupField parentField = (LookupField) modBean.getField(column.getParentFieldName(), moduleName);
                            if (parentField != null && parentField.getLookupModule() != null) {
                                FacilioField childField = modBean.getField(column.getName(), parentField.getLookupModule().getName());
                                column.setFieldId(childField.getFieldId());
                                column.setParentField(parentField);
                            }
                        }
                        else {
                            Long fieldId = modBean.getField(column.getName(), moduleName).getFieldId();
                            column.setFieldId(fieldId);
                        }
                    }
                }
                List<SortField> sortFields = view.getSortFields();
                if (sortFields != null && !sortFields.isEmpty()) {
                    for (SortField field : sortFields) {
                        String sortFieldName = field.getSortField().getName();
                        FacilioField sortfield = modBean.getField(sortFieldName, moduleName);
                        if (sortfield.getFieldId() != -1) {
                            field.setFieldId(sortfield.getFieldId());
                        }
                        else {
                            field.setFieldName(sortFieldName);
                        }
                        field.setOrgId(orgId);
                    }
                    ViewAPI.customizeViewSortColumns(viewId, sortFields);
                }
            }
        }
        if (columns != null && !columns.isEmpty()) {
            ViewAPI.customizeViewColumns(viewId, columns);
            List<SortField> sortFields = view.getSortFields();
            if (sortFields == null || sortFields.isEmpty()) {
                sortFields = ColumnFactory.getDefaultSortField(moduleName);
            }
            if (sortFields != null && !sortFields.isEmpty()) {
                for (SortField field : sortFields) {
                    FacilioField sortfield = modBean.getField(field.getSortField().getName(), moduleName);
                    Long fieldID = modBean.getField(sortfield.getName(), moduleName).getFieldId();
                    field.setFieldId(fieldID);
                    field.setOrgId(orgId);
                }
                ViewAPI.customizeViewSortColumns(viewId, sortFields);
            }
        }
    }
}