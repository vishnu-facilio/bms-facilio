package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantUserContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.pdf.PdfUtil;

public class TenantAction extends FacilioAction {
   
   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   
   private int responseCode = 0;
   public int getResponseCode() {
      return responseCode;
   }
   public void setResponseCode(int responseCode) {
      this.responseCode = responseCode;
   }
   
   private String message;
   public String getMessage() {
      return message;
   }
   private void setMessage(String message) {
      this.message = message;
   }
   
    private Boolean spacesUpdate;
   
   
    public Boolean getSpacesUpdate() {
	return spacesUpdate;
	}
   
	public void setSpacesUpdate(Boolean spacesUpdate) {
		this.spacesUpdate = spacesUpdate;
	}

private Boolean tenantPortal;
   public Boolean getTenantPortal() {
      if (tenantPortal == null) {
         return false;
      }
      return tenantPortal;
   }
   public void setTenantPortal(Boolean tenantPortal) {
      this.tenantPortal = tenantPortal;
   }
   
   private long id = -1;
   public long getId() {
      return id;
   }
   public void setId(long id) {
      this.id = id;
   }
   
   private long spacesId = -1;
   
   public long getSpacesId() {
	return spacesId;
	}
   
   public void setSpacesId(long spacesId) {
		this.spacesId = spacesId;
   }

   private List<Long> ids;
   public List<Long> getIds() {
	   return ids;
   }
   public void setIds(List<Long> ids) {
	   this.ids = ids;
   }

private int status;

   public int getStatus() {
      return status;
   }
   public void setStatus(int status) {
      this.status = status;
   }

   private String search;
   public void setSearch(String search) {
      this.search = search;
   }
   public String getSearch() {
      return this.search;
   }
   
   private String filters;
   public String getFilters() {
      return filters;
   }
   public void setFilters(String filters) {
      this.filters = filters;
   }
   private boolean includeParentFilter;

   public boolean getIncludeParentFilter() {
      return includeParentFilter;
   }

   public void setIncludeParentFilter(boolean includeParentFilter) {
      this.includeParentFilter = includeParentFilter;
   }


   private String viewName;
   public String getViewName() {
      return viewName;
   }
   public void setViewName(String viewName) {
      this.viewName = viewName;
   }
   
   private int page;
   public void setPage(int page) {
      this.page = page;
   }
   
   public int getPage() {
      return this.page;
   }
   
   private int perPage = -1;
   public void setPerPage(int perPage) {
      this.perPage = perPage;
   }
   
   public int getPerPage() {
      return this.perPage;
   }
   
   private long userId = -1;
   public long getUserId() {
      return userId;
   }
   public void setUserId(long id) {
      this.userId = userId;
   }
   private boolean show_In_Portal; 
   
   private long orgId = -1;

   public long getOrgId() {
      return orgId;
   }
   public void setOrgId(long orgId) {
      this.orgId = orgId;
   }
   
   private long zoneId = -1;

   public long getZoneId() {
      return zoneId;
   }
   public void setZoneId(long zoneId) {
      this.zoneId = zoneId;
   }
   private Long tenantCount;
   public Long getTenantCount() {
      if (tenantCount == null) {
         tenantCount = 0L;
      }
      return tenantCount;
   }

   public void setTenantCount(Long tenantCount) {
      this.tenantCount = tenantCount;
   }

   
   List<ResourceContext> item = new ArrayList<>();
   
   public List<ResourceContext> getItem() {
      return item;
   }
   public void setItem(List<ResourceContext> item) {
      this.item = item;
   }
   
    String logoUrl;
   
   public String getLogoUrl() {
      return logoUrl;
   }
   public void setLogoUrl(String logoUrl) {
      this.logoUrl = logoUrl;
   }
   private long logoId = -1;
   
   public long getLogoId() {
      return logoId;
   }
   public void setLogoId(long logoId) {
      this.logoId = logoId;
   }
   

   private long usersId = -1;
   
