package com.facilio.bmsconsole.context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Getter
@Setter
public class PageTabContext {
    public PageTabContext(){
    }

    private long id = -1;
    private long orgId = -1;
    private long layoutId = -1;
    private String name;
    private String displayName;
    private Double sequenceNumber = -1D;
    private Boolean status;
    private Boolean isSelected = false;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private List<PageColumnContext> columns;
    private int featureLicense = -1;
    public PageTabContext(String name, String displayName, Double sequenceNumber, Boolean status, int featureLicense) {
        this.name = name;
        this.displayName = displayName;
        this.sequenceNumber = sequenceNumber;
        this.status = status;
        this.featureLicense = featureLicense;


    }

    public PageColumnContext addColumn(PageColumnContext.ColumnWidth columnWidthEnum) {
        double sequenceNumber = CollectionUtils.isNotEmpty(this.getColumns()) ? ((this.getColumns().size()+1) * 10D ) : 10; //(number of columnsin tab incremented by one * 10) to get sequence number
        PageColumnContext column = new PageColumnContext(sequenceNumber, columnWidthEnum.getWidth());
        if(this.getColumns() == null) {
            this.setColumns(new ArrayList<>(Arrays.asList(column)));
        }
        else {
            this.getColumns().add(column);
        }
        column.setParentContext(this);
        return column;
    }
    @JsonIgnore
    private PagesContext parentContext;
    public PagesContext tabDone() {
        return this.parentContext;
    }
}