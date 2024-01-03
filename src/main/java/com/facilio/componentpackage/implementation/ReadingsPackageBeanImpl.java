package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j
public class ReadingsPackageBeanImpl implements PackageBean<ReadingsPackageBeanImpl.AssetReading> {


    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getModuleIdVsFieldId(ModuleFactory.getAssetCategoryReadingRelModule(), Boolean.TRUE);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getModuleIdVsFieldId(ModuleFactory.getAssetCategoryReadingRelModule(), Boolean.FALSE);
    }

    @Override
    public Map<Long, AssetReading> fetchComponents(List<Long> ids) throws Exception {
        return getAssetReading(ids);
    }

    @Override
    public void convertToXMLComponent(AssetReading readingComponent, XMLBuilder readingsBuilder) throws Exception {
        XMLBuilder assetReadingBuilder = readingsBuilder.e("AssetCategoryReading");
        assetReadingBuilder.e("assetCategoryName").text(readingComponent.getAssetCategoryName());
        assetReadingBuilder.e("readingModuleName").text(readingComponent.getModuleName());
        assetReadingBuilder.e("tableName").text(readingComponent.getReadingField().getTableName());
        assetReadingBuilder.e("displayName").text(readingComponent.getReadingField().getModule().getDisplayName());
        PackageBeanUtil.convertFacilioFieldToXML(readingComponent.getReadingField(), readingsBuilder.e("ReadingField"));
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
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsFieldId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder fieldElement = idVsData.getValue();
            String moduleName = fieldElement.getElement("ReadingField").getElement("moduleName").getText();
            String fieldName = fieldElement.getElement("ReadingField").getElement("name").getText();

            FacilioModule module = moduleBean.getModule(moduleName);
            if (module == null) {
                LOGGER.error("###Sandbox - Module not found - " + moduleName);
                continue;
            }

            FacilioField field = PackageBeanUtil.getFieldFromDB(module, fieldName);
            if (field != null) {
                uniqueIdentifierVsFieldId.put(uniqueIdentifier, field.getFieldId());
            } else {
                LOGGER.error("###Sandbox - Field not found - ModuleName - " + moduleName + " FieldName - " + fieldName);
            }
        }
        return uniqueIdentifierVsFieldId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String, Map<String, List<FacilioField>>> moduleNameVsFields = new HashMap<>();
        Map<String, Map<String, String>> moduleNameVsFieldNameVsUniqueIdentifier = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder fieldElement = idVsData.getValue();

            AssetReading assetReading = constructReadingFields(fieldElement);
            FacilioField facilioField = assetReading.getReadingField();
            String assetCategoryName = assetReading.getAssetCategoryName();
            String moduleName = assetReading.getModuleName();
            String tableName=assetReading.getTableName();
            String moduleDisplayName=assetReading.getModuleDisplayName();
            
            String moduleFieldsKey=getModuleFieldsKey(moduleName,tableName,moduleDisplayName);

            if (StringUtils.isNotEmpty(assetCategoryName) && !moduleNameVsFields.containsKey(assetCategoryName)) {
                moduleNameVsFields.put(assetCategoryName, new HashMap<>());
            }
            Map<String, List<FacilioField>> moduleNdFields = moduleNameVsFields.get(assetCategoryName);
            if (StringUtils.isNotEmpty(moduleFieldsKey) && !moduleNdFields.containsKey(moduleFieldsKey)) {
                moduleNdFields.put(moduleFieldsKey, new ArrayList<>());
            }
            moduleNdFields.get(moduleFieldsKey).add(assetReading.getReadingField());


            if (!moduleNameVsFieldNameVsUniqueIdentifier.containsKey(moduleFieldsKey)) {
                moduleNameVsFieldNameVsUniqueIdentifier.put(moduleFieldsKey, new HashMap<>());
            }
            moduleNameVsFieldNameVsUniqueIdentifier.get(moduleFieldsKey).put(facilioField.getName(), idVsData.getKey());
        }

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        for (Map.Entry<String, Map<String, List<FacilioField>>> moduleFields : moduleNameVsFields.entrySet()) {
            String assetCategoryName = moduleFields.getKey();
            Map<String, List<FacilioField>> fieldsList = moduleFields.getValue();

            uniqueIdentifierVsComponentId.putAll(groupAndModuleFields(moduleNameVsFieldNameVsUniqueIdentifier, assetCategoryName, fieldsList));
        }

        return uniqueIdentifierVsComponentId;
    }

    private String getModuleFieldsKey(String moduleName, String tableName, String displayName) {
        String moduleFieldKey = "moduleName : " + moduleName + " , tableName : " + tableName + " , displayName : " + displayName;
        return moduleFieldKey;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        for (Map.Entry<Long, XMLBuilder> uniqueIdentifierVsComponent : idVsXMLComponents.entrySet()) {
            Long fieldId = uniqueIdentifierVsComponent.getKey();
            AssetReading assetReading = constructReadingFields(uniqueIdentifierVsComponent.getValue());
            FacilioField readingField = assetReading.getReadingField();
            readingField.setFieldId(fieldId);

            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.MODULE_FIELD, readingField);
            FacilioChain c = FacilioChainFactory.getUpdateReadingChain();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private AssetReading constructReadingFields(XMLBuilder fieldsBuilder) throws Exception {
        XMLBuilder assetReading = fieldsBuilder.getElement("AssetCategoryReading");
        String assetCategoryName = assetReading.getElement("assetCategoryName").getText();
        String moduleName = assetReading.getElement("readingModuleName").getText();
        String tableName = assetReading.getElement("tableName").getText();
        String moduleDisplayName=assetReading.getElement("displayName").getText();

        FacilioField fields = PackageBeanUtil.getFieldFromXMLComponent(fieldsBuilder.getElement("ReadingField"));
        AssetReading assetReadingCtx = new AssetReading(moduleName, assetCategoryName, fields,tableName,moduleDisplayName);

        return assetReadingCtx;
    }

    private Map<Long, List<FacilioField>> getReadingFieldsVsCategory(FacilioModule module) throws Exception {
        Map<Long, Long> assetCategoryMap = PackageBeanUtil.getAssetCategoryIdVsModuleId(null);
        Map<Long, List<FacilioField>> moduleNdCategoryId = new HashMap<>();

        for (Long assetCategoryId : assetCategoryMap.keySet()) {

            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, Boolean.FALSE);
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, assetCategoryId);

            FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
            getCategoryReadingChain.execute(context);

            List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
            if (CollectionUtils.isNotEmpty(readingModules)) {
                List<FacilioField> fields = readingModules.stream().map(m -> ignoreDeltaFields(m.getFields())).flatMap(field->field.stream()).collect(Collectors.toList());
                moduleNdCategoryId.put(assetCategoryId, fields);
            }
        }

        PackageUtil.addAssetCategoryIdVsReadingFields(FacilioConstants.ContextNames.ASSET_CATEGORY, moduleNdCategoryId);

        return moduleNdCategoryId;
    }

    private static List<FacilioField> ignoreDeltaFields(List<FacilioField> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            List<FacilioField> filteredFields = fields.stream().filter(field -> BooleanUtils.isNotTrue(field.getRequired()) && !field.getName().endsWith("Delta")).collect(Collectors.toList());
            return filteredFields;
        }
        return new ArrayList<>();
    }

    private Map<Long, Long> getModuleIdVsFieldId(FacilioModule module, boolean isDefault) throws Exception {
        Map<Long, Long> fieldIdVsModuleId = new HashMap<>();
        Map<Long, List<FacilioField>> categoryIdVsModule = getReadingFieldsVsCategory(module);
        if (MapUtils.isNotEmpty(categoryIdVsModule)) {
            for (Map.Entry<Long, List<FacilioField>> moduleMap : categoryIdVsModule.entrySet()) {
                fieldIdVsModuleId.putAll(moduleMap.getValue().stream().filter(m -> m.getDefault() == isDefault).collect(Collectors.toMap(m -> m.getFieldId(), m -> m.getModuleId())));
            }
        }
        return fieldIdVsModuleId;
    }

    private Map<Long, AssetReading> getAssetReading(List<Long> ids) throws Exception {
        Map<Long, List<FacilioField>> categoryMap = PackageUtil.getAssetCategoryIdVsReadingFields(FacilioConstants.ContextNames.ASSET_CATEGORY);
        Map<Long, AssetReading> assetReadings = new HashMap<>();
        if (MapUtils.isNotEmpty(categoryMap)) {
            for (Map.Entry<Long, List<FacilioField>> prop : categoryMap.entrySet()) {
                V3AssetCategoryContext assetCategoryContext = AssetsAPI.getAssetCategories(Collections.singletonList(prop.getKey())).get(0);
                assetReadings.putAll(prop.getValue().stream().filter(field -> ids.contains(field.getFieldId())).map(field -> new AssetReading(field.getModule().getName(), assetCategoryContext.getDisplayName(), field, field.getTableName(),field.getModule().getDisplayName())).collect(Collectors.toMap(m -> m.getReadingField().getFieldId(), m -> m)));
            }
        }
        return assetReadings;
    }

    private static Map<String, Long> groupAndModuleFields(Map<String, Map<String, String>> moduleVsFieldVsUniqueId, String assetCategoryName, Map<String, List<FacilioField>> fieldsList) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, Long> assetNameVsId = PackageBeanUtil.getAssetCategoryNameVsId(null);
        for (Map.Entry<String, List<FacilioField>> m : fieldsList.entrySet()) {
            String moduleFieldsKey = m.getKey();
            Map<String, String> deSerilializedModuleFieldKey=deSerilializeModuleFieldKey(moduleFieldsKey);

            List<FacilioField> assetReadings = createAssetReadings(assetNameVsId.get(assetCategoryName), deSerilializedModuleFieldKey.get("moduleName"), deSerilializedModuleFieldKey.get("tableName"),deSerilializedModuleFieldKey.get("displayName"), m.getValue());
            if (moduleVsFieldVsUniqueId.containsKey(m.getKey()) && CollectionUtils.isNotEmpty(assetReadings)) {
                Map<String, String> fieldNameVsUniqueIdentifier = moduleVsFieldVsUniqueId.get(moduleFieldsKey);
                for (FacilioField field : assetReadings) {
                    String fieldName = field.getName();
                    String uniqueIdentifier = fieldNameVsUniqueIdentifier.get(fieldName);

                    uniqueIdentifierVsComponentId.put(uniqueIdentifier, field.getFieldId());
                }
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    private static Map<String, String> deSerilializeModuleFieldKey(String moduleFieldsKey) {

        Map<String, String> keyMap = Pattern.compile("\\s*,\\s*")
                .splitAsStream(moduleFieldsKey.trim())
                .map(s -> s.split(":", 2))
                .collect(Collectors.toMap(a -> a[0].trim(), a -> a[1].trim()));

        return keyMap;
    }

    private static List<FacilioField> createAssetReadings(Long categoryId, String moduleName, String tableName,String displayName, List<FacilioField> fields) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
        context.put(FacilioConstants.ContextNames.READING_NAME, displayName);
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
        context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,tableName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.Module.SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK, Boolean.TRUE);

        FacilioChain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
        addReadingChain.execute(context);

        List<Long> newFields = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
        List<FacilioField> facilioFields = newFields.stream().map(m -> {
            try {
                return Constants.getModBean().getField(m);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return facilioFields;
    }


    @Getter
    @Setter
    class AssetReading {

        FacilioField readingField;
        Long assetCategoryId;
        String assetCategoryName;
        Long moduleId;
        String moduleName;

        String tableName;

        String moduleDisplayName;

        public AssetReading(String moduleName, String assetCategoryName, FacilioField readingField,String tableName,String moduleDisplayName) {
            this.moduleName = moduleName;
            this.readingField = readingField;
            this.assetCategoryName = assetCategoryName;
            this.tableName=tableName;
            this.moduleDisplayName=moduleDisplayName;
        }

        public AssetReading(Long moduleId, Long assetCategoryId, FacilioField readingField,String tableName) {
            this.moduleId = moduleId;
            this.assetCategoryId = assetCategoryId;
            this.readingField = readingField;
            this.tableName=tableName;
        }


        public AssetReading() {
        }

    }

}
