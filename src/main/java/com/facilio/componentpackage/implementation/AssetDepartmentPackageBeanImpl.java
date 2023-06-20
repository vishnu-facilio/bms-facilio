package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetDepartmentContext;
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

public class AssetDepartmentPackageBeanImpl implements PackageBean<V3AssetDepartmentContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getAssetDepartmentIdVsModuleId();
    }

    @Override
    public Map<Long, V3AssetDepartmentContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3AssetDepartmentContext> assetDepartments  = getAssetDepartmentForIds(ids);
        Map<Long, V3AssetDepartmentContext> assetDepartmentIdVsAssetDepartmentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(assetDepartments)) {
            assetDepartments.forEach(assetDepartmentContext -> assetDepartmentIdVsAssetDepartmentMap.put(assetDepartmentContext.getId(), assetDepartmentContext));
        }
        return assetDepartmentIdVsAssetDepartmentMap;
    }

    @Override
    public void convertToXMLComponent(V3AssetDepartmentContext component, XMLBuilder element) throws Exception {
        element.element(PackageConstants.AssetDepartmentConstants.ASSET_DEPARTMENT_NAME).text(component.getName());
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
            XMLBuilder assetDepartmentElement = idVsData.getValue();
            String moduleName = new String();
            V3AssetDepartmentContext assetDepartmentContext = constructAssetDepartmentFromBuilder(assetDepartmentElement);
            long assetDepartmentModuleId = assetDepartmentContext.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(assetDepartmentModuleId))) {
                FacilioModule module = moduleBean.getModule(assetDepartmentModuleId);
                moduleName = module.getName();
            }
            long assetDepartmentId = addAssetDepartment(moduleName, assetDepartmentContext);
            assetDepartmentContext.setId(assetDepartmentId);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), assetDepartmentId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long assetDepartmentId = idVsData.getKey();
            XMLBuilder assetDepartmentElement = idVsData.getValue();
            String moduleName = new String();
            V3AssetDepartmentContext assetDepartmentContext = constructAssetDepartmentFromBuilder(assetDepartmentElement);
            assetDepartmentContext.setId(assetDepartmentId);
            long assetDepartmentModuleId = assetDepartmentContext.getModuleId();
            if (CollectionUtils.isNotEmpty(Collections.singleton(assetDepartmentModuleId))) {
                FacilioModule module = moduleBean.getModule(assetDepartmentModuleId);
                moduleName = module.getName();
            }
            updateAssetDepartment(moduleName, assetDepartmentContext);
        }

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        JSONObject data = new JSONObject();
        for (long id : ids) {
            data.put("assetdepartment", id);
            V3Util.deleteRecords("assetdepartment", data,null,null,false);
            data.clear();
        }
    }
    public static V3AssetDepartmentContext constructAssetDepartmentFromBuilder(XMLBuilder assetDepartmentElement) throws Exception {
        String name = assetDepartmentElement.getElement(PackageConstants.AssetDepartmentConstants.ASSET_DEPARTMENT_NAME).getText();
        V3AssetDepartmentContext assetDepartmentContext = new V3AssetDepartmentContext();
        assetDepartmentContext.setName(name);
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetdepartment");
        assetDepartmentContext.setModuleId(module.getModuleId());

        return assetDepartmentContext;

    }
    private long addAssetDepartment(String module, V3AssetDepartmentContext v3AssetDepartmentContext) throws Exception {
        FacilioModule assetDepartmentModule = ChainUtil.getModule(module);
        V3AssetDepartmentContext assetDepartmentContext = new V3AssetDepartmentContext();
        assetDepartmentContext.setName(v3AssetDepartmentContext.getName());
        Map<String, Object> assetDepartmentData = FieldUtil.getAsProperties(assetDepartmentContext);
        FacilioContext context = V3Util.createRecord(assetDepartmentModule, assetDepartmentData);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3AssetDepartmentContext> assetDepartment = recordMap.get(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
        if(CollectionUtils.isNotEmpty(assetDepartment)){
            for(V3AssetDepartmentContext department : assetDepartment){
                return department.getId();
            }
        }
        return -1;
    }
    public void updateAssetDepartment(String module, V3AssetDepartmentContext v3AssetDepartmentContext) throws Exception {
        FacilioModule assetDepartmentModule = ChainUtil.getModule(module);
        V3AssetDepartmentContext assetDepartmentContext = new V3AssetDepartmentContext();
        assetDepartmentContext.setId(v3AssetDepartmentContext.getId());
        assetDepartmentContext.setName(v3AssetDepartmentContext.getName());
        Map<String, Object> assetDepartmentData = FieldUtil.getAsProperties(assetDepartmentContext);
        V3Util.processAndUpdateSingleRecord(assetDepartmentModule.getName(), assetDepartmentContext.getId(), assetDepartmentData, null, null, null, null, null, null, null, null);
    }
    public Map<Long, Long> getAssetDepartmentIdVsModuleId() throws Exception {
        Map<Long, Long> AssetDepartmentIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetDepartmentModule = moduleBean.getModule("assetdepartment");
        List<V3AssetDepartmentContext> props = (List<V3AssetDepartmentContext>) PackageBeanUtil.getContextIdVsParentId(null, assetDepartmentModule, V3AssetDepartmentContext.class);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3AssetDepartmentContext prop : props) {
                AssetDepartmentIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return AssetDepartmentIdVsModuleId;
    }
    public List<V3AssetDepartmentContext> getAssetDepartmentForIds(Collection<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetDepartmentModule = moduleBean.getModule("assetdepartment");
        List<V3AssetDepartmentContext> assetDepartments = (List<V3AssetDepartmentContext>) PackageBeanUtil.getContextListsForIds(ids,assetDepartmentModule, V3AssetDepartmentContext.class);
        return assetDepartments;
    }
}
