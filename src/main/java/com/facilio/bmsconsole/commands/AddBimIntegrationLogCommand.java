package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext.ThirdParty;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddBimIntegrationLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		
		BimIntegrationLogsContext bimIntegrationLog=new BimIntegrationLogsContext();
		bimIntegrationLog.setFileId((long) context.get(FacilioConstants.ContextNames.FILE_ID));
		bimIntegrationLog.setFileName((String) context.get(FacilioConstants.ContextNames.FILE_NAME));
		bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.INPROGRESS);
		bimIntegrationLog.setUploadedBy(AccountUtil.getCurrentUser().getOuid());
		bimIntegrationLog.setThirdParty(ThirdParty.INTERNAL.getValue());
		long bimId = BimAPI.addBimIntegrationLog(module,fields,bimIntegrationLog);
		
		context.put(FacilioConstants.ContextNames.BIM_IMPORT_ID,bimId);
		
		return false;
	}

}
