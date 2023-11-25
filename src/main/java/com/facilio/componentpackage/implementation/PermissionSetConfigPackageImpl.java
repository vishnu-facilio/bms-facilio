package com.facilio.componentpackage.implementation;

import com.facilio.beans.PermissionSetBean;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.context.module.FieldPermissionSet;
import com.facilio.permission.context.module.RelatedListPermissionSet;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.permission.handlers.group.RelatedRecordsPermissionSetHandler;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Log4j
public class PermissionSetConfigPackageImpl implements PackageBean<BasePermissionContext> {

    PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
    List<PermissionSetContext> permissionSet = permissionSetBean.getPermissionSetsList(-1,-1,null,false);
    List<Long> permissionSetIds = permissionSet.stream().map(PermissionSetContext::getId).collect(Collectors.toList());

    public PermissionSetConfigPackageImpl() throws Exception {
    }

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long, Long> permissionSetConfigIdMap = new HashMap<>();
        List<String> permissionFieldEnumValues = new ArrayList<>();

        for(PermissionSetType.Type type : PermissionSetType.Type.values()){
            permissionFieldEnumValues.addAll(type.getPermissionFieldEnumList().stream().map(PermissionFieldEnum::getValue).collect(Collectors.toList()));
        }

        List<Map<String, Object >> props = getModuleTypeRecords(permissionSetIds, permissionFieldEnumValues, null);

