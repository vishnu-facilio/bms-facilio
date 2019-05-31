package com.facilio.bmsconsole.db;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.util.DBConf;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.LRUCache;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.time.ZoneId;
import java.util.*;

public class BmsDBConf extends DBConf {
    private static final Logger LOGGER = LogManager.getLogger(BmsDBConf.class.getName());

    private static final HashSet<String> CACHE_ENABLED_TABLES = new HashSet<>();
    private static final HashSet<Long> CACHE_ENABLED_ORG = new HashSet<>();

    public BmsDBConf() {
        super();
        addCacheEnabledOrgs();
    }

    private boolean isQueryCacheEnabled(long orgId, String tableName) {
        if(DBConf.getInstance().isProduction()) {
            return false;
        }
        return CACHE_ENABLED_ORG.contains(orgId) && CACHE_ENABLED_TABLES.contains(tableName);
    }

    private void addCacheEnabledTables() {
        try {
            String tables = super.getQuery("query.cache.tables");
            LOGGER.info("Loaded cache enabled tables" + tables);
            if (tables != null) {
                CACHE_ENABLED_TABLES.addAll(Arrays.asList(tables.split(",")));
            }
        } catch (Exception e) {
            LOGGER.info("Exception while trying to load query cache tables");
        }
    }

    private void addCacheEnabledOrgs() {
        try {
            String orgIds = super.getQuery("query.cache.orgId");
            LOGGER.info("Loaded cache enabled orgs" + orgIds);
            if (orgIds != null) {
                Arrays.stream(orgIds.split(",")).forEach(orgId -> CACHE_ENABLED_ORG.add(Long.parseLong(orgId.trim())));
            }
        } catch (Exception e) {
            LOGGER.info("Exception while trying to load query cache tables");
        }
    }

    @Override
    public String getTransactionId() {
        Account account = AccountUtil.getCurrentAccount();
        StringBuilder transactionId = new StringBuilder();
        if (account != null) {
            transactionId.append(account.getOrg().getDomain())
                            .append("-");
            if (account.getUser() != null) {
                transactionId.append(account.getUser().getId())
                                .append("-");
            }
        }
        transactionId.append(Thread.currentThread().getName());
        return transactionId.toString();
    }

    @Override
    public boolean isProduction() {
        return AwsUtil.isProduction();
    }

    @Override
    public boolean isDevelopment() {
        return AwsUtil.isDevelopment();
    }

    @Override
    public Number convertToDisplayUnit(Object val, NumberField field) throws Exception {
        return UnitsUtil.convertToDisplayUnit(val, field);
    }

    @Override
    public Number convertToSiUnit(Object val, Unit unit) {
        return UnitsUtil.convertToSiUnit(val, unit);
    }

    @Override
    public long getCurrentOrgId() {
        Organization org = AccountUtil.getCurrentOrg();
        if (org != null) {
            return org.getOrgId();
        }
        return -1;
    }

    public HashMap<String, String> getSecret(String secretKey) {
        return AwsUtil.getPassword(secretKey);
    }

