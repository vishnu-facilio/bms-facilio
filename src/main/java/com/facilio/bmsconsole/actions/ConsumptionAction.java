package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ConsumptionAction  extends FacilioAction{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Map<String,Object> totalConsumptionBySite;
		public Map<String,Object> getTotalConsumptionBySite() {
			return totalConsumptionBySite;
		}
		public void setTotalConsumptionBySite(Map<String,Object> totalConsumptionBySite) {
			this.totalConsumptionBySite = totalConsumptionBySite;
		}
		
		private String moduleName;

		public String getModuleName() {
			return moduleName;
		}
		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}


		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		private String fieldName;

		public String getFieldName() {
			return fieldName;
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

	
		
		
		public String getTotalConsumptionForSite() throws Exception {
			FacilioContext context = new FacilioContext();
		//	context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		//	context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, getFieldName());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME, getStartTime());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME, getEndTime());
			
			Chain totalConsumptionChain = FacilioChainFactory.getTotalConsumptionBySiteChain();
			totalConsumptionChain.execute(context);
			Map<String,Object> consumptionDataByBuilding = (Map<String,Object>)context.get(FacilioConstants.ContextNames.TOTAL_CONSUMPTION);
			
			
			setTotalConsumptionBySite(consumptionDataByBuilding);
			
			
			
			return SUCCESS;
		}
		
				
}
