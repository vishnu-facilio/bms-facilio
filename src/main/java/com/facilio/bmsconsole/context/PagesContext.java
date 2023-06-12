package com.facilio.bmsconsole.context;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioStringEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PagesContext implements Serializable {

    public PagesContext() {
    }

    public PagesContext(String name, String displayName, String description, Criteria criteria, Boolean isTemplate, Boolean isDefaultPage, Boolean status){
        this.name = name;
        this.displayName = displayName;
        this.description = description;
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
    private long layoutId;
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
    private String description;
    private Boolean isTemplate;
    private Boolean isDefaultPage;
    private Boolean isSystemPage = false;
    private Boolean status;
    private Long sysCreatedBy;
    private Long sysCreatedTime;
    private Long sysModifiedBy;
    private Long sysModifiedTime;
    private Long sysDeletedBy;
    private Long sysDeletedTime;
    private List<Long> appDomainTypes;
    private long appDomainType;
    private Map<String,List<PageTabContext>> layouts ;
    private SharingContext<SingleSharingContext> pageSharing;

    public enum PageLayoutType implements FacilioStringEnum {
        WEB,
        MOBILE;
    }

    public PagesContext shareTemplateToFacilioDomain() throws Exception {
        return this.addDomainSharingTemplatePage(true, false);
    }

    public PagesContext shareTemplateToNonFacilioDomains() throws Exception {
        return this.addDomainSharingTemplatePage(false, true);
    }

    public PagesContext shareTemplateToDomains(AppDomain.AppDomainType...domains) throws Exception {
        return this.addDomainSharingTemplatePage(false, false, domains);
    }

    private PagesContext addDomainSharingTemplatePage(boolean isFacilioDomainAlone, boolean isNonFacilioDomains, AppDomain.AppDomainType...domains) {
        if(this.getIsTemplate() != null && this.getIsTemplate()) {
            if(isFacilioDomainAlone) {
                this.setAppDomainTypes(Arrays.asList(Long.valueOf(AppDomain.AppDomainType.FACILIO.getIndex())));
            }
            else if(isNonFacilioDomains) {
                this.setAppDomainTypes(Arrays.asList(-1L));
            }
            else {
                this.setAppDomainTypes(Arrays.stream(domains).map(f->Long.valueOf(f.getIndex())).collect(Collectors.toList()));
            }
        }
        return this;
    }

    public PageTabContext addWebTab(String name, String displayName, Boolean status, AccountUtil.FeatureLicense featureLicense) {
        int featureId = featureLicense != null ? featureLicense.getFeatureId() : -1;
        return addTab(PageLayoutType.WEB.name(), name, displayName, status, featureId);
    }
    public PageTabContext addMobileTab(String name, String displayName, Boolean status, AccountUtil.FeatureLicense featureLicense) {
        int featureId = featureLicense != null ? featureLicense.getFeatureId() : -1;
        return addTab(PageLayoutType.MOBILE.name(), name, displayName, status, featureId);
    }

    private PageTabContext addTab(String layoutType, String name, String displayName, Boolean status, int featureLicense) {
        if(this.getLayouts() == null ) {
            this.setLayouts(new HashMap<String, List<PageTabContext>>());
        }

        double sequenceNumber = CollectionUtils.isNotEmpty(this.getLayouts().get(layoutType)) ? ((this.getLayouts().get(layoutType).size()+1) * 10D ) : 10; //(number of tabs incremented by one * 10) to get sequence number
        PageTabContext tab = new PageTabContext(name, displayName, sequenceNumber, status, featureLicense);

        if(this.getLayouts().get(layoutType) == null) {
            this.getLayouts().put(layoutType, new ArrayList<>(Arrays.asList(tab)));
        }
        else {
            this.getLayouts().get(layoutType).add(tab);
        }
        tab.setParentContext(this);
        return tab;
    }
    @JsonIgnore
    private ModulePages parentContext;
    public ModulePages pageDone() {
        return this.parentContext;
    }
}