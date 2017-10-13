package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.device.Device;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.twilio.sdk.Twilio;

public class CommonCommandUtil {
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
	
	public static Map<Long, RequesterContext> getRequesters(String ids, Connection conn) throws Exception {
		
		FacilioField field = new FacilioField();
		field.setName("requesterId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("REQUESTER_ID");
		field.setModule(ModuleFactory.getRequesterModule());
		
		Condition idCondition = new Condition();
		idCondition.setField(field);
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
											.connection(conn)
											.table("Requester")
											.select(FieldFactory.getRequesterFields())
											.andCondition(idCondition);
		List<Map<String, Object>> requesterList = builder.get();
		
		Map<Long, RequesterContext> requesters = new HashMap<>();
		for(Map<String, Object> requester : requesterList)
		{
			requesters.put((Long) requester.get("requesterId"), getRequesterObject(requester));
		}
		return requesters;
	}
	
	private static RequesterContext getRequesterObject(Map<String, Object> requester) throws SQLException {
		
		RequesterContext rc = new RequesterContext();
		rc.setEmail((String) requester.get("email"));
		rc.setName((String) requester.get("name"));
		rc.setId((Long) requester.get("requesterId"));
		return rc;
	}
	
	public static void appendModuleNameInKey(String moduleName, String prefix, Map<String, Object> beanMap, Map<String, Object> placeHolders) throws Exception {
		if(beanMap != null) {
			if(moduleName != null && !moduleName.isEmpty() && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				
				if(fields != null && !fields.isEmpty()) {
					for(FacilioField field : fields) {
						if(field.getDataType() == FieldType.LOOKUP) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if(props != null) {
								LookupField lookupField = (LookupField) field;
								if(props.size() <= 2) {
									Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"), 0);
									placeHolders.put(prefix+"."+field.getName(), lookupVal);
									props = FieldUtil.getAsProperties(lookupVal);
								}
								String childModuleName = lookupField.getLookupModule() == null?lookupField.getSpecialType():lookupField.getLookupModule().getName();
								appendModuleNameInKey(childModuleName, prefix+"."+field.getName(), props, placeHolders);
							}
						}
						else {
							placeHolders.put(prefix+"."+field.getName(), beanMap.remove(field.getName()));
						}
					}
				}
			}
			for(Map.Entry<String, Object> entry : beanMap.entrySet()) {
				if(entry.getValue() instanceof Map<?, ?>) {
					appendModuleNameInKey(null, prefix+"."+entry.getKey(), (Map<String, Object>) entry.getValue(), placeHolders);
				}
				else {
					placeHolders.put(prefix+"."+entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	public static void updateAlarmDetailsInTicket(AlarmContext alarm, Connection conn) throws Exception {
		boolean isChanged = false;
		//if(alarm.getType() == AlarmContext.AlarmType.LIFE_SAFETY.getIntVal()) 
		{
			TicketCategoryContext category = TicketAPI.getCategory(OrgInfo.getCurrentOrgInfo().getOrgid(), "Fire Safety");
			if(category != null) {
				alarm.getTicket().setCategory(category);
				isChanged = true;
			}
		}
		if(alarm.getDeviceId() != -1) {
			Device device = DeviceAPI.getDevice(alarm.getDeviceId());
			if(device != null) {
				String description;
				BaseSpaceContext space = SpaceAPI.getBaseSpace(device.getSpaceId(), OrgInfo.getCurrentOrgInfo().getOrgid(), conn);
				if(alarm.getIsAcknowledged()) {
					description = MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nSensor Type - {2}\nLocation - {3}, Movenpick Hotel",alarm.getTypeVal(),device.getName(),device.getType(), space.getName());
				}
				else {
					description = MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nSensor Type - {2}\nLocation - {3}, Movenpick Hotel",alarm.getTypeVal(),device.getName(),device.getType(), space.getName());
				}
				alarm.getTicket().setDescription(description);
				isChanged = true;
			}
		}
		if(isChanged) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
					.connection(conn)
					.moduleName(FacilioConstants.ContextNames.TICKET)
					.table("Tickets")
					.fields(modBean.getAllFields(FacilioConstants.ContextNames.TICKET))
					.andCustomWhere("ID = ?", alarm.getTicket().getId());
			
			updateBuilder.update(alarm.getTicket());
		}
	}
	
	public static String sendAlarmSMS(AlarmContext alarm, String to, String message) throws Exception {
		Device device = DeviceAPI.getDevice(alarm.getDeviceId());
		BaseSpaceContext space = getSpace(device.getSpaceId());
		String sms = null;
		if(message != null && !message.isEmpty()) {
			sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, Movenpick Hotel - {3}", alarm.getTypeVal(), alarm.getTicket().getSubject(), space.getName(), message);
		}
		else {
			sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, Movenpick Hotel", alarm.getTypeVal(), alarm.getTicket().getSubject(), space.getName());
		}
		return sendSMS(to, sms);
	}
	
	private static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
	private static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
	public static String sendSMS(String to, String message) {

		try {

			Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);

			com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,
					new com.twilio.sdk.type.PhoneNumber(to),  // To number
					new com.twilio.sdk.type.PhoneNumber("+16106248741"),  // From number
					message                    // SMS body
					).execute();


			//com.twilio.sdk.resource.lookups.v1.PhoneNumber
			//	com.twilio.sdk.resource.api.v2010.account.Message.create(accountSid, to, from, mediaUrl)
			System.out.println(tmessage.getSid());
			System.out.println(tmessage.getTo());

			return tmessage.getTo();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static BaseSpaceContext getSpace(long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			BaseSpaceContext space = SpaceAPI.getBaseSpace(id, OrgInfo.getCurrentOrgInfo().getOrgid(), conn);
			return space;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
