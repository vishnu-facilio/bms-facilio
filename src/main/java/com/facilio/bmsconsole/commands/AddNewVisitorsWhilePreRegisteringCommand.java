package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Collections;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;


public class AddNewVisitorsWhilePreRegisteringCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLoggingRecords = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLoggingRecords)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			
			for(VisitorLoggingContext visitorLogging : visitorLoggingRecords) {
				if(visitorLogging.getVisitor() != null && visitorLogging.getVisitor().getId() <= 0) {
					RecordAPI.addRecord(true, Collections.singletonList(visitorLogging.getVisitor()) , module, fields);
				}
			}
		}
		return false;
	}

}
