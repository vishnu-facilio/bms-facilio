package com.facilio.trigger.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.chain.FacilioContext;
import com.facilio.workflows.util.WorkflowUtil;


public enum Trigger_Action_Type {

	FORMULA_CALCULATION(1) {

		@Override
		public Object performAction(FacilioContext context, Long recordId) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	},
	RULE_EXECUTION(2) {

		@Override
		public Object performAction(FacilioContext context, Long recordId) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	},
	SCRIPT_EXECTION(3) {

		@Override
		public Object performAction(FacilioContext context, Long recordId) throws Exception {
			
			return WorkflowUtil.getResult(recordId, null);
		}
	},
	;
	
	private int val;

	private Trigger_Action_Type(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	abstract public Object performAction(FacilioContext context,Long recordId) throws Exception;

	public static Trigger_Action_Type getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}

	private static final Map<Integer, Trigger_Action_Type> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

	private static final Logger LOGGER = LogManager.getLogger(Trigger_Action_Type.class.getName());

	private static Map<Integer, Trigger_Action_Type> initTypeMap() {
		Map<Integer, Trigger_Action_Type> typeMap = new HashMap<>();
		for (Trigger_Action_Type type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}

}
