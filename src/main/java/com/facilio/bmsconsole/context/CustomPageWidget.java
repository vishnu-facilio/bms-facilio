package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPageWidget {
    private long id = -1;
    private long orgId;
    private String name;
    private String displayName;
    private long moduleId = -1;
    private long appId;
    private List<SummaryWidgetGroup> widgetGroups = new ArrayList<>();
}
