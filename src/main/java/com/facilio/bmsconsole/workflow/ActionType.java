package com.facilio.bmsconsole.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public enum ActionType {
	EMAIL_NOTIFICATION(1) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					String to = (String) obj.get("to");
					if(to != null && !to.isEmpty()) {
						List<String> emails = new ArrayList<>();
						AwsUtil.sendEmail(obj);
						
						emails.add(to);
						context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	SMS_NOTIFICATION(2)
	{
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					String to = (String) obj.get("to");
					if(to != null && !to.isEmpty()) {
						List<String> sms = new ArrayList<>();
						SMSUtil.sendSMS(obj);
						
						sms.add(to);
						context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	BULK_EMAIL_NOTIFICATION(3) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					JSONArray toEmails = null;
					Object toAddr = obj.remove("to");
					
					if(toAddr instanceof JSONArray) {
						toEmails = (JSONArray) toAddr;
					}
					else if(toAddr instanceof String) {
						toEmails = new JSONArray();
						toEmails.add((String) toAddr);
					}
					
					if(toEmails != null && !toEmails.isEmpty()) {
						List<String> emails = new ArrayList<>();
						for(Object toEmail : toEmails) {
							String to = (String) toEmail;
							if(to != null && !to.isEmpty()) {
								obj.put("to", (String)to);
								AwsUtil.sendEmail(obj);
								emails.add(to);
							}
						}
						context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	BULK_SMS_NOTIFICATION(4) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
				try {
					JSONArray tos = null;
					Object toNums = obj.remove("to");
					
					if(toNums instanceof JSONArray) {
						tos = (JSONArray) toNums;
					}
					else if(toNums instanceof String) {
						tos = new JSONArray();
						tos.add((String) toNums);
					}
					if(tos != null && !tos.isEmpty()) {
						List<String> sms = new ArrayList<>();
						for(Object toObj : tos) {
							String to = (String) toObj;
							if(to != null && !to.isEmpty()) {
								obj.put("to", (String) to);
								SMSUtil.sendSMS(obj);
								sms.add(to);
							}
						}
						context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	},
	WEB_NOTIFICATION(5) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			if(obj != null) {
//				NotificationContext notification = new NotificationContext();
//				NotificationAPI.sendNotification(notification);
			}
		}
	},
	ADD_ALARM(6){
		@Override
		public void performAction(JSONObject obj, Context context) {

			if(obj != null) {
				try {
					if(obj.containsKey("type")) {
						obj.remove("type");
					}
					if(obj.containsKey("subject")) {
						String subject = (String) obj.get("subject");
						obj.put("message", subject);
					}
					obj.put("type", 5);
					FacilioContext context1 = new FacilioContext();
					context1.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
					Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
					getAddEventChain.execute(context1);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	},
	PUSH_NOTIFICATION(7) {

		@SuppressWarnings("unchecked")
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			try {
				if(obj != null) {
					
					List<String> emails = getEmailAddresses();
					
					if(emails != null && !emails.isEmpty()) {
						Map<String, String> emailToMobileId = getMobileInstanceIDs(emails);
						
						for(String email : emails) {
							String mobileId = emailToMobileId.get(email);
							if(mobileId != null) {
								JSONObject content = new JSONObject();
//								content.put("to", "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
								content.put("to", mobileId);
								JSONObject notification = new JSONObject();
								notification.put("body", obj.get("message"));
								notification.put("title", "New Alarm");
								notification.put("content_available", true);
								notification.put("priority", "high");
								content.put("notification", notification);
								
								JSONObject data = new JSONObject();
								data.put("id", 1253);
								content.put("data", data);
								
								Map<String, String> headers = new HashMap<>();
								headers.put("Content-Type","application/json");
								String url = "https://fcm.googleapis.com/fcm/send";
								
								AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
								System.out.println("Push notification sent");
								System.out.println(content.toJSONString());
							}
						}
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		private Map<String, String> getMobileInstanceIDs(List<String> emails) throws Exception {
			Map<String, String> emailToMobileId = new HashMap<>();
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField email = new FacilioField();
			email.setName("email");
			email.setColumnName("EMAIL");
			email.setDataType(FieldType.STRING);
			email.setModule(ModuleFactory.getUserModule());
			fields.add(email);
			
			FacilioField mobileInstanceId = new FacilioField();
			mobileInstanceId.setName("mobileInstanceId");
			mobileInstanceId.setColumnName("MOBILE_INSTANCE_ID");
			mobileInstanceId.setDataType(FieldType.STRING);
			mobileInstanceId.setModule(AccountConstants.getUserMobileSettingModule());
			fields.add(mobileInstanceId);
			
			Condition condition = CriteriaAPI.getCondition("EMAIL", "email", StringUtils.join(emails, ","), StringOperators.IS);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Users")
															.innerJoin("User_Mobile_Setting")
															.on("Users.USERID = User_Mobile_Setting.USERID")
															.andCondition(condition)
															.orderBy("USER_MOBILE_SETTING_ID")
															;
			
			List<Map<String, Object>> props = selectBuilder.get();
			if(props != null && !props.isEmpty()) {
				for(Map<String, Object> prop : props) {
					emailToMobileId.put((String) prop.get("email"), (String) prop.get("mobileInstanceId"));
				}
			}
			return emailToMobileId;
		}
		
		private List<String> getEmailAddresses() throws Exception {
			FacilioModule module = ModuleFactory.getEMailTemplatesModule();
			FacilioField field = new FacilioField();
			field.setName("toAddr");
			field.setColumnName("TO_ADDR");
			field.setDataType(FieldType.STRING);
			field.setModule(module);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(Collections.singletonList(field))
															.table(module.getTableName())
															.innerJoin("Templates")
															.on("EMail_Templates.ID = Templates.ID")
															.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getId());
			
			List<Map<String, Object>> props = selectBuilder.get();
			if(props != null && !props.isEmpty()) {
				List<String> addresses = new ArrayList<>();
				for(Map<String, Object> emailProp : props) {
					String emails = (String) emailProp.get("toAddr");
					String[] emailList = emails.trim().split("\\s*,\\s*");
					if(emailList != null && emailList.length != 0) {
						for(String email : emailList) {
							addresses.add(email);
						}
					}
				}
				return addresses;
			}
			return null;
		}
	};
	
	private int val;
	private ActionType(int val) {
		// TODO Auto-generated constructor stub
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
	
	abstract public void performAction(JSONObject obj, Context context);
	
	public static ActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}
	
	private static final Map<Integer, ActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, ActionType> initTypeMap() {
		Map<Integer, ActionType> typeMap = new HashMap<>();
		for(ActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	public static void main(String arg[])
	{
		System.out.println("hello world");
		ActionType t = ActionType.SMS_NOTIFICATION;
		JSONObject json = new JSONObject();
		json.put("to", "+919840425388");
		json.put("message", "hello world");
		t.performAction(json, null);
	}
}