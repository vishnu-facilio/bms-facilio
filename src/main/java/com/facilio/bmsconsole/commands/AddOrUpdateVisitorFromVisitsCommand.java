package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateVisitorFromVisitsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLogs = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLogs)) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(VisitorLoggingContext vL : visitorLogs) {
				CommonCommandUtil.handleLookupFormData(modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING), vL.getData());
				if(StringUtils.isNotEmpty(vL.getVisitorPhone())) {
					VisitorContext visitor = VisitorManagementAPI.getVisitor(-1, vL.getVisitorPhone());
					if(visitor == null) {
						visitor = new VisitorContext();
						visitor.setEmail(vL.getVisitorEmail());
						visitor.setPhone(vL.getVisitorPhone());
						visitor.setName(vL.getVisitorName());
						visitor.setIsReturningVisitor(false);
					    RecordAPI.addRecord(true, Collections.singletonList(visitor) , module, fields);
					}
					else {
						visitor.setEmail(vL.getVisitorEmail());
						visitor.setPhone(vL.getVisitorPhone());
						visitor.setName(vL.getVisitorName());
						visitor.setIsReturningVisitor(true);
						RecordAPI.updateRecord(visitor, module, fields);
					}
					vL.setVisitor(visitor);
				}
			}
		}
		return false;
	}

}
