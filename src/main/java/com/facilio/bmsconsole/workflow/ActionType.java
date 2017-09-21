package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.twilio.sdk.Twilio;

public enum ActionType {
		EMAIL_NOTIFICATION(1) {
			@Override
			public void performAction(JSONObject obj) {
				// TODO Auto-generated method stub
				if(obj != null) {
					try {
						AwsUtil.sendEmail(obj);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		},
		SMS_NOTIFICATION(2)
		{
			private static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
			private static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
			@Override
			public void performAction(JSONObject obj) {
				// TODO Auto-generated method stub
				if(obj != null) {
					try {
						
						String message = (String) obj.get("message");
						String to = (String) obj.get("to");
						
						Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);

						com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,
						    new com.twilio.sdk.type.PhoneNumber(to),  // To number
						    new com.twilio.sdk.type.PhoneNumber("+16106248741"),  // From number
						    message                    // SMS body
						).execute();

						
						//com.twilio.sdk.resource.lookups.v1.PhoneNumber
					//	com.twilio.sdk.resource.api.v2010.account.Message.create(accountSid, to, from, mediaUrl)
						System.out.println(tmessage.getSid());
						
						//AwsUtil.sendEmail(obj);
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
		
		abstract public void performAction(JSONObject obj);
		
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
			t.performAction(json);
		}
	}