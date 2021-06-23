package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;

public class UpdateVisitorInviteRelArrivedStateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLoggingRecords = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLoggingRecords)) {
			Map<String, Map<Long, List<UpdateChangeSet>>> moduleChangeSetMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
			Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<Long, List<UpdateChangeSet>>();
			
			for(VisitorLoggingContext vl : visitorLoggingRecords) {
				List<UpdateChangeSet> changes = new ArrayList<UpdateChangeSet>();
				UpdateChangeSet updateChangeState = new UpdateChangeSet();
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
				long fieldId = modBean.getField("moduleState", module.getName()).getFieldId();
				
				VisitorLoggingContext visitorLogging = VisitorManagementAPI.getVisitorLoggingTriggers(vl.getId(), null, false);
						
				if(visitorLogging != null) {
					updateChangeState.setFieldId(fieldId);
					updateChangeState.setNewValue(visitorLogging.getModuleState().getId());
					changes.add(updateChangeState);
					changeSet.put(visitorLogging.getId(), changes);
					
				}
			}
			moduleChangeSetMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING, changeSet);
			context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, moduleChangeSetMap);
		}
		return false;
	}

}