        if(CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props){
                Long recordId = (Long)prop.get("id");
                Long permissionSetId = (Long)prop.get("permissionSetId");
                if(recordId > 0 && permissionSetId >0) {
                    permissionSetConfigIdMap.put(recordId, permissionSetId);
                }
            }
        }

        return permissionSetConfigIdMap;
    }

    @Override

    public Map<Long, BasePermissionContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, BasePermissionContext> permissionConfigIdVsContext = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<Map<String, Object>> props = getModuleTypeRecords(null, null, ids);
        List<Triple<Long, Long, String>> moduleConfigs = props.stream()
                .map(prop -> Triple.of((Long) prop.get("moduleId"), (Long) prop.get("permissionSetId"), (String) prop.get("permissionType")))
                .distinct()
                .collect(Collectors.toList());

        for(Triple<Long, Long, String> moduleConfig : moduleConfigs){
            Long moduleId = moduleConfig.getLeft();
            Long permissionSetId = moduleConfig.getMiddle();
            String groupType = moduleConfig.getRight();
            for(PermissionSetType.Type permissionGroupType : PermissionSetType.Type.values()){
                if(permissionGroupType.getPermissionFieldEnumList().contains(PermissionFieldEnum.valueOf(groupType.toUpperCase()))){
                    groupType = permissionGroupType.name();
                    break;
                }
            }

            List<BasePermissionContext> permissionConfigForModuleVsPermissionSet = permissionSetBean.getPermissionSetItems(permissionSetId,moduleId,groupType);
            if(Constants.getModBean().getModule(moduleId).getName().equals("workorder")) {
                BasePermissionContext permissionSet = new RelatedListPermissionSet(moduleId, moduleId, Constants.getModBean().getField("parentWO", "workorder").getFieldId(), "Dependant Workorders");
                permissionConfigForModuleVsPermissionSet.add(permissionSet);
            }
            for(BasePermissionContext context : permissionConfigForModuleVsPermissionSet){
                if(context != null) {
                    permissionConfigIdVsContext.put(context.getId(), context);
                }
            }

        }
        return permissionConfigIdVsContext;
    }

    @Override
    public void convertToXMLComponent(BasePermissionContext component, XMLBuilder permissionSetConfigElement) throws Exception {
        if(component != null){
            PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

            permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.PERMISSION_TYPE).text(component.getPermissionType().getValue());
            permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.PERMISSION).text(String.valueOf(component.getPermission()));
            permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.TYPE).text(component.getType().name());

            Long permissionSetId = component.getPermissionSetId();
            String permissionSetLinkName = permissionSetBean.getPermissionSet(permissionSetId).getLinkName();
            permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.PERMISSION_SET_LINK_NAME).text(permissionSetLinkName);

            if(component instanceof RelatedListPermissionSet){
                RelatedListPermissionSet relatedListPermissionComponent = (RelatedListPermissionSet) component;
                Long moduleId = relatedListPermissionComponent.getModuleId();
                FacilioModule module = Constants.getModBean().getModule(moduleId);
                String moduleName = module!=null? module.getName() : "";

                Long relatedModuleId = relatedListPermissionComponent.getRelatedModuleId();
                FacilioModule relatedModule = Constants.getModBean().getModule(relatedModuleId);
                String relatedModuleName = relatedModule!=null? relatedModule.getName() : "";

                Long relatedFieldId = relatedListPermissionComponent.getRelatedFieldId();
                FacilioField relatedField = Constants.getModBean().getField(relatedFieldId);
                String relatedFieldName = relatedField!=null? relatedField.getName(): "";

                permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.MODULE_NAME).text(moduleName);
                permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.RELATED_MODULE_NAME).text(relatedModuleName);
                permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.RELATED_FIELD_NAME).text(relatedFieldName);
            }

            if(component instanceof FieldPermissionSet){
                FieldPermissionSet fieldPermissionComponent = (FieldPermissionSet) component;
                Long moduleId = fieldPermissionComponent.getModuleId();
                FacilioModule relatedModule = Constants.getModBean().getModule(moduleId);
                String moduleName = relatedModule!=null? relatedModule.getName() : "";

                Long fieldId = fieldPermissionComponent.getFieldId();
                FacilioField field = Constants.getModBean().getField(fieldId);
                String fieldName = field!=null? field.getName() : "";

                permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.MODULE_NAME).text(moduleName);
                permissionSetConfigElement.element(PackageConstants.PermissionSetConfig.FIELD_NAME).text(fieldName);
            }

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
        Map<String, Long> uniqueIdentifierVsPermissionSetConfigIds = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder permissionSetComponentElement = idVsData.getValue();

            Long recordId = addOrUpdatePermissionSetConfig(permissionSetComponentElement);
            if(recordId!=null && recordId == -1){
                continue;
            }
            uniqueIdentifierVsPermissionSetConfigIds.put(idVsData.getKey(), recordId);
        }
        return uniqueIdentifierVsPermissionSetConfigIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsXMLBuilder : idVsXMLComponents.entrySet()) {
            XMLBuilder permissionSetConfigComponentElement = idVsXMLBuilder.getValue();
            addOrUpdatePermissionSetConfig(permissionSetConfigComponentElement);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioModule module = PermissionSetModuleFactory.getModuleTypePermissionSetModule();
        FacilioField idField = FieldFactory.getIdField(module);

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(PermissionSetModuleFactory.getModuleTypePermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(idField,ids,NumberOperators.EQUALS));

        deleteRecordBuilder.delete();
    }

    private List<Map<String, Object>> getModuleTypeRecords(List<Long> permissionSetIds, List<String> permissionFieldEnumValues, List<Long> ids) throws Exception {
        if((CollectionUtils.isNotEmpty(permissionSetIds) && CollectionUtils.isNotEmpty(permissionFieldEnumValues)) || CollectionUtils.isNotEmpty(ids)) {
            FacilioModule module = PermissionSetModuleFactory.getModuleTypePermissionSetModule();
            FacilioField IdField = FieldFactory.getIdField(module);
            FacilioField PermissionSetField = FieldFactory.getNumberField("permissionSetId", "PERMISSION_SET_ID", module);
            FacilioField PermissionTypeField = FieldFactory.getStringField("permissionType", "PERMISSION_TYPE", module);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(PermissionSetFieldFactory.getModuleTypePermissionSetFields())
                    .table(PermissionSetModuleFactory.getModuleTypePermissionSetModule().getTableName());

            if(CollectionUtils.isEmpty(ids)) {
                selectRecordBuilder
                        .andCondition(CriteriaAPI.getCondition(PermissionSetField, permissionSetIds, NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(PermissionTypeField, StringUtils.join(permissionFieldEnumValues, ","), StringOperators.IS));
            }else {
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition(IdField, ids, NumberOperators.EQUALS));
            }
            return selectRecordBuilder.get();
        }
        return new ArrayList<>();
    }

    private long addOrUpdatePermissionSetConfig(XMLBuilder permissionSetConfigComponentElement) throws Exception {
        Map<String, Object> permissionSetConfigMap = new HashMap<>();
        String moduleName = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.MODULE_NAME).getText();
        FacilioModule module = Constants.getModBean().getModule(moduleName);

        if(module!=null && module.getModuleId() > 0) {
            Long moduleId = module.getModuleId();
            permissionSetConfigMap.put("moduleId", moduleId);
            String type = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.TYPE).getText();

            if (type.equals("FIELD_LIST")) {
                String fieldName = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.FIELD_NAME).getText();
                FacilioField field = Constants.getModBean().getField(fieldName, module.getName());
                Long fieldId = field != null ? field.getFieldId() : -1L;
                permissionSetConfigMap.put("fieldId", fieldId);

                if(fieldId == null || fieldId <= 0){
                    LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since Field is null for - "+fieldName+ " of module - "+moduleName);
                    return -1L;
                }
            } else if (type.equals("RELATED_LIST")) {
                RelatedRecordsPermissionSetHandler recordsPermissionSetHandler = new RelatedRecordsPermissionSetHandler();
                List<FacilioModule> subModules = recordsPermissionSetHandler.getFilteredSubModules(moduleId);

                String relatedModuleName = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.RELATED_MODULE_NAME).getText();
                String relatedFieldName = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.RELATED_FIELD_NAME).getText();

                if(Constants.getModBean().getModule(relatedModuleName) == null){
                    LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since relatedModule is null for relatedModuleName - "+relatedModuleName);
                    return -1L;
                }

                FacilioField relatedField = Constants.getModBean().getField(relatedFieldName, relatedModuleName);

                Long relatedFieldId = relatedField != null ? relatedField.getId() : -1L;
                Long relatedModuleId = relatedField != null ? relatedField.getModuleId() : -1L;

                permissionSetConfigMap.put("relatedModuleId", relatedModuleId);
                permissionSetConfigMap.put("relatedFieldId", relatedFieldId);

                if(relatedModuleId <= 0 || relatedFieldId <= 0){
                    LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since relatedModuleId or relatedFieldId is null for relatedModule - "+relatedModuleName+" relatedField - "+relatedFieldName);
                    return -1L;
                }
            }

            PermissionSetType.Type.valueOf(type).getHandler();
            String permissionSetLinkName = permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.PERMISSION_SET_LINK_NAME).getText();
            PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(permissionSetLinkName);
            Long permissionSetId = permissionSet != null ? permissionSet.getId() : -1L;

            permissionSetConfigMap.put("permission", Boolean.valueOf(permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.PERMISSION).getText()));
            permissionSetConfigMap.put("permissionType", permissionSetConfigComponentElement.getElement(PackageConstants.PermissionSetConfig.PERMISSION_TYPE).getText());
            permissionSetConfigMap.put("permissionSetId", permissionSetId);
            permissionSetConfigMap.put("type", type);

            if(MapUtils.isNotEmpty(permissionSetConfigMap)){
                Map<String, Long> insertRecordId = new HashMap<>();
                permissionSetBean.addPermissionsForPermissionSet(permissionSetConfigMap, insertRecordId);
                return insertRecordId.get("ModuleTypePermissionSet");
            }
        }
        LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since module is null for - "+moduleName);
        return -1L;
    }

}
