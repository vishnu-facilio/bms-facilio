package com.facilio.events.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class V2AlarmAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String fetchAlarmSummary() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		
		Chain chain = ReadOnlyChainFactory.getV2AlarmDetailsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	String alarmModule;
	public String getAlarmModule() {
		return alarmModule;
	}
	public void setAlarmModule(String alarmModule) {
		this.alarmModule = alarmModule;
	}
	
	public String alarmList() throws Exception {
 		FacilioContext context = constructListContext();
 		context.put(ContextNames.MODULE_NAME, alarmModule);
 		
 		Chain alarmListChain = ReadOnlyChainFactory.fetchModuleDataListChain();
		alarmListChain.execute(context);
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		}
		else {
			setResult(ContextNames.ALARM_LIST, context.get(ContextNames.RECORD_LIST));
		}
 		
		
		return SUCCESS;
	}
	
	
}