   public long getUsersId() {
      return usersId;
   }
   public void setUsersId(long usersId) {
      this.usersId = usersId;
   }
   public boolean isShow_In_Portal() {
      return show_In_Portal;
   }
   public void setShow_In_Portal(boolean show_In_Portal) {
      this.show_In_Portal = show_In_Portal;
   }


   private List<ContactsContext> tenantContacts;
   public List<ContactsContext> getTenantContacts() {
      return tenantContacts;
   }
   public void setTenantContacts(List<ContactsContext> tenantContacts) {
      this.tenantContacts = tenantContacts;
   }

   private List<String> meterId;
   public List<String> getMeterId() {
      return meterId;
   }
   public void setMeterId(List<String> meterId) {
      this.meterId = meterId;
   }
   
   private Long tenantMeterId;

   public Long getTenantMeterId() {
      return tenantMeterId;
   }
   public void setTenantMeterId(Long tenantMeterId) {
      this.tenantMeterId = tenantMeterId;
   }

   private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

   private JSONObject error;
   public JSONObject getError() {
      return error;
   }
   
   @SuppressWarnings("unchecked")
   public void setError(String key, Object error) {
      if (this.error == null) {
         this.error = new JSONObject();
      }
      this.error.put(key, error);          
   }
   
// private String result;
// public String getResult() {
//    return result;
// }
// public void setResult(String result) {
//    this.result = result;
// }
   private TenantContext tenant;
   public TenantContext getTenant() {
      return tenant;
   }
   public void setTenant(TenantContext tenant) {
      this.tenant = tenant;
   }
   
   private List<TenantContext> tenants;
   public List<TenantContext> getTenants() {
      return tenants;
   }
   public void setTenants(List<TenantContext> tenants) {
      this.tenants = tenants;
   }
   
   
   private List<SpaceContext> spaces;
   
   public List<SpaceContext> getSpaces() {
	return spaces;
	}
	public void setSpaces(List<SpaceContext> spaces) {
		this.spaces = spaces;
	}

private boolean tenantZone;
   
   public boolean getTenantZone() {
      return tenantZone;
   }
   public void setTenantZone(boolean isTenantZone) {
      this.tenantZone = isTenantZone;
   }

   private ZoneContext zone;
   public ZoneContext getZone() {
      return zone;
   }
   public void setZone(ZoneContext zone) {
      this.zone = zone;
   }
   
   
   private long spaceId = -1;
   public long getSpaceId() {
      return spaceId;
   }

   public void setSpaceId(long spaceId) {
      this.spaceId = spaceId;
   }

   private List<String> spaceIds;
   public List<String> getSpaceIds() {
      return spaceIds;
   }
   public void setSpaceIds(List<String> spaceIds) {
      this.spaceIds = spaceIds;
   }

   private List<Long> tenantsId;
   public List<Long> getTenantsId() {
      return tenantsId;
   }

   public void setTenantsId(List<Long> tenantsId) {
      this.tenantsId = tenantsId;
   }

   
   private List<TenantUserContext> tenantsUser;
   
   
// public List<TenantUserContext> getTenantsUsers() {
//    return tenantUsers;
// }
// public void setTenantUsers(List<TenantUserContext> tenants) {
//    this.tenantUsers = tenantUsers;
// }
   
   private Boolean count;
   public Boolean getCount() {
      if (count == null) {
         return false;
      }
      return count;
   }
   public void setCount(Boolean count) {
      this.count = count;
   }

private Map<String, Double> readingData;
   
   
   
   public Map<String, Double> getReadingData() {
      return readingData;
   }
   public void setReadingData(Map<String, Double> readingData) {
      this.readingData = readingData;
   }
   
   private long tenantReadingData;
   public long getTenantReadingData() {
      return tenantReadingData;
   }
   public void setTenantReadingData(long tenantReadingData) {
      this.tenantReadingData = tenantReadingData;
   }


