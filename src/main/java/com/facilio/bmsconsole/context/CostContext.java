package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.tenant.FacilioUtility;

import java.util.List;

public class CostContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private FacilioUtility utility;
	public FacilioUtility getUtilityEnum() {
		return utility;
	}
	public void setUtility(FacilioUtility utility) {
		this.utility = utility;
	}
	public int getUtility() {
		if (utility != null) {
			return utility.getValue();
		}
		return -1;
	}
	public void setUtility(int utility) {
		this.utility = FacilioUtility.valueOf(utility);
	}
	
	private String utilityProvider;
	public String getUtilityProvider() {
		return utilityProvider;
	}
	public void setUtilityProvider(String utilityProvider) {
		this.utilityProvider = utilityProvider;
	}

	private List<CostSlabContext> slabs;
	public List<CostSlabContext> getSlabs() {
		return slabs;
	}
	public void setSlabs(List<CostSlabContext> slabs) {
		this.slabs = slabs;
	}
	
	private List<AdditionalCostContext> additionalCosts;
	public List<AdditionalCostContext> getAdditionalCosts() {
		return additionalCosts;
	}
	public void setAdditionalCosts(List<AdditionalCostContext> additionalCosts) {
		this.additionalCosts = additionalCosts;
	}

	private List<CostAssetsContext> assets;
	public List<CostAssetsContext> getAssets() {
		return assets;
	}
	public void setAssets(List<CostAssetsContext> assets) {
		this.assets = assets;
	}
	
	private long readingId = -1;
	public long getReadingId() {
		return readingId;
	}
	public void setReadingId(long readingId) {
		this.readingId = readingId;
	}
}
