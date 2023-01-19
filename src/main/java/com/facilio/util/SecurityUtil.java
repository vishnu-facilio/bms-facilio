package com.facilio.util;

import org.apache.commons.lang3.StringUtils;
import com.facilio.aws.util.FacilioProperties;

import static com.facilio.security.SecurityUtil.sanitizeSqlOrderbyParam;

public class SecurityUtil {
    public static boolean isClean(String orderBy,String orderType){
        if (StringUtils.isEmpty(orderBy) && StringUtils.isEmpty(orderType)) {
            return true;
        } else if (!StringUtils.isEmpty(orderBy) && !StringUtils.isEmpty(orderType)) {
            return sanitizeSqlOrderbyParam(orderBy + " " + orderType);
        } else if (!StringUtils.isEmpty(orderBy)) {
            return sanitizeSqlOrderbyParam(orderBy);
        } else if (!StringUtils.isEmpty(orderType)) {
            return sanitizeSqlOrderbyParam(orderType);
        }
        return false;
    }
}
