package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddNewVisitorWhileLoggingCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLogs = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLogs)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(VisitorLoggingContext vL : visitorLogs) {
				if(vL.getVisitor() != null && vL.getVisitor().getId() > 0 && VisitorManagementAPI.getVisitorLogging(vL.getVisitor().getId(), true) != null){
					throw new IllegalArgumentException("This visitor has an active record already. Kindly Checkout and proceed to CheckIn");
				}
				if(vL.getVisitor() != null && vL.getVisitor().getId() <= 0) {
					RecordAPI.addRecord(true, Collections.singletonList(vL.getVisitor()) , module, fields);
				}
			}
		}
		return false;
	}

}
