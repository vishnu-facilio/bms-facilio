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
    public LayoutBuilder addLayout(PageLayoutType layoutType) {
        return layoutBuilder(layoutType);
    }
    public LayoutBuilder addWebLayout() {
        return layoutBuilder(PageLayoutType.WEB);
    }
    private LayoutBuilder layoutBuilder(PageLayoutType layoutType) {
        if (this.getLayouts() == null) {
            this.setLayouts(new HashMap<String, List<PageTabContext>>());
        }
        return new LayoutBuilder(layoutType, this);
    }
    public LayoutBuilder addMobileLayout() {
        return layoutBuilder(PageLayoutType.MOBILE);
    }

    @JsonIgnore
    private ModulePages parentContext;
    public ModulePages pageDone() {
        return this.parentContext;
    }

    public static class LayoutBuilder {
        @JsonIgnore
        private PagesContext page;
        private PageLayoutType layoutType;

        public LayoutBuilder(PageLayoutType layoutType, PagesContext page) {
            this.layoutType = layoutType;
            this.page = page;
        }

        @Deprecated
        public PageTabContext addWebTab(String name, String displayName, Boolean status, AccountUtil.FeatureLicense featureLicense) {
            return addWebTab(name, displayName, PageTabContext.TabType.SIMPLE, status, featureLicense);
        }

        @Deprecated
        public PageTabContext addWebTab(String name, String displayName, PageTabContext.TabType tabType, Boolean status, AccountUtil.FeatureLicense featureLicense) {
            int featureId = featureLicense != null ? featureLicense.getFeatureId() : -1;
            return addTab(PageLayoutType.WEB.name(), name, displayName, tabType, status, featureId);
        }

        @Deprecated
        public PageTabContext addMobileTab(String name, String displayName, Boolean status, AccountUtil.FeatureLicense featureLicense) {
            return addMobileTab(name, displayName, PageTabContext.TabType.SIMPLE, status, featureLicense);
        }

        @Deprecated
        public PageTabContext addMobileTab(String name, String displayName, PageTabContext.TabType tabType, Boolean status, AccountUtil.FeatureLicense featureLicense) {
            int featureId = featureLicense != null ? featureLicense.getFeatureId() : -1;
            return addTab(PageLayoutType.MOBILE.name(), name, displayName, tabType, status, featureId);
        }

        @Deprecated
        public PageTabContext addTab(String layoutType, String name, String displayName, PageTabContext.TabType tabType, Boolean status, int featureLicense) {

            double sequenceNumber = CollectionUtils.isNotEmpty(this.page.getLayouts().get(layoutType)) ? ((this.page.getLayouts().get(layoutType).size()+1) * 10D ) : 10; //(number of tabs incremented by one * 10) to get sequence number
            PageTabContext tab = new PageTabContext(name, displayName, sequenceNumber, tabType, status, featureLicense);

            if(this.page.getLayouts().get(layoutType) == null) {
                this.page.getLayouts().put(layoutType, new ArrayList<>(Arrays.asList(tab)));
            }
            else {
                this.page.getLayouts().get(layoutType).add(tab);
            }
            tab.setParentContext(this);
            return tab;
        }

        public PageTabContext addTab(String name, String displayName, PageTabContext.TabType tabType, Boolean status, AccountUtil.FeatureLicense featureLicense) {

            double sequenceNumber = CollectionUtils.isNotEmpty(page.getLayouts().get(layoutType.name())) ? ((this.page.getLayouts().get(layoutType.name()).size()+1) * 10D ) : 10; //(number of tabs incremented by one * 10) to get sequence number
            PageTabContext tab = new PageTabContext(name, displayName, sequenceNumber, tabType, status, featureLicense!=null? featureLicense.getFeatureId():-1);

            if(this.page.getLayouts().get(layoutType.name()) == null) {
                this.page.getLayouts().put(layoutType.name(), new ArrayList<>(Arrays.asList(tab)));
            }
            else {
                this.page.getLayouts().get(layoutType.name()).add(tab);
            }
            tab.setParentContext(this);
            return tab;
        }

        public PagesContext layoutDone() {
            return this.page;
        }
    }
}