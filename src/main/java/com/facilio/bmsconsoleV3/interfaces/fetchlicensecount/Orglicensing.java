package com.facilio.bmsconsoleV3.interfaces.fetchlicensecount;


import java.util.Map;


public abstract class Orglicensing {
    public abstract Long fetchLicenseCount(Map<String, Object> licenseInfoObj);
}