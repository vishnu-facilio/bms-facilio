package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.Role;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class CommunitySharingInfoContext extends V3Context {

    private SharingType sharingType;


    public enum SharingType implements FacilioIntEnum {
        BUILDING("Building"),
        SITE("Site"),
        ALL_SITES("All Sites"),
        ROLE("Role"),
        PEOPLE("People")
        ;

        private String name;

        SharingType(String name) {
            this.name = name;
        }

        public static SharingType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    private BaseSpaceContext sharedToSpace;

    public BaseSpaceContext getSharedToSpace() {
        return sharedToSpace;
    }

    public void setSharedToSpace(BaseSpaceContext sharedToSpace) {
        this.sharedToSpace = sharedToSpace;
    }

    public void setSharingType(Integer sharingType) {
        if (sharingType != null) {
            this.sharingType = SharingType.valueOf(sharingType);
        }
    }

    private Role sharedToRole;

    private PeopleContext sharedToPeople;

    public Role getSharedToRole() {
        return sharedToRole;
    }

    public void setSharedToRole(Role sharedToRole) {
        this.sharedToRole = sharedToRole;
    }

    public PeopleContext getSharedToPeople() {
        return sharedToPeople;
    }

    public void setSharedToPeople(PeopleContext sharedToPeople) {
        this.sharedToPeople = sharedToPeople;
    }

    public SharingType getSharingTypeEnum() {
        return sharingType;
    }
    public Integer getSharingType() {
        if (sharingType != null) {
            return sharingType.getIndex();
        }
        return null;
    }

   private Long audienceId;

    public Long getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(Long audienceId) {
        this.audienceId = audienceId;
    }
}
