package com.facilio.datamigration.beans;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleBeanImpl;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.context.DataMigrationMappingContext;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationBeanImpl implements DataMigrationBean{
    private static final List<Integer> SKIP_SITE_FILTER = Arrays.asList(FacilioModule.ModuleType.NOTES.getValue(), FacilioModule.ModuleType.ATTACHMENTS.getValue());

    @Override
    public List<Map<String, Object>> getModuleDataForIds(FacilioModule module, List<FacilioField> fields, List<SupplementRecord> supplements, int offset, int limit, Set<Long> siteIds, Criteria moduleCriteria,List<Long> recordIds) throws Exception {
//        if(CollectionUtils.isNotEmpty(siteIds)) {
//            AccountUtil.setCurrentSiteId(siteIds.iterator().next());
//        }
        ScopeHandler.ScopeFieldsAndCriteria siteScopeCriteria = null;
        if (CollectionUtils.isNotEmpty(siteIds) && (FieldUtil.isSiteIdFieldPresent(module) || (module.isCustom() && !SKIP_SITE_FILTER.contains(module.getType())))) {
            siteScopeCriteria = ScopingUtil.constructSiteFieldsAndCriteria(module, false, new ArrayList<Long>(){{addAll(siteIds);}});
        }
        if(siteScopeCriteria != null) {
            fields.addAll(siteScopeCriteria.getFields());
        }

        SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder<>();
        selectBuilder.module(module)
                        .select(fields)
                                .skipModuleCriteria()
                                        .offset(offset)
                                                .limit(limit);

        if(CollectionUtils.isNotEmpty(recordIds)){
            selectBuilder.andCondition(CriteriaAPI.getIdCondition(recordIds,module));
        }

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(supplements)) {
            selectBuilder.fetchSupplements(supplements);
        }
        Criteria cr = new Criteria();
        if(siteScopeCriteria != null) {
            cr.andCriteria(siteScopeCriteria.getCriteria());
        }
        if(moduleCriteria != null) {
            cr.andCriteria(moduleCriteria);
        }

        if (module.getTypeEnum().equals(FacilioModule.ModuleType.READING)) {
            List<Integer> resourceTypeCategories = Arrays.asList(
                    ResourceContext.ResourceType.ASSET.getValue(), ResourceContext.ResourceType.SPACE.getValue());

            ModuleBean targetModuleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", module.getOrgId());
            FacilioModule resourceModule = targetModuleBean.getModule("resource");
            FacilioField resourceIdField = FieldFactory.getIdField(resourceModule);

            Criteria resourceFilterCriteria = new Criteria();
            resourceFilterCriteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", StringUtils.join(resourceTypeCategories, ","), NumberOperators.EQUALS));

            ScopeHandler.ScopeFieldsAndCriteria resourceSiteScopeCriteria = null;
            if (CollectionUtils.isNotEmpty(siteIds)) {
                 resourceSiteScopeCriteria = ScopingUtil.constructSiteFieldsAndCriteria(resourceModule, false, new ArrayList<Long>() {{
                    addAll(siteIds);
                }});
                if (resourceSiteScopeCriteria != null) {
                    resourceFilterCriteria.andCriteria(resourceSiteScopeCriteria.getCriteria());
                }
            }

            SelectRecordsBuilder<ModuleBaseWithCustomFields> subQueryBuilder = new SelectRecordsBuilder<>();
            subQueryBuilder.module(resourceModule)
                    .select(Collections.singleton(resourceIdField))
                    .andCriteria(resourceFilterCriteria)
                    .setAggregation()
                    .skipModuleCriteria();

            String valueList = subQueryBuilder.constructQueryString();

            FacilioField parentIdField = targetModuleBean.getField("parentId", module.getName());
            Criteria additionalModCriteria = new Criteria();
            additionalModCriteria.addAndCondition(CriteriaAPI.getCondition(parentIdField, valueList, NumberOperators.EQUALS));

            cr.andCriteria(additionalModCriteria);
        }

        if(!cr.isEmpty()) {
            selectBuilder.andCriteria(cr);
        }

        return selectBuilder.getAsProps();
    }

    @Override
    public List<FacilioModule> getSystemSubModules(List<Long> parentModuleIds) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> systemSubModules = new ArrayList<>();
        FacilioModule relModule = ModuleFactory.getSubModulesRelModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(moduleFields);
        Map<String,FacilioField> relFieldMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields(relModule));


        Set<String> skipModules = DataMigrationUtil.getAllSkipMigrationModules();
        List<Integer> moduleTypesToSkip = DataMigrationUtil.getModuleTypesToSkip();

        Criteria skipCriteria = new Criteria();
        skipCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("type"), StringUtils.join(moduleTypesToSkip, ","), NumberOperators.NOT_EQUALS));
        skipCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(skipModules, ","), StringOperators.ISN_T));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSubModulesRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("custom"),"false", BooleanOperators.IS))
                .leftJoin(ModuleFactory.getModuleModule().getTableName())
                .on("Modules.MODULEID=SubModulesRel.CHILD_MODULE_ID")
                .andCondition(CriteriaAPI.getCondition(relFieldMap.get("parentModuleId"),parentModuleIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), String.valueOf(FacilioModule.ModuleType.BASE_ENTITY.getValue()), NumberOperators.NOT_EQUALS))
                .andCriteria(skipCriteria)
                .orderBy( relFieldMap.get("parentModuleId").getCompleteColumnName() +" ASC")
                .select(moduleFields);

        List<Map<String, Object>> props = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {

            for (Map<String, Object> prop : props) {
                if (prop.containsKey("createdBy")) {
                    IAMUser user = new IAMUser();
                    user.setId((long) prop.get("createdBy"));
                    prop.put("createdBy", user);
                }
            }

            systemSubModules = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);
            Map<Long, FacilioModule> moduleIdVsObj = systemSubModules.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity(),(name1, name2) -> { return name1; }));
            int index = 0 ;

            for (Map<String, Object> prop : props) {
                if(prop.containsKey("extendsId") && prop.get("extendsId") != null) {
                    FacilioModule extendModule = null;
                    if(moduleIdVsObj.get((Long)prop.get("extendsId"))==null){
                        long extendModuleId = (Long)prop.get("extendsId");
                        extendModule = modBean.getModule(extendModuleId);
                    }else{
                        extendModule = moduleIdVsObj.get((Long)prop.get("extendsId"));
                    }
                    systemSubModules.get(index).setExtendModule(extendModule);
                }
                index++;
            }
        }

        return systemSubModules;
    }

    @Override
    public List<FacilioField> getRelatedFields(List<Long> parentModuleIds, List<Long> childModuleIds) throws Exception {
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();
        FacilioModule lookupFieldsModule = ModuleFactory.getLookupFieldsModule();
        List<FacilioField> fieldFields = FieldFactory.getFieldFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldFields);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(fieldsModule.getTableName())
                .select(Collections.singleton(fieldsMap.get("fieldId")))
                .innerJoin(lookupFieldsModule.getTableName())
                .on(lookupFieldsModule.getTableName() + ".FIELDID = " + fieldsModule.getTableName() + ".FIELDID")
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), childModuleIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("dataType"), String.valueOf(FieldType.LOOKUP.getTypeAsInt()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("LOOKUP_MODULE_ID", "lookupModuleId", StringUtils.join(parentModuleIds, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            List<Long> fieldIds = propsList.stream().map(field -> (long) field.get("fieldId")).collect(Collectors.toList());

            List<FacilioField> fieldList = Constants.getModBean().getFields(fieldIds);
            return fieldList;
        }

        return null;
    }

    public List<FacilioModule> getAllModules() throws Exception{

        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(moduleModule.getTableName())
                .select(moduleFields)
                .orderBy("MODULEID ASC");

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isEmpty(props)) {
            return null;
        }

        for (Map<String, Object> prop : props) {
            if (prop.containsKey("createdBy")) {
                IAMUser user = new IAMUser();
                user.setId((long) prop.get("createdBy"));
                prop.put("createdBy", user);
            }
        }

        List<FacilioModule> moduleList = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);
        Map<Long, FacilioModule> moduleIdVsObj = moduleList.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity()));
        int index = 0;
        for (Map<String, Object> prop : props) {
            if(prop.containsKey("extendsId") && prop.get("extendsId") != null) {
                moduleList.get(index).setExtendModule(moduleIdVsObj.get((Long)prop.get("extendsId")));
            }
            index++;
        }
        return moduleList;

    }
    @Override
    public List<Map<String, Object>> getModuleData(FacilioModule module, List<FacilioField> fields, List<SupplementRecord> supplements, int offset, int limit, Set<Long> siteIds, Criteria moduleCriteria) throws Exception {
        return getModuleDataForIds(module,fields,supplements,offset,limit,siteIds,moduleCriteria,new ArrayList<>());
    }
    @Override
    public Map<Long, Long> createModuleData(FacilioModule module, List<FacilioField> targetFields, List<SupplementRecord> supplements,
                                            List<Map<String, Object>> props, Boolean addLogger) throws Exception {
        List<Long> oldIds =  new ArrayList<Long>();
        Map<Long,Long> childOldIdVsNewIds = new LinkedHashMap<>();
        List<Map<String, Object>> propToInsert = new ArrayList<>();
        for(Map<String,Object> prop : props) {
            if(MapUtils.isEmpty(prop)){
                continue;
            }
            if(prop.containsKey("##Insert_Only_ID_Mapping##")) {
                childOldIdVsNewIds.putAll((Map<Long, Long>) prop.get("##Insert_Only_ID_Mapping##"));
            } else {
                oldIds.add(new Long((Long) prop.get("id")));
                prop.remove("id");
                propToInsert.add(prop);
                if(addLogger) {
                    LOGGER.info(module.getName()+" - Insert prop :::"+prop);
                }
            }
        }

        InsertRecordBuilder insertBuilder = new InsertRecordBuilder()
                .module(module)
                .fields(targetFields)
                .allowSysCreatedFieldsProps()
                .addRecordProps(propToInsert);

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(supplements)) {
            insertBuilder.insertSupplements(supplements);
        }
        insertBuilder.save();

        int index = 0;
        Map<Long,Long> oldIdVsNewIds = new LinkedHashMap<>();
        for(Map<String,Object> insertedProp : propToInsert) {
            oldIdVsNewIds.put(oldIds.get(index), (Long) insertedProp.get("id"));
            index++;
        }
