package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetSupportEmailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long supportEmailId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(supportEmailId != -1) {
			List<FacilioField> fields = FieldFactory.getSupportEmailFields();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(((FacilioContext) context).getConnectionWithTransaction())
					.table("SupportEmails")
					.select(fields)
					.andCustomWhere("ID = ?", supportEmailId);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				Map<String, Object> email = emailList.get(0);
				SupportEmailContext supportEmail = CommonCommandUtil.getSupportEmailFromMap(email);
				context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
			}
		}
		
		return false;
	}

}
