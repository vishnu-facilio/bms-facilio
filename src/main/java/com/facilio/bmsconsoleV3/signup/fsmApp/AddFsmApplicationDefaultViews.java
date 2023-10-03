package com.facilio.bmsconsoleV3.signup.fsmApp;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewGroups;
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
public class AddFsmApplicationDefaultViews extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

            ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<String> sysModuleNames = V3ModuleAPI.getSystemModuleNamesForApp(fsm.getLinkName());
            if (!AddSignupDataCommandV3.createViews) {
                for (String moduleName : sysModuleNames) {
                    List<Map<String, Object>> groupViews = ViewFactory.getGroupVsViews(moduleName);
                    if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER) || moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
                        for (Map<String, Object> groupView : groupViews) {
                            ViewGroups viewGroup = new ViewGroups();
                            viewGroup.setName((String) groupView.get("name") + "_system_fsm");
                            viewGroup.setDisplayName((String) groupView.get("displayName"));
                            viewGroup.setAppId(fsm.getId());
                            long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
                            if (groupView.get("views") != null) {
                                for (String view : (List<String>) groupView.get("views")) {
                                    addViewsAndColumns(moduleName, groupId, view, modBean, fsm);
                                }
                            }
                        }
                    } else {
                        Map<String, FacilioView> moduleViews = ViewFactory.getModuleViews(moduleName, modBean.getModule(moduleName));
                        ViewGroups viewGroup = new ViewGroups();
                        viewGroup.setName(moduleName + "_system_fsm");
                        viewGroup.setDisplayName("System");
                        viewGroup.setAppId(fsm.getId());
                        long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), moduleName);
                        for (String viewName : moduleViews.keySet()) {
                            addViewsAndColumns(moduleName, groupId, viewName, modBean, fsm);
                        }
                    }
                }
            }
        return false;
    }
    private void addViewsAndColumns(String moduleName,Long groupId,String viewName,ModuleBean modBean,ApplicationContext fsm) throws Exception {
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
        FacilioView view = ViewAPI.getView(viewName, moduleId, orgId, fsm.getId());
        if(view == null) {
            view = ViewFactory.getView(module, viewName, modBean);
            if(view != null) {
                view.setName("fsm_" + view.getName());
                view.setAppId(fsm.getId());
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