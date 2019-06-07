package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SetJoinsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext) context;
		String moduleName = repContext.getModuleName();
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the report");
		}
		if(moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			getForWorkOrder(repContext);
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER_REQUEST)) {
			getForWorkOrder(repContext);
		}
		
		return false;
	}
	private void getForWorkOrder(FacilioReportContext repContext) throws Exception {
		JSONArray reportJoins = repContext.getJoins();
		if(reportJoins != null && !reportJoins.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = repContext.getModuleName();
			FacilioModule woModule = modBean.getModule(moduleName);
			FacilioModule tktModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
			for(int i=0;i<reportJoins.size();i++) {
				JSONObject thisJoin = (JSONObject) reportJoins.get(i);
				if(thisJoin !=null) {
					String joinTable = (String) thisJoin.get(FacilioConstants.Reports.JOIN_TABLE);
					if(joinTable !=null && !joinTable.isEmpty()) {
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET)) {
							String joinOn = woModule.getTableName()+"."+FacilioConstants.ContextNames.ID+"="+tktModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET_STATUS)) {
							FacilioModule tktStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
							String joinOn = tktModule.getTableName()+"."+FacilioConstants.Ticket.STATUS_ID+"="+tktStatusModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktStatusModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET_CATEGORY)) {
							FacilioModule tktCategoryModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_CATEGORY);
							String joinOn = tktModule.getTableName()+"."+FacilioConstants.Ticket.CATEGORY_ID+"="+tktCategoryModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktCategoryModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
						if(joinTable.equals(FacilioConstants.ContextNames.BUILDING)) {
							FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
							String joinOn = tktModule.getTableName()+"."+FacilioConstants.Ticket.SPACE_ID+"="+buildingModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,buildingModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
					}
					
				}
			}
		}
	}
	private void getForWorkOrderRequest(FacilioReportContext repContext) throws Exception {
		JSONArray reportJoins = repContext.getJoins();
		if(reportJoins != null && !reportJoins.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = repContext.getModuleName();
			FacilioModule woReqModule = modBean.getModule(moduleName);
			FacilioModule tktModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
			for(int i=0;i<reportJoins.size();i++) {
				JSONObject thisJoin = (JSONObject) reportJoins.get(i);
				if(thisJoin !=null) {
					String joinTable = (String) thisJoin.get(FacilioConstants.Reports.JOIN_TABLE);
					if(joinTable !=null && !joinTable.isEmpty()) {
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET)) {
							String joinOn = woReqModule.getTableName()+"."+FacilioConstants.ContextNames.ID+"="+tktModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET_STATUS)) {
							FacilioModule tktStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
							String joinOn = tktModule.getTableName()+"."+FacilioConstants.Ticket.STATUS_ID+"="+tktStatusModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktStatusModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
						if(joinTable.equals(FacilioConstants.ContextNames.TICKET_CATEGORY)) {
							FacilioModule tktCategoryModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_CATEGORY);
							String joinOn = tktModule.getTableName()+"."+FacilioConstants.Ticket.CATEGORY_ID+"="+tktCategoryModule.getTableName()+"."+FacilioConstants.ContextNames.ID; 
							thisJoin.put(FacilioConstants.Reports.JOIN_TABLE,tktCategoryModule.getTableName());
							thisJoin.put(FacilioConstants.Reports.JOIN_ON,joinOn);
						}
					}
					
				}
			}
		}
	}
}
