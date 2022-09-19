package com.facilio.mailtracking.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.extern.log4j.Log4j;

@Log4j
public class MailEnums {

    public enum RecipientStatus {
        IN_PROGRESS,
        SENT,
        DELIVERED,
        BOUNCED;

        public int getValue() {
            return ordinal() + 1;
        }

    }

    public enum MailStatus implements FacilioStringEnum {
        TRIGGERED("Triggered"),
        INVALID("Validation failed"),
        IN_PROCESS("Processing"),
        SENT("Sent"),
        SENT_WITHOUT_TRACKING("Sent - Tracking disabled"),
        FAILED("Failed");

        private String description;
        MailStatus(String description) {
            this.description = description;
        }

        @Override
        public String getValue() {
            return this.description;
        }
    }

}
