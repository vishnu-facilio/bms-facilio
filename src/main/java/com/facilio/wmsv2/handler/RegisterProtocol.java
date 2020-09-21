package com.facilio.wmsv2.handler;

public class RegisterProtocol {

    public static void registerProtocol(Protocol protocol, Object object) {
        switch (protocol) {
            case PUSH_NOTIFICATION:
                // handle the code here..
                break;
        }
    }

    public enum Protocol {
        PUSH_NOTIFICATION
        ;
    }
}
