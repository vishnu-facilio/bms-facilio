package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class PMPlannerAction extends FacilioAction{
	private static final Logger log = LogManager.getLogger(PMPlannerAction.class.getName());

	private static final long serialVersionUID = 1L;
	private String settings;
	
	public String getSettings() {
		return settings;
	}
	public void setSettings(String settings) {
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
	
	
}
