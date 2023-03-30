package com.facilio.agentv2.migration;

import com.facilio.agentv2.migration.beans.AgentMigrationBean;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ReadingMetaMigration extends FacilioCommand {

    public boolean addModule(long sourceOrgId, long targetOrgId ) throws Exception {

        AgentMigrationBean sourceBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, sourceOrgId);
        AgentMigrationBean targetBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, targetOrgId);

        ModuleCRUDBean sourceModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", sourceOrgId);
        ModuleCRUDBean targetModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", targetOrgId);

        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        List<AssetCategoryContext> sourceCategories = sourceModCrudBean.getCategoryList();
        List<AssetCategoryContext> targetCategories = targetModCrudBean.getCategoryList();

        Map<String, AssetCategoryContext> categoryMap = targetCategories.stream().collect(Collectors.toMap(AssetCategoryContext::getName, Function.identity()));

        for(AssetCategoryContext category: sourceCategories) {

            AssetCategoryContext newCategory = categoryMap.get(category.getName());
            if (newCategory == null) {
                LOGGER.error("Category not available in new org for "+ category.getDisplayName());
                continue;
            }

            List<FacilioModule> readingModules = sourceBean.fetchModuleReadings(category.getId());
            List<FacilioModule> modulesToAdd = new ArrayList<>();
            for(FacilioModule module: readingModules) {
                if (!module.getTableName().equals("Readings_3")) { // is custom is false for custom reading modules
                    continue;
                }
                List<FacilioField> fields = module.getFields();
                FacilioModule newModule = new FacilioModule(module);
                fields.addAll(FieldFactory.getDefaultReadingFields(newModule));
                newModule.setFields(fields);
                modulesToAdd.add(newModule);
            }
            FacilioModule assetModule = targetModuleBean.getModule(newCategory.getAssetModuleID());
            if (!modulesToAdd.isEmpty()) {
                targetBean.addReadingModule(modulesToAdd, assetModule);
                LOGGER.info("Migrated " + modulesToAdd.size() + " modules for the category " + newCategory.getDisplayName());
            }
        }

        return false;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.get(AgentMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(AgentMigrationConstants.TARGET_ORG_ID);
        boolean copyModule = (boolean) context.get("copyModule");

        if (copyModule) {
            return addModule(sourceOrgId, targetOrgId);
        }

        AgentMigrationBean sourceBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, sourceOrgId);
        AgentMigrationBean targetBean = (AgentMigrationBean) BeanFactory.lookup("AgentMigrationBean", true, targetOrgId);

        ModuleCRUDBean sourceModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", sourceOrgId);
        ModuleCRUDBean targetModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", targetOrgId);

        ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);

        Map<Long, List<FacilioField>> sourceCategoryReadings = sourceBean.fetchAllReadings();
        Map<Long, List<FacilioField>> targetCategoryReadings = targetBean.fetchAllReadings();

        List<FacilioField> fieldsToUpdate = new ArrayList<>();
        Map<Long, FacilioModule> assetModuleMap = new HashMap<>();

        List<AssetCategoryContext> sourceCategories = sourceModCrudBean.getCategoryList();
        List<AssetCategoryContext> targetCategories = targetModCrudBean.getCategoryList();

        Map<Long, Long> targetVsSourceCategories = new HashMap<>();
        Map<Long, AssetCategoryContext> categoryMap = new HashMap<>();
        Map<String, Long> nameVsId = sourceCategories.stream().collect(Collectors.toMap(AssetCategoryContext::getName, ModuleBaseWithCustomFields::getId, (p, c) -> c));
        for(AssetCategoryContext category: targetCategories) {
            targetVsSourceCategories.put(category.getId(), nameVsId.get(category.getName()));
            categoryMap.put(category.getId(), category);
        }


        for (Map.Entry<Long, List<FacilioField>> entry : targetCategoryReadings.entrySet()) {
            long targetCategoryId = entry.getKey();
            AssetCategoryContext category = categoryMap.get(targetCategoryId);
            List<FacilioField> targetReadings = entry.getValue();

            Map<String, FacilioModule> modules = new HashMap<>();
            Map<String, FacilioModule> targetModules = targetBean.fetchModuleReadings(targetCategoryId)
                                                            .stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity()));

            if (category == null) { // controller type
                continue;
            }

            LOGGER.info("Reading name migration starting for category " + category.getDisplayName());

            Long sourceCategoryId = targetVsSourceCategories.get(targetCategoryId);

            if (sourceCategoryId == null) {
                LOGGER.error("No category available in source org for " + category.getDisplayName());
                continue;
            }

            List<FacilioField> sourceReadings = sourceCategoryReadings.get(sourceCategoryId);
            Map<String, FacilioField> sourceMap = sourceReadings.stream().collect(Collectors.toMap(f -> f.getDisplayName().toLowerCase(), Function.identity(), (o, n) -> n));

            for (FacilioField field : targetReadings) {
                FacilioField sourceField = sourceMap.get(field.getDisplayName().toLowerCase());
                if (sourceField == null) {
                    LOGGER.error("No source field available for " + field.getDisplayName() + " under " + category.getDisplayName());
                    continue;
                }

                String sourceModuleName = sourceField.getModule().getName();
                String targetModuleName = field.getModule().getName();

                FacilioField fieldToUpdate = null;

                if (!sourceModuleName.equals(targetModuleName)) {

                    if (field.isDefault()) {
                        LOGGER.error("Module name is not same for the default field" + field.getFieldId() + " and " +sourceField.getFieldId());
                        continue;
                    }

                    FacilioModule module = modules.get(sourceModuleName);
                    if (module == null) {
                        module = targetModules.get(sourceModuleName);
                        if (module == null) {
                            FacilioModule assetModule = assetModuleMap.get(category.getAssetModuleID());
                            if (assetModule == null) {
                                assetModule = targetModuleBean.getModule(category.getAssetModuleID());
                                assetModuleMap.put(category.getAssetModuleID(), assetModule);
                            }
                            module = targetBean.addReadingModule(sourceField.getModule(), assetModule);
                        }
                        modules.put(module.getName(), module);
                    }

                    fieldToUpdate = new FacilioField();
                    fieldToUpdate.setModule(module);
                    fieldToUpdate.setFieldId(field.getFieldId());
                    fieldToUpdate.setName(field.getName());
                    fieldToUpdate.setColumnName(sourceField.getColumnName());
                    fieldsToUpdate.add(fieldToUpdate);

                    LOGGER.info(MessageFormat.format("Changing module of field from {0} to {1} for {2}", targetModuleName, module.getName(), field.getId()));
                }

                if (!sourceField.getName().equals(field.getName()) || !sourceField.getColumnName().equals(field.getColumnName()) ) {

                    if (field.isDefault()) {
                        LOGGER.error("Field name is not same for the default field " + sourceField.getFieldId() + " and " +field.getFieldId());
                        continue;
                    }

                    if (fieldToUpdate == null) {
                        fieldToUpdate = new FacilioField();
                        fieldToUpdate.setFieldId(field.getFieldId());
                        fieldToUpdate.setModule(field.getModule());
                        fieldToUpdate.setColumnName(sourceField.getColumnName());
                        fieldsToUpdate.add(fieldToUpdate);
                    }
                    fieldToUpdate.setName(sourceField.getName());
                    LOGGER.info(MessageFormat.format("Changing field name from {0} to {1} for {2}", field.getName(), sourceField.getName(), field.getId()));
                }

            }
        }

        if(!fieldsToUpdate.isEmpty()) {
            targetBean.updateFields(fieldsToUpdate);
            LOGGER.info("Reading details changed for " + fieldsToUpdate.size() + " fields");
        }


        return false;
    }

}
