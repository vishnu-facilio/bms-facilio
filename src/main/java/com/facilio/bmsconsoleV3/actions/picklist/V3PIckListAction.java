package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PickListAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.V3Action;

import java.util.HashMap;
import java.util.Map;


public class V3PIckListAction extends V3Action {

    public String pickList() throws Exception {
        if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
            Map<String,Object> paramsData = new HashMap<>();
            paramsData.put("page",getPage());
            paramsData.put("perPage",getPerPage());
            paramsData.put("search" , getSearch());

            setData(FacilioConstants.ContextNames.PICKLIST, LookupSpecialTypeUtil.getNewPickList(moduleName,paramsData));
            setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());

            //supported api search for users module
            if(FacilioConstants.ContextNames.USERS.equals(moduleName)){
                setMeta("localSearch", false);
            }
            else{
                setMeta("localSearch", true);
            }
        }
        else {
            FacilioChain pickListChain = ReadOnlyChainFactory.newPicklistFromDataChain();
            
            if(AccountUtil.getCurrentOrg().getOrgId() == 396 && "custom_activities_1".equals(moduleName)) { // Temp fix for CIT org custom_activities_1 module Picklist limit
            	perPage = 150;
            }
            // Temp fix for altayer org site module Picklist limit until its handled in mobile
            if (AccountUtil.getCurrentAccount().isFromMobile()) {
                if (AccountUtil.getCurrentOrg().getOrgId() == 418l && "site".equals(moduleName)) {
                    perPage = 150;
                }
            }
            
            PickListAction.populatePicklistContext(pickListChain.getContext(), getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());
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
