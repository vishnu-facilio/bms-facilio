package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceCategoryPackageBeanImpl implements PackageBean<V3SpaceCategoryContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getSpaceCategoryIdVsModuleId();
    }

    @Override
    public Map<Long, V3SpaceCategoryContext> fetchComponents(List<Long> ids) throws Exception {
        List<V3SpaceCategoryContext> spaceCategories  = getSpaceCategoryForIds(ids);
        Map<Long, V3SpaceCategoryContext> spaceCategoryIdVsSpaceCategoryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(spaceCategories)) {
            spaceCategories.forEach(spaceCategoryContext -> spaceCategoryIdVsSpaceCategoryMap.put(spaceCategoryContext.getId(), spaceCategoryContext));
            PackageBeanUtil.addPickListConfForXML(FacilioConstants.ContextNames.SPACE_CATEGORY, "name", spaceCategories, V3SpaceCategoryContext.class, false);
        }
        return spaceCategoryIdVsSpaceCategoryMap;
    }

    @Override
    public void convertToXMLComponent(V3SpaceCategoryContext component, XMLBuilder element) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        element.element(PackageConstants.SpaceCategoryConstants.SPACE_CATEGORY_NAME).text(component.getName());
        element.element(PackageConstants.SpaceCategoryConstants.DESCRIPTION).text(component.getDescription());
        if(component.getCommonArea()!=null) {
            element.element(PackageConstants.SpaceCategoryConstants.COMMON_AREA).text(String.valueOf(component.getCommonArea()));
        }
        if (component.getSpaceModuleId()!=null) {
            FacilioModule module = moduleBean.getModule(component.getSpaceModuleId());
            element.element(PackageConstants.SpaceCategoryConstants.SPACE_MODULE_NAME).text(module.getName());
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
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule spaceCategoryModule = moduleBean.getModule("spacecategory");
        SelectRecordsBuilder<V3SpaceCategoryContext> builder = new SelectRecordsBuilder<V3SpaceCategoryContext>()
                .table(spaceCategoryModule.getTableName())
                .select(moduleBean.getAllFields(spaceCategoryModule.getName()))
                .beanClass(V3SpaceCategoryContext.class)
                .module(spaceCategoryModule);
        List<V3SpaceCategoryContext> spaceCategoryContexts = builder.get();
        List<Map<String,Object>> createSpaceCategoryDatas = new ArrayList<>();
        List<Map<String,Object>> updateSpaceCategoryDatas = new ArrayList<>();
        List<Map<String,Object>> oldUpdateSpaceCategoryDatas = new ArrayList<>();
        List<Long> keyList = new ArrayList<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder spaceCategoryElement = idVsData.getValue();
            String spaceCategoryName = spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.SPACE_CATEGORY_NAME).getText();
            boolean containsName = false;
            Long id = -1L;
            for (V3SpaceCategoryContext spaceCategoryContext : spaceCategoryContexts) {
                if (spaceCategoryContext.getName().equals(spaceCategoryName)) {
                    containsName = true;
                    id = spaceCategoryContext.getId();
                    keyList.add(id);
                    Map<String, Object> oldUpdateSpaceCategoryData = FieldUtil.getAsProperties(spaceCategoryContext);
                    oldUpdateSpaceCategoryDatas.add(oldUpdateSpaceCategoryData);
                    break;
                }
            }
            V3SpaceCategoryContext v3spaceCategoryContext = constructSpaceCategoryFromBuilder(spaceCategoryElement);
            if (!containsName) {
                Map<String, Object> data = addSpaceCategory(idVsData.getKey(), v3spaceCategoryContext);
                createSpaceCategoryDatas.add(data);
            }else{
                v3spaceCategoryContext.setId(id);
                Map<String, Object> data = updateSpaceCategory(idVsData.getKey(),v3spaceCategoryContext);
                updateSpaceCategoryDatas.add(data);
            }
        }
        if(CollectionUtils.isNotEmpty(createSpaceCategoryDatas)) {
            FacilioContext context = V3Util.createRecordList(spaceCategoryModule, createSpaceCategoryDatas, null, null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3SpaceCategoryContext> spaceCategoriesFromContext = recordMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY);
            for (V3SpaceCategoryContext spaceCategoryFromContext : spaceCategoriesFromContext) {
                uniqueIdentifierVsComponentId.put((String) spaceCategoryFromContext.getData().get("xmlDataKey"), spaceCategoryFromContext.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(updateSpaceCategoryDatas)) {
            V3Config v3Config = ChainUtil.getV3Config(spaceCategoryModule);
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, spaceCategoryModule, V3SpaceCategoryContext.class);
            FacilioContext context = V3Util.updateBulkRecords(spaceCategoryModule, v3Config, oldRecords, updateSpaceCategoryDatas, keyList, null, null, null, null, null, null, null, null, false, false, null,null);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3SpaceCategoryContext> updatedSpaceCategoriesFromContext = recordMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY);
            for (V3SpaceCategoryContext spaceCategoryFromContext : updatedSpaceCategoriesFromContext) {
                uniqueIdentifierVsComponentId.put((String) spaceCategoryFromContext.getData().get("xmlDataKey"), spaceCategoryFromContext.getId());
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("spacecategory");
        List<Long> keyList = new ArrayList<>(idVsXMLComponents.keySet());
        if(CollectionUtils.isNotEmpty(keyList)) {
            List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) PackageBeanUtil.getModuleDataListsForIds(keyList, module, V3SpaceCategoryContext.class);
            List<Map<String, Object>> newSpaceCategoryDatas = new ArrayList<>();
            for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
                long spaceCategoryId = idVsData.getKey();
                XMLBuilder spaceCategoryElement = idVsData.getValue();
                V3SpaceCategoryContext spaceCategoryContext = constructSpaceCategoryFromBuilder(spaceCategoryElement);
                spaceCategoryContext.setId(spaceCategoryId);
                Map<String, Object> data = updateSpaceCategory(null,spaceCategoryContext);
                newSpaceCategoryDatas.add(data);
            }
            V3Config v3Config = ChainUtil.getV3Config(module);
            V3Util.updateBulkRecords(module, v3Config, oldRecords, newSpaceCategoryDatas, keyList, null, null, null, null, null, null, null, null, false, false, null,null);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("spacecategory");
        List<Long> targetSpaceCategoryIds = new ArrayList<>(idVsXMLComponents.keySet());
        Map<String, Long> spaceCategoriesUIdVsIdsFromPackage = PackageUtil.getComponentsUIdVsComponentIdForComponent(ComponentType.SPACE_CATEGORY);
        if(PackageUtil.isInstallThread()) {
            PackageBeanUtil.deleteV3OldRecordFromTargetOrg(module.getName(), spaceCategoriesUIdVsIdsFromPackage,targetSpaceCategoryIds);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    @Override
    public void addPickListConf() throws Exception {
        PackageBeanUtil.addPickListConfForContext(FacilioConstants.ContextNames.SPACE_CATEGORY, "name", V3SpaceCategoryContext.class);
    }

    private V3SpaceCategoryContext constructSpaceCategoryFromBuilder(XMLBuilder spaceCategoryElement) throws Exception {
        String name = spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.SPACE_CATEGORY_NAME).getText();
        String description = spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.DESCRIPTION).getText();
        XMLBuilder commonAreaBuilder = spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.COMMON_AREA);
        V3SpaceCategoryContext v3SpaceCategoryContext = new V3SpaceCategoryContext();
        v3SpaceCategoryContext.setName(name);
        v3SpaceCategoryContext.setDescription(description);
        if(commonAreaBuilder!=null) {
            boolean commonArea = Boolean.parseBoolean(spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.COMMON_AREA).getText());
            v3SpaceCategoryContext.setCommonArea(commonArea);
        }

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule("spacecategory");
        v3SpaceCategoryContext.setModuleId(module.getModuleId());
        XMLBuilder spaceModuleBuilder = spaceCategoryElement.getElement(PackageConstants.SpaceCategoryConstants.SPACE_MODULE_NAME);
        ModuleBean spaceModBean = Constants.getModBean();
        if (spaceModuleBuilder!=null) {
            String spaceModuleName = spaceModuleBuilder.getText();
            FacilioModule spaceModule = spaceModBean.getModule(spaceModuleName);
            v3SpaceCategoryContext.setSpaceModuleId(spaceModule.getModuleId());
        }
        return v3SpaceCategoryContext;
    }

    private Map<Long, Long> getSpaceCategoryIdVsModuleId() throws Exception {
        Map<Long, Long> spaceIdVsModuleId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule spaceCategoryModule = moduleBean.getModule("spacecategory");
        List<V3SpaceCategoryContext> props = (List<V3SpaceCategoryContext>) PackageBeanUtil.getModuleData(null, spaceCategoryModule,V3SpaceCategoryContext.class, false);
        if (CollectionUtils.isNotEmpty(props)) {
            for (V3SpaceCategoryContext prop : props) {
                spaceIdVsModuleId.put(prop.getId(), prop.getModuleId());
            }
        }
        return spaceIdVsModuleId;
    }

    private List<V3SpaceCategoryContext> getSpaceCategoryForIds(List<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule spaceCategoryModule = moduleBean.getModule("spacecategory");
        List<V3SpaceCategoryContext> spaceCategories = (List<V3SpaceCategoryContext>) PackageBeanUtil.getModuleDataListsForIds(ids,spaceCategoryModule, V3SpaceCategoryContext.class);
        return spaceCategories;
    }
    private Map<String,Object> addSpaceCategory(String xmlDataKey, V3SpaceCategoryContext v3SpaceCategoryContext) throws Exception {
        Map<String, Object> spaceCategoryData = FieldUtil.getAsProperties(v3SpaceCategoryContext);
        spaceCategoryData.put("xmlDataKey", xmlDataKey);
        return spaceCategoryData;
    }
    private Map<String, Object> updateSpaceCategory(String xmlDataKey,V3SpaceCategoryContext v3SpaceCategoryContext) throws Exception {
        Map<String, Object> spaceCategoryData = FieldUtil.getAsProperties(v3SpaceCategoryContext);
        spaceCategoryData.put("xmlDataKey", xmlDataKey);
        return spaceCategoryData;
    }
}


