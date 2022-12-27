package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddModuleViewsAndGroups {
    private static final Logger LOGGER = Logger.getLogger(AddModuleViewsAndGroups.class.getName());
    private static long addViewGroup(long orgId, long appId, String moduleName, Map<String, Object> group) throws Exception {
        long groupId = -1;
        ViewGroups viewGroup = new ViewGroups();
        String groupName = (String) group.get("name");
        String groupDisplayName = (String) group.get("displayName");

        viewGroup.setName(groupName);
        viewGroup.setDisplayName(groupDisplayName);
        viewGroup.setAppId(appId);
        groupId = ViewAPI.addViewGroup(viewGroup, orgId, moduleName);
        return groupId;
    }

    public static void addViews(String moduleName, List<Map<String, Object>> groupVsViews, List<ApplicationContext> allApplications) throws Exception {
        LOGGER.info("Started adding Views and Groups for module - " + moduleName);
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        long orgUserId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        Map<String, ApplicationContext> allApplicationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allApplications)) {
            allApplicationMap = allApplications.stream().collect(Collectors.toMap(ApplicationContext::getLinkName, Function.identity()));
        }
        List<Long> allApplicationIds = allApplications.stream().map(ApplicationContext::getId).collect(Collectors.toList());
        long mainAppId = allApplicationMap.get(SignupUtil.getSignupApplicationLinkName()).getId();

        Map<Long, Map<String, ViewGroups>> allViewGroupsMap = new HashMap<>();
        allApplicationIds.forEach(appId -> allViewGroupsMap.put(appId, new HashMap<>()));
        List<FacilioField> allModuleFields = modBean.getAllFields(moduleName);

        // For "hidden-all" & "pendingapproval"
        FacilioView allView = new FacilioView();
        boolean containsAll = false;
        boolean containsPendingApproval = false;

        try {
            for (Map<String, Object> group : groupVsViews) {
                //Add ViewGroups
                String groupName = (String) group.get("name");
                ArrayList<FacilioView> views = (ArrayList<FacilioView>) group.get("views");
                List<String> groupAppLinkNamesFromMap = (List<String>) group.get("appLinkNames");

                List<String> groupAppLinkNames = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(groupAppLinkNamesFromMap)){
                    groupAppLinkNames.addAll(groupAppLinkNamesFromMap);
                    if(SignupUtil.maintenanceAppSignup()) {
                        groupAppLinkNames.remove(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    }
                } else{
                    groupAppLinkNames.add(SignupUtil.getSignupApplicationLinkName());
                }
                for (String groupAppLinkName : groupAppLinkNames) {
                    long appId = allApplicationMap.get(groupAppLinkName).getId();
                    long groupId = addViewGroup(orgId, appId, moduleName, group);
                    allViewGroupsMap.get(appId).put(groupName, ViewAPI.getGroup(groupId));
                }

                for (FacilioView view : views) {
                    List<String> viewAppLinkNames = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(view.getAppLinkNames())) {
                        viewAppLinkNames.addAll(view.getAppLinkNames());
                    } else {
                        viewAppLinkNames.add(SignupUtil.getSignupApplicationLinkName());
                    }
                    if(SignupUtil.maintenanceAppSignup()) {
                        viewAppLinkNames.remove(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    }
                    for (String viewAppLinkName : viewAppLinkNames) {
                        long groupId = -1;
                        String viewName = view.getName();
                        long appId = allApplicationMap.get(viewAppLinkName).getId();

                        //Add/ Use ViewGroups
                        if (allViewGroupsMap.get(appId).containsKey(groupName)) {
                            groupId = allViewGroupsMap.get(appId).get(groupName).getId();
                        } else {
                            groupId = addViewGroup(orgId, appId, moduleName, group);
                            allViewGroupsMap.get(appId).put(groupName, ViewAPI.getGroup(groupId));
                        }

                        //Add Views
                        view.setId(-1);
                        view.setAppId(appId);
                        view.setDefault(true);
                        view.setLocked(false);
                        view.setGroupId(groupId);
                        view.setOwnerId(orgUserId);
                        view.setModuleName(moduleName);
                        view.setModuleId(module.getModuleId());
                        view.setType(FacilioView.ViewType.TABLE_LIST);

                        long viewId = ViewAPI.addView(view, orgId);
                        view.setId(viewId);

                        //Add Columns And SortFields
                        addColumnsAndSortField(moduleName, modBean, view, allModuleFields);

                        if (viewName.equals("all")){
                            containsAll = true;
                            allView = view;
                        }
                        if (viewName.equals("pendingapproval")) {
                            containsPendingApproval = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error occurred in ORGID - " + orgId + " ModuleId - " + module.getModuleId() + " ModuleName - " + moduleName + " Error - " + e.getMessage());
            throw e;
        }

        if (containsAll){
            addHiddenViews(orgId, mainAppId, moduleName, module, modBean, allView, "hidden-all", allModuleFields);
            if (!containsPendingApproval){
                addHiddenViews(orgId, mainAppId, moduleName, module, modBean, allView, "pendingapproval", allModuleFields);
            }
        }
        LOGGER.info("Completed adding Views and Groups for module - " + moduleName);
    }

    public static void addHiddenViews(long orgId, long mainAppId, String moduleName, FacilioModule module, ModuleBean modBean, FacilioView view, String viewName, List<FacilioField> allModuleFields) throws Exception{
        if (ViewAPI.getView(viewName, view.getModuleId(), orgId, view.getAppId()) == null) {
            view.setGroupId(-1);
            view.setHidden(true);
            view.setDefault(true);
            view.setName(viewName);
            view.setAppId(mainAppId);
            view.setDisplayName(null);

            long hiddenViewId = addView(view, orgId);
            view.setId(hiddenViewId);

            addColumnsAndSortField(moduleName, modBean, view, allModuleFields);
        }
    }
    public static void addColumnsAndSortField(String moduleName, ModuleBean modBean, FacilioView view, List<FacilioField> fields) throws Exception {
        // Add Columns
        LOGGER.info(String.format("Started adding ViewColumns for ViewId - %d ViewName - %s", view.getId(), view.getName()));
        List<ViewField> viewFields = new ArrayList<>();
        Map<String, FacilioField> fieldMap = fields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(),(name1, name2) -> { return name1; }));

        if (CollectionUtils.isNotEmpty(view.getFields())){
            viewFields = view.getFields();
        } else {
            viewFields = ColumnFactory.getColumns(moduleName, view.getName());
        }

        if (viewFields != null && !viewFields.isEmpty()) {
            for (ViewField viewField : viewFields) {
                viewField.setFieldName(viewField.getName());

                if (StringUtils.isNotEmpty(viewField.getParentFieldName())) {
                    LookupField parentField = (LookupField) modBean.getField(viewField.getParentFieldName(), moduleName);
                    if (parentField != null && parentField.getLookupModule() != null) {
                        FacilioField childField = modBean.getField(viewField.getName(), parentField.getLookupModule().getName());
                        viewField.setFieldId(childField.getFieldId());
                        viewField.setParentField(parentField);
                        viewField.setParentFieldId(parentField.getFieldId());
                    }
                } else {
                    FacilioField field = null;
                    if (fieldMap.get(viewField.getName()) != null) {
                        field = fieldMap.get(viewField.getName());
                    }
                    if (viewField.getName().equals("id")) {
                        field = new FacilioField();
                        field.setFieldId(-1);
                        field.setName("id");
                        field.setDisplayName("Id");
                    }
                    if (viewField.getName().equals("siteId")) {
                        field = new FacilioField();
                        field.setFieldId(-1);
                        field.setName("siteId");
                        field.setDisplayName("Site");
                    }

                    viewField.setField(field);
                    viewField.setFieldId(field.getFieldId());
                }
            }
        } else {
            viewFields = new ArrayList<>();
            for (FacilioField field : fields) {
                ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
                viewField.setField(field);
                viewField.setViewId(view.getId());
                viewField.setFieldName(field.getName());
                viewField.setFieldId(field.getFieldId());
                viewFields.add(viewField);
            }
        }
        ViewAPI.customizeViewColumns(view.getId(), viewFields);
        LOGGER.info(String.format("Completed adding ViewColumns for ViewId - %d ViewName - %s",view.getId(), view.getName()));

        // Add Sort Fields
        LOGGER.info(String.format("Started adding SortColumns for ViewId - %d ViewName - %s",view.getId(), view.getName()));
        List<SortField> sortFields = view.getSortFields();
        if (sortFields == null || sortFields.isEmpty()) {
            sortFields = ColumnFactory.getDefaultSortField(moduleName);
        }
        if (sortFields != null && !sortFields.isEmpty()) {
            for (SortField field : sortFields) {
                String fieldName = field.getSortField().getName();
                if (fieldName.equals("id")){
                    field.setFieldName("id");
                    field.setFieldId(-1);
                } else {
                    FacilioField sortField = fieldMap.get(field.getSortField().getName());
                    field.setFieldId(sortField.getId());
                    field.setFieldName(fieldName);
                }
                ViewAPI.customizeViewSortColumns(view.getId(), sortFields);
                LOGGER.info(String.format("Completed adding SortColumns for ViewId - %d ViewName - %s",view.getId(), view.getName()));
            }
        }
    }

    public static long addView(FacilioView view, long orgId) throws Exception {
        view.setOrgId(orgId);
        view.setId(-1);
        try {
            Criteria criteria = view.getCriteria();
            if(criteria != null) {
                long criteriaId = CriteriaAPI.addCriteria(criteria, orgId);
                view.setCriteriaId(criteriaId);
            }

            Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table("Views")
                    .fields(FieldFactory.getViewFields())
                    .addRecord(viewProp);

            insertBuilder.save();
            long viewId = (long) viewProp.get("id");
            view.setId(viewId);
            ViewAPI.addOrUpdateExtendedViewDetails(view, viewProp, true);

            return viewId;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
            if (e.getMessage().contains("Duplicate entry")) {
                throw new IllegalArgumentException("ViewName already taken");
            } else {
                throw e;
            }
        }
    }
}