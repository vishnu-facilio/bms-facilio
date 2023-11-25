package com.facilio.bmsconsole.enums;

import com.facilio.aws.util.FacilioProperties;

public enum Version {
    V1(getExponentValue(0)),
    V2(getExponentValue(1));

    public long getVersionId() {
        return versionId;
    }
    private long versionId;
    Version(long versionId) {
        this.versionId = versionId;
    }

    public static Version getCurrentVersion() {
        return FacilioProperties.getBuildVersion();
    }
    private static long getExponentValue(int exponent) {
        return (long) Math.pow(2, (exponent));
    }

}
