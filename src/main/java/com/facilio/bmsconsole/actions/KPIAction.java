package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateRange;

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
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXTS, KPIUtil.getAllKPICategories());
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
	
	public String kpiViolations() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getKPIOccurrencesChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.RECORD_ID_LIST, kpiResourceIds);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		chain.execute();
		setResult("violations", context.get(ContextNames.ALARM_COUNT));
		return SUCCESS;
	}
	
	private List<String> kpiResourceIds;
	public List<String> getKpiResourceIds() {
		return kpiResourceIds;
	}
	public void setKpiResourceIds(List<String> kpiResourceIds) {
		this.kpiResourceIds = kpiResourceIds;
	}

	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	public String kpiViewerList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getKPIViewListChain();
		FacilioContext context = chain.getContext();
		constructListContext(context);
		context.put(ContextNames.MODULE_NAME, ContextNames.FORMULA_FIELD);
		context.put(ContextNames.SITE_ID, siteId);
		context.put(ContextNames.SITE_LIST, siteIds);
		context.put(ContextNames.BUILDING_ID, buildingId);
		context.put(ContextNames.BUILDING_LIST, buildingIds);
		context.put(ContextNames.FLOOR_ID, floorId);
		context.put(ContextNames.FLOOR_LIST, floorIds);
		context.put(ContextNames.RESOURCE_LIST, resourceIds);
		context.put(ContextNames.CATEGORY_ID, categoryId);
		context.put(ContextNames.PARENT_CATEGORY_IDS, categoryIds);
		context.put(ContextNames.FREQUENCY, getFrequencyEnum());
		context.put("groupBy", getGroupBy());
		chain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
			setResult(ContextNames.KPI_LIST, context.get(ContextNames.RESULT));
			setResult("lastUpdatedTime", context.get(ContextNames.MODIFIED_TIME));
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
	private List<Long> siteIds;
	public List<Long> getSiteIds() {
		return siteIds;
	}
	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
	}

	private long buildingId = -1;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	private List<Long> buildingIds;
	public List<Long> getBuildingIds() {
		return buildingIds;
	}
	public void setBuildingIds(List<Long> buildingIds) {
		this.buildingIds = buildingIds;
	}

	private long floorId = -1;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	private List<Long> floorIds;
	public List<Long> getFloorIds() {
		return floorIds;
	}
	public void setFloorIds(List<Long> floorIds) {
		this.floorIds = floorIds;
	}

	private long categoryId = -1;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	private List<Long> categoryIds;
	public List<Long> getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
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
	
	private List<Long> resourceIds;
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
	
	private KPIContext kpi;
	public KPIContext getKpi() {
		return kpi;
	}
	public void setKpi(KPIContext kpi) {
		this.kpi = kpi;
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String addKpi() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateKPICommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.KPI, kpi);
		chain.execute();
		
		setResult(ContextNames.KPI, kpi);
		
		return SUCCESS;
	}
	
	public String addOrUpdateKpi() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateKPICommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.KPI, kpi);
		chain.execute();
		
		setResult(ContextNames.KPI, kpi);
		
		return SUCCESS;
	}
	
	public String kpiList() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getKPIListChain();
		FacilioContext context = chain.getContext();
		constructListContext(context);
		context.put(ContextNames.MODULE_NAME, ContextNames.KPI);
		if (fetchCurrentValue != null && fetchCurrentValue) {
			context.put("fetchCurrentValue", fetchCurrentValue);
		}
		chain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		}
		else {
			setResult(ContextNames.KPI_LIST, context.get(ContextNames.KPI_LIST));
		}
		
		return SUCCESS;
	}
	
	private Boolean fetchCurrentValue;
	public Boolean getFetchCurrentValue() {
		return fetchCurrentValue;
	}
	public void setFetchCurrentValue(Boolean fetchCurrentValue) {
		this.fetchCurrentValue = fetchCurrentValue;
	}
	
	public String delteKpi() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getDeleteKPICommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.ID, id);
		chain.execute();
		setResult(ContextNames.RESULT, "success");
		
		return SUCCESS;
	}
	
	public String kpiMetrics() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getKPIMetricsChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		setResult("metrics", context.get(ContextNames.FIELDS));
		
		return SUCCESS;
	}
	
	public String kpiDetails() throws Exception {
		
		KPIContext kpi = KPIUtil.getKPI(id);
		setResult(ContextNames.KPI, kpi);
		
		return SUCCESS;
	}
	
	public String fetchValue() throws Exception {
		
		Object value = KPIUtil.getKPIValue(id);
		setResult("value", value);
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
