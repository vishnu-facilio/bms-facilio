package com.facilio.bmsconsoleV3.handler;

import com.facilio.bmsconsoleV3.interfaces.fetchlicensecount.Orglicensing;
import java.util.Map;

public class SiteHandler extends Orglicensing {
    @Override
    public Long fetchLicenseCount(Map<String, Object> licenseInfoObj) {
        //fetch and return current count of sites
        return null;
    }
}