package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.interceptors.ErrorUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.xpath.operations.Bool;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class V3PIckListAction extends V3Action {
    Boolean isToFetchDecommissionedResource;
    public String scopeVariableOptions() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getGlobalSwitchAccessiblityChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();
        Boolean isAccessible = (Boolean) context.get(FacilioConstants.ContextNames.IS_ACCESSIBLE);
        if(isAccessible != null && isAccessible) {
            return pickList();
        }
        throw new IllegalArgumentException("Module is not accessible");
    }
    public String pickList() throws Exception {
        if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
            List<String> localSearchDisabled = Arrays.asList(FacilioConstants.ContextNames.USERS,FacilioConstants.ContextNames.READING_RULE_MODULE);
            setData(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(moduleName, page, perPage, search, filters, _default));
            setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());

            //supported api search for users module
            setMeta("localSearch", !localSearchDisabled.contains(moduleName));
        }
        else {
            if(AccountUtil.getCurrentOrg().getOrgId() == 396 && "custom_activities_1".equals(moduleName)) { // Temp fix for CIT org custom_activities_1 module Picklist limit
            	perPage = 150;
            }
            // Temp fix for altayer org site module Picklist limit until its handled in mobile
            if (AccountUtil.getCurrentAccount().isFromMobile()) {
                if (AccountUtil.getCurrentOrg().getOrgId() == 418l && "site".equals(moduleName)) {
                    perPage = 150;
                }
            }

            FacilioContext pickListContext =  new FacilioContext();
            pickListContext.put(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE, BooleanUtils.isTrue(isToFetchDecommissionedResource));
            PickListUtil.populatePicklistContext(pickListContext, getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());
            pickListContext = PickListUtil.fetchPickListData(pickListContext);

            setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
            setMeta("moduleType", ((FacilioModule)pickListContext.get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
            setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, true));
        }
        return SUCCESS;
    }

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private String filters;
    public void setFilters(String filters) {
        this.filters = filters;
    }
    public String getFilters() {
        return this.filters;
    }

    private String search;
    public void setSearch(String search) {
        this.search = search;
    }
    public String getSearch() {
        return this.search;
    }

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private String clientCriteria;
    public String getClientCriteria() {
        return clientCriteria;
    }
    public void setClientCriteria(String clientCriteria) {
        this.clientCriteria = clientCriteria;
    }

    private String _default;
    public String getDefault() {
        return _default;
    }
    public void setDefault(String _default) {
        this._default = _default;
    }

    private int page = 1;
    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return this.page;
    }

    public int perPage = 50;
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
    public int getPerPage() {
        return this.perPage;
    }

    private String timelineViewName;
    public void setTimelineViewName(String timelineViewName) {
        this.timelineViewName = timelineViewName;
    }
    public String getTimelineViewName() {
        return timelineViewName;
    }

    private String timelineModuleName;
    public void setTimelineModuleName(String timelineModuleName) {
        this.timelineModuleName = timelineModuleName;
    }
    public String getTimelineModuleName() {
        return timelineModuleName;
    }

    private long appId;
    public void setAppId(long appId) {
        this.appId = appId;
    }
    public long getAppId() {
        return appId;
    }
}
