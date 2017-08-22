package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.RequesterAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddRequesterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		RequesterContext requester = (RequesterContext) context.get(FacilioConstants.ContextNames.REQUESTER);
		
		if(requester != null && requester.getEmail() != null) 
		{
			Map<String, Object> requesterMap = RequesterAPI.getRequester(OrgInfo.getCurrentOrgInfo().getOrgid(), requester.getEmail());
			if(requesterMap == null)
			{
				requester.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_DEFAULT);
				Map<String, Object> props = mapper.convertValue(requester, Map.class);
				System.out.println(props);
				
				List<FacilioField> fields = FieldFactory.getRequesterFields();
				GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
														.connection(((FacilioContext)context).getConnectionWithTransaction())
														.table("Requester")
														.fields(fields)
														.addRecord(props);
				builder.save();
				requester.setRequesterId((long) props.get("id"));
			}
			else
			{
				requester.setRequesterId((long) requesterMap.get("requesterId"));
			}
		}
		else 
		{
			throw new IllegalArgumentException("Requester Object cannot be null");
		}
		return false;
	}

}
