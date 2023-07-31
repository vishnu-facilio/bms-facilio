package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetTypeContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AssetTypePackageBeanImpl implements PackageBean<V3AssetTypeContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getAssetTypeIdVsModuleId();
    }

    @Override
    public Map<Long, V3AssetTypeContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3AssetTypeContext> assetTypes  = getAssetTypeForIds(ids);
        Map<Long, V3AssetTypeContext> assetTypeIdVsAssetTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetTypes)) {
            assetTypes.forEach(assetTypeContext -> assetTypeIdVsAssetTypeMap.put(assetTypeContext.getId(), assetTypeContext));
            PackageBeanUtil.addPickListConfForXML(FacilioConstants.ContextNames.ASSET_TYPE, "name", assetTypes, V3AssetTypeContext.class, false);
        }
        return assetTypeIdVsAssetTypeMap;
    }

    @Override
    public void convertToXMLComponent(V3AssetTypeContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.AssetTypeConstants.ASSET_TYPE_NAME).text(component.getName());
        element.element(PackageConstants.AssetTypeConstants.IS_MOVABLE).text(String.valueOf(component.getMovable()));
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
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assettype");
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        SelectRecordsBuilder<V3AssetTypeContext> builder = new SelectRecordsBuilder<V3AssetTypeContext>()
                .table(module.getTableName())
                .select(moduleBean.getAllFields(module.getName()))
                .beanClass(V3AssetTypeContext.class)
                .module(module);
        List<V3AssetTypeContext> assetTypeContexts = builder.get();
        List<Map<String,Object>> createAssetTypeDatas = new ArrayList<>();
        List<Map<String,Object>> updateAssetTypeDatas = new ArrayList<>();
        List<Map<String,Object>> oldUpdateAssetTypeDatas = new ArrayList<>();
        List<Long> keyList = new ArrayList<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder assetTypeElement = idVsData.getValue();
            String assetTypeName = assetTypeElement.getElement(PackageConstants.AssetTypeConstants.ASSET_TYPE_NAME).getText();
            boolean containsName = false;
            Long id = -1L;
            for (V3AssetTypeContext assetTypeContext : assetTypeContexts) {
                if (assetTypeContext.getName().equals(assetTypeName)) {
                    containsName = true;
                    id = assetTypeContext.getId();
                    keyList.add(id);
                    Map<String, Object> oldUpdateAssetTypeData = FieldUtil.getAsProperties(assetTypeContext);
                    oldUpdateAssetTypeDatas.add(oldUpdateAssetTypeData);
                    break;
                }
            }
            V3AssetTypeContext v3AssetTypeContext = constructAssetTypeFromBuilder(assetTypeElement);
            if (!containsName) {
                Map<String, Object> data = addAssetType(idVsData.getKey(), v3AssetTypeContext);
                createAssetTypeDatas.add(data);
            }else{
                v3AssetTypeContext.setId(id);
                Map<String, Object> data = updateAssetType(idVsData.getKey(),v3AssetTypeContext);
                updateAssetTypeDatas.add(data);
            }
        }
        if(CollectionUtils.isNotEmpty(createAssetTypeDatas)) {
            FacilioContext context = V3Util.createRecordList(module,createAssetTypeDatas, null, null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3AssetTypeContext> assetTypesFromContext = recordMap.get(FacilioConstants.ContextNames.ASSET_TYPE);
            for (V3AssetTypeContext assetTypeFromContext : assetTypesFromContext) {
                uniqueIdentifierVsComponentId.put((String) assetTypeFromContext.getData().get("xmlDataKey"), assetTypeFromContext.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(updateAssetTypeDatas)) {
            V3Config v3Config = ChainUtil.getV3Config(module);
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3AssetTypeContext.class);
            FacilioContext context = V3Util.updateBulkRecords(module, v3Config, oldRecords, updateAssetTypeDatas, keyList, null, null, null, null, null, null, null, null, false, false, null,null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3AssetTypeContext> updatedAssetTypesFromContext = recordMap.get(FacilioConstants.ContextNames.ASSET_TYPE);
            for (V3AssetTypeContext assetTypeFromContext : updatedAssetTypesFromContext) {
                uniqueIdentifierVsComponentId.put((String) assetTypeFromContext.getData().get("xmlDataKey"), assetTypeFromContext.getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetdepartment");
        List<Long> keyList = new ArrayList<>(idVsXMLComponents.keySet());
        if(CollectionUtils.isNotEmpty(keyList)) {
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3AssetTypeContext.class);
            List<Map<String, Object>> newAssetTypeDatas = new ArrayList<>();
            for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
                long assetTypeId = idVsData.getKey();
                XMLBuilder assetTypeElement = idVsData.getValue();
                V3AssetTypeContext assetTypeContext = constructAssetTypeFromBuilder(assetTypeElement);
                assetTypeContext.setId(assetTypeId);
                Map<String, Object> data = updateAssetType(null,assetTypeContext);
                newAssetTypeDatas.add(data);
            }
            V3Config v3Config = ChainUtil.getV3Config(module);
            V3Util.updateBulkRecords(module, v3Config, oldRecords, newAssetTypeDatas, keyList, null, null, null, null, null, null, null, null, false, false, null,null);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            JSONObject data = new JSONObject();
            data.put("assettype", id);
            V3Util.deleteRecords("assettype", data,null,null,false);
        }
    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addPickListConfForContext(FacilioConstants.ContextNames.ASSET_TYPE, "name", V3AssetTypeContext.class);
    }

    public Map<Long, Long> getAssetTypeIdVsModuleId() throws Exception {
        Map<Long, Long> assetTypeIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetTypeModule = moduleBean.getModule("assettype");
        List<V3AssetTypeContext> props = (List<V3AssetTypeContext>) PackageBeanUtil.getModuleDataIdVsModuleId(null, assetTypeModule,V3AssetTypeContext.class);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetTypeContext prop : props) {
                assetTypeIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return assetTypeIdVsModuleId;
    }
    public List<V3AssetTypeContext> getAssetTypeForIds(Collection<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetTypeModule = moduleBean.getModule("assettype");
        List<V3AssetTypeContext> assetTypes = (List<V3AssetTypeContext>) PackageBeanUtil.getModuleDataListsForIds(ids,assetTypeModule, V3AssetTypeContext.class);
        return assetTypes;
    }
    public static V3AssetTypeContext constructAssetTypeFromBuilder(XMLBuilder assetTypeElement) throws Exception {
        String name = assetTypeElement.getElement(PackageConstants.AssetTypeConstants.ASSET_TYPE_NAME).getText();
        boolean isMovable = Boolean.parseBoolean(assetTypeElement.getElement(PackageConstants.AssetTypeConstants.IS_MOVABLE).getText());
        V3AssetTypeContext assetTypeContext = new V3AssetTypeContext();
        assetTypeContext.setName(name);
        assetTypeContext.setMovable(isMovable);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assettype");
        assetTypeContext.setModuleId(module.getModuleId());
        return assetTypeContext;
    }
    private Map<String,Object> addAssetType( String xmlDataKey, V3AssetTypeContext v3AssetTypeContext) throws Exception {
        Map<String, Object> assetTypeData = FieldUtil.getAsProperties(v3AssetTypeContext);
        assetTypeData.put("xmlDataKey", xmlDataKey);
        return assetTypeData;
    }
    public Map<String, Object> updateAssetType(String xmlDataKey,V3AssetTypeContext v3AssetTypeContext) throws Exception {
        Map<String, Object> assetTypeData = FieldUtil.getAsProperties(v3AssetTypeContext);
        assetTypeData.put("xmlDataKey", xmlDataKey);
        return assetTypeData;
    }

}
