package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;

public class KPIAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KPICategoryContext kpiCategory;
	
	public KPICategoryContext getKpiCategory() {
		return kpiCategory;
	}
	public void setKpiCategory(KPICategoryContext kpiCategory) {
		this.kpiCategory = kpiCategory;
	}
	
	public String getAllKPICategories() throws Exception {
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXTS, KPIUtil.getAllKPIContext());
		return SUCCESS;
	}
	
	
	public String addKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getAddKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		return SUCCESS;
	}
	public String updateKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getUpdateKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		return SUCCESS;
	}
	public String deleteKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getDeleteKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
	
		return SUCCESS;
	}
	
	public String kpiViewerList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getKPIViewListChain();
		FacilioContext context = chain.getContext();
		constructListContext(context);
		context.put(ContextNames.MODULE_NAME, ContextNames.FORMULA_FIELD);
		context.put(ContextNames.SITE_ID, siteId);
		context.put(ContextNames.BUILDING_ID, buildingId);
		context.put(ContextNames.CATEGORY_ID, categoryId);
		context.put(ContextNames.FREQUENCY, getFrequencyEnum());
		context.put("groupBy", getGroupBy());
		chain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
			setResult(ContextNames.RESULT, context.get(ContextNames.RESULT));
		}
		
		return SUCCESS;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long buildingId = -1;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	
	private long categoryId = -1;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
	private String groupBy; // Can have values kpi, asset
	public String getGroupBy() {
		if (groupBy == null) {
			return ContextNames.KPI;
		}
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	
	private FacilioFrequency frequency;
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}
}
