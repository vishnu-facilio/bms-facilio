package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateRange;

public class PMPlannerAction extends FacilioAction{
	private static final Logger log = LogManager.getLogger(PMPlannerAction.class.getName());

	private static final long serialVersionUID = 1L;
	private PMPlannerSettingsContext settings;
	

	
	public PMPlannerSettingsContext getSettings() {
		return settings;
	}
	public void setSettings(PMPlannerSettingsContext settings) {
		this.settings = settings;
	}
	public String getPMPlannerSettings()
	{
		FacilioContext context=new FacilioContext();
			try {
				ReadOnlyChainFactory.getPMPlannerSettingschain().execute(context);
				setResult("settings", context.get(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS));
				
			}catch (Exception e) {
				log.error("Failed to retrive planner settings", e);
				
			}
			finally {
				return SUCCESS;
			}
			
	}
	public String updatePMPlannerSettings()
	{
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS,settings);
			try {
				//setResult("settings", context.get(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS));
				TransactionChainFactory.getPMPlannerSettingsChain().execute(context);
				
			}catch (Exception e) {
				log.error("Failed to update planner settings", e);
				
			}
			finally {
				return SUCCESS;
			}

	}

	public String getPMCalendarJobs() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.SITE_ID, siteId);
		context.put(ContextNames.BUILDING_ID, buildingId);
		context.put(ContextNames.CATEGORY_ID, categoryId);
		if (filterJson != null) {
			context.put(FacilioConstants.ContextNames.FILTERS, filterJson);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.WORK_ORDER);
		}
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		ReadOnlyChainFactory.getCalendarResourceJobChain().execute(context);

		setResult(ContextNames.RESULT, context.get(ContextNames.RESULT));

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
	
	private JSONObject filterJson;
	public JSONObject getFilterJson() {
		return filterJson;
	}
	public void setFilterJson(JSONObject filterJson) {
		this.filterJson = filterJson;
	}

	private int dateOperator = -1;
	public int getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(int dateOperator) {
		this.dateOperator = dateOperator;
	}

	private String dateOperatorValue;
	public String getDateOperatorValue() {
		return dateOperatorValue;
	}
	public void setDateOperatorValue(String dateOperatorValue) {
		this.dateOperatorValue = dateOperatorValue;
	}

	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
	private WorkOrderContext workorder;
	public WorkOrderContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(WorkOrderContext workorder) {
		this.workorder = workorder;
	}
	public String updatePMJob() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.IS_NEW_EVENT, true);	// temp

		Chain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceJobChain();
		updatePM.execute(context);

		setResult(ContextNames.RESULT, "success");
		return SUCCESS;
	}
}