    @Override
    public ZoneId getCurrentZoneId() {
        //TODO TimeZone related changes to be done.
        Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            String zone = org.getTimezone();
            if(zone != null && !zone.isEmpty()) {
                return ZoneId.of(zone.trim());
            }
        }
        return DBConf.getInstance().isDevelopment() ? ZoneId.systemDefault() : ZoneId.of("Z");
    }

    @Override
    public Locale getCurrentLocale() {
        //TODO Locale related changes to be done..
        //like OrgInfo.getCurrentOrgInfo().getLocale() & set the Locale..
        if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getCountry() != null && !"".equalsIgnoreCase(AccountUtil.getCurrentOrg().getCountry().trim())) {
            return new Locale("en", AccountUtil.getCurrentOrg().getCountry());
        }
        return Locale.US;
    }

    /**
     * fetches file's URL, File Name and Content type
     * @param selectFields
     * @param records
     * @param fileIds
     * @throws Exception
     */
    @Override
    public void fetchFileUrls(Collection<FacilioField> selectFields, List<Map<String,Object>> records, List<Long> fileIds) throws Exception {
        FileStore fs = FileStoreFactory.getInstance().getFileStore();

        // TODO get filePrivateUrl in bulk
        Map<Long, String> fileUrls = new HashMap<>();
        for(Long fileId: fileIds) {
            fileUrls.put(fileId, fs.getPrivateUrl(fileId));
        }
        Map<Long, FileInfo> files = fs.getFileInfoAsMap(fileIds);

        for(Map<String, Object> record: records) {
            for(FacilioField field : selectFields) {
                if(field != null && field.getDataTypeEnum() == FieldType.FILE && record.containsKey(field.getName()+"Id")) {
                    Long id = (Long) record.get(field.getName()+"Id");
                    record.put(field.getName()+"Url", fileUrls.get(id));
                    record.put(field.getName()+"FileName", files.get(id).getFileName());
                    record.put(field.getName()+"ContentType", files.get(id).getContentType());
                }
            }
        }
    }

    @Override
    public void addFiles(List<FileField> fileFields, List<Map<String, Object>> values) throws Exception {
        if (fileFields == null || fileFields.isEmpty()) {
            return;
        }
        FileStore fs = FileStoreFactory.getInstance().getFileStore();
        for(Map<String, Object> value : values) {
            for(FacilioField field : fileFields) {
                if (value.containsKey(field.getName())) {
                    Object fileObj = value.get(field.getName());
                    fileObj = fileObj instanceof List && ((ArrayList)fileObj).get(0) != null ? ((Map<String,Object>)((ArrayList)fileObj).get(0)) : fileObj;
                    File file = null;
                    String fileName = null;
                    String fileType = null;

                    if (fileObj instanceof File || fileObj instanceof String){
                        file = fileObj instanceof File ? (File) fileObj : new File((String)fileObj);
                        fileName = (String) value.get(field.getName()+"FileName");
                        fileType = (String) value.get(field.getName()+"ContentType");
                    }
                    else {
                        Map<String, Object> fileMap = (Map<String, Object>) fileObj;
                        file = new File((String) fileMap.get("content"));
                        fileName = (String) fileMap.get(field.getName()+"FileName");
                        fileType = (String) fileMap.get(field.getName()+"ContentType");
                    }

                    // TODO add file in bulk
					/*value.put("file", file);
					value.put("fileName", fileName);
					value.put("contentType", fileType);
					files.add(value);*/

                    long fileId = fs.addFile(fileName, file, fileType);
                    value.put(field.getName(), fileId);
                }
            }
        }

		/*for(Map<String, Object> value : values) {
			for(FacilioField field : fileFields) {
				if (value.containsKey("fileId")) {
					value.put(field.getName(), value.get("fileId"));
				}
			}
		}*/
    }

    @Override
    public void markFilesAsDeleted(List<Long> fileIds) throws Exception {
        FileStore fs = FileStoreFactory.getInstance().getFileStore();
        fs.markAsDeleted(fileIds);
    }

    private static final Map<Long, Map<String, Map<String, SelectQueryCache>>> QUERY_CACHE = new HashMap<>();

    @Override
    public void addToCache(String tableName, String queryToCache, List<String> tables, List<Map<String,Object>> records) {
        long orgId = getCurrentOrgId();
        if (orgId > 0 && isQueryCacheEnabled(orgId, tableName)) {
            long queryGetTime = System.currentTimeMillis();
            Map<String, Map<String, SelectQueryCache>> table = QUERY_CACHE.getOrDefault(orgId, new HashMap<>());
            Map<String, SelectQueryCache> query = table.getOrDefault(tableName, new HashMap<>());
            query.put(queryToCache, new SelectQueryCache(tables, records));
            table.put(tableName, query);
            LOGGER.debug("building cache for query " + queryToCache);
            for (String tablesInQuery : tables) {
                LRUCache.getQueryCache().put(getRedisKey(orgId, tablesInQuery), queryGetTime);
            }
            QUERY_CACHE.put(orgId, table);
        }
    }

    @Override
    public List<Map<String,Object>> getFromCache(String tableName, String queryToCache) {
        long orgId = getCurrentOrgId();
        if(orgId > 0 && isQueryCacheEnabled(orgId, tableName)) {
            Map<String, Map<String, SelectQueryCache>> table = QUERY_CACHE.get(orgId);
            List<Map<String, Object>> returnValue = new ArrayList<>();
            if (table != null) {
                Map<String, SelectQueryCache> tableCache = table.get(tableName);
                if(tableCache != null) {
                    SelectQueryCache cache = tableCache.get(queryToCache);
                    if (cache != null) {
                        for (String tablesInQuery : cache.getTables()) {
                            Object value = LRUCache.getQueryCache().get(getRedisKey(orgId, tablesInQuery));
                            if (value == null) {
                                tableCache.remove(queryToCache);
                                LOGGER.debug("cache miss for query " + queryToCache);
                                returnValue = null;
                                break;
                            }
                        }
                        if (returnValue != null) {
                            LOGGER.debug("cache hit for query " + queryToCache);
                            return cache.getResult();
                        }
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void invalidateQueryCache(String tableName, List<String> tablesToBeDeleted) {
        long orgId = -1;
        if(AccountUtil.getCurrentOrg() != null) {
            orgId = AccountUtil.getCurrentOrg().getOrgId();
            if(isQueryCacheEnabled(orgId, tableName)) {
//                LOGGER.debug("cache invalidate for query " + sql);
                for (String tablesInQuery : tablesToBeDeleted) {
                    LRUCache.getQueryCache().remove(getRedisKey(orgId, tablesInQuery));
                }
            }
        }
    }

    private String getRedisKey(long orgId, String tableName) {
        return  orgId+'_'+tableName;
    }

    @Override
    public void incrementInsertQueryCount(int count) {
        if(AccountUtil.getCurrentAccount() != null ) {
            AccountUtil.getCurrentAccount().incrementInsertQueryCount(count);
        }
    }

    @Override
    public void incrementInsertQueryTime(long duration) {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementInsertQueryTime(duration);
        }
    }

    @Override
    public void incrementSelectQueryCount(int count) {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementSelectQueryCount(count);
        }
    }

    @Override
    public void incrementSelectQueryTime(long duration) {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementSelectQueryTime(duration);
        }
    }

    @Override
    public void incrementUpdateQueryCount(int count) {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementUpdateQueryCount(1);
        }
    }

    @Override
    public void incrementUpdateQueryTime(long duration) {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementUpdateQueryTime(duration);
        }
    }

    @Override
    public void incrementDeleteQueryCount() {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementDeleteQueryCount(1);
        }
    }

    @Override
    public void incrementDeleteQueryTime(long duration) {
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementDeleteQueryTime(duration);
        }
    }

    private class SelectQueryCache {

        private List<String> tables;
        private List<Map<String, Object>> result;

        SelectQueryCache(List<String> tables, List<Map<String, Object>> result) {
            this.tables = tables;
            this.result = deepCloneResult(result);
        }

        private List<Map<String, Object>> deepCloneResult (List<Map<String, Object>> result) {
            if (result != null) {
                List<Map<String, Object>> newResult = new ArrayList<>();
                for (Map<String, Object> prop : result) {
                    newResult.add(new HashMap<>(prop));
                }
                return newResult;
            }
            return null;
        }

        public List<String> getTables() {
            return tables;
        }

        public void setTables(ArrayList<String> tables) {
            this.tables = tables;
        }

        public List<Map<String, Object>> getResult() {
            return deepCloneResult(result);
        }

        public void setResult(List<Map<String, Object>> result) {
            this.result = result;
        }
    }

    @Override
    public void emailException(String fromClass, String msg, Throwable e, String info) {
        CommonCommandUtil.emailException(fromClass, msg, e, info);
    }
}
