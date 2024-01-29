package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.*;
import com.facilio.permission.context.module.FieldPermissionSet;
import com.facilio.permission.context.module.RelatedListPermissionSet;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.permission.handlers.group.PermissionSetGroupHandler;
import com.facilio.permission.handlers.group.RelatedRecordsPermissionSetHandler;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class PermissionSetPackageBeanImpl implements PackageBean<PermissionSetSandboxContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getPermissionSetComponent(true, true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getPermissionSetComponent(false, false);
    }

    @Override
    public Map<Long, PermissionSetSandboxContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, PermissionSetSandboxContext> permissionSetIdsVsContext = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        Map<Long, List<BasePermissionContext>> basePermissionContext = getAllBasePermissionContext(ids);
        Map<Long, List<Long>> peoplePermissionSetMapping = getAllPeoplePermissionSet(ids);

        for (Long id : ids) {
            PermissionSetSandboxContext permissionSetSandboxContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(permissionSetBean.getPermissionSet(id)), PermissionSetSandboxContext.class);
            permissionSetSandboxContext.setBasePermissionContext(basePermissionContext.get(id));
            permissionSetSandboxContext.setPeopleIds(peoplePermissionSetMapping.get(id));
            if (permissionSetSandboxContext != null) {
                permissionSetIdsVsContext.put(id, permissionSetSandboxContext);
            }
        }
        return permissionSetIdsVsContext;
    }

    @Override
    public void convertToXMLComponent(PermissionSetSandboxContext component, XMLBuilder permissionSetElement) throws Exception {
        permissionSetElement.element(PackageConstants.PermissionSetConstants.LINK_NAME).text(component.getLinkName());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.DISPLAY_NAME).text(component.getDisplayName());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.DESCRIPTION).text(component.getDescription());
        permissionSetElement.element(PackageConstants.PermissionSetConstants.STATUS).text(String.valueOf(component.getStatus()));
        permissionSetElement.element(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).text(String.valueOf(component.isPrivileged()));
        permissionSetElement.element(PackageConstants.PermissionSetConstants.IS_DELETED).text(String.valueOf(BooleanUtils.isTrue(component.getDeleted())));
        permissionSetElement.addElement(getBasePermissionContextXMLBuilder(component.getBasePermissionContext(), permissionSetElement));
        permissionSetElement.addElement(getPeoplePermissionXMLBuilder(component.getPeopleIds(), permissionSetElement));
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> permissionSetComponentIds) throws Exception {
        List<Long> deletedPermissionSetComponentIds = new ArrayList<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1, -1, null, false);

        existingPermissionSetList.forEach(set -> {
            if (set.getSysDeletedTime() > 0 || set.getSysDeletedTime() != -1) {
                deletedPermissionSetComponentIds.add(set.getId());
            }
        });

        return deletedPermissionSetComponentIds;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        Map<String, Long> uniqueIdentifierVsPermissionSetId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder permissionSetComponentElement = idVsData.getValue();
            String linkName = permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText();
            boolean isPriviliged = Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());

            if (isPriviliged) {
                PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(linkName);
                if (permissionSet != null && permissionSet.getId() > 0) {
                    uniqueIdentifierVsPermissionSetId.put(uniqueIdentifier, permissionSet.getId());
                } else {
                    LOGGER.info("###Sandbox - PermissionSet with linkName not found - " + linkName);
                }
            }
        }
        return uniqueIdentifierVsPermissionSetId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsPermissionIds = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder permissionSetComponentElement = idVsData.getValue();
            PermissionSetContext permissionSetContext = new PermissionSetContext();
            String linkName = permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText();
            boolean isPriviliged = Boolean.parseBoolean(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());

            if (permissionSetComponentElement != null) {
                permissionSetContext.setDisplayName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DISPLAY_NAME).getText());
                permissionSetContext.setDescription(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DESCRIPTION).getText());
                permissionSetContext.setStatus(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.STATUS).getText()));
                permissionSetContext.setLinkName(linkName);
                permissionSetContext.setSysCreatedBy(Objects.requireNonNull(AccountUtil.getCurrentUser()).getId());
                permissionSetContext.setIsPrivileged(isPriviliged);
                permissionSetContext.setDeleted(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_DELETED).getText()));

                Long newPermissionSetId = permissionSetBean.addPermissionSet(permissionSetContext);

                addOrUpdatePermissionSetConfig(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.PERMISSION_SET_CONFIGS));
                addPeoplePermissionSetConfig(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.PEOPLE_PERMISSION_SET_CONFIGS), linkName);

                uniqueIdentifierVsPermissionIds.put(idVsData.getKey(), newPermissionSetId);
            }
        }
        return uniqueIdentifierVsPermissionIds;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder permissionSetComponentElement = idVsComponent.getValue();
            PermissionSetContext permissionSetContext = new PermissionSetContext();
            boolean isPriviliged = Boolean.parseBoolean(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_PRIVILEGED).getText());
            String linkName = permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.LINK_NAME).getText();
            PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(linkName);

            if (permissionSetComponentElement != null && isPriviliged) {
                permissionSetContext.setDisplayName(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DISPLAY_NAME).getText());
                permissionSetContext.setDescription(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.DESCRIPTION).getText());
                permissionSetContext.setStatus(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.STATUS).getText()));
                permissionSetContext.setLinkName(linkName);
                permissionSetContext.setIsPrivileged(isPriviliged);
                permissionSetContext.setDeleted(Boolean.valueOf(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.IS_DELETED).getText()));
                if (permissionSet != null && permissionSet.getId() > 0) {
                    permissionSetContext.setId(permissionSet.getId());
                } else {
                    LOGGER.info("####Sandbox - Skipping update PermissionSet since permissionSetId null for permissionSetLinkName - " + linkName);
                    continue;
                }

                addOrUpdatePermissionSetConfig(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.PERMISSION_SET_CONFIGS));
                addPeoplePermissionSetConfig(permissionSetComponentElement.getElement(PackageConstants.PermissionSetConstants.PEOPLE_PERMISSION_SET_CONFIGS), linkName);

                permissionSetBean.updatePermissionSet(permissionSetContext);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        for (Long permissionSetId : ids) {
            permissionSetBean.deletePermissionSet(permissionSetId);
        }

    }

    private Map<Long, Long> getPermissionSetComponent(boolean getSystemPrivilegedSet, boolean fetchDeleted) throws Exception {
        String systemPrivilegedLinkName = "privilegedpermissionset";
        Map<Long, Long> permissionSetIdsMap = new HashMap<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1, -1, null, fetchDeleted);
        existingPermissionSetList.forEach(permissionSet -> {
            if (!(getSystemPrivilegedSet ^ systemPrivilegedLinkName.equals(permissionSet.getLinkName()))) {
                permissionSetIdsMap.put(permissionSet.getId(), -1l);
            }
        });

        return permissionSetIdsMap;
    }

    private Map<Long, List<BasePermissionContext>> getAllBasePermissionContext(List<Long> permissionSetIds) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        FacilioModule moduleTypePermissionSetModule = PermissionSetModuleFactory.getModuleTypePermissionSetModule();
        FacilioField PermissionSetField = FieldFactory.getNumberField("permissionSetId", "PERMISSION_SET_ID", moduleTypePermissionSetModule);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getModuleTypePermissionSetFields())
                .table(moduleTypePermissionSetModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(PermissionSetField, permissionSetIds, NumberOperators.EQUALS));


        List<Map<String, Object>> props = selectRecordBuilder.get();
        List<Triple<Long, Long, String>> moduleConfigs = props.stream()
                .map(prop -> Triple.of((Long) prop.get("moduleId"), (Long) prop.get("permissionSetId"), (String) prop.get("permissionType")))
                .distinct()
                .collect(Collectors.toList());

        List<BasePermissionContext> permissionConfigForModuleVsPermissionSet = new ArrayList<>();
        for (Triple<Long, Long, String> moduleConfig : moduleConfigs) {
            Long moduleId = moduleConfig.getLeft();
            Long permissionSetId = moduleConfig.getMiddle();
            String groupType = moduleConfig.getRight();
            for (PermissionSetType.Type permissionGroupType : PermissionSetType.Type.values()) {
                if (permissionGroupType.getPermissionFieldEnumList().contains(PermissionFieldEnum.valueOf(groupType.toUpperCase()))) {
                    groupType = permissionGroupType.name();
                    break;
                }
            }

            PermissionSetGroupHandler handler = PermissionSetType.Type.valueOf(groupType.toUpperCase()).getHandler();
            List<BasePermissionContext> permissionLists = handler.getPermissions(moduleId);
            FacilioModule module = Constants.getModBean().getModule(moduleId);
            if (module != null && StringUtils.isNotEmpty(module.getName()) && handler instanceof RelatedRecordsPermissionSetHandler) {
                switch (module.getName()) {
                    case FacilioConstants.ContextNames.WORK_ORDER: {
                        RelatedListPermissionSet permissionSet = new RelatedListPermissionSet(moduleId, moduleId, Constants.getModBean().getField("parentWO", "workorder").getFieldId(), "Dependant Workorders");
                        permissionLists.add(permissionSet);
                        break;
                    }
                }
            }

            permissionConfigForModuleVsPermissionSet.addAll(permissionSetBean.readPermissionValuesForPermissionSetType(PermissionSetType.Type.valueOf(groupType.toUpperCase()), permissionSetId, permissionLists));
        }
        return permissionConfigForModuleVsPermissionSet.stream().collect(Collectors.groupingBy(BasePermissionContext::getPermissionSetId));
    }

    private Map<Long, List<Long>> getAllPeoplePermissionSet(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getUserPermissionSetsFields())
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getConditionFromList("PERMISSION_SET_ID", "permissionSetId", ids, NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordBuilder.get();

        return props.stream().collect(Collectors.groupingBy(
                map -> (Long) map.get("permissionSetId"),
                Collectors.mapping(map -> (Long) map.get("peopleId"), Collectors.toList())));
    }

    private XMLBuilder getBasePermissionContextXMLBuilder(List<BasePermissionContext> basePermissionComponents, XMLBuilder permissionSetElement) throws Exception {
        XMLBuilder permissionSetConfigsList = permissionSetElement.element(PackageConstants.PermissionSetConstants.PERMISSION_SET_CONFIGS);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        if (CollectionUtils.isNotEmpty(basePermissionComponents)) {
            for (BasePermissionContext basePermissionComponent : basePermissionComponents) {
                if (basePermissionComponent != null) {
                    XMLBuilder permissionSetConfig = permissionSetConfigsList.element(PackageConstants.PermissionSetConfig.PERMISSION_SET_CONFIG);

                    permissionSetConfig.element(PackageConstants.PermissionSetConfig.PERMISSION_TYPE).text(basePermissionComponent.getPermissionType().getValue());
                    permissionSetConfig.element(PackageConstants.PermissionSetConfig.PERMISSION).text(String.valueOf(basePermissionComponent.getPermission()));
                    permissionSetConfig.element(PackageConstants.PermissionSetConfig.TYPE).text(basePermissionComponent.getType().name());

                    Long permissionSetId = basePermissionComponent.getPermissionSetId();
                    String permissionSetLinkName = permissionSetBean.getPermissionSet(permissionSetId).getLinkName();
                    permissionSetConfig.element(PackageConstants.PermissionSetConfig.PERMISSION_SET_LINK_NAME).text(permissionSetLinkName);

                    if (basePermissionComponent instanceof RelatedListPermissionSet) {
                        RelatedListPermissionSet relatedListPermissionComponent = (RelatedListPermissionSet) basePermissionComponent;
                        Long moduleId = relatedListPermissionComponent.getModuleId();
                        FacilioModule module = Constants.getModBean().getModule(moduleId);
                        String moduleName = module != null ? module.getName() : "";

                        Long relatedModuleId = relatedListPermissionComponent.getRelatedModuleId();
                        FacilioModule relatedModule = Constants.getModBean().getModule(relatedModuleId);
                        String relatedModuleName = relatedModule != null ? relatedModule.getName() : "";

                        Long relatedFieldId = relatedListPermissionComponent.getRelatedFieldId();
                        FacilioField relatedField = Constants.getModBean().getField(relatedFieldId);
                        String relatedFieldName = relatedField != null ? relatedField.getName() : "";

                        permissionSetConfig.element(PackageConstants.PermissionSetConfig.MODULE_NAME).text(moduleName);
                        permissionSetConfig.element(PackageConstants.PermissionSetConfig.RELATED_MODULE_NAME).text(relatedModuleName);
                        permissionSetConfig.element(PackageConstants.PermissionSetConfig.RELATED_FIELD_NAME).text(relatedFieldName);
                    }

                    if (basePermissionComponent instanceof FieldPermissionSet) {
                        FieldPermissionSet fieldPermissionComponent = (FieldPermissionSet) basePermissionComponent;
                        Long moduleId = fieldPermissionComponent.getModuleId();
                        FacilioModule relatedModule = Constants.getModBean().getModule(moduleId);
                        String moduleName = relatedModule != null ? relatedModule.getName() : "";

                        Long fieldId = fieldPermissionComponent.getFieldId();
                        FacilioField field = Constants.getModBean().getField(fieldId);
                        String fieldName = field != null ? field.getName() : "";

                        permissionSetConfig.element(PackageConstants.PermissionSetConfig.MODULE_NAME).text(moduleName);
                        permissionSetConfig.element(PackageConstants.PermissionSetConfig.FIELD_NAME).text(fieldName);
                    }
                }
            }
        }
        return permissionSetConfigsList;
    }

    private XMLBuilder getPeoplePermissionXMLBuilder(List<Long> peopleIds, XMLBuilder permissionSetElement) throws Exception {
        XMLBuilder peoplePermissionSetConfigsList = permissionSetElement.element(PackageConstants.PermissionSetConstants.PEOPLE_PERMISSION_SET_CONFIGS);

        if (CollectionUtils.isNotEmpty(peopleIds)) {
            for (Long peopleId : peopleIds) {
                if (peopleId > 0) {
                    XMLBuilder peoplePermissionSetConfig = peoplePermissionSetConfigsList.element(PackageConstants.PeoplePermissionSetConfig.PEOPLE_PERMISSION_SET_CONFIG);

                    String peopleMail = PackageUtil.getPeopleMail(peopleId);
                    peopleMail = StringUtils.isNotEmpty(peopleMail) ? peopleMail : "";

                    peoplePermissionSetConfig.element(PackageConstants.PeoplePermissionSetConfig.PEOPLE_MAIL).text(peopleMail);
                }
            }
        }
        return peoplePermissionSetConfigsList;
    }

    private void addOrUpdatePermissionSetConfig(XMLBuilder permissionSetConfigList) throws Exception {
        List<XMLBuilder> PermissionSetConfigs = permissionSetConfigList.getFirstLevelElementListForTagName(PackageConstants.PermissionSetConfig.PERMISSION_SET_CONFIG);
        if (CollectionUtils.isNotEmpty(PermissionSetConfigs)) {
            PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
            for (XMLBuilder PermissionSetConfig : PermissionSetConfigs) {
                Map<String, Object> permissionSetConfigMap = new HashMap<>();
                String moduleName = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.MODULE_NAME).getText();
                FacilioModule module = Constants.getModBean().getModule(moduleName);

                if (module == null || module.getModuleId() <= 0) {
                    LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since module is null for - " + moduleName);
                    continue;
                }
                Long moduleId = module.getModuleId();
                permissionSetConfigMap.put("moduleId", moduleId);
                String type = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.TYPE).getText();

                if (type.equals("FIELD_LIST")) {
                    String fieldName = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.FIELD_NAME).getText();
                    FacilioField field = Constants.getModBean().getField(fieldName, module.getName());
                    Long fieldId = field != null ? field.getFieldId() : -1L;

                    if (fieldId == null || fieldId <= 0) {
                        LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since Field is null for - " + fieldName + " of module - " + moduleName);
                        continue;
                    }

                    permissionSetConfigMap.put("fieldId", fieldId);
                } else if (type.equals("RELATED_LIST")) {
                    String relatedModuleName = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.RELATED_MODULE_NAME).getText();
                    String relatedFieldName = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.RELATED_FIELD_NAME).getText();

                    if (Constants.getModBean().getModule(relatedModuleName) == null) {
                        LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since relatedModule is null for relatedModuleName - " + relatedModuleName);
                        continue;
                    }

                    FacilioField relatedField = Constants.getModBean().getField(relatedFieldName, relatedModuleName);
                    Long relatedFieldId = relatedField != null ? relatedField.getId() : -1L;
                    Long relatedModuleId = relatedField != null ? relatedField.getModuleId() : -1L;

                    if (relatedModuleId <= 0 || relatedFieldId <= 0) {
                        LOGGER.info("####Sandbox - Skipping add or update PermissionSetConfig since relatedModuleId or relatedFieldId is null for relatedModule - " + relatedModuleName + " relatedField - " + relatedFieldName);
                        continue;
                    }

                    permissionSetConfigMap.put("relatedModuleId", relatedModuleId);
                    permissionSetConfigMap.put("relatedFieldId", relatedFieldId);
                }

                String permissionSetLinkName = PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.PERMISSION_SET_LINK_NAME).getText();
                PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(permissionSetLinkName);
                Long permissionSetId = permissionSet != null ? permissionSet.getId() : -1L;

                permissionSetConfigMap.put("permission", Boolean.valueOf(PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.PERMISSION).getText()));
                permissionSetConfigMap.put("permissionType", PermissionSetConfig.getElement(PackageConstants.PermissionSetConfig.PERMISSION_TYPE).getText());
                permissionSetConfigMap.put("permissionSetId", permissionSetId);
                permissionSetConfigMap.put("type", type);

                if (MapUtils.isNotEmpty(permissionSetConfigMap)) {
                    permissionSetBean.addPermissionsForPermissionSet(permissionSetConfigMap);
                }
            }
        }
    }

    private void addPeoplePermissionSetConfig(XMLBuilder PeoplePermissionSetConfigList, String PermissionSetLinkName) throws Exception {
        List<XMLBuilder> PermissionSetConfigs = PeoplePermissionSetConfigList.getFirstLevelElementListForTagName(PackageConstants.PeoplePermissionSetConfig.PEOPLE_PERMISSION_SET_CONFIG);
        List<Map<String, Object>> propsList = new ArrayList<>();
        List<Long> peopleIds = new ArrayList<>();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        Long newPermissionSetId = permissionSetBean.getPermissionSet(PermissionSetLinkName).getId();

        if (newPermissionSetId <= 0) {
            LOGGER.info("####Sandbox - Skipping add PeoplePermissionSetConfig since permissionSetId is null for PermissionSetLinkName - " + PermissionSetLinkName);
            return;
        }
        for (XMLBuilder PermissionSetConfig : PermissionSetConfigs) {
            String peopleMail = PermissionSetConfig.getElement(PackageConstants.PeoplePermissionSetConfig.PEOPLE_MAIL).getText();
            Long peopleId = PackageUtil.getPeopleId(peopleMail);

            if (peopleId < 0) {
                LOGGER.info("####Sandbox - Skipping add PeoplePermissionSetConfig since peopleId or permissionSetId is null for peopleMail - " + peopleMail);
                continue;
            }
            peopleIds.add(peopleId);
            Map<String, Object> prop = new HashMap<>();
            prop.put("peopleId", peopleId);
            prop.put("permissionSetId", newPermissionSetId);
            propsList.add(prop);
        }

        if (CollectionUtils.isNotEmpty(peopleIds)) {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                    .andCondition(CriteriaAPI.getConditionFromList("PEOPLE_ID", "peopleId", peopleIds, NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("PERMISSION_SET_ID", "permissionSetId", String.valueOf(newPermissionSetId), NumberOperators.EQUALS));
            deleteRecordBuilder.delete();

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                    .fields(PermissionSetFieldFactory.getUserPermissionSetsFields());
            insertBuilder.addRecords(propsList);
            insertBuilder.save();
        }
    }

}
