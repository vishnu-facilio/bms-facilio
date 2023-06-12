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
public class PageColumnContext {
    public PageColumnContext(){
    }
    private long id = -1;
    private long orgId = -1;
    private long tabId = -1;
    private String name;
    private String displayName;
    private Double sequenceNumber=-1D;
    private long width = -1;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private List<PageSectionContext> sections;

    public enum ColumnWidth {
        QUARTER_WIDTH(3),
        HALF_WIDTH(6),
        THREE_QUARTER_WIDTH(9),
        FULL_WIDTH(12);

        private long width;

        ColumnWidth(long width) {
            this.width = width;
        }

        public long getWidth() {
            return width;
        }
    }
    public PageColumnContext(Double sequenceNumber, long width) {
        this.sequenceNumber = sequenceNumber;
        this.width = width;
    }

    public PageSectionContext addSection(String name, String displayName, String description) {
        double sequenceNumber = CollectionUtils.isNotEmpty(this.getSections()) ? ((this.getSections().size()+1) * 10D ) : 10; //(number of sections in column incremented by one * 10) to get sequence number
        PageSectionContext section = new PageSectionContext(name, displayName, sequenceNumber, description);
        if(this.getSections() == null) {
            this.setSections(new ArrayList<>(Arrays.asList(section)));
        }
        else {
            this.getSections().add(section);
        }
        section.setParentContext(this);
        return section;
    }
    @JsonIgnore
    private PageTabContext parentContext;
    public PageTabContext columnDone() {
        return this.parentContext;
    }
}
