package com.facilio.componentpackage.utils;

import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import org.apache.commons.collections4.MapUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.dto.Organization;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;

import java.util.*;

@Log4j
public class PackageUtil {

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
        List<FacilioField> fields = FieldFactory.getPackageChangesetsFields();
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        Map<String, Object> changeSetProps = FieldUtil.getAsProperties(changeSetContext);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                                                    .fields(fields)
                                                    .addRecord(changeSetProps)
                                                    .table(changeSetModule.getTableName());

        insertRecordBuilder.save();
    }

    public static int updatePackageMappingChangeSet(PackageChangeSetMappingContext changeSetContext) throws Exception {
        FacilioModule changeSetModule = ModuleFactory.getPackageChangesetsModule();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPackageChangesetsFields());

        List<FacilioField> updatableFields = new ArrayList<>();
        updatableFields.add(fieldsMap.get("id"));
        updatableFields.add(fieldsMap.get("status"));
        updatableFields.add(fieldsMap.get("modifiedVersion"));
        updatableFields.add(fieldsMap.get("componentDisplayName"));
        updatableFields.add(fieldsMap.get("componentLastEditedTime"));

        Map<String, Object> changeSetProps = FieldUtil.getAsProperties(changeSetContext);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(updatableFields)
                .table(changeSetModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(changeSetContext.getId(), changeSetModule));

        return updateRecordBuilder.update(changeSetProps);
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
}
