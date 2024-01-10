package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class AssetCategoryPackageBeanImpl implements PackageBean<V3AssetCategoryContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return PackageBeanUtil.getAssetCategoryIdVsModuleId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return PackageBeanUtil.getAssetCategoryIdVsModuleId(false);
    }

    @Override
    public Map<Long, V3AssetCategoryContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3AssetCategoryContext> assetCategories  = getAssetCategoryForIds(ids);
        Map<Long, V3AssetCategoryContext> assetCategoryIdVsAssetCategoryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetCategories)) {
            assetCategories.forEach(assetCategoryContext -> assetCategoryIdVsAssetCategoryMap.put(assetCategoryContext.getId(), assetCategoryContext));
            PackageBeanUtil.addPickListConfForXML(FacilioConstants.ContextNames.ASSET_CATEGORY, "name", assetCategories, V3AssetCategoryContext.class, false);
        }
        return assetCategoryIdVsAssetCategoryMap;
    }
    @Override
    public void convertToXMLComponent(V3AssetCategoryContext component, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        element.element(PackageConstants.AssetCategoryConstants.ASSET_CATEGORY_NAME).text(component.getName());
        element.element(PackageConstants.AssetCategoryConstants.DISPLAY_NAME).text(component.getDisplayName());
        element.element(PackageConstants.AssetCategoryConstants.ASSET_TYPE).text(String.valueOf(component.getType()));
        if(component.getDefault() != null && component.getDefault()){
            element.element(PackageConstants.AssetCategoryConstants.IS_DEFAULT).text(String.valueOf(true));
        }else {
            element.element(PackageConstants.AssetCategoryConstants.IS_DEFAULT).text(String.valueOf(false));
        }
        if(component.isDeleted()){
            element.element(PackageConstants.AssetCategoryConstants.IS_DELETED).text(String.valueOf(true));
        }else {
            element.element(PackageConstants.AssetCategoryConstants.IS_DELETED).text(String.valueOf(false));
        }
        List<FacilioField> parentAssetField = new ArrayList<>();
        parentAssetField.add(FieldFactory.getField("NAME", "NAME", FieldType.STRING ));
        GenericSelectRecordBuilder assetCategoriesbuilder = new GenericSelectRecordBuilder()
                .select(parentAssetField)
                .table(ModuleFactory.getAssetCategoryModule().getTableName())
                .andCondition((CriteriaAPI.getCondition("ID", "id", String.valueOf(component.getParentCategoryId()), NumberOperators.EQUALS)));
        Map<String, Object> record = assetCategoriesbuilder.fetchFirst();
        if(record != null && !record.isEmpty()) {
            String parentAssetName = (String) record.get("NAME");
            element.element(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME).text(parentAssetName);
        }
        if (component.getAssetModuleID()!=null) {
            FacilioModule module = moduleBean.getModule(component.getAssetModuleID());
            element.element(PackageConstants.AssetCategoryConstants.ASSET_MODULE_NAME).text(module.getName());
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
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetCategoryModule = moduleBean.getModule("assetcategory");
        Map<String, Long> uniqueIdentifierVsAssetCategoryId = new HashMap<>();
        SelectRecordsBuilder<V3AssetCategoryContext> assetCategoryBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
                .select(moduleBean.getAllFields(assetCategoryModule.getName()))
                .beanClass(V3AssetCategoryContext.class)
                .module(assetCategoryModule)
                .table(assetCategoryModule.getTableName());
        List<V3AssetCategoryContext> assetCategories = assetCategoryBuilder.get();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder assetCategoryElement = idVsData.getValue();
            V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
            boolean isDefault = Boolean.parseBoolean(assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.IS_DEFAULT).getText());
            if (isDefault) {
                for(V3AssetCategoryContext assetCategory : assetCategories) {
                    if (assetCategory.getName().equals(assetCategoryContext.getName())) {
                        uniqueIdentifierVsAssetCategoryId.put(uniqueIdentifier, assetCategory.getId());
                        break;
                    }
                }
            }
        }
        return uniqueIdentifierVsAssetCategoryId;
    }
    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder assetCategoryElement = idVsData.getValue();
            String uniqueIdentifier = idVsData.getKey();
            String assetModuleName = null;
            V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
            XMLBuilder assetModuleBuilder = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.ASSET_MODULE_NAME);
            if(assetModuleBuilder != null) {
                assetModuleName = assetModuleBuilder.getText();
            }
            long assetCategoryId = addAssetCategory(assetCategoryContext, assetModuleName);
            uniqueIdentifierVsComponentId.put(uniqueIdentifier, assetCategoryId);
        }
        return uniqueIdentifierVsComponentId;
    }
    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long assetCategoryId = idVsData.getKey();
            XMLBuilder assetCategoryElement = idVsData.getValue();
            V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
            assetCategoryContext.setId(assetCategoryId);
            updateAssetCategory(assetCategoryContext);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetcategory");
        SelectRecordsBuilder<V3AssetCategoryContext> assetCategoriesBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
                .select(moduleBean.getAllFields(module.getName()))
                .module(module)
                .beanClass(V3AssetCategoryContext.class)
                .table(module.getTableName());
        List<V3AssetCategoryContext> assetCategories = assetCategoriesBuilder.get();
        List<Long> deletedIds = new ArrayList<>();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long assetCategoryId = idVsData.getKey();
            XMLBuilder assetCategoryElement = idVsData.getValue();
            boolean isDeleted = Boolean.parseBoolean(assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.IS_DELETED).getText());
            if(isDeleted) {
                deletedIds.add(assetCategoryId);
            }
            V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
            assetCategories.add(assetCategoryContext);
            assetCategoryContext.setId(assetCategoryId);
            XMLBuilder parentCategory = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME);
            if (parentCategory != null) {
                String parentName = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME).getText();
                for (V3AssetCategoryContext assetCategory : assetCategories) {
                    if ((assetCategory.getName()).equals(parentName)) {
                        assetCategoryContext.setParentCategoryId(assetCategory.getId());
                        updateAssetCategory(assetCategoryContext);
                        break;
                    }
                }
            }
        }
        PackageBeanUtil.bulkDeleteV3Records(module.getName() , deletedIds);
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            JSONObject data = new JSONObject();
            data.put("assetcategory", id);
            V3Util.deleteRecords("assetcategory", data,null,null,false);
        }
    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addPickListConfForContext(FacilioConstants.ContextNames.ASSET_CATEGORY, "name", V3AssetCategoryContext.class);
    }

    public static V3AssetCategoryContext constructAssetCategoryFromBuilder(XMLBuilder assetCategoryElement) throws Exception {
        String name = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.ASSET_CATEGORY_NAME).getText();
        String displayName = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.DISPLAY_NAME).getText();
        int type = Integer.parseInt(assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.ASSET_TYPE).getText());

        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
        assetCategoryContext.setName(name);
        assetCategoryContext.setDisplayName(displayName);
        assetCategoryContext.setType(type);

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetcategory");
        assetCategoryContext.setModuleId(module.getModuleId());

        XMLBuilder assetModuleBuilder = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.ASSET_MODULE_NAME);
        ModuleBean assetModBean = Constants.getModBean();
        String assetModuleName = assetModuleBuilder.getText();
        FacilioModule assetModule = assetModBean.getModule(assetModuleName);
        if (assetModule != null) {
            assetCategoryContext.setAssetModuleID(assetModule.getModuleId());
        }
        return assetCategoryContext;

    }
    public List<V3AssetCategoryContext> getAssetCategoryForIds(List<Long> ids) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
        List<V3AssetCategoryContext> assetCategories = (List<V3AssetCategoryContext>) PackageBeanUtil.getModuleDataListsForIds(ids,assetCategoryModule, V3AssetCategoryContext.class, Boolean.TRUE);
        if(CollectionUtils.isNotEmpty(assetCategories)){
            for(V3AssetCategoryContext assetCategory : assetCategories){
                if(assetCategory.getData()!=null)
                    assetCategory.setDefault((Boolean) assetCategory.getData().get("isDefault"));
            }
        }
        return assetCategories;
    }

    private long addAssetCategory(V3AssetCategoryContext v3AssetCategoryContext, String assetModuleName) throws Exception {
        Map<String, Object> assetCategoryData = FieldUtil.getAsProperties(v3AssetCategoryContext);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetcategory");
        Map<String, List<Object>> queryParams = new HashMap<>();
        List<Object> assetModule = new ArrayList<>();
        if (assetModuleName != null){
            assetModule.add(assetModuleName);
            queryParams.put("assetModuleName", assetModule);
        }
        FacilioContext context = V3Util.createRecord(module, assetCategoryData, null, queryParams);
        Map<String, Object> recordMap =(Map<String, Object>)  context.get(Constants.RECORD_MAP);
        List<V3AssetCategoryContext> assetCategoryContexts = (List<V3AssetCategoryContext>) recordMap.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
        return assetCategoryContexts.get(0).getId();
    }
    public void updateAssetCategory(V3AssetCategoryContext v3AssetCategoryContext) throws Exception {
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.ASSET_CATEGORY, v3AssetCategoryContext.getId(), FieldUtil.getAsJSON(v3AssetCategoryContext), null, null, null, null, null,null,null, null,null);
    }

}
