package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddNoteCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note != null)
		{
			note.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			note.setCreationTime(System.currentTimeMillis());
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_DEFAULT);
			Map<String, Object> props = mapper.convertValue(note, Map.class);
			System.out.println(props);
			
			List<FacilioField> fields = FieldFactory.getNoteFields();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.connection(((FacilioContext)context).getConnectionWithTransaction())
													.table("Notes")
													.fields(fields)
													.addRecord(props);
			builder.save();
			note.setNoteId((long) props.get("id"));
			if(note.getParentModuleLinkName() != null && note.getParentModuleLinkName().equals("workorder"))
			{
				WorkOrderAPI.addWorkOrderNote(note.getParentId(), note.getNoteId(), ((FacilioContext)context).getConnectionWithTransaction());
				if(note.getNotifyRequester())
				{
					JSONObject mailJson = new JSONObject();
					mailJson.put("sender", "support@thingscient.com");
					mailJson.put("to", "shivaraj@thingscient.com");
					mailJson.put("subject", "New note added by ");
					mailJson.put("message", note.getBody());
					AwsUtil.sendEmail(mailJson);
				}
			}
		}
		return false;
	}
}
