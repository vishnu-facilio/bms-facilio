package com.facilio.wmsv2.endpoint;

import com.facilio.aws.util.FacilioProperties;

public class Broadcaster {

    private static final DefaultBroadcaster broadcaster = getDefaultBroadcaster();

    private static DefaultBroadcaster getDefaultBroadcaster() {
        String broadcasterName = FacilioProperties.getWmsBroadcaster();
        switch (broadcasterName) {
            case "redis":
                return RedisBroadcaster.getBroadcaster();
            case "default":
            default:
                return LocalBroadcaster.getBroadcaster();
        }
    }

    public static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    protected Broadcaster() { }
}
