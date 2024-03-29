package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SummaryWidgetGroup {
    private long id=-1;
    private long orgId;
    private String name;
    private String displayName;
    private long widgetId;
    private int sequenceNumber;
    private long columns;
    private String colorCode;
    private List<SummaryWidgetGroupFields> fields = new ArrayList<>();
}
