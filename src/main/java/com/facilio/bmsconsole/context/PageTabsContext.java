package com.facilio.bmsconsole.context;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class PageTabsContext {
    private long id = -1;
    private long orgId = -1;
    private long pageId = -1;
    private String name;
    private String displayName;
    private Double sequenceNumber = -1D;
    private Boolean status;
    private Boolean isSelected = false;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
//    private List<PageColumnsContext> columns;
}