package com.facilio.bmsconsole.db;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.db.util.DBConf;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.LRUCache;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.NumberField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.text.MessageFormat;
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
    public boolean isMalwareScannerEnabled() {
        return FacilioProperties.isMalwareScanningEnabled();
    }

    @Override
    public boolean throwMalwareScanErrors() {
        try {
            return AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MALWARE_SCAN_ERRORS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getMalwareScannerTimeout() {
        return FacilioProperties.getMalwareScannerTimeout();
    }

    @Override
    public String getMalwareScannerHost() {
        return FacilioProperties.getMalwareScannerHost();
    }

    @Override
    public int getMalwareScannerPort() {
        return FacilioProperties.getMalwareScannerPort();
    }

    @Override
    public String getMalwareScannerEngine() {
        return FacilioProperties.getMalwareScannerEngine();
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
        return FacilioProperties.isProduction();
    }

    @Override
    public boolean isDevelopment() {
        return FacilioProperties.isDevelopment();
    }

    @Override
    public String getDefaultDataSource() {
        return FacilioProperties.getDefaultDataSource();
    }

    @Override
    public String getDefaultDB() {
        return FacilioProperties.getDefaultDB();
    }

    @Override
    public String getDefaultAppDB() {
        return FacilioProperties.getDefaultAppDB();
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
    public Organization getCurrentOrg() {
        return AccountUtil.getCurrentOrg();
    }

    @Override
    public User getCurrentUser() {
        return AccountUtil.getCurrentUser();
    }

    @Override
    public Set<String> getDBIdentifiers() {
        return FacilioProperties.getDBIdentifiers();
    }

    public HashMap<String, String> getSecret(String secretKey) {
        HashMap<String, String> password = FacilioProperties.getPassword(secretKey);
        String url = String.format("jdbc:mysql://%s:%s/", password.get("host"), password.get("port"));
        password.put("url", url);
        return password;
    }

    @Override
    public String getSecretManager() {
        return FacilioProperties.getSecretManager();
    }

    @Override
    public ZoneId getCurrentZoneId() {
        //TODO TimeZone related changes to be done.

    		Account currentAccount = AccountUtil.getCurrentAccount();
    		if(currentAccount != null) {
    			String zone = currentAccount.getTimeZone();
    		 	if(StringUtils.isNotEmpty(zone)) {
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

    @Override
    public String getCurrentDataSource() {
        if (AccountUtil.getCurrentAccount() != null) {
            return AccountUtil.getCurrentAccount().getCurrentDataSource();
        }
        return null;
    }

    @Override
    public String getCurrentDBName() {
        if (AccountUtil.getCurrentAccount() != null) {
            return AccountUtil.getCurrentAccount().getCurrentDBName();
        }
        return null;
    }

    @Override
    public String getCurrentRequestUri() {
        if (AccountUtil.getCurrentAccount() != null) {
            return AccountUtil.getCurrentAccount().getRequestUri();
        }
        return null;
    }

    /**
     * fetches file's URL, File Name and Content type
     * @param selectFields
     * @param records
     * @param fileIds
     * @throws Exception
     */
    @Override
    public void fetchFileUrls(Collection<FacilioField> selectFields, List<Map<String,Object>> records, List<Long> fileIds, Connection conn) throws Exception {
        FileStore fs = FacilioFactory.getFileStore();

        // TODO get filePrivateUrl in bulk
        Map<Long, String> fileUrls = new HashMap<>();
        Map<Long, String> downloadUrls = new HashMap<>();
        for(Long fileId: fileIds) {
            fileUrls.put(fileId, fs.getPrivateUrl(fileId));
            downloadUrls.put(fileId, fs.getDownloadUrl(fileId));
        }
        Map<Long, FileInfo> files = fs.getFileInfoAsMap(fileIds, conn);
        Map<Long, Object> orgUserMap = new HashMap<>();
        for(Map<String, Object> record: records) {
            for(FacilioField field : selectFields) {
                if(field != null && field.getDataTypeEnum() == FieldType.FILE && record.containsKey(field.getName()+"Id")) {
                    try {
                        Long id = (Long) record.get(field.getName() + "Id");
                        FileInfo info = files.get(id);
                        if (info != null) {
                            record.put(field.getName() + "Url", fileUrls.get(id));
                            record.put(field.getName() + "DownloadUrl", downloadUrls.get(id));
                            record.put(field.getName() + "FileName", info.getFileName());
                            record.put(field.getName() + "ContentType", info.getContentType());
                            if (field.getDisplayType() == FacilioField.FieldDisplayType.SIGNATURE && MapUtils.isNotEmpty(info.getUploadedBy()) && info.getUploadedBy().containsKey("id")) {
                                Long ouId = (Long) info.getUploadedBy().get("id");
                                if (ouId != null) {
                                    User user = null;
                                    if (!orgUserMap.containsKey(ouId)) {
                                        user = AccountUtil.getUserBean().getUser(ouId, true);
                                        orgUserMap.put(ouId, user);
                                    } else {
                                        user = (User) orgUserMap.get(ouId);
                                    }
                                    if (user != null) {
                                        info.setUploadedBy(FieldUtil.getAsProperties(user));
                                    }
                                }
                            }
                            record.put(field.getName() + "UploadedBy", info.getUploadedBy());
                            record.put(field.getName() + "UploadedTime", info.getUploadedTime());
                        }
                        else { //Since we are anyway not showing file fields in filter, this is okay for now
                            record.remove(field.getName() + "Id");
                            LOGGER.error(MessageFormat.format("Invalid fieldid : {0} is present in record {1} of {2}", id, record.get("id"), field.getModule().getName()));
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(MessageFormat.format("Error occurred while getting file info for field {0}", field.getName()), e);
                        throw e;
                    }
                }
            }
        }
    }

    @Override
    public void addFiles(List<FileField> fileFields, List<Map<String, Object>> values) throws Exception {
        if (fileFields == null || fileFields.isEmpty()) {
            return;
        }
        FileStore fs = FacilioFactory.getFileStore();
        List<Long> orphanFileIds = new ArrayList<>();
        for(Map<String, Object> value : values) {
            for(FacilioField field : fileFields) {
                if (value.get(field.getName()) != null) {
                    Object fileObj = value.get(field.getName());
                    fileObj = fileObj instanceof List && ((ArrayList)fileObj).get(0) != null ? ((Map<String,Object>)((ArrayList)fileObj).get(0)) : fileObj;
                    File file = null;
                    String fileName = null;
                    String fileType = null;

                    if (fileObj instanceof File || fileObj instanceof String){
                        file = fileObj instanceof File ? (File) fileObj : new File((String)fileObj);
                    }
                    else {
                        Map<String, Object> fileMap = (Map<String, Object>) fileObj;
                        file = new File((String) fileMap.get("content"));
                    }
                    fileName = (String) value.get(field.getName()+"FileName");
                    fileType = (String) value.get(field.getName()+"ContentType");

                    // TODO add file in bulk
					/*value.put("file", file);
					value.put("fileName", fileName);
					value.put("contentType", fileType);
					files.add(value);*/
                    long fileId = fs.addFile(fileName, file, fileType);
                    value.put(field.getName(), fileId);
                }
                else if (value.get(field.getName()+"Id") != null) {
                        Long val = FacilioUtil.parseLong(value.get(field.getName()+"Id"));
                		value.put(field.getName(), val);
                		if (val != null && val != -99) { // Since it's sent as -99 from client and fileId cannot be less than 0, this is okay I guess
                            orphanFileIds.add(val);
                        }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(orphanFileIds)) {
            fs.unOrphan(orphanFileIds);
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
        FileStore fs = FacilioFactory.getFileStore();
        fs.markAsDeleted(fileIds);
    }

    @Override
    public void markFilesAsDeleted(String namespace, List<Long> fileIds) throws Exception {
        FacilioFactory.getFileStore().markAsDeleted(namespace, fileIds);
    }

    @Override
    public AggregateOperator getAggregateOperator(int value) {
        return BmsAggregateOperators.getAggregateOperator(value);
    }

    @Override
    public AggregateOperator getAggregateOperator(String value) {
        return BmsAggregateOperators.getAggregateOperator(value);
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
        AccountUtil.incrementInsertQueryCount(count);
    }

    @Override
    public void incrementInsertQueryTime(long duration) {
        AccountUtil.incrementInsertQueryTime(duration);
    }

    @Override
    public void incrementSelectQueryCount(int count) {
        AccountUtil.incrementSelectQueryCount(count);
    }

    @Override
    public void incrementSelectQueryTime(long duration) {
        AccountUtil.incrementSelectQueryTime(duration);
    }

    @Override
    public void incrementUpdateQueryCount(int count) {
        AccountUtil.incrementUpdateQueryCount(count);
    }

    @Override
    public void incrementUpdateQueryTime(long duration) {
        AccountUtil.incrementUpdateQueryTime(duration);
    }

    @Override
    public void incrementDeleteQueryCount() {
        AccountUtil.incrementDeleteQueryCount(1);
    }

    @Override
    public void incrementDeleteQueryTime(long duration) {
        AccountUtil.incrementDeleteQueryTime(duration);
    }

    @Override
    public void incrementInstantJobCount(int count) {
    	AccountUtil.incrementInstantJobCount(count);
    }

    @Override
    public void incrementInstantJobFileAddTime(long duration) {
        AccountUtil.incrementInstantJobFileAddTime(duration);
    }

    @Override
    public void incrementRedisDeleteCount(int redisQueries) {
        AccountUtil.incrementRedisDeleteCount(redisQueries);
    }

    @Override
    public void incrementRedisDeleteTime(long redisTime) {
        AccountUtil.incrementRedisDeleteTime(redisTime);
    }

    @Override
    public void incrementRedisGetTime(long redisTime) {
        AccountUtil.incrementRedisGetTime(redisTime);
    }

    @Override
    public void incrementRedisGetCount(int redisQueries) {
        AccountUtil.incrementRedisGetCount(redisQueries);
    }

    @Override
    public void setJsonConversionTime(long jsonConversionTime) {
        AccountUtil.setJsonConversionTime(jsonConversionTime);
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

    @Override
    public boolean logQueries() {
        return super.logQueries();
    }

    @Override
	public void cleanCurrentAccount() throws Exception {
        AccountUtil.cleanCurrentAccount();
	}

    @Override
    public void setCurrentAccount(long l) throws Exception {
        AccountUtil.setCurrentAccount(l);
    }

    @Override
    public void setReqUri(String s) {
        AccountUtil.setReqUri(s);
    }

    @Override
    public Properties getTranslationFile () throws Exception {
        IAMUser user = AccountUtil.getCurrentUser();
        TranslationBean bean = (TranslationBean)BeanFactory.lookup("TranslationBean");
        String userLang = user.getLanguage();
        return (userLang == null || userLang.trim().isEmpty()) ? null : bean.getTranslationFile(user.getLanguage());
    }

    @Override
    public String getEmailDomain() throws Exception {
        return FacilioProperties.getMailDomain();
    }

    @Override
    public String getEnvironment() throws Exception {
        return FacilioProperties.getEnvironment();
    }

    @Override
    public String getConfigProp(String name) throws Exception {
        return FacilioProperties.getConfig(name);
    }

    @Override
    public Organization getOrg(String orgDomain) throws Exception {
        return IAMUtil.getOrgBean().getOrgv2(orgDomain);
    }

    @Override
    public String getRegion() throws Exception {
        return FacilioProperties.getRegion();
    }

    @Override
    public String getService() throws Exception {
        return FacilioProperties.getService();
    }

    @Override
    public String getMailTrackingConfName() throws Exception {
        return FacilioProperties.getConfig("outgoing.mail.tracking.conf");
    }

    @Override
	public Account getCurrentAccount() throws Exception {
		return AccountUtil.getCurrentAccount();
	}

	@Override
	public void setNewAccount(long orgId) throws Exception {
		AccountUtil.setCurrentAccount(orgId);
	}

	@Override
    public void setNewAccount(AccountsInterface account) throws Exception {
        AccountUtil.setCurrentAccount((Account)account);
    }

	@Override
	public void removeOrgCache(long currentOrgId) {
		ResponseCacheUtil.removeOrgCache(DBConf.getInstance().getCurrentOrgId());
	}

	@Override
	public long addFile(String msg, String fileName,String contentType) throws Exception {
		return FacilioFactory.getFileStore().addFile(fileName, msg,contentType );
	}

    @Override
    public long addFile(String namespace, String msg, String fileName, String contentType) throws Exception {
        return FacilioFactory.getFileStore().addFile(namespace, fileName, msg,contentType );
    }

    @Override
	public InputStream getFileContent(long fileId) throws Exception {
		return FacilioFactory.getFileStore().readFile(fileId);
	}

    @Override
    public InputStream getFileContent(String namespace, long fileId) throws Exception {
        return FacilioFactory.getFileStore().readFile(namespace, fileId);
    }

    @Override
	public boolean deleteFileContent(List<Long> fileIds) throws Exception {
		return FacilioFactory.getFileStore().deleteFiles(fileIds);
	}

    @Override
    public long getResponseSizeThreshold() {
        return FacilioProperties.getResponseSizeThreshold();
    }

    @Override
    public List<String> getResponseSizeThresholdWhiteListedUrls() {
        return FacilioProperties.getResponseSizeThresholdWhiteListedUrls();
    }

    private static final String TRANSACTION_ROOT_CHAIN = "transactionRootChain";
    @Override
    public Map<String, Object> getOtherTransactionThreadLocalProps() {
//        LOGGER.debug(MessageFormat.format("Prev Root chain before new transaction : {0}", rootChain));
        return Collections.singletonMap(TRANSACTION_ROOT_CHAIN, FacilioChain.getRootchain());
    }

    @Override
    public void setOtherTransactionThreadLocalProps(Map<String, Object> props) {
        FacilioChain rootChain = props == null ? null : (FacilioChain) props.get(TRANSACTION_ROOT_CHAIN);
//            LOGGER.debug(MessageFormat.format("Setting the same prev root chain as current one : {0}", rootChain));
        FacilioChain.setRootChain(rootChain);
    }

}