   private List<TenantUserContext> tenantsUsers;
   public List<TenantUserContext> getTenantsUsers() {
      return tenantsUsers;
   }
   public void setTenantsUsers(List<TenantUserContext> tenantsUsers) {
      this.tenantsUsers = tenantsUsers;
   }
   
   
   private RateCardContext rateCard;
   public RateCardContext getRateCard() {
      return rateCard;
   }
   public void setRateCard(RateCardContext rateCard) {
      this.rateCard = rateCard;
   }
   
   private List<RateCardContext> rateCards;
   public List<RateCardContext> getRateCards() {
      return rateCards;
   }
   public void setRateCards(List<RateCardContext> rateCards) {
      this.rateCards = rateCards;
   }
   
   private JSONObject reports;
   public JSONObject getReports() {
      return reports;
   }
   public void setReports(JSONObject reports) {
      this.reports = reports;
   }
   
   private JSONObject readings;
   public JSONObject getReadings() {
      return readings;
   }
   public void setReadings(JSONObject readings) {
      this.readings = readings;
   }
   
   private JSONArray reportcards;
   public JSONArray getReportcards() {
      return reportcards;
   }
   public void setReportcards(JSONArray reportcards) {
      this.reportcards = reportcards;
   }

   
   public String addV2Tenant() throws Exception {
	         
		   	 FacilioChain addTenant = TransactionChainFactory.v2AddTenantChain();
	  	     FacilioContext context = addTenant.getContext();
	         if(CollectionUtils.isNotEmpty(utilityAssets)) {
	            tenant.setUtilityAssets(utilityAssets);
	         }
	         tenant.parseFormData();
	         context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
	         context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
	         context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
	         context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
	         context.put(FacilioConstants.ContextNames.RECORD, tenant);
	         context.put(FacilioConstants.ContextNames.SITE_ID, tenant.getSiteId());
	         context.put(FacilioConstants.ContextNames.CONTACTS, tenantContacts);
	         
	         context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
	         if(tenantLogo != null) {
	            tenant.setTenantLogo(tenantLogo);
	         }
	         addTenant.execute();
	         tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
	         setResult(FacilioConstants.ContextNames.TENANT, (TenantContext)context.get(FacilioConstants.ContextNames.TENANT));
	         return SUCCESS;
	   } 
   
   
   private ContactsContext contact;
   
   public ContactsContext getContact() {
      return contact;
   }
   public void setContact(ContactsContext contact) {
      this.contact = contact;
   }
   
   private File tenantLogo;
   
   public File getTenantLogo() {
      return tenantLogo;
   }
   public void setTenantLogo(File tenantLogo) {
      this.tenantLogo = tenantLogo;
   }
   
   private String tenantLogoFileName;
	public String getTenantLogoFileName() {
		return tenantLogoFileName;
	}
	public void setTenantLogoFileName(String tenantLogoFileName) {
		this.tenantLogoFileName = tenantLogoFileName;
	}
	
