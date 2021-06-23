package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class SchedulePreOpenVisitorLogsCreateCommand extends FacilioCommand {

	private boolean isEdit = false;

	public SchedulePreOpenVisitorLogsCreateCommand(boolean b) {
		this.isEdit = b;
	}

	public SchedulePreOpenVisitorLogsCreateCommand() {}

	    @Override
	    public boolean executeCommand(Context context) throws Exception {
	        List<VisitorLoggingContext> vLogs =  (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
	        List<Long> vLogIds = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(vLogs)) {
                    vLogs.forEach(i -> vLogIds.add(i.getId()));
            }
	      
	        int delay = 0;
	    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
	        for (VisitorLoggingContext vLog : vLogs) {
	        	
	        	FacilioStatus status = TicketAPI.getStatus(module, "Upcoming");
	        	if(vLog.getModuleState().getId() == status.getId()) {
		        	BmsJobUtil.deleteJobWithProps(vLog.getId(), "ScheduleNewVisitorLogs");
		            JSONObject jObj = new JSONObject();
		            jObj.put("isEdit", isEdit);
		            BmsJobUtil.scheduleOneTimeJobWithProps(vLog.getId(), "ScheduleNewVisitorLogs", delay, "priority",  jObj);
	        	}
	        }
	        return false;
	    }
	}
