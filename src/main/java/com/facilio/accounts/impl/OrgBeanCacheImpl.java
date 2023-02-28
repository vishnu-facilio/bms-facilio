package com.facilio.accounts.impl;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.LicenseMapping;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.unitconversion.Unit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Objects;

public class OrgBeanCacheImpl extends OrgBeanImpl implements OrgBean {
    private static final Logger LOGGER = LogManager.getLogger(OrgBeanCacheImpl.class.getName());

    @Override
    public Map<String,Long> getFeatureLicense () throws Exception {
        FacilioCache<String, Map<String,Long>> featureLicenseCache = LRUCache.getFeatureLicenseCache();
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while fetching Feature License");
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String key = CacheUtil.ORG_KEY(orgId);
        Map<String,Long> featureLicense = featureLicenseCache.get(key);
        if(featureLicense == null) {
            featureLicense = super.getFeatureLicense();
            featureLicenseCache.put(key,featureLicense);
        }
        /*
        else {
            LOGGER.info("Feature license cache is hit");
        }*/
        return featureLicense;
    }

    @Override
    public boolean isFeatureEnabled(AccountUtil.FeatureLicense featureLicense) throws Exception {
        return (getFeatureLicense().containsKey(featureLicense.getGroup().getLicenseKey()) && (getFeatureLicense().get(featureLicense.getGroup().getLicenseKey()) & featureLicense.getLicense()) == featureLicense.getLicense());
    }

    @Override
    public int addLicence(Map<LicenseMapping, Long> summodule) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(), "Current Org cannot be null in AccountUtil while adding Feature License");
        int rows = super.addLicence(summodule);
        if (rows > 0) {
            LRUCache.getFeatureLicenseCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        }
        return rows;
    }

    @Override
    public Unit getOrgDisplayUnit ( int metricId ) throws Exception {
        FacilioCache<String, Object> orgUnitCache = LRUCache.getOrgUnitCachePs();
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while fetching Org Display unit");
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String key = CacheUtil.METRIC_KEY(orgId,metricId);
        Unit units = (Unit)orgUnitCache.get(key);
        if(units == null) {
            units = super.getOrgDisplayUnit(metricId);
            orgUnitCache.put(key,units);
        }
        return units;
    }

    @Override
    public boolean updateOrgUnit ( int metric,int unit ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while adding Org Display Unit");
        boolean updateStatus = super.updateOrgUnit(metric,unit);
        if(updateStatus) {
            LRUCache.getOrgUnitCachePs().remove(CacheUtil.METRIC_KEY(AccountUtil.getCurrentOrg().getOrgId(),metric));
        }
        return updateStatus;
    }

    @Override
    public void updateOrgUnitsList ( JSONObject metricUnitMap ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while adding Org Display Unit");
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        super.updateOrgUnitsList(metricUnitMap);
        metricUnitMap.keySet().forEach(metric ->{
            LRUCache.getOrgUnitCachePs().remove(CacheUtil.METRIC_KEY(orgId,(Integer)metricUnitMap.get(metric)));
        });
    }

    @Override
    public User getSuperAdmin(long orgId) throws Exception {
        FacilioCache<String, Object> superAdminCache = LRUCache.getSuperAdminCache();
        String key = CacheUtil.ORG_KEY(orgId);
        User user = (User) superAdminCache.get(key);
        if(user == null) {
            user = super.getSuperAdmin(orgId);
            superAdminCache.put(key,user);
        }
        return user;
    }
}
