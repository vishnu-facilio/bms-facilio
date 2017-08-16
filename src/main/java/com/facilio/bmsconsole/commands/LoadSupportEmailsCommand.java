package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class LoadSupportEmailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = FieldFactory.getSupportEmailFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
												.connection(((FacilioContext) context).getConnectionWithTransaction())
												.table("SupportEmails")
												.select(fields)
												.where("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
		List<Map<String, Object>> emailList = builder.get();
		List<SupportEmailContext> emails = new ArrayList<>();
		if(emailList != null) {
			for(Map<String, Object> props : emailList) {
				emails.add(CommonCommandUtil.getSupportEmailFromMap(props));
			}
		}
		
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST, emails);
		return false;
	}

}
