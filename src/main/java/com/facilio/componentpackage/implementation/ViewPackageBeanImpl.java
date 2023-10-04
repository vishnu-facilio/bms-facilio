package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class ViewPackageBeanImpl implements PackageBean<FacilioView> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getViewIdVsModuleId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getViewIdVsModuleId(false);
    }

    @Override
    public Map<Long, FacilioView> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FacilioView> viewIdVsView = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            List<FacilioView> dbViewsList = getViewsFromIds(idsSubList);
            Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(idsSubList, ModuleFactory.getViewSharingModule(), SingleSharingContext.class,
                    FieldFactory.getSharingFields(ModuleFactory.getViewSharingModule()));

            dbViewsList.forEach(view -> viewIdVsView.put(view.getId(), view));
            if (MapUtils.isNotEmpty(sharingMap)) {
                viewIdVsView.keySet().stream().filter(sharingMap::containsKey).forEach(viewId -> viewIdVsView.get(viewId).setViewSharing(sharingMap.get(viewId)));
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return viewIdVsView;
    }

    @Override
    public void convertToXMLComponent(FacilioView view, XMLBuilder viewElement) throws Exception {
        // TODO - Handle Criteria (Lookup), Sharing(User, Group)
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule currModule = view.getModuleId() > 0 ?
                                moduleBean.getModule(view.getModuleId()) :
                                (StringUtils.isNotEmpty(view.getModuleName()) ? moduleBean.getModule(view.getModuleName()) : null);
        ApplicationContext application = view.getAppId() > 0 ?
                                ApplicationApi.getApplicationForId(view.getAppId()) :
                                ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        String currModuleName = currModule != null ? currModule.getName() : null;

        // View
        viewElement.element(PackageConstants.NAME).text(view.getName());
        viewElement.element(PackageConstants.MODULENAME).text(currModuleName);
        viewElement.element(PackageConstants.DISPLAY_NAME).text(view.getDisplayName());
        viewElement.element(PackageConstants.IS_DEFAULT).text(String.valueOf(view.isDefault()));
        viewElement.element(PackageConstants.ViewConstants.VIEW_TYPE).text(String.valueOf(view.getType()));
        viewElement.element(PackageConstants.ViewConstants.IS_LOCKED).text(String.valueOf(view.isLocked()));
        viewElement.element(PackageConstants.ViewConstants.STATUS).text(String.valueOf(view.isStatus()));
        viewElement.element(PackageConstants.SEQUENCE_NUMBER).text(String.valueOf(view.getSequenceNumber()));
        viewElement.element(PackageConstants.ViewConstants.IS_LIST_VIEW).text(String.valueOf(view.isListView()));
        viewElement.element(PackageConstants.ViewConstants.IS_CALENDAR_VIEW).text(String.valueOf(view.isCalendarView()));
        viewElement.element(PackageConstants.ViewConstants.EXCLUDE_MODULE_CRITERIA).text(String.valueOf(view.isExcludeModuleCriteria()));

        if (LoadViewCommand.HIDDEN_VIEW_NAMES.contains(view.getName())) {
            viewElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        } else {
            viewElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(application.getLinkName());
        }

        // View Group
        if (view.getGroupId() > 0) {
            ViewGroups viewGroup = ViewAPI.getViewGroup(view.getGroupId());
            XMLBuilder viewGroupElement = viewElement.element(PackageConstants.ViewConstants.VIEW_GROUP);

            viewGroupElement.element(PackageConstants.NAME).text(viewGroup.getName());
            viewGroupElement.element(PackageConstants.DISPLAY_NAME).text(viewGroup.getDisplayName());
            viewGroupElement.element(PackageConstants.SEQUENCE_NUMBER).text(String.valueOf(viewGroup.getSequenceNumber()));
            viewGroupElement.element(PackageConstants.ViewConstants.VIEW_GROUP_TYPE).text(String.valueOf(viewGroup.getGroupType()));
        }

        // View Fields
        if (CollectionUtils.isNotEmpty(view.getFields())) {
            XMLBuilder viewFieldElementsList = viewElement.element(PackageConstants.ViewConstants.VIEW_FIELDS_LIST);
            for (ViewField viewField : view.getFields()) {
                XMLBuilder viewFieldElement = viewFieldElementsList.element(PackageConstants.ViewConstants.VIEW_FIELD);

                viewFieldElement.element(PackageConstants.DISPLAY_NAME).text(viewField.getColumnDisplayName());
                viewFieldElement.element(PackageConstants.ViewConstants.VIEW_FIELD_NAME).text(viewField.getFieldName());
                viewFieldElement.element(PackageConstants.ViewConstants.CUSTOMIZATION).text(viewField.getCustomization());

                if (viewField.getField() == null && ((StringUtils.isNotEmpty(viewField.getName()) && viewField.getName().equals("siteId")) ||
                        (StringUtils.isNotEmpty(viewField.getColumnDisplayName()) && viewField.getColumnDisplayName().equals("Site")))) {
                    viewField.setField(FieldFactory.getSiteIdField(currModule));
                }
                if (viewField.getField() != null) {
                    FacilioField field = viewField.getField();
                    viewFieldElement.element(PackageConstants.MODULENAME).text(field.getModule().getName());
                    viewFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                }
                if (viewField.getParentField() != null) {
                    FacilioField parentFieldName = viewField.getParentField();
                    viewFieldElement.element(PackageConstants.ViewConstants.PARENT_FIELD_NAME).text(parentFieldName.getName());
                    viewFieldElement.element(PackageConstants.ViewConstants.PARENT_MODULE_NAME).text(parentFieldName.getModule().getName());
                }
            }
        }

        // View Sort Fields
        if (CollectionUtils.isNotEmpty(view.getSortFields())) {
            XMLBuilder sortFieldsElementsList = viewElement.element(PackageConstants.ViewConstants.SORT_FIELDS_LIST);
            for (SortField sortField : view.getSortFields()) {
                XMLBuilder sortFieldElement = sortFieldsElementsList.element(PackageConstants.ViewConstants.SORT_FIELD);

                sortFieldElement.element(PackageConstants.ViewConstants.SORT_FIELD_NAME).text(sortField.getFieldName());
                sortFieldElement.element(PackageConstants.ViewConstants.IS_ASCENDING).text(String.valueOf(sortField.getIsAscending()));

                if (sortField.getFieldId() > 0) {
                    FacilioField field = sortField.getSortField();
                    sortFieldElement.element(PackageConstants.MODULENAME).text(field.getModule().getName());
                    sortFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                }
            }
        }

        // Calendar View
        if (view.isCalendarView()) {
            CalendarViewContext calendarViewContext = view.getCalendarViewContext();
            XMLBuilder calendarViewElement = viewElement.element(PackageConstants.ViewConstants.CALENDAR_VIEW_CONTEXT);
            calendarViewElement.element(PackageConstants.ViewConstants.START_DATE_FIELD_NAME).text(calendarViewContext.getStartDateField().getName());
            calendarViewElement.element(PackageConstants.ViewConstants.DEFAULT_CALENDAR_VIEW).text(String.valueOf(calendarViewContext.getDefaultCalendarView()));
            if (calendarViewContext.getEndDateFieldId() > 0) {
                calendarViewElement.element(PackageConstants.ViewConstants.END_DATE_FIELD_NAME).text(calendarViewContext.getEndDateField().getName());
            }
        }

        // Criteria
        if (view.getCriteria() != null && !view.getCriteria().isEmpty()) {
            LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + currModuleName + " ViewName - " + view.getName());
            viewElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(view.getCriteria(), viewElement.element(PackageConstants.CriteriaConstants.CRITERIA), currModuleName));
        }

        // View Sharing
        if (CollectionUtils.isNotEmpty(view.getViewSharing())) {
            PackageBeanUtil.constructBuilderFromSharingContext(view.getViewSharing(), viewElement.element(PackageConstants.ViewConstants.VIEW_SHARING));
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
        Map<String, Map<String, Map<String, Long>>> moduleNameVsAppLinkNameVsViewName = getBasicViewDetails();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder viewElement = idVsData.getValue();
            String viewName = viewElement.getElement(PackageConstants.NAME).getText();
            String moduleName = viewElement.getElement(PackageConstants.MODULENAME).getText();
            String appLinkName = viewElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();

            if (moduleNameVsAppLinkNameVsViewName.containsKey(moduleName) && moduleNameVsAppLinkNameVsViewName.get(moduleName).containsKey(appLinkName) &&
                            moduleNameVsAppLinkNameVsViewName.get(moduleName).get(appLinkName).containsKey(viewName) ) {
                long viewId = moduleNameVsAppLinkNameVsViewName.get(moduleName).get(appLinkName).get(viewName);
                uniqueIdentifierVsComponentId.put(uniqueIdentifier, viewId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioView facilioView;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formElement = idVsData.getValue();
            facilioView = constructViewFromXMLBuilder(formElement, appNameVsAppId, moduleBean);

            long viewId = ViewAPI.addView(facilioView, facilioView.getOrgId());
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), viewId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioView facilioView;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long viewId = idVsData.getKey();
            if (viewId == null || viewId < 0) {
                continue;
            }

            XMLBuilder formElement = idVsData.getValue();

            facilioView = constructViewFromXMLBuilder(formElement, appNameVsAppId, moduleBean);
            facilioView.setId(viewId);

            ViewAPI.updateView(viewId, facilioView);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            ViewAPI.deleteFacilioViews(ids);
        }
    }

    private Map<String, Map<String, Map<String, Long>>> getBasicViewDetails() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule viewsModule = ModuleFactory.getViewsModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(viewsModule));
            add(FieldFactory.getNameField(viewsModule));
            add(FieldFactory.getModuleIdField(viewsModule));
            add(FieldFactory.getNumberField("appId", "APP_ID", viewsModule));
            add(FieldFactory.getStringField("moduleName", "MODULENAME", viewsModule));
        }};

        List<Map<String, Object>> propsList = getViewProps(selectableFields, null);

        List<ApplicationContext> applicationContexts = ApplicationApi.getAllApplicationsWithOutFilter();
        Map<Long, String> appIdVsAppLinkName = new HashMap<>();
        if (CollectionUtils.isNotEmpty(applicationContexts)) {
            appIdVsAppLinkName = applicationContexts.stream().collect(Collectors.toMap(ApplicationContext::getId, ApplicationContext::getLinkName));
        }

        Map<String, Map<String, Map<String, Long>>> moduleNameVsAppLinkNameVsViewName = new HashMap<>();
        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                long viewId = (long) prop.get("id");
                long appId = (long) prop.get("appId");
                String viewName = (String) prop.get("name");
                String moduleName = (String) prop.get("moduleName");
                String appLinkName = appIdVsAppLinkName.get(appId);
                long moduleId = (long) prop.getOrDefault("moduleId", -1l);

                if (StringUtils.isEmpty(moduleName) && moduleId > 0) {
                    FacilioModule module = moduleBean.getModule(moduleId);
                    moduleName = module != null ? module.getName() : null;
                }

                if (!moduleNameVsAppLinkNameVsViewName.containsKey(moduleName)) {
                    moduleNameVsAppLinkNameVsViewName.put(moduleName, new HashMap<>());
                }

                if (!moduleNameVsAppLinkNameVsViewName.get(moduleName).containsKey(appLinkName)) {
                    moduleNameVsAppLinkNameVsViewName.get(moduleName).put(appLinkName, new HashMap<>());
                }

                moduleNameVsAppLinkNameVsViewName.get(moduleName).get(appLinkName).put(viewName, viewId);
            }
        }
        return moduleNameVsAppLinkNameVsViewName;
    }

    private Map<Long, Long> getViewIdVsModuleId(boolean fetchSystem) throws Exception {
        Map<Long, Long> viewIdVsModuleId = new HashMap<>();
        FacilioModule viewsModule = ModuleFactory.getViewsModule();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getModuleIdField(viewsModule));
            add(FieldFactory.getIdField(viewsModule));
        }};

        Criteria filterCriteria = new Criteria();
        filterCriteria.addAndCondition(CriteriaAPI.getCondition("ISDEFAULT", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));
        filterCriteria.addAndCondition(CriteriaAPI.getCondition("VIEW_TYPE", "type", String.valueOf(FacilioView.ViewType.TABLE_LIST.getIntVal()), NumberOperators.EQUALS));
        if (CollectionUtils.isNotEmpty(applicationIds)) {
            filterCriteria.addAndCondition(CriteriaAPI.getCondition("APP_ID", "appId", StringUtils.join(applicationIds, ","), NumberOperators.EQUALS));
        }
        if (fetchSystem) {
            filterCriteria.addOrCondition(CriteriaAPI.getCondition("NAME", "name", "hidden-all,pendingapproval", StringOperators.IS));
        }

        List<Map<String, Object>> propsList = getViewProps(selectableFields, filterCriteria);

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                long moduleId = prop.containsKey("moduleId") ? (Long) prop.get("moduleId") : -1;
                viewIdVsModuleId.put((Long) prop.get("id"), moduleId);
            }
        }
        return viewIdVsModuleId;
    }

    private List<Map<String, Object>> getViewProps(List<FacilioField> selectableFields, Criteria criteria) throws Exception {
        FacilioModule viewsModule = ModuleFactory.getViewsModule();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(viewsModule.getTableName())
                .select(selectableFields);

        if (criteria != null && !criteria.isEmpty()) {
                selectRecordBuilder.andCriteria(criteria);
        }

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        return propsList;
    }

    private List<FacilioView> getViewsFromIds(List<Long> viewIds) throws Exception {
        FacilioModule module = ModuleFactory.getViewsModule();
        List<FacilioField> fields = FieldFactory.getViewFields();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(viewIds, module));
        List<Map<String, Object>> viewProps = builder.get();

        List<FacilioView> views = ViewAPI.getAllViewDetails(viewProps, AccountUtil.getCurrentOrg().getOrgId(), false);

        return views;
    }

    private FacilioView constructViewFromXMLBuilder(XMLBuilder viewElement, Map<String, Long> appNameVsAppId, ModuleBean moduleBean) throws Exception {
        // Criteria will be constructed only for custom views

        boolean isDefault, status, isHidden, isLocked, isListView, isCalendarView, excludeModuleCriteria;
        String viewName, displayName, moduleName, appLinkName, sequenceNumberStr;
        int sequenceNumber, viewTypeInt;
        FacilioView.ViewType viewType;
        long moduleId, appId;

        viewName = viewElement.getElement(PackageConstants.NAME).getText();
        moduleName = viewElement.getElement(PackageConstants.MODULENAME).getText();
        displayName = viewElement.getElement(PackageConstants.DISPLAY_NAME).getText();
        appLinkName = viewElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
        isDefault = Boolean.parseBoolean(viewElement.getElement(PackageConstants.IS_DEFAULT).getText());
        sequenceNumberStr = viewElement.getElement(PackageConstants.SEQUENCE_NUMBER).getText();
        sequenceNumber = StringUtils.isNotEmpty(sequenceNumberStr) ? Integer.parseInt(sequenceNumberStr) : -1;
        status = Boolean.parseBoolean(viewElement.getElement(PackageConstants.ViewConstants.STATUS).getText());
        viewTypeInt = Integer.parseInt(viewElement.getElement(PackageConstants.ViewConstants.VIEW_TYPE).getText());
        isLocked = Boolean.parseBoolean(viewElement.getElement(PackageConstants.ViewConstants.IS_LOCKED).getText());
        isListView = Boolean.parseBoolean(viewElement.getElement(PackageConstants.ViewConstants.IS_LIST_VIEW).getText());
        isCalendarView = Boolean.parseBoolean(viewElement.getElement(PackageConstants.ViewConstants.IS_CALENDAR_VIEW).getText());
        excludeModuleCriteria = Boolean.parseBoolean(viewElement.getElement(PackageConstants.ViewConstants.EXCLUDE_MODULE_CRITERIA).getText());

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        long ownerId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();

        appId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;
        viewType = FacilioView.ViewType.getViewType(viewTypeInt);
        FacilioModule module = moduleBean.getModule(moduleName);
        moduleId = module.getModuleId();

        // View
        FacilioView view = new FacilioView(viewName, displayName, viewType, moduleName, moduleId, appId, isDefault,
                                        ownerId, sequenceNumber, isLocked, isListView, isCalendarView, excludeModuleCriteria);
        view.setOrgId(orgId);
        view.setStatus(status);
        XMLBuilder criteriaElement = viewElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        XMLBuilder viewGroupElement = viewElement.getElement(PackageConstants.ViewConstants.VIEW_GROUP);
        XMLBuilder viewFieldElementsList = viewElement.getElement(PackageConstants.ViewConstants.VIEW_FIELDS_LIST);
        XMLBuilder sortFieldsElementsList = viewElement.getElement(PackageConstants.ViewConstants.SORT_FIELDS_LIST);
        XMLBuilder calendarViewElement = viewElement.getElement(PackageConstants.ViewConstants.CALENDAR_VIEW_CONTEXT);
        XMLBuilder sharingElement = viewElement.getElement(PackageConstants.ViewConstants.VIEW_SHARING);

        // View Group
        if (viewGroupElement != null) {
            String groupName = viewGroupElement.getElement(PackageConstants.NAME).getText();
            String groupDisplayName = viewGroupElement.getElement(PackageConstants.DISPLAY_NAME).getText();
            String groupSeqNumberStr = viewGroupElement.getElement(PackageConstants.SEQUENCE_NUMBER).getText();
            int groupSequenceNumber = StringUtils.isNotEmpty(groupSeqNumberStr) ? Integer.parseInt(groupSeqNumberStr) : -1;
            int viewGroupType = Integer.parseInt(viewGroupElement.getElement(PackageConstants.ViewConstants.VIEW_GROUP_TYPE).getText());

            ViewGroups viewGroup = new ViewGroups(groupName, groupDisplayName, moduleName, moduleId, appId, groupSequenceNumber, viewGroupType);
            long viewGroupId = checkAndAddViewGroup(viewGroup);
            view.setGroupId(viewGroupId);
        }

        // View Fields
        if (viewFieldElementsList != null) {
            List<ViewField> viewFieldsList = new ArrayList<>();
            List<XMLBuilder> viewFieldsElementList = viewElement.getElementList(PackageConstants.ViewConstants.VIEW_FIELD);
            for (XMLBuilder viewFieldElement : viewFieldsElementList) {
                long fieldId = -1, parentFieldId = -1;
                String viewFieldDisplayName = viewFieldElement.getElement(PackageConstants.DISPLAY_NAME).getText();
                String customization = viewFieldElement.getElement(PackageConstants.ViewConstants.CUSTOMIZATION).getText();
                String viewFieldName = viewFieldElement.getElement(PackageConstants.ViewConstants.VIEW_FIELD_NAME).getText();
                if (viewFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME) != null) {
                    String facilioFieldName = viewFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).getText();
                    String fieldModuleName = viewFieldElement.getElement(PackageConstants.MODULENAME).getText();
                    FacilioField facilioField = moduleBean.getField(facilioFieldName, fieldModuleName);
                    fieldId = facilioField != null ? facilioField.getFieldId() : -1;
                }

                if (viewFieldElement.getElement(PackageConstants.ViewConstants.PARENT_FIELD_NAME) != null) {
                    String parentFieldName = viewFieldElement.getElement(PackageConstants.ViewConstants.PARENT_FIELD_NAME).getText();
                    String parentModuleName = viewFieldElement.getElement(PackageConstants.ViewConstants.PARENT_MODULE_NAME).getText();
                    FacilioField parentField = moduleBean.getField(parentFieldName, parentModuleName);
                    parentFieldId = parentField != null ? parentField.getFieldId() : -1;
                }

                ViewField viewField = new ViewField(viewFieldName, viewFieldDisplayName, fieldId, parentFieldId, customization);
                viewFieldsList.add(viewField);
            }
            view.setFields(viewFieldsList);
        }

        // Sort Fields
        if (sortFieldsElementsList != null) {
            List<SortField> sortFieldList = new ArrayList<>();
            List<XMLBuilder> sortFieldsElementList = viewElement.getElementList(PackageConstants.ViewConstants.SORT_FIELD);
            for (XMLBuilder sortFieldElement : sortFieldsElementList) {
                long fieldId = -1;
                String sortFieldName = sortFieldElement.getElement(PackageConstants.ViewConstants.SORT_FIELD_NAME).getText();
                boolean isAscending = Boolean.parseBoolean(sortFieldElement.getElement(PackageConstants.ViewConstants.SORT_FIELD_NAME).getText());

                if (sortFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME) != null) {
                    String facilioFieldName = sortFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).getText();
                    String fieldModuleName = sortFieldElement.getElement(PackageConstants.MODULENAME).getText();
                    FacilioField facilioField = moduleBean.getField(facilioFieldName, fieldModuleName);
                    fieldId = facilioField != null ? facilioField.getFieldId() : -1;
                }

                SortField sortField = new SortField(sortFieldName, fieldId, isAscending);
                sortFieldList.add(sortField);
            }
            view.setSortFields(sortFieldList);
        }

        // Calendar View
        if (calendarViewElement != null) {
            long startFieldId = -1, endFieldId = -1;
            int defaultCalendarView = Integer.parseInt(calendarViewElement.getElement(PackageConstants.ViewConstants.DEFAULT_CALENDAR_VIEW).getText());
            String startFieldName = calendarViewElement.getElement(PackageConstants.ViewConstants.START_DATE_FIELD_NAME).getText();
            FacilioField startField = moduleBean.getField(startFieldName, moduleName);
            startFieldId = startField != null ? startField.getFieldId() : -1;
            if (calendarViewElement.getElement(PackageConstants.ViewConstants.END_DATE_FIELD_NAME) != null) {
                String endFieldName = calendarViewElement.getElement(PackageConstants.ViewConstants.END_DATE_FIELD_NAME).getText();
                FacilioField endField = moduleBean.getField(endFieldName, moduleName);
                endFieldId = endField != null ? endField.getFieldId() : -1;
            }

            CalendarViewContext calendarViewContext = new CalendarViewContext(startFieldId, endFieldId, defaultCalendarView);
            view.setCalendarViewContext(calendarViewContext);
        }

        // Criteria
        if (criteriaElement != null && !view.isDefault()) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            view.setCriteria(criteria);
        }

        // View Sharing
        if (sharingElement != null) {
            SharingContext<SingleSharingContext> sharingContexts = PackageBeanUtil.constructSharingContextFromBuilder(sharingElement);
            view.setViewSharing(sharingContexts);
        }

        return view;
    }

    private long checkAndAddViewGroup(ViewGroups viewGroup) throws Exception {
        long viewGroupId;
        ViewGroups dbViewGroup = ViewAPI.getViewGroup(viewGroup.getName(), viewGroup.getModuleName(), viewGroup.getModuleId(), viewGroup.getAppId());
        if (dbViewGroup != null) {
            viewGroupId = dbViewGroup.getId();
            viewGroup.setId(viewGroupId);

            ViewAPI.updateViewGroup(viewGroup);
        } else {
            viewGroupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), viewGroup.getModuleName());
        }

        return viewGroupId;
    }
}
