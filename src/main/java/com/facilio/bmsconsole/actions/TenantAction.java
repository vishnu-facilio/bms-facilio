package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.pdf.PdfUtil;
import com.opensymphony.xwork2.ActionSupport;

public class TenantAction extends ActionSupport {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
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
	
	public String addTenant() throws Exception {
		TenantsAPI.addTenant(tenant);
		return SUCCESS;
	}
	
	public String updateTenant() throws Exception {
		TenantsAPI.updateTenant(tenant);
		result = SUCCESS;
		return SUCCESS;
	}
	
	public String fetchTenant() throws Exception {
		tenant = TenantsAPI.getTenant(id);
		return SUCCESS;
	}
	
	public String deleteTenant() throws Exception {
		TenantsAPI.deleteTenant(id);
		result = SUCCESS;
		return SUCCESS;
	}
	
	public String fetchAllTenants() throws Exception {
		tenants = TenantsAPI.getAllTenants();
		return SUCCESS;
	}
	
	public String addRateCard() throws Exception {
		TenantsAPI.addRateCard(rateCard);
		return SUCCESS;
	}
	
	public String updateRateCard() throws Exception {
		TenantsAPI.updateRateCard(rateCard);
		result = SUCCESS;
		return SUCCESS;
	}
	
	public String fetchRateCard() throws Exception {
		rateCard = TenantsAPI.getRateCard(id);
		return SUCCESS;
	}
	
	public String deleteRateCard() throws Exception {
		TenantsAPI.deleteRateCard(id);
		result = SUCCESS;
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
