package com.facilio.bmsconsole.workflow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.NotificationAPI;
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
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
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
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
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
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
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
						if(context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
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
				try {
					List<Long> reciepents = null;
					String toIds = (String) obj.get("to");
					String[] toList = toIds.trim().split(",");
					for ( String toId : toList) {
						reciepents.add(Long.valueOf(toId));
					}
					
					NotificationContext notification = new NotificationContext();
					notification.setInfo((String) obj.get("message"));
					int type = (int) obj.get("activityType");
					notification.setNotificationType(ActivityType.valueOf(type));
					NotificationAPI.sendNotification(reciepents, notification);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				if (obj != null) {
					List<String> mobileInstanceIds = getMobileInstanceIDs(AccountUtil.getCurrentOrg().getId());
					
					if (mobileInstanceIds != null && !mobileInstanceIds.isEmpty()) {
						for(String mobileInstanceId : mobileInstanceIds) {
							if(mobileInstanceId != null) {
//								content.put("to", "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
								obj.put("to", mobileInstanceId);
								Map<String, String> headers = new HashMap<>();
								headers.put("Content-Type","application/json");
								headers.put("Authorization","key=AAAA7I5dN-o:APA91bE70uJ4z21h9jh3A3TfExeHmtsESVYR0W79qbgcW8iyJZ1hKFzTkqV9xXJU-KPqpO1TstbqufHBp8tTCJRjiRAHP2ghNN49T6W0e13pYvtLd_qfPn_dhiKkTpE_BrpVg0WrxxVG");
								
								String url = "https://fcm.googleapis.com/fcm/send";
								
								AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
								System.out.println("Push notification sent");
								System.out.println(obj.toJSONString());
							}
						}
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		private List<String> getMobileInstanceIDs(long orgId) throws Exception {
			List<String> mobileInstanceIds = new ArrayList<String>();
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
			
//			Condition condition = CriteriaAPI.getCondition("EMAIL", "email", StringUtils.join(emails, ","), StringOperators.IS);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Users")
															.innerJoin("ORG_Users")
															.on("Users.USERID = ORG_Users.USERID")
															.innerJoin("User_Mobile_Setting")
															.on("ORG_Users.USERID = User_Mobile_Setting.USERID")
															.andCustomWhere("ORG_Users.ORGID = ? and ORG_Users.USER_STATUS = true and ORG_Users.DELETED_TIME = -1", orgId)
															.orderBy("USER_MOBILE_SETTING_ID");
			
			List<Map<String, Object>> props = selectBuilder.get();
			if(props != null && !props.isEmpty()) {
				for(Map<String, Object> prop : props) {
					mobileInstanceIds.add((String) prop.get("mobileInstanceId"));
//					emailToMobileId.put((String) prop.get("email"), (String) prop.get("mobileInstanceId"));
				}
			}
			return mobileInstanceIds;
		}
	},
	EXECUTE_PM(8) {
		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			try {
				long ruleId = (long) obj.get("rule.id");
				
				FacilioModule pmModule = ModuleFactory.getPreventiveMaintenancetModule();
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																	.select(FieldFactory.getPreventiveMaintenanceFields())
																	.table(pmModule.getTableName())
																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmModule))
																	.andCustomWhere("READING_RULE_ID = ?", ruleId);
				List<Map<String, Object>> pmProps = selectRecordBuilder.get();
				if(pmProps != null && pmProps.isEmpty()) {
					PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(pmProps.get(0), PreventiveMaintenance.class);
					
					FacilioContext pmContext = new FacilioContext();
					pmContext.put(FacilioConstants.ContextNames.RECORD_ID, pm.getId());
					pmContext.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
					pmContext.put(FacilioConstants.ContextNames.PM_REMINDER_TYPE, PMReminder.ReminderType.AFTER);
					pmContext.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);
					
					Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
					executePm.execute(pmContext);
					
					if(context != null) {
						context.put(FacilioConstants.ContextNames.WORK_ORDER, (WorkOrderContext) pmContext.get(FacilioConstants.ContextNames.WORK_ORDER));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
																
		}
	},
	ASSIGNMENT_ACTION(9) {

		@Override
		public void performAction(JSONObject obj, Context context) {
			// TODO Auto-generated method stub
			
		}
		
	}
	;
	
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