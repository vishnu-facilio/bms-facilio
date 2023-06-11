package com.facilio.metamigration.util;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.metamigration.beans.MetaMigrationBean;
import org.apache.commons.collections.CollectionUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.util.FormsAPI;
import org.apache.commons.collections.MapUtils;
import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.beans.WebTabBean;
import com.facilio.beans.ModuleBean;
import com.facilio.util.FacilioUtil;
import com.facilio.fw.BeanFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import com.facilio.modules.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class MetaMigrationAPI {
    public enum ComponentsEnum implements FacilioIntEnum {
        MODULES_AND_FIELDS,
        TABS_AND_LAYOUTS,
        FORMS;

        public ComponentsEnum getComponentsEnum(int val) {
            return TYPE_MAP.get(val);
        }

        public static final Map<Integer, ComponentsEnum> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, ComponentsEnum> initTypeMap() {
            Map<Integer, ComponentsEnum> typeMap = new HashMap<>();
            for(ComponentsEnum type : values()) {
                typeMap.put(type.getIndex(), type);
            }
            return typeMap;
        }
    }
    public static final List<Integer> IGNORE_MODULE_TYPES = new ArrayList<Integer>(){{
        add(FacilioModule.ModuleType.NOTES.getValue());
        add(FacilioModule.ModuleType.PHOTOS.getValue());
        add(FacilioModule.ModuleType.ACTIVITY.getValue());
        add(FacilioModule.ModuleType.ATTACHMENTS.getValue());
        add(FacilioModule.ModuleType.LIVE_FORMULA.getValue());
        add(FacilioModule.ModuleType.RELATION_DATA.getValue());
        add(FacilioModule.ModuleType.ENUM_REL_MODULE.getValue());
        add(FacilioModule.ModuleType.SCHEDULED_FORMULA.getValue());
        add(FacilioModule.ModuleType.LOOKUP_REL_MODULE.getValue());
        add(FacilioModule.ModuleType.CLASSIFICATION_DATA.getValue());
        add(FacilioModule.ModuleType.LARGE_TEXT_DATA_MODULE.getValue());
        add(FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA.getValue());
        add(FacilioModule.ModuleType.CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP.getValue());
    }};
    public static void migrateMetaData(long sourceOrgId, long targetOrgId, List<Integer> componentsToMigrate) throws Exception {
        Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "metaMigration");
        boolean hasMetaMigrationPermission = false;
        if (MapUtils.isNotEmpty(orgInfo)) {
            hasMetaMigrationPermission = (boolean) Boolean.parseBoolean(String.valueOf(orgInfo.get("value")));
        }

        if (!hasMetaMigrationPermission) {
            return;
        }

        MetaMigrationBean sourceMetaMigration = (MetaMigrationBean) BeanFactory.lookup("MetaMigrationBean", true, sourceOrgId);
        MetaMigrationBean targetMetaMigration = (MetaMigrationBean) BeanFactory.lookup("MetaMigrationBean", true, targetOrgId);
        ModuleBean sourceModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        List<ApplicationContext> oldOrgApplications = sourceMetaMigration.getAllApplications();
        List<ApplicationContext> newOrgApplications = targetMetaMigration.getAllApplications();
        Map<String, ApplicationContext> newOrgAppNameVsApplication = newOrgApplications.stream()
                .collect(Collectors.toMap(ApplicationContext::getLinkName, Function.identity()));

        // oldVsNewAppIds
        Map<Long, Long> oldVsNewAppIds = new HashMap<>();
        for (ApplicationContext oldAppContext : oldOrgApplications) {
            if (newOrgAppNameVsApplication.containsKey(oldAppContext.getLinkName())) {
                long newOrgAppId = newOrgAppNameVsApplication.get(oldAppContext.getLinkName()).getId();
                oldVsNewAppIds.put(oldAppContext.getId(), newOrgAppId);
            }
        }

        // Migrate Modules
        if (componentsToMigrate.contains(ComponentsEnum.MODULES_AND_FIELDS.getIndex())) {
            List<FacilioModule> oldSystemModules = sourceModuleBean.getModuleList(getModuleFetchCriteria(false));
            List<FacilioModule> newSystemModules = targetModuleBean.getModuleList(getModuleFetchCriteria(false));

            List<FacilioModule> newCustomModules = new ArrayList<>();
            Map<Long, Long> oldVsNewCustomModuleIds = new HashMap<>();
            Map<String, Object> result = migrateModules(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration);
            if (MapUtils.isNotEmpty(result)) {
                newCustomModules = (List<FacilioModule>) result.get(MetaMigrationConstants.MODULES);
                oldVsNewCustomModuleIds = (Map<Long, Long>) result.get(MetaMigrationConstants.OLD_VS_NEW_IDS);
            }


            Map<String, FacilioModule> newSystemModuleIdsVsModule = newSystemModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity()));

            Map<Long, Long> oldVsNewSystemModuleIds = new HashMap<>();
            if (CollectionUtils.isNotEmpty(oldSystemModules) && CollectionUtils.isNotEmpty(newSystemModules)) {
                for (FacilioModule oldSystemModule : oldSystemModules) {
                    FacilioModule newSystemModule = newSystemModuleIdsVsModule.get(oldSystemModule.getName());
                    if (newSystemModule == null) {
                        LOGGER.info("System Module not present - ModuleName - " + oldSystemModule.getName() + " ModuleId - " + oldSystemModule.getModuleId());
                        continue;
                    }
                    oldVsNewSystemModuleIds.put(oldSystemModule.getModuleId(), newSystemModule.getModuleId());
                }
            }

            Map<Long, Long> oldVsNewModuleIds = new HashMap<>();
            oldVsNewModuleIds.putAll(oldVsNewCustomModuleIds);
            oldVsNewModuleIds.putAll(oldVsNewSystemModuleIds);

            // Migrate Fields
            migrateFields(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, oldVsNewModuleIds);
        }

        // Tabs and layouts
        if (componentsToMigrate.contains(ComponentsEnum.TABS_AND_LAYOUTS.getIndex())) {
            migrateTabsAndLayouts(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, sourceOrgId, targetOrgId, oldVsNewAppIds);
        }
        // Add Custom Forms, Update System Forms
        if (componentsToMigrate.contains(ComponentsEnum.FORMS.getIndex())) {
            migrateForms(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, oldVsNewAppIds);
        }
    }

    public static Map<String, Object> migrateModules(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration) throws Exception {
        List<FacilioModule> oldCustomModules = sourceModuleBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true, null, null);
        LOGGER.info("####MetaMigration - Started adding custom modules");

        Map<String, Object> result = null;
        List<FacilioModule> newCustomModules = new ArrayList<>();
        Map<Long, Long> oldVsNewCustomModuleIds = new HashMap<>();
        if (CollectionUtils.isNotEmpty(oldCustomModules)) {
            result = targetMetaMigration.createModules(oldCustomModules);
            newCustomModules = (List<FacilioModule>) result.get(MetaMigrationConstants.MODULES);
            oldVsNewCustomModuleIds = (Map<Long, Long>) result.get(MetaMigrationConstants.OLD_VS_NEW_IDS);
        }

        LOGGER.info("####MetaMigration - Completed adding custom modules");

        return result;
    }

    public static void migrateFields(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration,
                                     MetaMigrationBean targetMetaMigration, Map<Long, Long> oldVsNewModuleIds) throws Exception {

        List<FacilioModule> oldSystemModules = sourceModuleBean.getModuleList(getModuleFetchCriteria(false));
        List<FacilioModule> newSystemModules = targetModuleBean.getModuleList(getModuleFetchCriteria(false));
        List<FacilioModule> oldCustomModules = sourceModuleBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true, null, null);
        List<FacilioModule> newCustomModules = targetModuleBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true, null, null);

        LOGGER.info("####MetaMigration - Started adding custom fields in system modules");

        // Add Custom Fields - System Modules
        createCustomFields(sourceModuleBean, targetModuleBean, targetMetaMigration, oldSystemModules, newSystemModules, oldVsNewModuleIds);

        LOGGER.info("####MetaMigration - Completed adding custom fields in system modules");
        LOGGER.info("####MetaMigration - Started adding custom fields in custom modules");

        // Add Custom Fields - Custom Modules
        createCustomFields(sourceModuleBean, targetModuleBean, targetMetaMigration, oldCustomModules, newCustomModules, oldVsNewModuleIds);

        LOGGER.info("####MetaMigration - Completed adding custom fields in custom modules");
    }

    public static void migrateForms(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration,
                                    MetaMigrationBean targetMetaMigration, Map<Long, Long> oldVsNewAppIds) throws Exception {
        List<Long> moduleIdsWithForms = sourceMetaMigration.getModuleIdsWithForms();
        Map<String, Map<Long, Long>> moduleNameVsOldVsNewFormIds = new HashMap<>();
        if (CollectionUtils.isNotEmpty(moduleIdsWithForms)) {
            moduleNameVsOldVsNewFormIds = addOrUpdateForms(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, oldVsNewAppIds, moduleIdsWithForms);
        }

        Map<Long, Long> oldVsNewFormIds = new HashMap<>();
        for (String moduleName : moduleNameVsOldVsNewFormIds.keySet()) {
            oldVsNewFormIds.putAll(moduleNameVsOldVsNewFormIds.get(moduleName));
        }
    }

    public static void migrateTabsAndLayouts(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration,
                                             MetaMigrationBean targetMetaMigration, long sourceOrgId, long targetOrgId, Map<Long, Long> oldVsNewAppIds) throws Exception {
        // oldVsNewTabLayoutIds
        Map<Long, Long> oldVsNewLayoutIds = new HashMap<>();
        for (long oldOrgAppId : oldVsNewAppIds.keySet()) {
            long newOrgAppId = oldVsNewAppIds.get(oldOrgAppId);
            List<ApplicationLayoutContext> oldOrgLayouts = sourceMetaMigration.getLayoutsForAppId(oldOrgAppId);
            List<ApplicationLayoutContext> newOrgLayouts = targetMetaMigration.getLayoutsForAppId(newOrgAppId);
            Map<Integer, ApplicationLayoutContext> newOrgDeviceTypeVsLayout = newOrgLayouts.stream()
                    .collect(Collectors.toMap(ApplicationLayoutContext::getLayoutDeviceType, Function.identity()));

            for (ApplicationLayoutContext oldAppLayout : oldOrgLayouts) {
                if (newOrgDeviceTypeVsLayout.containsKey(oldAppLayout.getLayoutDeviceType())) {
                    ApplicationLayoutContext newAppLayout = newOrgDeviceTypeVsLayout.get(oldAppLayout.getLayoutDeviceType());
                    oldVsNewLayoutIds.put(oldAppLayout.getId(), newAppLayout.getId());
                }
            }
        }

        handleTabsAndLayouts(sourceOrgId, targetOrgId, sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, oldVsNewAppIds, oldVsNewLayoutIds);
    }
    public static Criteria getModuleFetchCriteria(boolean fetchCustom) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_CUSTOM", "custom", String.valueOf(fetchCustom), BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULE_TYPE", "type", StringUtils.join(IGNORE_MODULE_TYPES, ","), NumberOperators.NOT_EQUALS));
        return criteria;
    }

    public static Map<String, Map<Long, Long>> createCustomFields(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean targetMetaMigration,
                                                                  List<FacilioModule> oldModules, List<FacilioModule> newModules,
                                                                  Map<Long, Long> oldVsNewModuleIds) throws Exception {

        Map<Long, FacilioModule> newModuleIdsVsModule = newModules.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity()));
        Map<String, Map<Long, Long>> moduleNameVsOldVsNewIds = new HashMap<>();

        for (FacilioModule oldModule : oldModules) {
            FacilioModule newModule = newModuleIdsVsModule.get(oldVsNewModuleIds.get(oldModule.getModuleId()));
            if (newModule == null) {
                LOGGER.info("####MetaMigration - Module not migrated - ModuleName - " + oldModule.getName() + " ModuleId - " + oldModule.getModuleId());
                continue;
            }

            Map<Long, Long> oldVsNewIds = new HashMap<>();
            JSONArray customFieldsJson = new JSONArray();

            List<FacilioField> oldCustomFields = sourceModuleBean.getAllCustomFields(oldModule.getName());

            if (CollectionUtils.isEmpty(oldCustomFields)) {
                LOGGER.info("####MetaMigration - No Custom Modules found - " + oldModule.getName());
                continue;
            } else {
                oldCustomFields = checkFieldPresence(targetModuleBean, oldModule.getName(), oldCustomFields);
                oldCustomFields.removeIf(facilioField -> facilioField.getModuleId() != oldModule.getModuleId());
            }

            if (CollectionUtils.isEmpty(oldCustomFields)) {
                LOGGER.info("####MetaMigration - No New Custom Modules to be added - " + oldModule.getName());
                continue;
            }

            for (FacilioField oldField : oldCustomFields) {
                JSONObject newFieldJson = MetaMigrationUtil.createNewField(sourceModuleBean, targetModuleBean, oldField);
                customFieldsJson.add(newFieldJson);
            }
            List<FacilioField> newCustomFields = FieldUtil.parseFieldJson(customFieldsJson);

            newCustomFields = targetMetaMigration.createFields(newModule, newCustomFields);

            Map<String, Long> newFieldNameVsFieldId = new HashMap<>();
            if (CollectionUtils.isNotEmpty(newCustomFields)) {
                newFieldNameVsFieldId = newCustomFields.stream().collect(Collectors.toMap(FacilioField::getName, FacilioField::getFieldId));
            }

            for (FacilioField oldField : oldCustomFields) {
                if (newFieldNameVsFieldId.containsKey(oldField.getName())) {
                    oldVsNewIds.put(newFieldNameVsFieldId.get(oldField.getName()), oldField.getFieldId());
                } else {
                    LOGGER.info("####MetaMigration - Custom Field not migrated - ModuleName - " + oldModule.getName() + " FieldName - " + oldField.getName());
                }
            }
            moduleNameVsOldVsNewIds.put(oldModule.getName(), oldVsNewIds);
        }
        return moduleNameVsOldVsNewIds;
    }

    private static List<FacilioField> checkFieldPresence(ModuleBean targetModuleBean, String moduleName, List<FacilioField> oldCustomFields) throws Exception {
        List<FacilioField> resultFields = new ArrayList<>();

        for (FacilioField field : oldCustomFields) {
            FacilioField dbField = targetModuleBean.getField(field.getName(), moduleName);
            if (dbField != null) {
                LOGGER.info("####MetaMigration - Field with the name Decommission already exists - ModuleName - " + moduleName + " FieldName - " + field.getName());
            } else {
                resultFields.add(field);
            }
        }

        return resultFields;
    }

    public static Map<String, Map<Long, Long>> addOrUpdateForms(ModuleBean sourceModuleBean, ModuleBean targetModuleBean,
                                                                MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration,
                                                                Map<Long, Long> oldVsNewAppIds, List<Long> moduleIds) throws Exception{
        LOGGER.info("####MetaMigration - Started adding forms");
        Map<String, Map<Long, Long>> resultOldVsNewIds = new HashMap<>();

        List<FacilioForm> oldSystemForms = new ArrayList<>();
        List<FacilioForm> oldCustomForms = new ArrayList<>();

        for (Long moduleId : moduleIds) {
            FacilioModule oldOrgModule = sourceModuleBean.getModule(moduleId);
            FacilioModule newOrgModule = targetModuleBean.getModule(oldOrgModule.getName());
            String moduleName = oldOrgModule.getName();

            LOGGER.info("####MetaMigration - Started adding forms for module - " + moduleName);

            Map<Long, Long> oldVsNewFieldIdsMap = new HashMap<>();
            List<FacilioField> oldOrgModuleFields = sourceModuleBean.getAllFields(moduleName);
            List<FacilioField> newOrgModuleFields = targetModuleBean.getAllFields(moduleName);

            if (CollectionUtils.isNotEmpty(oldOrgModuleFields) && CollectionUtils.isNotEmpty(newOrgModuleFields)) {
                oldVsNewFieldIdsMap = MetaMigrationUtil.getOldVsNewFieldIdMap(moduleName, oldOrgModuleFields, newOrgModuleFields);
            }

            Criteria moduleIdCriteria = new Criteria();
            moduleIdCriteria.addAndCondition(CriteriaAPI.getModuleIdIdCondition(moduleId, ModuleFactory.getFormModule()));
            moduleIdCriteria.addAndCondition(CriteriaAPI.getCondition("HIDE_IN_LIST", "hideInList", Boolean.FALSE.toString(), BooleanOperators.IS));

            List<FacilioForm> dbFormsList = sourceMetaMigration.getForms(moduleIdCriteria);
            oldSystemForms = new ArrayList<>();
            oldCustomForms = new ArrayList<>();

            for (FacilioForm form : dbFormsList) {
                if (oldVsNewAppIds.containsKey(form.getAppId())) {
                    if (form.getIsSystemForm() != null && form.getIsSystemForm()) {
                        oldSystemForms.add(form);
                    } else {
                        oldCustomForms.add(form);
                    }
                }
            }

            Map<Long, Long> currModuleOldVsNewAddedIds = addForms(newOrgModule, oldCustomForms, oldVsNewFieldIdsMap, oldVsNewAppIds, sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration);
            LOGGER.info("####MetaMigration - CurrModuleOldVsNewAddedIds - ModuleName - " + newOrgModule.getName() + " " + currModuleOldVsNewAddedIds);

            Map<Long, Long> currModuleOldVsNewUpdatedIds = updateForms(newOrgModule, oldSystemForms, oldVsNewFieldIdsMap, oldVsNewAppIds, sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration);
            LOGGER.info("####MetaMigration - CurrModuleOldVsNewUpdatedIds - ModuleName - " + newOrgModule.getName() + " " + currModuleOldVsNewUpdatedIds);

            resultOldVsNewIds.put(moduleName, currModuleOldVsNewAddedIds);
            resultOldVsNewIds.get(moduleName).putAll(currModuleOldVsNewUpdatedIds);
            LOGGER.info("####MetaMigration - Completed adding forms for module - " + moduleName);
        }

        LOGGER.info("####MetaMigration - Completed adding forms");
        return resultOldVsNewIds;
    }

    public static Map<Long, Long> addForms(FacilioModule newOrgModule, List<FacilioForm> oldCustomForms, Map<Long, Long> oldVsNewFieldIdsMap, Map<Long, Long> oldVsNewAppIds,
                                           ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration ,MetaMigrationBean targetMetaMigration) throws Exception {

        LOGGER.info("####MetaMigration - Started adding custom forms for module - " + newOrgModule.getName());

        Map<Long, Long> oldVsNewAddedFormIds = new HashMap<>();

        for (FacilioForm form : oldCustomForms) {
            long oldFormId = form.getId();
            FacilioForm newDbForm = targetMetaMigration.getForm(form.getModule().getName(), form.getName());
            if (newDbForm != null) {
                updateForms(newOrgModule, Collections.singletonList(form), oldVsNewFieldIdsMap, oldVsNewAppIds, sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration);
                continue;
            }

            List<FormField> formFieldsFromSections = FormsAPI.getFormFieldsFromSections(form.getSections());
            MetaMigrationUtil.updateSubFormSection(sourceMetaMigration, targetMetaMigration, sourceModuleBean, targetModuleBean, form.getSections(), oldVsNewFieldIdsMap, oldVsNewAppIds);
            MetaMigrationUtil.updateFormFieldFieldIds(formFieldsFromSections, oldVsNewFieldIdsMap);
            if (oldVsNewAppIds.containsKey(form.getAppId())) { form.setAppId(oldVsNewAppIds.get(form.getAppId())); }
            form.setStateFlowId(-1);
            form.setId(-1);

            long newFormId = targetMetaMigration.createForm(form, newOrgModule);

            oldVsNewAddedFormIds.put(oldFormId, newFormId);
        }

        LOGGER.info("####MetaMigration - Completed adding custom forms for module - " + newOrgModule.getName());

        return oldVsNewAddedFormIds;
    }

    public static Map<Long, Long> updateForms(FacilioModule newOrgModule, List<FacilioForm> oldSystemForms, Map<Long, Long> oldVsNewFieldIdsMap, Map<Long, Long> oldVsNewAppIds, ModuleBean sourceModuleBean,
                                              ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration) throws Exception {
        if (CollectionUtils.isEmpty(oldSystemForms)) {
            return new HashMap<>();
        }

        LOGGER.info("####MetaMigration - Started updating system forms for module - " + newOrgModule.getName());

        Map<String, FacilioForm> oldSystemFormsMap = oldSystemForms.stream().collect(Collectors.toMap(FacilioForm::getName, Function.identity()));

        Criteria formNamesCriteria = new Criteria();
        formNamesCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(oldSystemFormsMap.keySet(), ","), StringOperators.IS));
        formNamesCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(newOrgModule.getModuleId()), NumberOperators.EQUALS));

        List<FacilioForm> newSystemForms = targetMetaMigration.getForms(formNamesCriteria);

        Map<String, FacilioForm> newSystemFormsMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(newSystemForms)) {
            newSystemFormsMap = newSystemForms.stream().collect(Collectors.toMap(FacilioForm::getName, Function.identity()));
        }

        Map<Long, Long> oldVsNewUpdatedFormIds = new HashMap<>();

        for (FacilioForm oldOrgForm : oldSystemForms) {
            String formName = oldOrgForm.getName();
            if (newSystemFormsMap.containsKey(formName)) {
                FacilioForm newOrgForm = newSystemFormsMap.get(formName);
                List<FormField> dbFields = FormsAPI.getFormFieldsFromSections(newOrgForm.getSections());
                List<Long> dbFieldIds = dbFields.stream().map(FormField::getId).collect(Collectors.toList());
                List<Long> dbSectionIds =  newOrgForm.getSections().stream().map(FormSection::getId).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(dbSectionIds)) targetMetaMigration.deleteFormSections(dbSectionIds);
                if (CollectionUtils.isNotEmpty(dbFields)) targetMetaMigration.deleteFormFields(dbFieldIds);

                List<FormSection> oldOrgFormSections = oldOrgForm.getSections();
                List<FormField> formFieldsFromSections = FormsAPI.getFormFieldsFromSections(oldOrgFormSections);
                MetaMigrationUtil.updateSubFormSection(sourceMetaMigration, targetMetaMigration, sourceModuleBean, targetModuleBean, oldOrgForm.getSections(), oldVsNewFieldIdsMap, oldVsNewAppIds);
                MetaMigrationUtil.updateFormFieldFieldIds(formFieldsFromSections, oldVsNewFieldIdsMap);
                newOrgForm.setSections(oldOrgFormSections);

                targetMetaMigration.addFormFields(newOrgForm.getId(), newOrgForm);
                oldVsNewUpdatedFormIds.put(oldOrgForm.getId(), newOrgForm.getId());
            } else {
                LOGGER.info("####MetaMigration - Form not found in ModuleName - " + newOrgModule.getName() + " FormName - " + oldOrgForm.getName());
            }
        }

        LOGGER.info("####MetaMigration - Completed updating system forms for module - " + newOrgModule.getName());

        return oldVsNewUpdatedFormIds;
    }

    public static void handleTabsAndLayouts(long sourceOrgId, long targetOrgId, ModuleBean sourceModuleBean, ModuleBean targetModuleBean,
                                            MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration,
                                            Map<Long, Long> oldVsNewAppId, Map<Long, Long> oldVsNewLayoutIds) throws Exception {

        WebTabBean sourceTabBean = (WebTabBean) BeanFactory.lookup("TabBean", sourceOrgId);
        WebTabBean targetTabBean = (WebTabBean) BeanFactory.lookup("TabBean", targetOrgId);

        LOGGER.info("####MetaMigration - Started adding tabs and layouts in orgId - " + targetOrgId);
        // add new tabs and groups
        for (long oldOrgAppId : oldVsNewAppId.keySet()) {
            long newOrgAppId = oldVsNewAppId.get(oldOrgAppId);
            List<ApplicationLayoutContext> oldOrgLayouts = sourceMetaMigration.getLayoutsForAppId(oldOrgAppId);
            if (CollectionUtils.isNotEmpty(oldOrgLayouts)) {
                for (ApplicationLayoutContext oldOrgLayout : oldOrgLayouts) {
                    long oldLayoutId = oldOrgLayout.getId();
                    if (!oldVsNewLayoutIds.containsKey(oldLayoutId)) {
                        LOGGER.info("####MetaMigration - Layout not present in new org - Old AppId - " + oldOrgAppId + " Old layoutId - " + oldLayoutId);
                        continue;
                    }
                    long newLayoutId = oldVsNewLayoutIds.get(oldLayoutId);

                    List<WebTabGroupContext> oldOrgWebTabGroups = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(sourceTabBean.getWebTabGroupForLayoutID(oldOrgLayout), WebTabGroupCacheContext.class), WebTabGroupContext.class);
                    if (CollectionUtils.isNotEmpty(oldOrgWebTabGroups)) {
                        for (WebTabGroupContext webTabGroup : oldOrgWebTabGroups) {
                            List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(sourceTabBean.getWebTabsForWebGroup(webTabGroup.getId()), WebTabCacheContext.class), WebTabContext.class);
                            if (CollectionUtils.isNotEmpty(webTabs)) {
                                for (WebTabContext webTab : webTabs) {
                                    List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(sourceTabBean.getTabIdModules(webTab.getId()), TabIdAppIdMappingCacheContext.class), TabIdAppIdMappingContext.class);
                                    List<Long> moduleIds = new ArrayList<>();
                                    List<String> specialTypes = new ArrayList<>();
                                    if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
                                        for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                                            if (tabIdAppIdMappingContext.getModuleId() > 0) {
                                                moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                                            }
                                            if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                                                specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                                            }
                                        }
                                    }
                                    webTab.setModuleIds(moduleIds);
                                    webTab.setSpecialTypeModules(specialTypes);
                                }
                            }
                            webTabGroup.setWebTabs(webTabs);
                        }
                        oldOrgLayout.setWebTabGroupList(oldOrgWebTabGroups);

                        constructNewWebTabGroups(sourceModuleBean, targetModuleBean, sourceMetaMigration, targetMetaMigration, newOrgAppId, newLayoutId, oldOrgWebTabGroups);
                    }
                }
            }
        }
        LOGGER.info("####MetaMigration - Completed adding tabs and layouts in orgId - " + targetOrgId);
    }

    public static void constructNewWebTabGroups(ModuleBean sourceModuleBean, ModuleBean targetModuleBean, MetaMigrationBean sourceMetaMigration, MetaMigrationBean targetMetaMigration,
                                                long newOrgAppId, long newLayoutId, List<WebTabGroupContext> oldWebTabGroups) throws Exception {
        Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
        List<WebTabGroupContext> newWebTabGroups = new ArrayList<>();

        for (WebTabGroupContext oldWebTabGroup : oldWebTabGroups) {
            WebTabGroupContext newWebTabGroup = new WebTabGroupContext(oldWebTabGroup);
            newWebTabGroup.setLayoutId(newLayoutId);
            newWebTabGroup.setWebTabs(null);
            if (newWebTabGroup.getIconType()==-1 && newWebTabGroup.getIconTypeEnum()==null) {newWebTabGroup.setIconType(0);}

            WebTabGroupContext dbWebTabGroup = targetMetaMigration.getWebTabGroupForRouteAndLayout(newLayoutId, newWebTabGroup.getRoute());

            if (dbWebTabGroup != null) {
                newWebTabGroup.setId(dbWebTabGroup.getId());
            } else {
                newWebTabGroup.setId(-1);
            }

            List<WebTabContext> currGroupWebTabs = new ArrayList<>();
            for (WebTabContext oldWebTab : oldWebTabGroup.getWebTabs()) {
                WebTabContext newWebTab = new WebTabContext(oldWebTab);
                newWebTab.setApplicationId(newOrgAppId);

                if (CollectionUtils.isNotEmpty(oldWebTab.getModuleIds())) {
                    newWebTab.setModuleIds(new ArrayList<>());
                    newWebTab.setModules(new ArrayList<>());
                    for (long oldModuleId : oldWebTab.getModuleIds()) {
                        FacilioModule oldModule = sourceModuleBean.getModule(oldModuleId);
                        FacilioModule newModule = targetModuleBean.getModule(oldModule.getName());
                        if (newModule != null) {
                            newWebTab.getModuleIds().add(newModule.getModuleId());
                            newWebTab.getModules().add(newModule);
                        } else {
                            LOGGER.info("####MetaMigration - Module not present in new org - " + oldModule.getName());
                        }
                    }
                }

                WebTabContext dbWebTabContext = targetMetaMigration.getWebTabForAppAndRoute(newOrgAppId, oldWebTab.getRoute());

                if (dbWebTabContext != null) {
                    newWebTab.setId(dbWebTabContext.getId());
                } else {
                    newWebTab.setId(-1);
                }

                if (newWebTab != null) {
                    currGroupWebTabs.add(newWebTab);
                } else {
                    LOGGER.info("####MetaMigration - WebTab is null" + oldWebTab.getRoute() + " " + oldWebTab.getApplicationId());
                }
            }
            groupNameVsWebTabsMap.put(newWebTabGroup.getRoute(), currGroupWebTabs);
            newWebTabGroups.add(newWebTabGroup);
        }

        if (CollectionUtils.isNotEmpty(newWebTabGroups) && MapUtils.isNotEmpty(groupNameVsWebTabsMap)) {
            targetMetaMigration.addOrUpdateWebTabGroups(newWebTabGroups, groupNameVsWebTabsMap);
        }
    }
}
