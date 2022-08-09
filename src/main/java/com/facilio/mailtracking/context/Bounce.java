package com.facilio.mailtracking.context;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class Bounce {

    public enum Type {
        HARD_BOUNCE,
        SOFT_BOUNCE,
        UNKNOWN_BOUNCE;

        public int getValue() {
            return ordinal() + 1;
        }
    }

    public enum Reason {
        HARD_BOUNCE_1(Type.HARD_BOUNCE, "Permanent", "Account does not exist", "5.1.1"),
        HARD_BOUNCE_2(Type.HARD_BOUNCE, "Permanent", "Message Content Rejected", "5.6.1"),
        HARD_BOUNCE_3(Type.HARD_BOUNCE, "Permanent", "Unknown Failure", "5.0.0"),

        SOFT_BOUNCE_1(Type.SOFT_BOUNCE, "Transient", "Mailbox Full", "5.2.2"),
        SOFT_BOUNCE_2(Type.SOFT_BOUNCE, "Transient", "Message Too Large", "5.3.4"),
        SOFT_BOUNCE_3(Type.SOFT_BOUNCE, "Transient", "Temporary Failure", "4.0.0"),
        UNKNOWN_BOUNCE(Type.UNKNOWN_BOUNCE, "Undetermined", "Undetermined Failure", "6.0.0");

        @Getter
        private final String reason;
        @Getter
        private final Type bounce;
        @Getter
        private final String type;
        @Getter
        private final String statusCode;

        Reason(Type bounce, String type, String reason, String statusCode) {
            this.bounce = bounce;
            this.type = type;
            this.reason = reason;
            this.statusCode = statusCode;
        }

        public static Reason get(String type, String statusCode) {
            if(REASON_MAP.containsKey(statusCode)) {
                return REASON_MAP.get(statusCode);
            }
            LOGGER.info(statusCode + " :: statuscode not found");
            if(type.equals("Permanent")) {
                return HARD_BOUNCE_3;
            }
            if(type.equals("Transient")) {
                return SOFT_BOUNCE_3;
            }
            return UNKNOWN_BOUNCE;
        }

        private static final Map<String, Reason> REASON_MAP = Collections.unmodifiableMap(initStatusMap());
        private static Map<String, Reason> initStatusMap() {
            Map<String, Reason> map = new HashMap<>();
            for(Reason br : Reason.values()) {
                map.put(br.getStatusCode(), br);
            }
            return map;
        }

    }

}
