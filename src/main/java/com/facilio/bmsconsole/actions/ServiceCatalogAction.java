package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ServiceCatalogAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private ServiceCatalogContext serviceCatalog;
    public ServiceCatalogContext getServiceCatalog() {
        return serviceCatalog;
    }
    public void setServiceCatalog(ServiceCatalogContext serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    private long groupId = -1;
    public long getGroupId() {
        return groupId;
    }
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String addOrUpdateServiceCatalog() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateServiceCatalogChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG, serviceCatalog);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG, context.get(FacilioConstants.ContextNames.SERVICE_CATALOG));
        return SUCCESS;
    }

    public String getServiceCatalogList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GROUP_ID, groupId);
        context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

        if (getFilters() != null) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
        }

        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOGS, context.get(FacilioConstants.ContextNames.SERVICE_CATALOGS));
        return SUCCESS;
    }

    public String getServiceCatalogDetails() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogDetailsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG, context.get(FacilioConstants.ContextNames.SERVICE_CATALOG));
        return SUCCESS;
    }

    private ServiceCatalogGroupContext serviceCatalogGroup;
    public ServiceCatalogGroupContext getServiceCatalogGroup() {
        return serviceCatalogGroup;
    }
    public void setServiceCatalogGroup(ServiceCatalogGroupContext serviceCatalogGroup) {
        this.serviceCatalogGroup = serviceCatalogGroup;
    }

    public String addOrUpdateServiceCatalogGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateServiceCatalogGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP, serviceCatalogGroup);

        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP, serviceCatalogGroup);
        return SUCCESS;
    }

    public String getAllServiceCatalogGroup() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllServiceCatalogGroupChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS, context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS));
        return SUCCESS;
    }
}
