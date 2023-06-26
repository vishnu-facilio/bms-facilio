package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderActivityContext extends V3Context  {
    private static final long serialVersionUID = 1L;
    private String info;
    private Long parentId;
    private Long ttime;
    private Long activitytype;
    private V3PeopleContext donebyid;
}
