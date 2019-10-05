package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.PMPlannerSettingsContext.PlannerType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;

public class PMPlannerAction extends FacilioAction{
	private static final Logger log = LogManager.getLogger(PMPlannerAction.class.getName());

	private static final long serialVersionUID = 1L;
	private PMPlannerSettingsContext settings;
	private PlannerType plannerType;

	public PlannerType getPlannerTypeEnum() {
		return plannerType;
	}
	public void setPlannerType(PlannerType type) {
		this.plannerType = type;
	}
	public int getPlannerType() {
		if (plannerType != null) {
			return plannerType.getValue();
		}
		return -1;
	}
	public void setPlannerType(int type) {
		this.plannerType = PlannerType.valueOf(type);
	}
	


	
	public PMPlannerSettingsContext getSettings() {
		return settings;
	}
	public void setSettings(PMPlannerSettingsContext settings) {
		this.settings = settings;
	}
	public String getPMPlannerSettings()
	{
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS, this.settings);
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
		context.put(ContextNames.FLOOR_ID, floorId);
      
		PMPlannerSettingsContext plannerSettings=new PMPlannerSettingsContext();
		plannerSettings.setPlannerType(plannerType);
		
		
		if (filterJson != null) {
			filterJson = FacilioUtil.parseJson(filterJson.toJSONString());
			context.put(FacilioConstants.ContextNames.FILTERS, filterJson);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.WORK_ORDER);
		}
		context.put(ContextNames.PM_PLANNER_SETTINGS,plannerSettings);
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
	
	private long floorId = -1;
	
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
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
	
	private List<WorkOrderContext> workorders;
	public List<WorkOrderContext> getWorkorders() {
		return workorders;
	}
	public void setWorkorders(List<WorkOrderContext> workorders) {
		this.workorders = workorders;
	}
	
	public String updatePMJob() throws Exception {
		
		ModuleCRUDBean crudBean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD");
		if (workorders == null) {
			workorders = new ArrayList<WorkOrderContext>();
			workorders.add(workorder);
		}
		crudBean.updatePMJob(workorders);
		
		if (workorders.size() == 1) {
			
			FacilioChain getWorkOrderChain = ReadOnlyChainFactory.getWorkOrderDetailsChain();
			FacilioContext context = getWorkOrderChain.getContext();
			context.put(FacilioConstants.ContextNames.ID, workorders.get(0).getId());
			getWorkOrderChain.execute();
			
			setResult(ContextNames.WORK_ORDER, context.get(ContextNames.WORK_ORDER));
		}

		setResult(ContextNames.RESULT, "success");
		return SUCCESS;
	}
}
