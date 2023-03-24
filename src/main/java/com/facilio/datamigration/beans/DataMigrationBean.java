package com.facilio.datamigration.beans;

import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataMigrationBean {

    public List<Map<String, Object>> getModuleData(FacilioModule module, List<FacilioField> fields, List<SupplementRecord> supplements, int offset, int limit, Set<Long> siteIds, Criteria moduleCriteria) throws Exception;

    public Map<Long, Long> createModuleData(FacilioModule module, List<FacilioField> targetFields, List<SupplementRecord> supplements, List<Map<String, Object>> props, Boolean addLogger) throws Exception;

    public void updateModuleData(FacilioModule module, List<FacilioField> targetFields, List<SupplementRecord> supplements, List<Map<String, Object>> props, Boolean addLogger) throws Exception;

    public void addIntoDataMappingTable(Long migrationId, Long moduleId, Map<Long,Long> oldIdsVsNewIds) throws Exception;

    public Map<Long,Long> getOldVsNewId(Long migrationId, Long moduleId, List<Long> oldIds) throws Exception;

    public Map<Long,Long> getOldVsNewIdForCustomModules(Long migrationId, Long moduleId, List<Long> customModuleIds, List<Long> oldIds) throws Exception;

    public DataMigrationStatusContext checkAndAddDataMigrationStatus(Long sourceOrgId, Long dataMigrationId) throws Exception;

    public DataMigrationStatusContext getDataMigrationStatus(long dataMigrationId) throws Exception;

    public void updateDataMigrationStatus(Long id, DataMigrationStatusContext.DataMigrationStatus status, Long moduleId, int count) throws Exception;

    public Map<String, Object> getFileFromSource(Long fileId) throws Exception;

    public Long addFile(long targetOrgId, byte[] bytes, String fileName, String contentType) throws Exception;
}