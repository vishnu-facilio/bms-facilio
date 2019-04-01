package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantUserContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.pdf.PdfUtil;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	
//	private String result;
//	public String getResult() {
//		return result;
//	}
//	public void setResult(String result) {
//		this.result = result;
//	}
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
	
	
	private List<Long> spaceId;
	public List<Long> getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(List<Long> spaceId) {
		this.spaceId = spaceId;
	}

	private List<Long> tenantsId;
	public List<Long> getTenantsId() {
		return tenantsId;
	}

	public void setTenantsId(List<Long> tenantsId) {
		this.tenantsId = tenantsId;
	}

	
	private List<TenantUserContext> tenantsUser;
	
	
//	public List<TenantUserContext> getTenantsUsers() {
//		return tenantUsers;
//	}
//	public void setTenantUsers(List<TenantUserContext> tenants) {
//		this.tenantUsers = tenantUsers;
//	}
	
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

	public String addTenant() {
		try {
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			context.put(FacilioConstants.ContextNames.RECORD, tenant);
			context.put(FacilioConstants.ContextNames.SITE_ID, tenant.getSiteId());
			context.put(FacilioConstants.ContextNames.IS_TENANT_ZONE, tenantZone);
			context.put(FacilioConstants.ContextNames.ZONE, zone);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, spaceId);
	
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
			tenant.setZone(zone);
			
			Chain addZone = FacilioChainFactory.getAddZoneChain();
			addZone.addCommand(FacilioChainFactory.addTenantChain());
			addZone.execute(context);
			tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
			setResult("tenant", tenant);
			return SUCCESS;
		}
		catch (Exception e) {
			setError("error",e.getMessage());
			return ERROR;
		}
	}
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String updateTenant() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			context.put(FacilioConstants.ContextNames.RECORD, tenant);
			context.put(FacilioConstants.ContextNames.SITE_ID, tenant.getSiteId());
			context.put(FacilioConstants.ContextNames.IS_TENANT_ZONE, tenantZone);
			context.put(FacilioConstants.ContextNames.ZONE, zone);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, spaceId);
			tenant.setZone(zone);
		
			context.put(TenantsAPI.TENANT_CONTEXT, tenant);
			Chain updateZone = FacilioChainFactory.getUpdateZoneChain();
			updateZone.addCommand(FacilioChainFactory.updateTenantChain());
			updateZone.execute(context);
			setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
			return SUCCESS;
		}
		catch (Exception e) {
			setError("error",e.getMessage());
			return ERROR;
		}
	}
	
	public String markAsPrimaryContact() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID, tenantId);
			context.put(FacilioConstants.ContextNames.USER, user);
			Chain updatePrimaryContact = FacilioChainFactory.updateTenantPrimaryContactChain();
			updatePrimaryContact.execute(context);
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
	
		Chain tenantDetailChain = ReadOnlyChainFactory.fetchTenantDetails();
		tenantDetailChain.execute(context);
		tenant = (TenantContext )context.get(FacilioConstants.ContextNames.TENANT);
		setResult("tenant", tenant);
		return SUCCESS;
	}
	
	public String deleteTenant() {
        try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, tenantsId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "tenant");
			Chain deleteTenant = FacilioChainFactory.deleteTenantChain();
			deleteTenant.execute(context);
			setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
			return SUCCESS;
			
      	}
		catch (Exception e) {
			setError("error",e.getMessage());
			return ERROR;
		}
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
 		if (getCount()) {	// only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		}
 			JSONObject pagination = new JSONObject();
 	 		pagination.put("page", getPage());
 	 		pagination.put("perPage", getPerPage());
 	 		if (getPerPage() < 0) {
 	 			pagination.put("perPage", 5000);
 	 		}
 	 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
 		    Chain fetchTenants = FacilioChainFactory.getTenantListChain();
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
	
	public String getTenantUsers() throws Exception {
		tenantsUsers = TenantsAPI.getAllTenantsUsers(tenantId);
		
		return SUCCESS;
	}
	
	public String updatePortalAccess() throws Exception {
		userId = TenantsAPI.updatePortalUserAccess(user.getOuid() , user.getPortal_verified());
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
		Chain updateTenant = FacilioChainFactory.addRateCardChain();
		updateTenant.execute(context);
		return SUCCESS;
	}
	
	public String updateRateCard() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(TenantsAPI.RATECARD_CONTEXT, rateCard);
		Chain updateTenant = FacilioChainFactory.updateRateCardChain();
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
	    	
		Chain getReportCardsChain = FacilioChainFactory.getTenantReportCardsChain();
		getReportCardsChain.execute(context);
		
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.REPORTS));
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		
		return SUCCESS;
	}
    
    public String getTenantReadingCards() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TENANT_UTILITY_IDS, meterId);
			
		Chain getReportCardsChain = FacilioChainFactory.getTenantReadingCardsChain();
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
	
	
	
	public String addTenantUser() throws Exception {
		
	    Chain addTenantUser = FacilioChainFactory.addTenantUserChain();
	    FacilioContext context = new FacilioContext();
	    context.put(FacilioConstants.ContextNames.USER, user);
	    context.put(FacilioConstants.ContextNames.RECORD_ID, id);
	//    tenant = TenantsAPI.getTenant(id, true);
	//	context.put(TenantsAPI.TENANT_CONTEXT, tenant);
		addTenantUser.execute(context);
		return SUCCESS;
	}

	public String getTenantLogoUrl() throws Exception {
		
	    setLogoUrl(TenantsAPI.getLogoUrl(logoId));
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String generateBill() throws Exception {
		
		if (getConvertToPdf()) {
			StringBuilder url = new StringBuilder(AwsUtil.getConfig("clientapp.url")).append("/app/pdf/billing?")
					.append("tenantId=").append(tenantId).append("&rateCardId=").append(rateCardId)
					.append("&startTime=").append(startTime).append("&endTime=").append(endTime);
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),url.toString());
			return SUCCESS;
		}
		
		TenantContext tenant = TenantsAPI.getTenant(tenantId);
		RateCardContext rateCard = TenantsAPI.getRateCard(rateCardId);
		
		FacilioContext context = new FacilioContext();
		
		Chain chain = FacilioChainFactory.calculateTenantBill();
		
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
	
}
