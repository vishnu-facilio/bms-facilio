package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PickListAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.V3Action;

public class V3PIckListAction extends V3Action {

    public String pickList() throws Exception {
        if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
            setData(FacilioConstants.ContextNames.PICKLIST, LookupSpecialTypeUtil.getNewPickList(moduleName));
            setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());
            setMeta("localSearch", true);
        }
        else {
            FacilioChain pickListChain = ReadOnlyChainFactory.newPicklistFromDataChain();
            
            if(AccountUtil.getCurrentOrg().getOrgId() == 396 && "custom_activities_1".equals(moduleName)) { // Temp fix for CIT org custom_activities_1 module Picklist limit
            	perPage = 150;
            }
            
            PickListAction.populatePicklistContext(pickListChain.getContext(), getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getPage(), getPerPage());
            pickListChain.execute();
            setData(FacilioConstants.ContextNames.PICKLIST,pickListChain.getContext().get(FacilioConstants.ContextNames.PICKLIST));
            setMeta("moduleType", ((FacilioModule)pickListChain.getContext().get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
            setMeta("localSearch", pickListChain.getContext().getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, true));
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

}
