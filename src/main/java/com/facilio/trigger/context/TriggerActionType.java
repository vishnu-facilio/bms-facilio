package com.facilio.trigger.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.chain.FacilioContext;
import com.facilio.workflows.util.WorkflowUtil;


public enum TriggerActionType {

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

	private TriggerActionType(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	abstract public Object performAction(FacilioContext context,Long recordId) throws Exception;

	public static TriggerActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}

	private static final Map<Integer, TriggerActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

	private static final Logger LOGGER = LogManager.getLogger(TriggerActionType.class.getName());

	private static Map<Integer, TriggerActionType> initTypeMap() {
		Map<Integer, TriggerActionType> typeMap = new HashMap<>();
		for (TriggerActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}

}
