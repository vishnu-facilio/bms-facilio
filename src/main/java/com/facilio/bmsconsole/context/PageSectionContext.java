package com.facilio.bmsconsole.context;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class PageSectionContext {
    private long id = -1;
    private long orgId = -1;
    private long columnId = -1;
    private String name;
    private String displayName;
    private Double sequenceNumber=-1D;
    private String description;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
//    private List<PageSectionWidgetsContext> widgets;
}