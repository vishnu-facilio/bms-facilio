package com.facilio.bmsconsole.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

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
	PUSH_NOTIFICATION(5) {

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