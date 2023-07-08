package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AssetCategoryPackageBeanImpl implements PackageBean<V3AssetCategoryContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getAssetCategoryIdVsModuleId(true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getAssetCategoryIdVsModuleId(false);
    }

    @Override
    public Map<Long, V3AssetCategoryContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3AssetCategoryContext> assetCategories  = getAssetCategoryForIds(ids);
        Map<Long, V3AssetCategoryContext> assetCategoryIdVsAssetCategoryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetCategories)) {
            assetCategories.forEach(assetCategoryContext -> assetCategoryIdVsAssetCategoryMap.put(assetCategoryContext.getId(), assetCategoryContext));
        }
        return assetCategoryIdVsAssetCategoryMap;

        
    }
    @Override
    public void convertToXMLComponent(V3AssetCategoryContext component, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        element.element(PackageConstants.AssetCategoryConstants.ASSET_CATEGORY_NAME).text(component.getName());
        element.element(PackageConstants.AssetCategoryConstants.DISPLAY_NAME).text(component.getDisplayName());
        element.element(PackageConstants.AssetCategoryConstants.ASSET_TYPE).text(String.valueOf(component.getType()));
        if(CollectionUtils.isNotEmpty(Collections.singleton(component.getDefault()))){
            element.element(PackageConstants.AssetCategoryConstants.IS_DEFAULT).text(String.valueOf(component.getDefault()));
        }
        List<FacilioField> parentAssetField = new ArrayList<>();
        parentAssetField.add(FieldFactory.getField("NAME", "NAME", FieldType.STRING ));
        GenericSelectRecordBuilder assetCategoriesbuilder = new GenericSelectRecordBuilder()
                .select(parentAssetField)
                .table(ModuleFactory.getAssetCategoryModule().getTableName())
                .andCondition((CriteriaAPI.getCondition("ID", "id", String.valueOf(component.getParentCategoryId()), NumberOperators.EQUALS)));
        List<Map<String, Object>> r = assetCategoriesbuilder.get();
        if(r.size()!=0) {
            String parentAssetName = (String) r.get(0).get("NAME");
            element.element(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME).text(parentAssetName);
        }
        if (CollectionUtils.isNotEmpty(Collections.singleton(component.getAssetModuleID()))) {
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
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetcategory");
        SelectRecordsBuilder<V3AssetCategoryContext> assetCategoriesBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
                .select(moduleBean.getAllFields(module.getName()))
                .module(module)
                .beanClass(V3AssetCategoryContext.class)
                .table(module.getTableName());
        List<V3AssetCategoryContext> assetCategories = assetCategoriesBuilder.get();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        List<Map<String,Object>> assetCategoryDatas = new ArrayList<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder assetCategoryElement = idVsData.getValue();
            V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
            XMLBuilder parentCategory = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME);
            if(parentCategory!=null) {
                String parentName = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME).getText();
                for (V3AssetCategoryContext assetCategory : assetCategories) {
                    if((assetCategory.getName()).equals(parentName)){
                        assetCategoryContext.setParentCategoryId(assetCategory.getId());
                        break;
                    }
                }
            }
            Map<String, Object> data = addAssetCategory(idVsData.getKey(), assetCategoryContext);
            assetCategoryDatas.add(data);
        }
        if(CollectionUtils.isNotEmpty(assetCategoryDatas)) {
            FacilioContext context = V3Util.createRecordList(module, assetCategoryDatas, null, null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3AssetCategoryContext> assetCategoriesFromContext = recordMap.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
            for (V3AssetCategoryContext assetCategoryFromContext : assetCategoriesFromContext) {
                uniqueIdentifierVsComponentId.put((String) assetCategoryFromContext.getData().get("xmlDataKey"), assetCategoryFromContext.getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }
    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetcategory");
        List<Long> keyList = new ArrayList<>(idVsXMLComponents.keySet());
        if(CollectionUtils.isNotEmpty(keyList)) {
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3AssetCategoryContext.class);
            List<Map<String, Object>> newAssetCategoryDatas = new ArrayList<>();
            SelectRecordsBuilder<V3AssetCategoryContext> assetCategoriesBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
                    .select(moduleBean.getAllFields(module.getName()))
                    .module(module)
                    .beanClass(V3AssetCategoryContext.class)
                    .table(module.getTableName());
            List<V3AssetCategoryContext> assetCategories = assetCategoriesBuilder.get();
            for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
                long assetCategoryId = idVsData.getKey();
                XMLBuilder assetCategoryElement = idVsData.getValue();
                V3AssetCategoryContext assetCategoryContext = constructAssetCategoryFromBuilder(assetCategoryElement);
                assetCategories.add(assetCategoryContext);
                assetCategoryContext.setId(assetCategoryId);
                XMLBuilder parentCategory = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME);
                if (parentCategory != null) {
                    String parentName = assetCategoryElement.getElement(PackageConstants.AssetCategoryConstants.PARENT_CATEGORY_NAME).getText();
                    for (V3AssetCategoryContext assetCategory : assetCategories) {
                        if ((assetCategory.getName()).equals(parentName)) {
                            assetCategoryContext.setParentCategoryId(assetCategory.getId());
                            break;
                        }
                    }
                }
                Map<String, Object> data = updateAssetCategory(assetCategoryContext);
                newAssetCategoryDatas.add(data);
            }
            V3Config v3Config = ChainUtil.getV3Config(module);
            V3Util.updateBulkRecords(module, v3Config, oldRecords, newAssetCategoryDatas, keyList, null, null, null, null, null, null, null, null, false, false);
        }
    }
    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            JSONObject data = new JSONObject();
            data.put("assetcategory", id);
            V3Util.deleteRecords("assetcategory", data,null,null,false);
        }
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
        if (assetModBean.getModule(assetModuleName)!=null) {
            FacilioModule assetModule = assetModBean.getModule(assetModuleName);
            assetCategoryContext.setAssetModuleID(assetModule.getModuleId());
        }
        return assetCategoryContext;

    }
    public List<V3AssetCategoryContext> getAssetCategoryForIds(Collection<Long> ids) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
        List<V3AssetCategoryContext> assetCategories = (List<V3AssetCategoryContext>) PackageBeanUtil.getModuleDataListsForIds(ids,assetCategoryModule, V3AssetCategoryContext.class);
        if(CollectionUtils.isNotEmpty(assetCategories)){
            for(V3AssetCategoryContext assetCategory : assetCategories){
                if(assetCategory.getData()!=null)
                    assetCategory.setDefault((Boolean) assetCategory.getData().get("isDefault"));
            }
        }
        return assetCategories;
    }

    private Map<String,Object> addAssetCategory(String xmlDataKey, V3AssetCategoryContext v3AssetCategoryContext) throws Exception {
        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
        assetCategoryContext.setName(v3AssetCategoryContext.getName());
        assetCategoryContext.setDisplayName(v3AssetCategoryContext.getDisplayName());
        assetCategoryContext.setType(v3AssetCategoryContext.getType());
        assetCategoryContext.setParentCategoryId(v3AssetCategoryContext.getParentCategoryId());
        Map<String, Object> assetCategoryData = FieldUtil.getAsProperties(assetCategoryContext);
        assetCategoryData.put("xmlDataKey", xmlDataKey);
        return assetCategoryData;
    }
    public Map<String,Object> updateAssetCategory(V3AssetCategoryContext v3AssetCategoryContext) throws Exception {
        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
        assetCategoryContext.setId(v3AssetCategoryContext.getId());
        assetCategoryContext.setName(v3AssetCategoryContext.getName());
        assetCategoryContext.setDisplayName(v3AssetCategoryContext.getDisplayName());
        assetCategoryContext.setType(v3AssetCategoryContext.getType());
        assetCategoryContext.setParentCategoryId(v3AssetCategoryContext.getParentCategoryId());
        assetCategoryContext.setAssetModuleID(v3AssetCategoryContext.getAssetModuleID());
        Map<String, Object> assetCategoryData = FieldUtil.getAsProperties(assetCategoryContext);
        return assetCategoryData;
    }
    public Map<Long, Long> getAssetCategoryIdVsModuleId(Boolean fetchSystem) throws Exception {
        Map<Long, Long> assetCategoryIdVsModuleId = new HashMap<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(false), BooleanOperators.IS));
        List<V3AssetCategoryContext> props = (List<V3AssetCategoryContext>) PackageBeanUtil.getModuleDataIdVsModuleId(criteria, assetCategoryModule, V3AssetCategoryContext.class);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetCategoryContext prop : props) {
                assetCategoryIdVsModuleId.put( prop.getId(), prop.getModuleId());
            }
        }
        return assetCategoryIdVsModuleId;
    }

}
