package com.facilio.agentv2.point;

import com.facilio.modules.FacilioIntEnum;

public class PointEnum {

    public static enum ConfigureStatus implements FacilioIntEnum {
        UNCONFIGURED("Un Configured"),
        IN_PROGRESS("In Progress"),
        CONFIGURED("Configured");

        private String name;

        ConfigureStatus(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public String getName() {
            return name;
        }

        public static ConfigureStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return getName();
        }
    }

    public static enum SubscribeStatus implements FacilioIntEnum {
        UNSUBSCRIBED ("Un Subscribed"),
        IN_PROGRESS ("In Progress"),
        SUBSCRIBED ("Subscribed")
        ;

        private String name;
        SubscribeStatus (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public String getName() {
            return name;
        }

        public static SubscribeStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return getName();
        }
    }
}
