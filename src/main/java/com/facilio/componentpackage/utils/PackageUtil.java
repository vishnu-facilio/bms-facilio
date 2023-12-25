package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.UserInfo;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.ScheduledRuleJobsMetaUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApproverWorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class PackageUtil {

    private static ThreadLocal<Boolean> isInstallThread = new ThreadLocal<>();
    private static ThreadLocal<Boolean> userConfigFromAdminTool = new ThreadLocal<>();
    private static ThreadLocal<Map<String, Map<String, String>>> PICKLIST_CONF_FOR_XML = ThreadLocal.withInitial(HashMap::new);     // PickList ModuleName Vs Id vs RecordName
    private static ThreadLocal<Map<String, Map<String, String>>> PICKLIST_CONF_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new); // PickList ModuleName Vs RecordName vs Id
    private static ThreadLocal<Map<String, Map<String, String>>> TICKET_STATUS_CONF_FOR_XML = ThreadLocal.withInitial(HashMap::new);     // ParentModuleName Vs Id vs Status
    private static ThreadLocal<Map<String, Map<String, String>>> TICKET_STATUS_CONF_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new); // ParentModuleName Vs Status vs Id
    private static ThreadLocal<Map<ComponentType, Map<String, Long>>> COMPONENTS_UID_VS_COMPONENT_ID = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<Map<String, Map<String, Long>>> USER_CONFIG_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new);     // UserName vs Identifier vs OrgUserId
    private static ThreadLocal<Map<Long, UserInfo>> USER_CONFIG_FOR_XML = ThreadLocal.withInitial(HashMap::new);                    // OrgUserId vs UserInfo (UserName & Identifier)
    private static ThreadLocal<Map<String, Long>> PEOPLE_CONFIG_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new);                // PeopleEmail/Name vs PeopleId
    private static ThreadLocal<Map<Long, String>> PEOPLE_CONFIG_FOR_XML = ThreadLocal.withInitial(HashMap::new);                    // PeopleId vs PeopleEmail/Name
    private static ThreadLocal<Map<String, Long>> ROLE_CONFIG_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new);                  // RoleName vs RoleId
    private static ThreadLocal<Map<Long, String>> ROLE_CONFIG_FOR_XML = ThreadLocal.withInitial(HashMap::new);                      // RoleId vs RoleName
    private static ThreadLocal<Map<String, Long>> TEAMS_CONFIG_FOR_CONTEXT = ThreadLocal.withInitial(HashMap::new);                 // TeamName vs TeamId
    private static ThreadLocal<Map<Long, String>> TEAMS_CONFIG_FOR_XML = ThreadLocal.withInitial(HashMap::new);                     // TeamId vs TeamName
    private static ThreadLocal<Map<String, String>> PEOPLE_OLD_VS_NEW_MAIL = ThreadLocal.withInitial(HashMap::new);                 // People oldMail vs newMail from AdminTool
    private static ThreadLocal<Map<String, Map<Long, List<FacilioField>>>> ASSET_CATEGORY_ID_VS_READING_FIELDS = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<Map<String, Map<String, FileInfo>>> META_FILES_FOR_COMPONENTS = ThreadLocal.withInitial(HashMap::new);     // Filename vs Id vs File
    private static ThreadLocal<String> PACKAGE_ROOT_PATH = new ThreadLocal<>();         // Root folder path
    private static ThreadLocal<String> SANDBOX_BUCKET_NAME = new ThreadLocal<>();       // Sandbox Bucket Name (used for Cross DC cases)
    private static ThreadLocal<String> SANDBOX_BUCKET_REGION = new ThreadLocal<>();       // Sandbox Bucket Region (used for Cross DC cases)
    private static ThreadLocal<Long> PACKAGE_ID = new ThreadLocal<>();
    private static ThreadLocal<Long> DATA_MIGRATION_ID = new ThreadLocal<>();           // Data Migration Id

    public static void setPackageId(Long packageId) {
        PACKAGE_ID.set(packageId);
    }
    public static Long getPackageId() {
        return PACKAGE_ID.get();
    }

    public static void setDataMigrationId(Long dataMigrationId) {
        DATA_MIGRATION_ID.set(dataMigrationId);
    }
    public static Long getDataMigrationId() {
        return DATA_MIGRATION_ID.get();
    }

    public enum SandboxProcessLoadType {
        ORG_CREATION_PROCESS(20),
        CUSTOMIZATION_PACKAGE_PROCESS(30),
        CUSTOMIZATION_INSTALL_PROCESS(50);

        private int intVal;

        SandboxProcessLoadType(int intVal) {
            this.intVal = intVal;
        }

        public Integer getIntVal() {
            return intVal;
        }
    }
    public enum SandboxProgressCheckPointType {
        PACKAGE_CREATION_STARTED_ON_SETUP(0),
        PACKAGE_CREATION_STARTED_ON_RERUN(20),
        PACKAGE_CREATION_FINISHED_ON_SETUP(30),
        PACKAGE_CREATION_FINISHED_ON_RERUN(50),
        PACKAGE_CREATION_FAILED_ON_SETUP(0),
        PACKAGE_CREATION_FAILED_ON_RERUN(20),
        ORG_SIGNUP_STARTED(30),
        ORG_SIGNUP_FAILED(30),
        ORG_SIGNUP_FINISHED(50),
        PACKAGE_INSTALLATION_STARTED(50),
        PACKAGE_INSTALLATION_FAILED(50),
        PACKAGE_INSTALLATION_FINISHED(100);

        private int intVal;

        SandboxProgressCheckPointType(int intVal) {
            this.intVal = intVal;
        }

        public Integer getIntVal() {
            return intVal;
        }
    }
    public static void setInstallThread() throws Exception {
        isInstallThread.set(true);
    }

    public static Boolean isInstallThread(){
        return isInstallThread.get();
    }

    public static void setUserConfigFromAdminTool() {
        userConfigFromAdminTool.set(true);
    }

    public static boolean getUserConfigFromAdminTool() {
        return userConfigFromAdminTool.get() != null && userConfigFromAdminTool.get();
    }

    public static void clearInstallThread() throws Exception {
        isInstallThread.remove();
        userConfigFromAdminTool.remove();
        PEOPLE_OLD_VS_NEW_MAIL.remove();
        COMPONENTS_UID_VS_COMPONENT_ID.remove();
    }

    public static Map<ComponentType, Map<String, Long>> getComponentsUIdVsComponentId() {
        return COMPONENTS_UID_VS_COMPONENT_ID.get();
    }

    public static Long getComponentId(ComponentType componentType,String uniqueIdentifier){
        Long componentId = null;
        if (componentType != null) {
            componentId = getComponentsUIdVsComponentIdForComponent(componentType).get(uniqueIdentifier);
        }
        return componentId != null ? componentId : -1;
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

    public static Map<Long, List<FacilioField>> getAssetCategoryIdVsReadingFields(String moduleName) {
        return ASSET_CATEGORY_ID_VS_READING_FIELDS.get().computeIfAbsent(moduleName, k -> new HashMap<>());
    }

    public static void addAssetCategoryIdVsReadingFields(String moduleName, Map<Long, List<FacilioField>> records) {
        Map<Long, List<FacilioField>> assetCategoryIdVsReadingFields = getAssetCategoryIdVsReadingFields(moduleName);
        assetCategoryIdVsReadingFields.putAll(records);
    }

    public static long getPeopleId(String peopleMail) {
        if (PackageUtil.getUserConfigFromAdminTool()) {
            peopleMail = PackageUtil.getNewPeopleMail(peopleMail);
        }
        Map<String, Long> peopleMailVsPeopleId = PEOPLE_CONFIG_FOR_CONTEXT.get();
        return peopleMailVsPeopleId.getOrDefault(peopleMail, -1L);
    }

    public static void addPeopleConfigForContext(Map<String, Long> peopleMailVsPeopleIdToAdd) {
        Map<String, Long> peopleMailVsPeopleId = PEOPLE_CONFIG_FOR_CONTEXT.get();
        peopleMailVsPeopleId.putAll(peopleMailVsPeopleIdToAdd);
    }

    public static String getPeopleMail(long peopleId) {
        Map<Long, String> peopleIdVsPeopleMail = PEOPLE_CONFIG_FOR_XML.get();
        return peopleIdVsPeopleMail.get(peopleId);
    }

    public static void addPeopleConfigForXML(Map<Long, String> peopleIdVsPeopleMailtoAdd) {
        Map<Long, String> peopleIdVsPeopleMail = PEOPLE_CONFIG_FOR_XML.get();
        peopleIdVsPeopleMail.putAll(peopleIdVsPeopleMailtoAdd);
    }

    public static UserInfo getUserInfo(long orgUserId) {
        Map<Long, UserInfo> userInfoMap = USER_CONFIG_FOR_XML.get();
        return userInfoMap.get(orgUserId);
    }

    public static void addUserInfoForXML(Map<Long, UserInfo> userInfoMapToAdd) {
        Map<Long, UserInfo> userInfoMap = USER_CONFIG_FOR_XML.get();
        userInfoMap.putAll(userInfoMapToAdd);
    }

    public static long getOrgUserId(String userName, String identifier) {
        if (PackageUtil.getUserConfigFromAdminTool()) {
            userName = PackageUtil.getNewPeopleMail(userName);
        }
        Map<String, Map<String, Long>> userConfigMap = USER_CONFIG_FOR_CONTEXT.get();
        return (userConfigMap.containsKey(userName) && userConfigMap.get(userName).containsKey(identifier)) ? userConfigMap.get(userName).get(identifier) : -1L;
    }

    public static void addUserInfoForContext(String userName, String identifier, long orgUserId) {
        Map<String, Map<String, Long>> userConfigMap = USER_CONFIG_FOR_CONTEXT.get();
        userConfigMap.computeIfAbsent(userName, k -> new HashMap<>());
        userConfigMap.get(userName).put(identifier, orgUserId);
    }

    public static String getNewPeopleMail(String oldPeopleMail) {
        Map<String, String> oldVsNewPeopleMail = PEOPLE_OLD_VS_NEW_MAIL.get();
        return oldVsNewPeopleMail.get(oldPeopleMail);
    }

    public static void addOldVsNewPeopleMail(Map<String, String> oldVsNewPeopleMailToAdd) {
        Map<String, String> oldVsNewPeopleMail = PEOPLE_OLD_VS_NEW_MAIL.get();
        oldVsNewPeopleMail.putAll(oldVsNewPeopleMailToAdd);
    }

    public static long getRoleId(String roleName) {
        Map<String, Long> roleNameVsRoleId = ROLE_CONFIG_FOR_CONTEXT.get();
        return roleNameVsRoleId.getOrDefault(roleName, -1L);
    }

    public static void addRoleConfigForContext(Map<String, Long> roleNameVsRoleIdToAdd) {
        Map<String, Long> roleNameVsRoleId = ROLE_CONFIG_FOR_CONTEXT.get();
        roleNameVsRoleId.putAll(roleNameVsRoleIdToAdd);
    }

    public static String getRoleName(long roleId) {
        Map<Long, String> roleIdVsRoleName = ROLE_CONFIG_FOR_XML.get();
        return roleIdVsRoleName.get(roleId);
    }

    public static void addRoleConfigForXML(Map<Long, String> roleIdVsRoleNameToAdd) {
        Map<Long, String> roleIdVsRoleName = ROLE_CONFIG_FOR_XML.get();
        roleIdVsRoleName.putAll(roleIdVsRoleNameToAdd);
    }

    public static long getTeamId(String teamName) {
        Map<String, Long> teamNameVsTeamId = TEAMS_CONFIG_FOR_CONTEXT.get();
        return teamNameVsTeamId.getOrDefault(teamName, -1L);
    }

    public static void addTeamConfigForContext(Map<String, Long> teamNameVsTeamIdToAdd) {
        Map<String, Long> teamNameVsTeamId = TEAMS_CONFIG_FOR_CONTEXT.get();
        teamNameVsTeamId.putAll(teamNameVsTeamIdToAdd);
    }

    public static String getTeamName(long teamId) {
        Map<Long, String> teamIdVsTeamName = TEAMS_CONFIG_FOR_XML.get();
        return teamIdVsTeamName.get(teamId);
    }

    public static void addTeamConfigForXML(Map<Long, String> teamIdVsNameToAdd) {
        Map<Long, String> teamIdVsTeamName = TEAMS_CONFIG_FOR_XML.get();
        teamIdVsTeamName.putAll(teamIdVsNameToAdd);
    }

    public static void addComponentFileForContext(String componentName, String uniqueFileIdentifier, FileInfo fileInfo) {
        Map<String, Map<String, FileInfo>> metaFilesMap = META_FILES_FOR_COMPONENTS.get();
        metaFilesMap.computeIfAbsent(componentName, k -> new HashMap<>());;
        metaFilesMap.get(componentName).put(uniqueFileIdentifier, fileInfo);
    }
    public static Map<String,FileInfo> getComponentFileIdVsFileInfo(String componentName) {
        Map<String, Map<String, FileInfo>> metaFilesMap = META_FILES_FOR_COMPONENTS.get();
        return metaFilesMap.getOrDefault(componentName, null);
    }

    public static void clearComponentFiles() {
        META_FILES_FOR_COMPONENTS.remove();
    }
    public static void addRootFolderPath(String fileName) {
        PACKAGE_ROOT_PATH.set(fileName);
    }
    public static String getRootFolderPath() {
        return PACKAGE_ROOT_PATH.get();
    }

    public static void removeRootFolderPath() {
        PACKAGE_ROOT_PATH.remove();
    }

    public static void setSandboxBucketName(String bucketName) {
        SANDBOX_BUCKET_NAME.set(bucketName);
    }
    public static String getSandboxBucketName() {
        return SANDBOX_BUCKET_NAME.get();
    }

    public static void removeSandboxBucketName() {
        SANDBOX_BUCKET_NAME.remove();
    }

    public static void setSandboxBucketRegion(String bucketName) {
        SANDBOX_BUCKET_REGION.set(bucketName);
    }
    public static String getSandboxBucketRegion() {
        return SANDBOX_BUCKET_REGION.get();
    }

    public static void removeSandboxBucketRegion() {
        SANDBOX_BUCKET_REGION.remove();
    }

    public static void clearPickListConf() throws Exception {
        PICKLIST_CONF_FOR_XML.remove();
        PICKLIST_CONF_FOR_CONTEXT.remove();
        TICKET_STATUS_CONF_FOR_XML.remove();
        TICKET_STATUS_CONF_FOR_CONTEXT.remove();
        ASSET_CATEGORY_ID_VS_READING_FIELDS.remove();

        USER_CONFIG_FOR_XML.remove();
        USER_CONFIG_FOR_CONTEXT.remove();
        PEOPLE_CONFIG_FOR_XML.remove();
        PEOPLE_CONFIG_FOR_CONTEXT.remove();
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

        int offset = 0;
        int limit = 5000;
        List<Map<String,Object>> allProps = new ArrayList<>();
        boolean ischangeSetDone = false;

        do{

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(changeSetModule.getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("packageId"), String.valueOf(packageId), NumberOperators.EQUALS))
                    .limit(limit+1)
                    .offset(offset)
                    .orderBy(ComponentType.ORDER_BY_COMPONENT_TYPE_STR + ", COMPONENT_STATUS");

            List<Map<String, Object>> prop = selectRecordBuilder.get();

            if(prop.size() > limit){
                prop.remove(limit);
            }else{
                ischangeSetDone = true;
            }

            allProps.addAll(prop);
            offset = offset + prop.size();

        }while (!ischangeSetDone);

        Map<ComponentType, List<PackageChangeSetMappingContext>> typeVsComponents = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(allProps)) {
            List<PackageChangeSetMappingContext> changesets = FieldUtil.getAsBeanListFromMapList(allProps, PackageChangeSetMappingContext.class);
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

    public static PackageContext getPackageById(long packageId) throws Exception{
        PackageContext packageContext = null;

        List<FacilioField> fields = FieldFactory.getPackageFields();
        FacilioModule packageModule = ModuleFactory.getPackageModule();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(packageModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(packageId, packageModule));

        Map<String, Object> prop = selectRecordBuilder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            packageContext = FieldUtil.getAsBeanFromMap(prop, PackageContext.class);
        }

        return packageContext;
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

    public static void checkForDuplicateUIds(ComponentType componentType, Map<String, PackageChangeSetMappingContext> mappingToAdd) {
        Map<String, Long> uidVsCompId = PackageUtil.getComponentsUIdVsComponentIdForComponent(componentType);

        Map<Long, String> compIdVsUid = new HashMap<>();
        Map<Long, Set<String>> compIdVsDuplicateUid = new HashMap<>();
        for (Map.Entry<String, Long> entry : uidVsCompId.entrySet()) {
            String currUId = entry.getKey();
            long currCompId = entry.getValue();

            if (compIdVsUid.containsKey(currCompId)) {
                String oldUId = compIdVsUid.get(currCompId);
                mappingToAdd.remove(currUId);

                Set<String> duplicateUIdSet;
                if (!compIdVsDuplicateUid.containsKey(currCompId)) {
                    compIdVsDuplicateUid.put(currCompId, new HashSet<>());
                }
                duplicateUIdSet = compIdVsDuplicateUid.get(currCompId);
                duplicateUIdSet.add(oldUId);
                duplicateUIdSet.add(currUId);
            }
            compIdVsUid.put(currCompId, currUId);
        }

        LOGGER.info("####Sandbox - Duplicate ComponentIdVsUIds - " + componentType.name() + " - " + compIdVsDuplicateUid);
    }

    public static void computeAndAddPackageChangeset(Map<String, PackageChangeSetMappingContext> uniqueIdVsMapping, Map<String, Long> uniqueIdVsComponentId) throws Exception {
        List<PackageChangeSetMappingContext> mappings = uniqueIdVsMapping.entrySet().stream()
                .filter(entry -> uniqueIdVsComponentId.containsKey(entry.getKey()))
                .peek(entry -> entry.getValue().setComponentId(uniqueIdVsComponentId.get(entry.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList())
                ;
        PackageUtil.addPackageMappingChangesets(mappings);
    }

    public static void addPackageChangeset(ComponentType componentType, Map<String, Long> uIdVsComponentId) throws Exception {
        if (MapUtils.isEmpty(uIdVsComponentId)) {
            return;
        }

        Long packageId = PackageUtil.getPackageId();
        if (packageId == null || packageId < 0) {
            return;
        }

        PackageContext packageContext = PackageUtil.getPackageById(packageId);
        Map<String, PackageChangeSetMappingContext> uIDVsPackageChangeSet = new HashMap<>();
        for (Map.Entry<String, Long> entry : uIdVsComponentId.entrySet()) {
            PackageChangeSetMappingContext mapping = new PackageChangeSetMappingContext();
            mapping.setPackageId(packageId);
            mapping.setComponentType(componentType);
            mapping.setComponentId(entry.getValue());
            mapping.setOrgId(packageContext.getOrgId());
            mapping.setUniqueIdentifier(entry.getKey());
            mapping.setCreatedVersion(packageContext.getVersion());
            mapping.setStatus(PackageChangeSetMappingContext.ComponentStatus.ADDED);

            uIDVsPackageChangeSet.put(entry.getKey(), mapping);
        }

        PackageUtil.checkForDuplicateUIds(componentType, uIDVsPackageChangeSet);
        PackageUtil.computeAndAddPackageChangeset(uIDVsPackageChangeSet, uIdVsComponentId);
        PackageUtil.addComponentsUIdVsComponentId(componentType, uIdVsComponentId);
    }

    public static final Map<String,ComponentType> nameVsComponentType  = Collections.unmodifiableMap(initComponentTypes());

    private static Map<String,ComponentType> initComponentTypes(){
        Map<String, ComponentType> componentNameVsTypes = new HashMap<>();
        componentNameVsTypes.put(FacilioConstants.ContextNames.FORM_ID,ComponentType.FORM);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TICKET_CATEGORY,ComponentType.TICKET_CATEGORY);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TICKET_STATUS,ComponentType.TICKET_STATUS);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TICKET_PRIORITY,ComponentType.TICKET_PRIORITY);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TICKET_TYPE,ComponentType.TICKET_TYPE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.STATE_FLOW_ID,ComponentType.STATE_FLOW);
        componentNameVsTypes.put(FacilioConstants.ContextNames.SLA_POLICY_ID,ComponentType.SLA_POLICY);
        componentNameVsTypes.put(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME,ComponentType.APPROVAL_STATE_FLOW);
        componentNameVsTypes.put("approvalFlowId",ComponentType.APPROVAL_STATE_FLOW);
        componentNameVsTypes.put(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME,ComponentType.TICKET_STATUS);
        componentNameVsTypes.put(FacilioConstants.ContextNames.JOB_STATUS,ComponentType.TICKET_STATUS);
        componentNameVsTypes.put(FacilioConstants.ContextNames.MODULE,ComponentType.MODULE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.USERS,ComponentType.USER);
        componentNameVsTypes.put(FacilioConstants.ContextNames.APP_ID,ComponentType.APP);
        componentNameVsTypes.put(FacilioConstants.ContextNames.ASSET_CATEGORY,ComponentType.ASSET_CATEGORY);
        componentNameVsTypes.put(FacilioConstants.ContextNames.ASSET_TYPE,ComponentType.ASSET_TYPE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.ASSET_DEPARTMENT,ComponentType.ASSET_DEPARTMENT);
        componentNameVsTypes.put(FacilioConstants.ContextNames.ROLE,ComponentType.ROLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.SPACE_CATEGORY,ComponentType.SPACE_CATEGORY);
        componentNameVsTypes.put(FacilioConstants.ContextNames.PEOPLE,ComponentType.PEOPLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.ASSIGNMENT,ComponentType.TEAM);
        componentNameVsTypes.put(FacilioConstants.ContextNames.GROUPS,ComponentType.TEAM);
        componentNameVsTypes.put(FacilioConstants.ContextNames.CUSTOM_BUTTON_ID,ComponentType.CUSTOM_BUTTON);
        componentNameVsTypes.put(FacilioConstants.ContextNames.EMPLOYEE,ComponentType.PEOPLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TENANT_CONTACT,ComponentType.PEOPLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.VENDOR_CONTACT,ComponentType.PEOPLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.CLIENT_CONTACT,ComponentType.PEOPLE);
        componentNameVsTypes.put(FacilioConstants.ContextNames.VENDORS,ComponentType.VENDOR);
        componentNameVsTypes.put(FacilioConstants.ContextNames.CLIENT,ComponentType.CLIENT);
        componentNameVsTypes.put(FacilioConstants.ContextNames.TENANT,ComponentType.TENANT);
        componentNameVsTypes.put(FacilioConstants.PeopleGroup.PEOPLE_GROUP,ComponentType.TEAM);
        componentNameVsTypes.put(FacilioConstants.ContextNames.MODULE_STATE,ComponentType.TICKET_STATUS);

        return componentNameVsTypes;
    }

    public static void deleteWorkFlowRules(List<Long> workflowIds) throws Exception {
        if (workflowIds != null && !workflowIds.isEmpty()) {
            List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(workflowIds);
            List<Long> deleteIds = new ArrayList<Long>();
            List<Long> updateIds = new ArrayList<Long>();;
            FacilioModule module = ModuleFactory.getWorkflowRuleModule();

            if (rules != null && !rules.isEmpty()) {
                for(WorkflowRuleContext rule: rules ) {
                    if (rule.isLatestVersion() && rule.getRuleTypeEnum().versionSupported()) {
                        updateIds.add(rule.getId());
                    }
                    else {
                        deleteIds.add(rule.getId());
                    }
                    if (EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleType() != WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE.getIntVal() ) {
                        deleteScheduledRuleJob(rule);
                    }
                    else if(EventType.SCHEDULED.isPresent(rule.getActivityType()) && rule.getRuleType() == WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE.getIntVal()) {
                        deleteRecordSpecificRuleJob(rule);
                    }
                    else if (EventType.SCHEDULED_READING_RULE.isPresent(rule.getActivityType())) {
                        FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.SCHEDULED_READING_RULE_JOB_NAME);
                    }

                    // delete triggers for the particular rule
                    TriggerUtil.deleteTriggersForWorkflowRule(rule);
                }
            }
            if (deleteIds.size() > 0) {
                ActionAPI.deleteAllActionsFromWorkflowRules(workflowIds);
                GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(workflowIds, module));
                deleteBuilder.delete();

                for (WorkflowRuleContext rule : rules) {
                    switch (rule.getRuleTypeEnum()) {
                        case STATE_RULE:
                            ApprovalRulesAPI.deleteApproverRuleChildren((ApproverWorkflowRuleContext) rule);
                            break;
                        default:
                            break;
                    }

                   deleteChildIdsForWorkflow(rule, rule);
                }
            }
            if (updateIds.size() > 0) {
                Map<String, Object> ruleProps = new HashMap<>();
                ruleProps.put("latestVersion", false);
                ruleProps.put("status", false);
                ruleProps.put("modifiedTime", DateTimeUtil.getCurrenTime());
                GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(FieldFactory.getWorkflowRuleFields())
                        .andCondition(CriteriaAPI.getIdCondition(updateIds, module));
                updateBuilder.update(ruleProps);
            }
        }
    }

    protected static void deleteChildIdsForWorkflow(WorkflowRuleContext oldRule, WorkflowRuleContext newRule) throws Exception {
        try {
            if (newRule.getCriteria() != null && oldRule.getCriteriaId() != -1) {
                CriteriaAPI.deleteCriteria(oldRule.getCriteriaId());
            }
        }catch (MySQLIntegrityConstraintViolationException e) {
            LOGGER.info("Error while deleting existing workflow criteria", e);
        }
        if(newRule.getWorkflow() != null && oldRule.getWorkflowId() != -1) {
            WorkflowUtil.deleteWorkflow(oldRule.getWorkflowId());
        }
    }

    protected static void deleteScheduledRuleJob(WorkflowRuleContext rule) throws Exception {

        String jobName = rule.getSchedulerJobName();
        if(ScheduledRuleJobsMetaUtil.checkNewOrOldScheduleRuleExecution()) {
            jobName = rule.getScheduleRuleJobName();
        }

        FacilioTimer.deleteJob(rule.getId(), jobName);
    }

    protected static void deleteRecordSpecificRuleJob(WorkflowRuleContext rule) throws Exception {
        FacilioTimer.deleteJob(rule.getId(), FacilioConstants.Job.RECORD_SPECIFIC_RULE_JOB_NAME);
    }
}
