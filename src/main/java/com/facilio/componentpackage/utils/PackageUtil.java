package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Log4j
public class PackageUtil {

    private static ThreadLocal<Boolean> isInstallThread = new ThreadLocal<>();
    private static ThreadLocal<Map<String, Map<String, String>>> PICKLIST_CONF_FOR_XML = ThreadLocal.withInitial(HashMap::new);     // PickList ModuleName Vs Id vs RecordName
    private static ThreadLocal<Map<String, Map<String, String>>> PICKLIST_CONF_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new); // PickList ModuleName Vs RecordName vs Id
    private static ThreadLocal<Map<String, Map<String, String>>> TICKET_STATUS_CONF_FOR_XML = ThreadLocal.withInitial(HashMap::new);     // ParentModuleName Vs Id vs Status
    private static ThreadLocal<Map<String, Map<String, String>>> TICKET_STATUS_CONF_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new); // ParentModuleName Vs Status vs Id
    private static ThreadLocal<Map<ComponentType, Map<String, Long>>> COMPONENTS_UID_VS_COMPONENT_ID = ThreadLocal.withInitial(HashMap::new);

    public static void setInstallThread() throws Exception {
        isInstallThread.set(true);
    }

    public static Boolean isInstallThread() throws Exception {
        return isInstallThread.get();
    }

    public static void clearInstallThread() throws Exception {
        isInstallThread.remove();
        COMPONENTS_UID_VS_COMPONENT_ID.remove();
    }

    public static Map<ComponentType, Map<String, Long>> getComponentsUIdVsComponentId() {
        return COMPONENTS_UID_VS_COMPONENT_ID.get();
    }

    public static Map<String, Long> getComponentsUIdVsComponentIdForComponent(ComponentType componentType) {
        return getComponentsUIdVsComponentId().computeIfAbsent(componentType, k -> new HashMap<>());
    }

    public static void addComponentsUIdVsComponentId(ComponentType componentType, Map<String, Long> uniqueIdVsComponentId) {
        Map<String, Long> currComponentsUniqueIdVsComponentId = getComponentsUIdVsComponentIdForComponent(componentType);
        currComponentsUniqueIdVsComponentId.putAll(uniqueIdVsComponentId);
    }

    public static long getParentComponentId(ComponentType childComponentType, String uniqueIdentifier) {
        Long parentId = null;
        ComponentType parentComponentType = childComponentType.getParentComponentType();
        if (parentComponentType != null) {
            parentId = getComponentsUIdVsComponentIdForComponent(parentComponentType).get(uniqueIdentifier);
        }
        return parentId != null ? parentId : -1;
    }

    public static Map<String, String> getRecordIdVsNameForPicklistModule(String moduleName) {
        return PICKLIST_CONF_FOR_XML.get().computeIfAbsent(moduleName, k -> new HashMap<>());
    }

    public static void addRecordIdVsNameForPickListModule(String moduleName, Map<String, String> records) {
        Map<String, String> recordIdVsNameForPicklistModule = getRecordIdVsNameForPicklistModule(moduleName);
        recordIdVsNameForPicklistModule.putAll(records);
    }

    public static Map<String, String> getNameVsRecordIdForPicklistModule(String moduleName) {
        return PICKLIST_CONF_FOR_CONTEXT.get().computeIfAbsent(moduleName, k -> new HashMap<>());
    }

    public static void addNameVsRecordIdForPickListModule(String moduleName, Map<String, String> records) {
        Map<String, String> nameVsRecordIdForPicklistModule = getNameVsRecordIdForPicklistModule(moduleName);
        nameVsRecordIdForPicklistModule.putAll(records);
    }

    public static Map<String, String> getTicketStatusIdVsNameForModule(String moduleName) {
        return TICKET_STATUS_CONF_FOR_XML.get().computeIfAbsent(moduleName, k -> new HashMap<>());
    }

    public static void addTicketStatusIdVsNameForModule(String moduleName, Map<String, String> records) {
        Map<String, String> ticketStatusIdVsNameForModule = getTicketStatusIdVsNameForModule(moduleName);
        ticketStatusIdVsNameForModule.putAll(records);
    }
    
    public static Map<String, String> getTicketStatusNameVsIdForModule(String moduleName) {
        return TICKET_STATUS_CONF_FOR_CONTEXT.get().computeIfAbsent(moduleName, k -> new HashMap<>());
    }

    public static void addTicketStatusNameVsIdForModule(String moduleName, Map<String, String> records) {
        Map<String, String> ticketStatusNameVsIdForModule = getTicketStatusNameVsIdForModule(moduleName);
        ticketStatusNameVsIdForModule.putAll(records);
    }

    public static void clearPickListConf() throws Exception {
        PICKLIST_CONF_FOR_XML.remove();
        PICKLIST_CONF_FOR_CONTEXT.remove();
        TICKET_STATUS_CONF_FOR_XML.remove();
        TICKET_STATUS_CONF_FOR_CONTEXT.remove();
    }

    public static PackageContext getPackage(Organization organization) throws Exception {
        PackageContext packageContext = null;

        long orgId = organization.getOrgId();
        int packageType = organization.getOrgType();

        List<FacilioField> fields = FieldFactory.getPackageFields();
        FacilioModule packageModule = ModuleFactory.getPackageModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(packageModule.getTableName())
                .andCondition(CriteriaAPI.getOrgIdCondition(orgId, packageModule))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(packageType), NumberOperators.EQUALS));

        Map<String, Object> prop = selectRecordBuilder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            packageContext = FieldUtil.getAsBeanFromMap(prop, PackageContext.class);
        }

        return packageContext;
    }

    public static PackageContext constructPackageContext(String packageName, String packageDisplayName, PackageContext.PackageType packageType, double version, long createdUserId) {
        PackageContext packageContext = new PackageContext();
        packageContext.setUniqueName(packageName);
        packageContext.setDisplayName(packageDisplayName);
        packageContext.setType(packageType);
        packageContext.setVersion(version);
        packageContext.setSysCreatedTime(System.currentTimeMillis());
        packageContext.setSysModifiedTime(packageContext.getSysCreatedTime());
        packageContext.setSysCreatedBy(createdUserId);

        return packageContext;
    }
    public static PackageContext createPackage(String packageName, String packageDisplayName, PackageContext.PackageType packageType, double version, long createdUser) throws Exception{
        PackageContext packageContext = constructPackageContext(packageName, packageDisplayName, packageType, version, createdUser);
        List<FacilioField> fields = FieldFactory.getPackageFields();
        FacilioModule packageModule = ModuleFactory.getPackageModule();
        Map<String, Object> packageProps = FieldUtil.getAsProperties(packageContext);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(fields)
                .addRecord(packageProps)
                .table(packageModule.getTableName());

        insertRecordBuilder.save();

        return FieldUtil.getAsBeanFromMap(packageProps, PackageContext.class);
    }

    public static void updatePackageContext(PackageContext packageContext) throws Exception{
        List<FacilioField> fields = FieldFactory.getPackageFields();
        FacilioModule packageModule = ModuleFactory.getPackageModule();
        Map<String, Object> packageProps = FieldUtil.getAsProperties(packageContext);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(packageModule.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(packageContext.getId(), packageModule));

        updateRecordBuilder.update(packageProps);
    }

    public static Map<ComponentType, List<PackageChangeSetMappingContext>> getAllPackageChangsets(long packageId) throws Exception {
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        List<FacilioField> fields = FieldFactory.getPackageChangesetsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(changeSetModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("packageId"), String.valueOf(packageId), NumberOperators.EQUALS))
                .orderBy(ComponentType.ORDER_BY_COMPONENT_TYPE_STR + ", COMPONENT_STATUS");

        List<Map<String, Object>> prop = selectRecordBuilder.get();
        Map<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponents = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(prop)) {
            List<PackageChangeSetMappingContext> changesets = FieldUtil.getAsBeanListFromMapList(prop, PackageChangeSetMappingContext.class);
            for (PackageChangeSetMappingContext componentMapping : changesets) {
                List<PackageChangeSetMappingContext> componentList;
                if(typeVsComponents.containsKey(componentMapping.getComponentTypeEnum())) {
                    componentList = typeVsComponents.get(componentMapping.getComponentTypeEnum());
                } else {
                    componentList = new ArrayList<>();
                    typeVsComponents.put(componentMapping.getComponentTypeEnum(), componentList);
                }
                componentList.add(componentMapping);
            }
        }

        return typeVsComponents;
    }

    public static PackageChangeSetMappingContext getPackageMappingChangeSet(long packageId, String uniqueIdentifier, long componentId, ComponentType componentType) throws Exception {
        PackageChangeSetMappingContext changeSetContext = null;

        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        List<FacilioField> fields = FieldFactory.getPackageChangesetsFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(changeSetModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("packageId"), String.valueOf(packageId), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(uniqueIdentifier)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("uniqueIdentifier"), uniqueIdentifier, StringOperators.IS));
        }

        if (componentId > 0 && StringUtils.isNotEmpty(componentType.getValue())) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("componentType"), componentType.getValue(), StringOperators.IS));
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("componentId"), String.valueOf(componentId), NumberOperators.EQUALS));
        }

        Map<String, Object> prop = selectRecordBuilder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            changeSetContext = FieldUtil.getAsBeanFromMap(prop, PackageChangeSetMappingContext.class);
        }

        return changeSetContext;
    }

    public static void addOrUpdatePackageChangeSet(long componentId, ComponentType componentType, PackageChangeSetMappingContext.ComponentStatus status) throws Exception {
        if(PackageUtil.isInstallThread()) {
            return;
        }
        Organization organization = AccountUtil.getCurrentOrg();
        FacilioUtil.throwIllegalArgumentException(organization == null, "Organization object cannot be null");

        PackageContext packageContext = getPackage(organization);
        FacilioUtil.throwIllegalArgumentException(packageContext == null,
                String.format("No Package found for OrgId - %d, OrgType - %s", organization.getOrgId(), organization.getDataTypeEnum()));

        PackageChangeSetMappingContext changeSetContext;
        if (status == PackageChangeSetMappingContext.ComponentStatus.ADDED) {
            changeSetContext = constructPackageChangeSetInsertProp(packageContext, organization.getOrgId(), componentId, componentType);
            addPackageMappingChangeSet(changeSetContext);
        } else {
            PackageChangeSetMappingContext oldChangeSetContext = getPackageMappingChangeSet(packageContext.getId(), null, componentId, componentType);
            FacilioUtil.throwIllegalArgumentException(oldChangeSetContext == null,
                    String.format("No ChangeSet Mapping found for PackageId - %d, ComponentId - %d, ComponentType - %s", packageContext.getId(), componentId, componentType.getValue()));

            changeSetContext = constructPackageChangeSetUpdateProp(packageContext, oldChangeSetContext, PackageChangeSetMappingContext.ComponentStatus.UPDATED, null);
            updatePackageMappingChangeSet(changeSetContext);
        }
    }

    public static void deletePackageChangeSet(long componentId, ComponentType componentType, PackageChangeSetMappingContext.ComponentStatus status, String componentDisplayName) throws Exception {
        if(PackageUtil.isInstallThread()) {
            return;
        }
        Organization organization = AccountUtil.getCurrentOrg();
        FacilioUtil.throwIllegalArgumentException(organization == null, "Organization object cannot be null");

        PackageContext packageContext = getPackage(organization);
        FacilioUtil.throwIllegalArgumentException(packageContext == null,
                String.format("No Package found for OrgId - %d, OrgType - %s", organization.getOrgId(), organization.getDataTypeEnum()));

        PackageChangeSetMappingContext oldChangeSetContext = getPackageMappingChangeSet(packageContext.getId(), null, componentId, componentType);
        FacilioUtil.throwIllegalArgumentException(oldChangeSetContext == null,
                String.format("No ChangeSet Mapping found for PackageId - %d, ComponentId - %d, ComponentType - %s", packageContext.getId(), componentId, componentType.getValue()));

        PackageChangeSetMappingContext changeSetContext = constructPackageChangeSetUpdateProp(packageContext, oldChangeSetContext, status, componentDisplayName);
        updatePackageMappingChangeSet(changeSetContext);
    }

    public static PackageChangeSetMappingContext constructPackageChangeSetInsertProp(PackageContext packageContext, long orgId, long componentId, ComponentType componentType) throws Exception {
        PackageChangeSetMappingContext changeSetContext = new PackageChangeSetMappingContext();
        changeSetContext.setOrgId(orgId);
        changeSetContext.setComponentId(componentId);
        changeSetContext.setComponentType(componentType);
        changeSetContext.setPackageId(packageContext.getId());
        changeSetContext.setUniqueIdentifier(String.valueOf(componentId));
        changeSetContext.setStatus(PackageChangeSetMappingContext.ComponentStatus.ADDED);

        Double createdVersion = getPackageChangeSetVersion(PackageChangeSetMappingContext.ComponentStatus.ADDED, packageContext, null);
        changeSetContext.setCreatedVersion(createdVersion);

        return changeSetContext;
    }

    public static PackageChangeSetMappingContext constructPackageChangeSetUpdateProp(PackageContext packageContext,
                                                                                     PackageChangeSetMappingContext oldChangeSetContext,
                                                                                     PackageChangeSetMappingContext.ComponentStatus status,
                                                                                     String componentDisplayName) {
        Double modifiedVersion = getPackageChangeSetVersion(status, packageContext, oldChangeSetContext);

        PackageChangeSetMappingContext newChangeSetContext = new PackageChangeSetMappingContext();
        newChangeSetContext.setStatus(status);
        newChangeSetContext.setId(oldChangeSetContext.getId());
        newChangeSetContext.setModifiedVersion(modifiedVersion);
        newChangeSetContext.setComponentDisplayName(componentDisplayName);
        newChangeSetContext.setComponentLastEditedTime(System.currentTimeMillis());

        return newChangeSetContext;
    }

    public static void addPackageMappingChangeSet(PackageChangeSetMappingContext changeSetContext) throws Exception {
        addPackageMappingChangesets(Collections.singletonList(changeSetContext));
    }

    public static void addPackageMappingChangesets(List<PackageChangeSetMappingContext> changeSetContexts) throws Exception {
        List<FacilioField> fields = FieldFactory.getPackageChangesetsFields();
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        List<Map<String, Object>> changeSetProps = FieldUtil.getAsMapList(changeSetContexts, PackageChangeSetMappingContext.class);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(fields)
                .addRecords(changeSetProps)
                .table(changeSetModule.getTableName());

        insertRecordBuilder.save();
    }

    public static int updatePackageMappingChangesets(List<PackageChangeSetMappingContext> changeSetContexts) throws Exception {
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        List<FacilioField> updatableFields = FieldFactory.getPackageChangesetsUpdatableFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPackageChangesetsFields());
        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(fieldsMap.get("packageId"));
        whereFields.add(fieldsMap.get("componentType"));
        whereFields.add(fieldsMap.get("uniqueIdentifier"));

        List<Map<String, Object>> props = FieldUtil.getAsMapList(changeSetContexts, PackageChangeSetMappingContext.class);
        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        for (Map<String, Object> prop: props) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addWhereValue("packageId",prop.get("packageId"));
            updateVal.addWhereValue("componentType", prop.get("componentType"));
            updateVal.addWhereValue("uniqueIdentifier", prop.get("uniqueIdentifier"));
            updateVal.setUpdateValue(prop);
            batchUpdateList.add(updateVal);
        }

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(changeSetModule.getTableName())
                .fields(updatableFields)
                ;

        return updateBuilder.batchUpdate(whereFields, batchUpdateList);
    }

    public static int updatePackageMappingChangeSet(PackageChangeSetMappingContext changeSetContext) throws Exception {
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        Map<String, Object> changeSetProps = FieldUtil.getAsProperties(changeSetContext);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getPackageChangesetsUpdatableFields())
                .table(changeSetModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(changeSetContext.getId(), changeSetModule));

        return updateRecordBuilder.update(changeSetProps);
    }

    public static void deletePackageMappingChangeSet() throws Exception {

    }

    public static Double getPackageChangeSetVersion(PackageChangeSetMappingContext.ComponentStatus status, PackageContext packageContext, PackageChangeSetMappingContext changeSetContext) {
        Double oldVersion;
        Double newVersion = null;

        switch (status) {
            case ADDED:
                newVersion = packageContext.getVersion() + 0.1;
                break;

            case UPDATED:
            case DELETED:
                oldVersion = changeSetContext.getModifiedVersion();
                newVersion = oldVersion != null ? oldVersion + 0.1 : packageContext.getVersion() + 0.2;
                break;

            default:
                break;
        }

        return newVersion;
    }

    public static PackageContext getPackageByName(String uniqueName, PackageContext.PackageType packageType) throws Exception{
        PackageContext packageContext = null;

        List<FacilioField> fields = FieldFactory.getPackageFields();
        FacilioModule packageModule = ModuleFactory.getPackageModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(packageModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(packageType.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("uniqueName"), uniqueName, StringOperators.IS));

        Map<String, Object> prop = selectRecordBuilder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            packageContext = FieldUtil.getAsBeanFromMap(prop, PackageContext.class);
        }

        return packageContext;
    }

    public static void createBulkChangesetMapping(long packageId, Map<Long, Long> componentIdVsParentId, double version, ComponentType type, Boolean isSystemComponent) throws Exception{
        if(MapUtils.isEmpty(componentIdVsParentId)) {
            return;
        }
        PackageChangeSetMappingContext.ComponentStatus status;
        double modifiedVersion = 0.0;
        if(!isSystemComponent) {
            status = PackageChangeSetMappingContext.ComponentStatus.ADDED;
        } else {
            status = PackageChangeSetMappingContext.ComponentStatus.UPDATED;
            modifiedVersion = version;
        }
        List<PackageChangeSetMappingContext> packageChangesets = new ArrayList<>();
        for(Map.Entry<Long, Long> idVsUid : componentIdVsParentId.entrySet()) {
            PackageChangeSetMappingContext changeset = new PackageChangeSetMappingContext();
            changeset.setPackageId(packageId);
            changeset.setComponentId(idVsUid.getKey());
            changeset.setComponentType(type);
            changeset.setStatus(status);
            changeset.setCreatedVersion(version);
            changeset.setModifiedVersion(modifiedVersion);
            changeset.setUniqueIdentifier(String.valueOf(idVsUid.getKey()));
            changeset.setParentComponentId(idVsUid.getValue() != null ? idVsUid.getValue() : -1);

            packageChangesets.add(changeset);
        }

        List<FacilioField> fields = FieldFactory.getPackageChangesetsFields();
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        List<Map<String, Object>> changeSetProps = FieldUtil.getAsMapList(packageChangesets, PackageChangeSetMappingContext.class);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(changeSetModule.getTableName())
                .fields(fields)
                .addRecords(changeSetProps);

        insertRecordBuilder.save();
    }
}
