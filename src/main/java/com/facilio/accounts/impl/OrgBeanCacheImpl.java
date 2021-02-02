package com.facilio.accounts.impl;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;
import com.facilio.fw.cache.FacilioCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Objects;

public class OrgBeanCacheImpl extends OrgBeanImpl implements OrgBean  {
    private static final Logger LOGGER = LogManager.getLogger(OrgBeanCacheImpl.class.getName());

    @Override
    public long getFeatureLicense() throws Exception {
        FacilioCache<String, Long> featureLicenseCache = LRUCache.getFeatureLicenseCache();
        Objects.requireNonNull(AccountUtil.getCurrentOrg(), "Current Org cannot be null in AccountUtil while fetching Feature License");
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String key = CacheUtil.ORG_KEY(orgId);
        Long featureLicense = featureLicenseCache.get(key);
        if (featureLicense == null) {
            featureLicense = super.getFeatureLicense();
            featureLicenseCache.put(key, featureLicense);
        }
        else {
            LOGGER.info("Feature license cache is hit");
        }
        return featureLicense;
    }

    @Override
    public int addLicence(long summodule) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(), "Current Org cannot be null in AccountUtil while adding Feature License");
        int rows = super.addLicence(summodule);
        if (rows > 0) {
            LRUCache.getFeatureLicenseCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        }
        return rows;
    }
}
