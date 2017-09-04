package com.facilio.bmsconsole.commands.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.fw.OrgInfo;

public class CommonCommandUtil {
	public static ScheduleContext getScheduleContextFromRS(ResultSet rs) throws SQLException {
		ScheduleContext sc = new ScheduleContext();
		sc.setScheduleId(rs.getLong("SCHEDULEID"));
		sc.setOrgId(rs.getLong("ORGID"));
		sc.setScheduledStartFromTimestamp(rs.getLong("SCHEDULED_START"));
		sc.setEstimatedEndFromTimestamp(rs.getLong("ESTIMATED_END"));
		sc.setActualWorkStartFromTimestamp(rs.getLong("ACTUAL_WORK_START"));
		sc.setActualWorkEndFromTimestamp(rs.getLong("ACTUAL_WORK_END"));
		return sc;
	}
	
	public static NoteContext getNoteContextFromRS(ResultSet rs) throws SQLException {
		NoteContext context = new NoteContext();
		context.setNoteId(rs.getLong("NOTEID"));
		context.setOrgId(rs.getLong("ORGID"));
		context.setOwnerId(rs.getLong("OWNERID"));
		context.setCreationTime(rs.getLong("CREATION_TIME"));
		context.setTitle(rs.getString("TITLE"));
		context.setBody(rs.getString("BODY"));
		return context;
	}
	
	public static FacilioField getFieldFromRS(ResultSet rs) throws SQLException {
		FacilioField field = new FacilioField();
		field.setFieldId(rs.getLong("Fields.FIELDID"));
		field.setOrgId(rs.getLong("Fields.ORGID"));
		field.setModuleId(rs.getLong("Fields.MODULEID"));
		field.setName(rs.getString("Fields.NAME"));
		field.setDisplayName(rs.getString("Fields.DISPLAY_NAME"));
		field.setDisplayType(rs.getInt("Fields.DISPLAY_TYPE"));
		field.setColumnName(rs.getString("Fields.COLUMN_NAME"));
		field.setSequenceNumber(rs.getInt("Fields.SEQUENCE_NUMBER"));
		field.setDataType(FieldType.getCFType(rs.getInt("Fields.DATA_TYPE")));
		field.setDataTypeCode(rs.getInt("Fields.DATA_TYPE"));
		field.setDefault(rs.getBoolean("Fields.IS_DEFAULT"));
		field.setMainField(rs.getBoolean("Fields.IS_MAIN_FIELD"));
		field.setRequired(rs.getBoolean("Fields.REQUIRED"));
		field.setDisabled(rs.getBoolean("Fields.DISABLED"));
		field.setStyleClass(rs.getString("Fields.STYLE_CLASS"));
		field.setIcon(rs.getString("Fields.ICON"));
		field.setPlaceHolder(rs.getString("Fields.PLACE_HOLDER"));
		
		return field;
	}
	
	public static FacilioView getViewFromRS(ResultSet rs) throws SQLException {
		FacilioView view = new FacilioView();
		view.setViewId(rs.getLong("VIEWID"));
		view.setOrgId(rs.getLong("ORGID"));
		view.setName(rs.getString("NAME"));
		view.setDisplayName(rs.getString("DISPLAY_NAME"));
		view.setModuleId(rs.getLong("MODULEID"));
		view.setCriteriaId(rs.getLong("CRITERIAID"));
		return view;
	}
	
	public static FacilioModule getModuleFromRS(ResultSet rs) throws SQLException {
		long moduleId = rs.getLong("MODULEID");
		if(moduleId != 0) {
		FacilioModule module = new FacilioModule();
			module.setModuleId(moduleId);
			module.setOrgId(rs.getLong("ORGID"));
			module.setName(rs.getString("NAME"));
			module.setDisplayName(rs.getString("DISPLAY_NAME"));
			module.setTableName(rs.getString("TABLE_NAME"));
			return module;
		}
		return null;
	}
	
	public static SupportEmailContext getSupportEmailFromMap(Map<String, Object> props) throws Exception {
		SupportEmailContext email = new SupportEmailContext();
		
		long groupId = (long) props.get("autoAssignGroup");
		if(groupId != 0) {
			GroupContext group = GroupAPI.getGroup(groupId);
			props.put("autoAssignGroup", group);
		}
		else {
			props.remove("autoAssignGroup");
		}
		
		BeanUtils.populate(email, props);
		return email;
	}
	
	public static void setFwdMail(SupportEmailContext supportEmail) {
		String actualEmail = supportEmail.getActualEmail();
		String orgEmailDomain = "@"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".facilio.com";
		
		if(actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
			supportEmail.setFwdEmail(actualEmail);
			supportEmail.setVerified(true);
		}
		else {
			String[] emailSplit = actualEmail.toLowerCase().split("@");
			if(emailSplit.length < 2) {
				throw new IllegalArgumentException("Actual email address of SupportEmail is not valid");
			}
			supportEmail.setFwdEmail(emailSplit[1].replaceAll("\\.", "")+emailSplit[0]+orgEmailDomain);
			supportEmail.setVerified(false);
		}
	}
}
