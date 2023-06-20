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
        return getAssestTypeIdVsModuleId();
    }

    @Override
    public Map<Long, V3AssetTypeContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3AssetTypeContext> assetTypes  = getAssetTypeForIds(ids);
        Map<Long, V3AssetTypeContext> assetTypeIdVsAssetTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetTypes)) {
            assetTypes.forEach(assetTypeContext -> assetTypeIdVsAssetTypeMap.put(assetTypeContext.getId(), assetTypeContext));
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
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder assetTypeElement = idVsData.getValue();
            String moduleName = new String();
            V3AssetTypeContext assetTypeContext = constructAssetTypeFromBuilder(assetTypeElement);
            long assetTypeContextModuleId = assetTypeContext.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(assetTypeContextModuleId))) {
                FacilioModule module = moduleBean.getModule(assetTypeContextModuleId);
                moduleName = module.getName();
            }
            long assetTypeId = addAssetType(moduleName, assetTypeContext);
            assetTypeContext.setId(assetTypeId);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), assetTypeId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long assetTypeId = idVsData.getKey();
            XMLBuilder assetTypeElement = idVsData.getValue();
            String moduleName = new String();
            V3AssetTypeContext assetTypeContext = constructAssetTypeFromBuilder(assetTypeElement);
            assetTypeContext.setId(assetTypeId);
            long assetTypeModuleId = assetTypeContext.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(assetTypeModuleId))) {
                FacilioModule module = moduleBean.getModule(assetTypeModuleId);
                moduleName = module.getName();
            }
            updateAssetType(moduleName, assetTypeContext);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        JSONObject data = new JSONObject();
        for (long id : ids) {
            data.put("assettype", id);
            V3Util.deleteRecords("assettype", data,null,null,false);
            data.clear();
        }
    }
    public Map<Long, Long> getAssestTypeIdVsModuleId() throws Exception {
        Map<Long, Long> AssetTypeIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetTypeModule = moduleBean.getModule("assettype");
        List<V3AssetTypeContext> props = (List<V3AssetTypeContext>) PackageBeanUtil.getContextIdVsParentId(null, assetTypeModule,V3AssetTypeContext.class);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetTypeContext prop : props) {
                AssetTypeIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return AssetTypeIdVsModuleId;
    }
    public List<V3AssetTypeContext> getAssetTypeForIds(Collection<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetTypeModule = moduleBean.getModule("assettype");
        List<V3AssetTypeContext> assetTypes = (List<V3AssetTypeContext>) PackageBeanUtil.getContextListsForIds(ids,assetTypeModule, V3AssetTypeContext.class);
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
    private long addAssetType(String module, V3AssetTypeContext v3AssetTypeContext) throws Exception {
        FacilioModule assetTypeModule = ChainUtil.getModule(module);
        V3AssetTypeContext assetTypeContext = new V3AssetTypeContext();
        assetTypeContext.setName(v3AssetTypeContext.getName());
        assetTypeContext.setMovable(v3AssetTypeContext.getMovable());
        Map<String, Object> assetTypeData = FieldUtil.getAsProperties(assetTypeContext);
        FacilioContext context = V3Util.createRecord(assetTypeModule, assetTypeData);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3AssetTypeContext> assetType = recordMap.get(FacilioConstants.ContextNames.ASSET_TYPE);
        if(CollectionUtils.isNotEmpty(assetType)){
            for(V3AssetTypeContext type : assetType){
                return type.getId();
            }
        }
        return -1;
    }
    public void updateAssetType(String module, V3AssetTypeContext v3AssetTypeContext) throws Exception {
        FacilioModule assetTypeModule = ChainUtil.getModule(module);
        V3AssetTypeContext assetTypeContext = new V3AssetTypeContext();
        assetTypeContext.setId(v3AssetTypeContext.getId());
        assetTypeContext.setName(v3AssetTypeContext.getName());
        Map<String, Object> assetTypeData = FieldUtil.getAsProperties(assetTypeContext);
        V3Util.processAndUpdateSingleRecord(assetTypeModule.getName(), assetTypeContext.getId(), assetTypeData, null, null, null, null, null, null, null, null);
    }

}
