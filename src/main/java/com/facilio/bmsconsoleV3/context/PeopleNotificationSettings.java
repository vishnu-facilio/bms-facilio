package com.facilio.bmsconsoleV3.context;

import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PeopleNotificationSettings  extends ModuleBaseWithCustomFields {
    private static final long serialVersionUID = 1L;

    private long peopleId;
    public enum Notification_Types implements FacilioStringEnum{
        COMMENT_MENTION_EMAIL,
        COMMENT_MENTION_IN_APP
    }
    private Notification_Types notificationType;
    private Boolean disabled;


}
