package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateRange;

public class HistoricalOperationAlarmAction extends FacilioAction{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private long categoryId = -1;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	List<Long> resourceIds;
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
    public String runHistorical() throws Exception {
		
	
			if(startTime >= endTime)
			{
				throw new Exception("Start time should be less than the Endtime");
			}
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.CATEGORY_ID, categoryId);
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
			context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceIds);
			
			FacilioChain chain = TransactionChainFactory.getExecuteHistoricalRunOpAlarm();
			chain.execute(context);
			
			setResult("success", "Started");	
		
		
		return SUCCESS;
	}

}
