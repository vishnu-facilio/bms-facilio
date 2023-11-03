package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public class ServiceCatalogAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    private Boolean fetchComplaintType;

	public Boolean getFetchComplaintType() {
		return fetchComplaintType;
	}
	public void setFetchComplaintType(Boolean fetchComplaintType) {
		this.fetchComplaintType = fetchComplaintType;
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

    private String searchString;
    public String getSearchString() {
        return searchString;
    }
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    private Boolean fetchFullForm = false;
    public Boolean getFetchFullForm() {
        return fetchFullForm;
    }
    public void setFetchFullForm(Boolean fetchFullForm) {
        this.fetchFullForm = fetchFullForm;
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

    private Boolean serviceCatalogGroupOrderBy;
    public Boolean getServiceCatalogGroupOrderBy() {
        return serviceCatalogGroupOrderBy;
    }
    public void setServiceCatalogGroupOrderBy(Boolean serviceCatalogGroupOrderBy) {
        this.serviceCatalogGroupOrderBy = serviceCatalogGroupOrderBy;
    }
    
    private long appId = -1;
    public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	private Boolean fromSetup;
    public Boolean getFromSetup() {
		return fromSetup;
	}
	public void setFromSetup(Boolean fromSetup) {
		this.fromSetup = fromSetup;
	}
	
	public String getServiceCatalogList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GROUP_ID, groupId);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        if (fetchComplaintType != null) {
        		context.put(FacilioConstants.ContextNames.FETCH_COMPLAINT_TYPE, fetchComplaintType);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
        context.put(FacilioConstants.ContextNames.SEARCH, searchString);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.FETCH_FULL_FORM, fetchFullForm);
        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP_ORDER_BY, serviceCatalogGroupOrderBy);
        // Temp...needs to have a separate api or handling for admin actions
        if (fromSetup == null) {
        	fromSetup = false;
        }
        context.put(FacilioConstants.ContextNames.RESTRICT_PERMISSIONS, fromSetup);

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

    public String deleteServiceCatalog() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteServiceCatalogChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();
        
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

    public String deleteServiceCatalogGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteServiceCatalogGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }

    public String getAllServiceCatalogGroup() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllServiceCatalogGroupChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS, context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS));
        return SUCCESS;
    }

    public String getServiceCatalogDetailGroup() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogGroupDetailChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP, context.get(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP));

        return SUCCESS;
    }

    public String getModules() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getServiceCatalogModuleList();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public static List<Map<String, Object>> getServiceCatalogGroupProp() throws Exception {
        FacilioModule module = ModuleFactory.getServiceCatalogGroupModule();
        List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields);

        List<Map<String, Object>> props = builder.get();
        return props;
    }

}