//        oldIdVsNewIds.putAll(childOldIdVsNewIds);
        return oldIdVsNewIds;
    }

    @Override
    public void updateModuleData(FacilioModule module, List<FacilioField> targetFields, List<SupplementRecord> supplements, List<Map<String, Object>> props, Boolean addLogger) throws Exception {

        if(CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder()
                        .module(module)
                        .fields(targetFields)
                        .allowSysModifiedFieldsProps()
                        .andCondition(CriteriaAPI.getIdCondition((long)prop.get("id"), module));

                if(CollectionUtils.isNotEmpty(supplements)) {
                    updateRecordBuilder.updateSupplements(supplements);
                }
                if(addLogger) {
                    LOGGER.info(module.getName()+" - Update prop :::"+prop);
                }
                updateRecordBuilder.updateViaMap(prop);
            }
        }
    }

    @Override
    public void addIntoDataMappingTable(Long migrationId, Long moduleId, Map<Long,Long> oldIdsVsNewIds) throws Exception {

        List<DataMigrationMappingContext> props = new ArrayList<>();
        for(Map.Entry<Long,Long> entry : oldIdsVsNewIds.entrySet()) {
            DataMigrationMappingContext prop = new DataMigrationMappingContext();
            prop.setModuleId(moduleId);
            prop.setMigrationId(migrationId);
            prop.setOldId(entry.getKey());
            prop.setNewId(entry.getValue());
            prop.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            props.add(prop);
        }

        FacilioModule dataMappingModule = ModuleFactory.getDataMigrationMappingModule();
        GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                .table(dataMappingModule.getTableName())
                .fields(FieldFactory.getDataMigrationMappingFields());
        insert.addRecords(FieldUtil.getAsMapList(props, DataMigrationMappingContext.class));
        insert.save();

    }

    public Map<Long,Long> getOldVsNewId(Long migrationId, Long moduleId, List<Long> oldIds) throws Exception {
        Map<Long,Long> oldVsNew = new HashMap();

        int offset = 0;
        int limit = 2500;
        int oldIdSize ;
        if(CollectionUtils.isEmpty(oldIds)){
            oldIdSize = 0;
        }else{
            oldIdSize = oldIds.size();
        }
        do {
            FacilioModule dataMappingModule = ModuleFactory.getDataMigrationMappingModule();
            List<FacilioField> fields = FieldFactory.getDataMigrationMappingFields();
            GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                    .table(dataMappingModule.getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("moduleId", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
                    if(CollectionUtils.isNotEmpty(oldIds)){
                        select.andCondition(CriteriaAPI.getCondition("OLDID", "oldId", StringUtils.join(oldIds.subList(offset, (offset+limit > oldIds.size()) ? oldIds.size() : offset+limit), ","), NumberOperators.EQUALS));
                    }
            List<Map<String, Object>> props = select.get();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    oldVsNew.put((Long) prop.get("oldId"), (Long) prop.get("newId"));
                }
            }
            offset = offset + limit;
        } while(offset < oldIdSize);
        return oldVsNew;
    }

    public Map<Long,Long> getOldVsNewIdForCustomModules(Long migrationId, Long moduleId, List<Long> customModuleIds, List<Long> oldIds) throws Exception {
        Map<Long,Long> oldVsNew = new HashMap();

        int offset = 0;
        int limit = 2500;
        do {
            FacilioModule dataMappingModule = ModuleFactory.getDataMigrationMappingModule();
            List<FacilioField> fields = FieldFactory.getDataMigrationMappingFields();
            GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                    .table(dataMappingModule.getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("OLDID", "oldId", StringUtils.join(oldIds.subList(offset, (offset+limit > oldIds.size()) ? oldIds.size() : offset+limit), ","), NumberOperators.EQUALS));
            if (CollectionUtils.isNotEmpty(customModuleIds)) {
                select.andCondition(CriteriaAPI.getCondition("moduleId", "moduleId", StringUtils.join(customModuleIds, ","), NumberOperators.EQUALS));
            }
            List<Map<String, Object>> props = select.get();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    oldVsNew.put((Long) prop.get("oldId"), (Long) prop.get("newId"));
                }
            }
            offset = offset + limit;
        } while(offset < oldIds.size());
        return oldVsNew;
    }

    @Override
    public DataMigrationStatusContext checkAndAddDataMigrationStatus(Long sourceOrgId, Long dataMigrationId) throws Exception {

        FacilioModule dataMigrationModule = ModuleFactory.getDataMigrationStatusModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        Criteria criteria = new Criteria();
        if(sourceOrgId>0) {
            criteria.addAndCondition(CriteriaAPI.getCondition("SOURCE_ORGID", "sourceOrgId", String.valueOf(sourceOrgId), NumberOperators.EQUALS));
        }
//        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(DataMigrationStatusContext.DataMigrationStatus.COMPLETED.getIndex()), NumberOperators.NOT_EQUALS));
        if(dataMigrationId != null) {
            criteria.addAndCondition(CriteriaAPI.getIdCondition(dataMigrationId, dataMigrationModule));
        }
        selectBuilder.table(dataMigrationModule.getTableName())
                .select(FieldFactory.getDataMigrationStatusFields())
                .andCriteria(criteria);
        Map<String,Object> props = selectBuilder.fetchFirst();
        if(MapUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props, DataMigrationStatusContext.class);
        }

        DataMigrationStatusContext migrationStatus = new DataMigrationStatusContext();
        migrationStatus.setStatus(DataMigrationStatusContext.DataMigrationStatus.INITIATED);
        migrationStatus.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        migrationStatus.setSourceOrgId(sourceOrgId);
        migrationStatus.setSysCreatedTime(System.currentTimeMillis());

        GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                .table(dataMigrationModule.getTableName())
                .fields(FieldFactory.getDataMigrationStatusFields());
        long id = insert.insert(FieldUtil.getAsProperties(migrationStatus));
        migrationStatus.setId(id);

        return migrationStatus;
    }

    @Override
    public DataMigrationStatusContext getDataMigrationStatus(long dataMigrationId) throws Exception {
        FacilioModule dataMigrationModule = ModuleFactory.getDataMigrationStatusModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(dataMigrationModule.getTableName())
                .select(FieldFactory.getDataMigrationStatusFields())
                .andCondition(CriteriaAPI.getIdCondition(dataMigrationId, dataMigrationModule));

        Map<String,Object> props = selectBuilder.fetchFirst();
        if(MapUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props, DataMigrationStatusContext.class);
        }
        return null;
    }

    @Override
    public void updateDataMigrationStatus(Long id, DataMigrationStatusContext.DataMigrationStatus status, Long moduleId, int count) throws Exception {
        DataMigrationStatusContext migrationStatus = new DataMigrationStatusContext();
        migrationStatus.setId(id);
        migrationStatus.setStatus(status);
        migrationStatus.setSysModifiedTime(System.currentTimeMillis());
        if (moduleId == null) {
            migrationStatus.setLastModuleId(-99);
        } else {
            migrationStatus.setLastModuleId(moduleId);
        }
        if(count >= 0) {
            migrationStatus.setMigratedCount(count);
        }

        FacilioModule dataMigrationModule = ModuleFactory.getDataMigrationStatusModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(dataMigrationModule.getTableName())
                .fields(FieldFactory.getDataMigrationStatusFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(migrationStatus.getId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(FieldUtil.getAsProperties(migrationStatus));

    }

    @Override
    public Map<String, Object> getFileFromSource(Long fileId) throws Exception {

        Map<String, Object> fileData = new HashMap<>();
        FileInfo fileInfo= FacilioFactory.getFileStore().getFileInfo(fileId);
        String contentType = fileInfo.getContentType();
        String fileName = fileInfo.getFileName();

        InputStream defaultValue = FacilioFactory.getFileStore().readFile((long)fileId);
        byte[] bytes = IOUtils. toByteArray(defaultValue);

        fileData.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, contentType);
        fileData.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, fileName);
        fileData.put("bytes", bytes);

        return fileData;
    }

    @Override
    public Long addFile(long targetOrgId, byte[] bytes, String fileName, String contentType) throws Exception {
        FileStore fs = FacilioFactory.getFileStoreFromOrg(targetOrgId);
        long fileId = fs.addOrphanedFile(fileName, bytes, contentType);
        FileInfo fileInfo = fs.getFileInfo(fileId);
        if (contentType.contains("image/")) {
            fs.addCompressedFile(fileInfo.getNamespace(), fileId, fileInfo);
        }
        return fileId;

    }

}
