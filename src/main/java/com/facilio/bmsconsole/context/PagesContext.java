package com.facilio.bmsconsole.context;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class PagesContext implements Serializable {

    public PagesContext() {
    }

    public PagesContext(long appId, String name, String displayName, String moduleName, Criteria criteria, Boolean isTemplate, Boolean isDefaultPage, Boolean status){
        this.appId = appId;
        this.name = name;
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.criteria = criteria;
        this.isTemplate = isTemplate;
        this.isDefaultPage = isDefaultPage;
        this.status = status;
    }
    private long id = -1;
    private long orgId = -1;
    private long appId = -1;
    private String name;
    private String displayName;
    private String moduleName;
    private long moduleId = -1;
    private Criteria criteria;
    private long criteriaId = -1;
    public void setCriteriaId(long criteriaId) throws Exception {
        this.criteriaId = criteriaId;
        if((this.criteria == null || this.criteria.isEmpty())) {
            setCriteria(CriteriaAPI.getCriteria(criteriaId));
        }
    }
    public boolean evaluateCriteria (Object record, Map<String, Object> placeHolders) throws Exception {
        boolean criteriaFlag = true;
        if(criteria != null && record != null) {
            criteriaFlag = criteria.computePredicate(placeHolders).evaluate(record);
        }
        return criteriaFlag;
    }
    private Double sequenceNumber = -1D;
    private Boolean isTemplate;
    private Boolean isDefaultPage;
    private Boolean status;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private Long sysDeletedBy;
    private Long sysDeletedTime;
    private List<PageTabsContext> tabs ;
}