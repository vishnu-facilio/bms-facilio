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
        FacilioModule module = moduleBean.getModule("assetdepartment");
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        SelectRecordsBuilder<V3AssetDepartmentContext> builder = new SelectRecordsBuilder<V3AssetDepartmentContext>()
                .table(module.getTableName())
                .select(moduleBean.getAllFields(module.getName()))
                .beanClass(V3AssetDepartmentContext.class)
                .module(module);
        List<V3AssetDepartmentContext> assetDepartmentContexts = builder.get();
        List<Map<String,Object>> createAssetDepartmentDatas = new ArrayList<>();
        List<Map<String,Object>> updateAssetDepartmentDatas = new ArrayList<>();
        List<Map<String,Object>> oldUpdateAssetDepartmentDatas = new ArrayList<>();
        List<Long> keyList = new ArrayList<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder assetDepartmentElement = idVsData.getValue();
            String assetDepartmentName = assetDepartmentElement.getElement(PackageConstants.AssetDepartmentConstants.ASSET_DEPARTMENT_NAME).getText();
            boolean containsName = false;
            Long id = -1L;
            for (V3AssetDepartmentContext assetDepartmentContext : assetDepartmentContexts) {
                if (assetDepartmentContext.getName().equals(assetDepartmentName)) {
                    containsName = true;
                    id = assetDepartmentContext.getId();
                    keyList.add(id);
                    Map<String, Object> oldUpdateAssetDepartmentData = FieldUtil.getAsProperties(assetDepartmentContext);
                    oldUpdateAssetDepartmentDatas.add(oldUpdateAssetDepartmentData);
                    break;
                }
            }
            V3AssetDepartmentContext v3AssetDepartmentContext = constructAssetDepartmentFromBuilder(assetDepartmentElement);
            if (!containsName) {
                Map<String, Object> data = addAssetDepartment(idVsData.getKey(), v3AssetDepartmentContext);
                createAssetDepartmentDatas.add(data);
            }else{
                v3AssetDepartmentContext.setId(id);
                Map<String, Object> data = updateAssetDepartment(idVsData.getKey(),v3AssetDepartmentContext);
                updateAssetDepartmentDatas.add(data);
            }
        }
        if(CollectionUtils.isNotEmpty(createAssetDepartmentDatas)) {
            FacilioContext context = V3Util.createRecordList(module,createAssetDepartmentDatas, null, null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3AssetDepartmentContext> assetDepartmentsFromContext = recordMap.get(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
            for (V3AssetDepartmentContext assetDepartmentFromContext : assetDepartmentsFromContext) {
                uniqueIdentifierVsComponentId.put((String) assetDepartmentFromContext.getData().get("xmlDataKey"), assetDepartmentFromContext.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(updateAssetDepartmentDatas)) {
            V3Config v3Config = ChainUtil.getV3Config(module);
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3AssetDepartmentContext.class);
            FacilioContext context = V3Util.updateBulkRecords(module, v3Config, oldRecords, updateAssetDepartmentDatas, keyList, null, null, null, null, null, null, null, null, false, false);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3AssetDepartmentContext> updatedAssetDepartmentsFromContext = recordMap.get(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
            for (V3AssetDepartmentContext assetDepartmentFromContext : updatedAssetDepartmentsFromContext) {
                uniqueIdentifierVsComponentId.put((String) assetDepartmentFromContext.getData().get("xmlDataKey"), assetDepartmentFromContext.getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents, boolean isReUpdate) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("assetdepartment");
        List<Long> keyList = new ArrayList<>(idVsXMLComponents.keySet());
        if(CollectionUtils.isNotEmpty(keyList)) {
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3AssetDepartmentContext.class);
            List<Map<String, Object>> newAssetDepartmentDatas = new ArrayList<>();
            for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
                long assetDepartmentId = idVsData.getKey();
                XMLBuilder assetDepartmentElement = idVsData.getValue();
                V3AssetDepartmentContext assetDepartmentContext = constructAssetDepartmentFromBuilder(assetDepartmentElement);
                assetDepartmentContext.setId(assetDepartmentId);
                Map<String, Object> data = updateAssetDepartment(null, assetDepartmentContext);
                newAssetDepartmentDatas.add(data);
            }
            V3Config v3Config = ChainUtil.getV3Config(module);
            V3Util.updateBulkRecords(module, v3Config, oldRecords, newAssetDepartmentDatas, keyList, null, null, null, null, null, null, null, null, false, false);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for (long id : ids) {
            JSONObject data = new JSONObject();
            data.put("assetdepartment", id);
            V3Util.deleteRecords("assetdepartment", data,null,null,false);
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
    private Map<String,Object> addAssetDepartment(String xmlDataKey, V3AssetDepartmentContext v3AssetDepartmentContext) throws Exception {
        Map<String, Object> assetDepartmentData = FieldUtil.getAsProperties(v3AssetDepartmentContext);
        assetDepartmentData.put("xmlDataKey",xmlDataKey);
        return assetDepartmentData;
    }
    public Map<String,Object> updateAssetDepartment(String xmlDataKey, V3AssetDepartmentContext v3AssetDepartmentContext) throws Exception {
        Map<String, Object> assetDepartmentData = FieldUtil.getAsProperties(v3AssetDepartmentContext);
        assetDepartmentData.put("xmlDataKey",xmlDataKey);
        return assetDepartmentData;
    }
    public Map<Long, Long> getAssetDepartmentIdVsModuleId() throws Exception {
        Map<Long, Long> AssetDepartmentIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule assetDepartmentModule = moduleBean.getModule("assetdepartment");
        List<V3AssetDepartmentContext> props = (List<V3AssetDepartmentContext>) PackageBeanUtil.getModuleDataIdVsModuleId(null, assetDepartmentModule, V3AssetDepartmentContext.class);
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
        List<V3AssetDepartmentContext> assetDepartments = (List<V3AssetDepartmentContext>) PackageBeanUtil.getModuleDataListsForIds(ids,assetDepartmentModule, V3AssetDepartmentContext.class);
        return assetDepartments;
    }
}
