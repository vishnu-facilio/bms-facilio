package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;

public enum ActionType {
		EMAIL_NOTIFICATION(1) {
			@Override
			public void performAction(JSONObject obj) {
				// TODO Auto-generated method stub
				try {
					AwsUtil.sendEmail(obj);
				}
				catch(Exception e) {
					e.printStackTrace();
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
	}