	private  String tenantLogoContentType;
	public String getTenantLogoContentType() {
		return tenantLogoContentType;
	}
	public void setTenantLogoContentType(String tenantLogoContentType) {
		this.tenantLogoContentType = tenantLogoContentType;
	}
	
   
   public String v2updateTenant() throws Exception {
	   
		     FacilioChain update = TransactionChainFactory.v2UpdateTenantChain();
		     FacilioContext context = update.getContext();
	         if(CollectionUtils.isNotEmpty(utilityAssets)) {
	            tenant.setUtilityAssets(utilityAssets);
	         }
	         if (tenant != null) {
	        	 tenant.parseFormData();
	         }
	        if (tenantsId != null && tenantsId.size() > 0) {
	 			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, tenantsId);
	 		}else {
	 			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(id));
	 		}
	         context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.EDIT);
	         context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
	         context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
	         context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
	         context.put(FacilioConstants.ContextNames.SPACE_UPDATE, spacesUpdate);
	         context.put(FacilioConstants.ContextNames.RECORD, tenant);
	         if(tenantLogo != null) {
	            tenant.setTenantLogo(tenantLogo);
	         }
	         context.put(FacilioConstants.ContextNames.CONTACTS, tenantContacts);
	         context.put(TenantsAPI.TENANT_CONTEXT, tenant);
	         update.execute();
	         setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
	         return SUCCESS;
	   }
   
   
   public String markAsPrimaryContact() {
      try {
         FacilioContext context = new FacilioContext();
         context.put(FacilioConstants.ContextNames.RECORD_ID, tenantId);
         context.put(FacilioConstants.ContextNames.CONTACT, contact);
         FacilioChain updatePrimaryContact = FacilioChainFactory.updatePrimaryContactChain();
         updatePrimaryContact.execute(context);
         setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
         return SUCCESS;
      }
      catch (Exception e) {
         setError("error",e.getMessage());
         return ERROR;
      }
   }
   public String toggleStatus() throws Exception {
      try {
         FacilioContext context = new FacilioContext();
         context.put(FacilioConstants.ContextNames.RECORD_ID, tenantId);
         context.put(FacilioConstants.ContextNames.TENANT_STATUS, status);
         FacilioChain updateTnantStatusChain = FacilioChainFactory.toggleStatusChain();
         updateTnantStatusChain.execute(context);
         setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
         return SUCCESS;
      }
      catch (Exception e) {
         setError("error",e.getMessage());
         return ERROR;
      }
   }
   
   
   public String v2ToggleStatus() throws Exception {
	   
	   try {
	         FacilioContext context = new FacilioContext();
	         context.put(FacilioConstants.ContextNames.RECORD_ID, tenantId);
	         context.put(FacilioConstants.ContextNames.TENANT_STATUS, status);
	         FacilioChain updateTnantStatusChain = FacilioChainFactory.toggleStatusChain();
	         updateTnantStatusChain.execute(context);
	         setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
	         return SUCCESS;
	      }
	      catch (Exception e) {
	         setError("error",e.getMessage());
	         return ERROR;
	      }
	   
   }
   public String tenantCount () throws Exception {
      return fetchAllTenants();
   }

   public String fetchTenant() throws Exception {
      FacilioContext context = new FacilioContext();
      context.put(FacilioConstants.ContextNames.ID, getId());
      context.put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, getTenantPortal());
      
      FacilioChain tenantDetailChain = ReadOnlyChainFactory.fetchTenantDetails();
      tenantDetailChain.execute(context);
      tenant = (TenantContext )context.get(FacilioConstants.ContextNames.TENANT);
      setResult("tenant", tenant);
      setResult(FacilioConstants.ContextNames.SPACE_LIST, context.get(FacilioConstants.ContextNames.SPACE_LIST));
      
      return SUCCESS;
   }
   public String v2fetchTenant() throws Exception {
	      FacilioChain tenantDetailChain = TransactionChainFactory.v2fetchTenantDetails();
	      FacilioContext context = tenantDetailChain.getContext();
	      context.put(FacilioConstants.ContextNames.ID, getId());
	      context.put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, getTenantPortal());
	      tenantDetailChain.execute();
	      tenant = (TenantContext )context.get(FacilioConstants.ContextNames.TENANT);
	      setResult("tenant", tenant);
	      setResult(FacilioConstants.ContextNames.SPACE_LIST, context.get(FacilioConstants.ContextNames.SPACE_LIST));
	      
	      return SUCCESS;
	   }

   public String getTenantForAssetId() throws Exception {
      TenantContext tenantContext = TenantsAPI.getTenantForResource(assetId);
      setResult("tenant", tenantContext);
      return SUCCESS;

   }
   public String deleteTenant() {
        try {
         FacilioContext context = new FacilioContext();
         context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, tenantsId);
         context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
         FacilioChain deleteTenant = FacilioChainFactory.deleteTenantChain();
         deleteTenant.execute(context);
         setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
         return SUCCESS;
         
       }
      catch (Exception e) {
         setError("error",e.getMessage());
         return ERROR;
      }
   }
   
   public String v2deleteTenant() throws Exception {
	   FacilioChain deleteTenant = FacilioChainFactory.v2DeleteTenantChain();
	   FacilioContext context = deleteTenant.getContext();
       context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
       context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
       deleteTenant.execute();
       setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
       return SUCCESS;
  }
   
   public String v2disassociateSpace() throws Exception {
	   FacilioChain disassociateSpace = TransactionChainFactory.v2disassociateSpaceChain();
	   FacilioContext context = disassociateSpace.getContext();
	   context.put(FacilioConstants.ContextNames.RECORD_ID, id);
	   context.put(FacilioConstants.ContextNames.SPACE, spacesId);
	   context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
	   disassociateSpace.execute();
	   setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
	   return SUCCESS;
   }
   
   
   public String fetchAllTenants() throws Exception {
   
      FacilioContext context = new FacilioContext();
      context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
      context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
      
      context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Tenants.ID asc");
      if(getFilters() != null)
      {  
         JSONParser parser = new JSONParser();
         JSONObject json = (JSONObject) parser.parse(getFilters());
         context.put(FacilioConstants.ContextNames.FILTERS, json);
         context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
      }
      if (getSearch() != null) {
         JSONObject searchObj = new JSONObject();
         searchObj.put("fields", "tenant.name");
         searchObj.put("query", getSearch());
         context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
      }
      if (getCount()) {  // only count
         context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
      }
         JSONObject pagination = new JSONObject();
         pagination.put("page", getPage());
         pagination.put("perPage", getPerPage());
         if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
         }
         context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
          FacilioChain fetchTenants = FacilioChainFactory.getTenantListChain();
          fetchTenants.execute(context);
          if (getCount()) {
            setTenantCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
         }
         else {
          tenants =(List<TenantContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
         }
          setResult("tenants", tenants);
          return SUCCESS;
   
         }
   
   public String v2fetchAllTenants() throws Exception {
	   
	   		
	   	  FacilioChain fetchTenants = FacilioChainFactory.getTenantListChain();
	      FacilioContext context = fetchTenants.getContext();
	   
	      context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
	      context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
	      
	      context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Tenants.ID asc");
	      if(getFilters() != null)
	      {  
	         JSONParser parser = new JSONParser();
	         JSONObject json = (JSONObject) parser.parse(getFilters());
	         context.put(FacilioConstants.ContextNames.FILTERS, json);
	         context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	      }
	      if (getSearch() != null) {
	         JSONObject searchObj = new JSONObject();
	         searchObj.put("fields", "tenant.name");
	         searchObj.put("query", getSearch());
	         context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
	      }
	      if (getCount()) {  // only count
	         context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
	      }
	      context.put(ContextNames.SPACE_ID, spaceId);
	         JSONObject pagination = new JSONObject();
	         pagination.put("page", getPage());
	         pagination.put("perPage", getPerPage());
	         if (getPerPage() < 0) {
	            pagination.put("perPage", 5000);
	         }
	         context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
	         fetchTenants.execute();
	          if (getCount()) {
	            setTenantCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
	            setResult("count", getTenantCount());
	         }
	         else {
	          tenants =(List<TenantContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
	          setResult("tenants", tenants);
	         }
	          
	          return SUCCESS;
	   
	         }
  
  
   public String getTenantsForSpace() throws Exception {
	   List<TenantContext> tenants = TenantsAPI.getAllTenantsForSpace(Collections.singletonList(spaceId));
	   setResult(ContextNames.TENANT_LIST, tenants);
	   return SUCCESS;
   }
   
   public String getTenantUsers() throws Exception {
      tenantsUsers = TenantsAPI.getAllTenantsUsers(tenantId);
      
      return SUCCESS;
   }
   
   public String updatePortalAccess() throws Exception {
      ContactsAPI.updatePortalUserAccess(contact, true);
      return SUCCESS;
   }
   
   public String getMeterReadings() throws Exception {
      readingData = TenantsAPI.getTenantMeterReadings(meterId);
      return SUCCESS;
   }
   
   public String showInPortal() throws Exception {
      tenantReadingData = TenantsAPI.showInPortalAccess(tenantId,tenantMeterId,show_In_Portal);
      return SUCCESS;
   }
   
   
   public String tenantServicePortal() throws Exception {
      item = TenantsAPI.getUsersTenantId(usersId,orgId);
      return SUCCESS;
   }
   public String addRateCard() throws Exception {
      FacilioContext context = new FacilioContext();
      context.put(TenantsAPI.RATECARD_CONTEXT, rateCard);
      FacilioChain updateTenant = FacilioChainFactory.addRateCardChain();
      updateTenant.execute(context);
      return SUCCESS;
   }
   
   public String updateRateCard() throws Exception {
      FacilioContext context = new FacilioContext();
      context.put(TenantsAPI.RATECARD_CONTEXT, rateCard);
      FacilioChain updateTenant = FacilioChainFactory.updateRateCardChain();
      updateTenant.execute(context);
      
      return SUCCESS;
   }
   
   public String fetchRateCard() throws Exception {
      rateCard = TenantsAPI.getRateCard(id);
      return SUCCESS;
   }
   
    public String getTenantReportCards() throws Exception {
      
      FacilioContext context = new FacilioContext();
      context.put(FacilioConstants.ContextNames.ID, id);
      context.put(FacilioConstants.ContextNames.ZONE_ID, zoneId);
          
      FacilioChain getReportCardsChain = FacilioChainFactory.getTenantReportCardsChain();
      getReportCardsChain.execute(context);
      
      setReports((JSONObject) context.get(FacilioConstants.ContextNames.REPORTS));
      setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
      
      return SUCCESS;
   }
    
    public String getTenantReadingCards() throws Exception {
      
      FacilioContext context = new FacilioContext();
      context.put(FacilioConstants.ContextNames.TENANT_UTILITY_IDS, meterId);
         
      FacilioChain getReportCardsChain = FacilioChainFactory.getTenantReadingCardsChain();
      getReportCardsChain.execute(context);
      
      setReports((JSONObject) context.get(FacilioConstants.ContextNames.REPORT));
      
      return SUCCESS;
   }
   
   public String deleteRateCard() throws Exception {
      TenantsAPI.deleteRateCard(id);
      return SUCCESS;
   }
   
   public String fetchAllRateCards() throws Exception {
      rateCards = TenantsAPI.getAllRateCards();
      return SUCCESS;
   }
   
   private long startTime;
   public long getStartTime() {
      return startTime;
   }
   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }
   
   private long endTime;
   public long getEndTime() {
      return endTime;
   }
   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }
   
   private long tenantId;
   public long getTenantId() {
      return tenantId;
   }
   public void setTenantId(long tenantId) {
      this.tenantId = tenantId;
   }
   
   private long rateCardId;
   public long getRateCardId() {
      return rateCardId;
   }
   public void setRateCardId(long rateCardId) {
      this.rateCardId = rateCardId;
   }
   
   private Boolean convertToPdf;
   public Boolean getConvertToPdf() {
      if (convertToPdf == null) {
         return false;
      }
      return convertToPdf;
   }
   public void setConvertToPdf(Boolean convertToPdf) {
      this.convertToPdf = convertToPdf;
   }
   
   private String fileUrl;
   public String getFileUrl() {
      return fileUrl;
   }
   public void setFileUrl(String url) {
      this.fileUrl = url;
   }
   
   
   
   public String getTenantLogoUrl() throws Exception {
      
       setLogoUrl(TenantsAPI.getLogoUrl(logoId));
      return SUCCESS;
   }
   
   public String v2getTenantLogoUrl() throws Exception {
	      
      setLogoUrl(TenantsAPI.getLogoUrl(logoId));
      setResult("logoUrl", getLogoUrl());
      return SUCCESS;
   }
   
   
   
   @SuppressWarnings("unchecked")
   public String generateBill() throws Exception {
      
      if (getConvertToPdf()) {
         StringBuilder url = new StringBuilder(FacilioProperties.getConfig("clientapp.url")).append("/app/pdf/billing?")
               .append("tenantId=").append(tenantId).append("&rateCardId=").append(rateCardId)
               .append("&startTime=").append(startTime).append("&endTime=").append(endTime);
         fileUrl = PdfUtil.exportUrlAsPdf(url.toString());
         return SUCCESS;
      }
      
      TenantContext tenant = TenantsAPI.getTenant(tenantId);
      RateCardContext rateCard = TenantsAPI.getRateCard(rateCardId);
      
      FacilioContext context = new FacilioContext();
      
      FacilioChain chain = FacilioChainFactory.calculateTenantBill();
      
      context.put(TenantsAPI.TENANT_CONTEXT, tenant);
      context.put(TenantsAPI.RATECARD_CONTEXT, rateCard);
      context.put(TenantsAPI.START_TIME, startTime);
      context.put(TenantsAPI.END_TIME, endTime);
      
      chain.execute(context);
      
      List<Map<String,Object>> items = new ArrayList<>();
      List<Map<String,Object>> utilityItems = (List<Map<String,Object>>) context.get(TenantsAPI.UTILITY_VALUES);
      if (utilityItems != null) {
         items.addAll(utilityItems);
      }
      List<Map<String,Object>> formulaItems = (List<Map<String,Object>>) context.get(TenantsAPI.FORMULA_VALUES);
      if (formulaItems != null) {
         items.addAll(formulaItems);
      }
      
      Double total = (Double)context.get(TenantsAPI.FINAL_VALUES);
      Double tax = (Double)context.get(TenantsAPI.TAX_VALUE);
      
      this.billDetails = new HashMap<>();
      this.billDetails.put("items", items);
      this.billDetails.put("tax", tax != null ? tax : 0);
      this.billDetails.put("total", total != null ? total : 0);
      this.billDetails.put("tenant", tenant);
      this.billDetails.put("rateCard", rateCard);
      
            
      return SUCCESS;
   }
   
   private Map<String,Object> billDetails;
   public Map<String, Object> getBillDetails() {
      return billDetails;
   }
   public void setBillDetails(Map<String, Object> billDetails) {
      this.billDetails = billDetails;
   }
   
   private long assetId;
   public long getAssetId() {
      return assetId;
   }
   public void setAssetId(long assetId) {
      this.assetId = assetId;
   }
   
   private List<UtilityAsset> utilityAssets;
   public List<UtilityAsset> getUtilityAssets() {
      return utilityAssets;
   }
   public void setUtilityAssets(List<UtilityAsset> utilityAssets) {
      this.utilityAssets = utilityAssets;
   }
   
   public String updateTenantLogo() throws Exception {
	   if(getTenantLogo() != null && getId() > 0) {
	       FacilioChain addTenantLogo = FacilioChainFactory.addTenantLogoChain();
	       FacilioContext context = new FacilioContext();
	       TenantContext tenant = new TenantContext();
	       tenant.setId(getId());
	       tenant.setTenantLogo(getTenantLogo());
	       tenant.setTenantLogoFileName(getTenantLogoFileName());
	       tenant.setTenantLogoContentType(getTenantLogoContentType());
	       context.put(FacilioConstants.ContextNames.TENANT, tenant);
	       addTenantLogo.execute(context);
	       setResult("fileId", context.get(FacilioConstants.ContextNames.FILE_ID));
		   
	   }
	  return SUCCESS;
   }  
   
   public String v2updateTenantLogo() throws Exception {
	   if(getTenantLogo() != null && getId() > 0) {
	       FacilioChain addTenantLogo = FacilioChainFactory.addTenantLogoChain();
	       FacilioContext context = new FacilioContext();
	       TenantContext tenant = new TenantContext();
	       tenant.setId(getId());
	       tenant.setTenantLogo(getTenantLogo());
	       tenant.setTenantLogoFileName(getTenantLogoFileName());
	       tenant.setTenantLogoContentType(getTenantLogoContentType());
	       context.put(FacilioConstants.ContextNames.TENANT, tenant);
	       addTenantLogo.execute(context);
	       setResult("fileId", context.get(FacilioConstants.ContextNames.FILE_ID));
		   
	   }
	  return SUCCESS;
   } 

   
}