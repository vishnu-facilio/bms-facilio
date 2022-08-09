package com.facilio.qa.displaylogic.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public enum DisplayLogicActionType {

	SHOW_QUESTION(1,"show",2) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), SHOW_QUESTION, null);
			
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	HIDE_QUESTION(2,"hide",1) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), HIDE_QUESTION, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	ENABLE_QUESTION(3,"enable",4) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), ENABLE_QUESTION, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	DISABLE_QUESTION(4,"disable",3) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), DISABLE_QUESTION, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	SET_FIELD_VALUE(5,"set",6) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			DisplayLogicAction displayLogicAction = (DisplayLogicAction)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_ACTION_CONTEXT);
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), SET_FIELD_VALUE, displayLogicAction.getActionMeta());
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	RE_SET_FIELD_VALUE(6,"reset",-1) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), RE_SET_FIELD_VALUE, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	EXECUTE_SCRIPT(7,"executeScript",-1) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
		}

	},
	SET_MANDATORY_QUESTION(8,"setMandatory",9) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), SET_MANDATORY_QUESTION, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	REMOVE_MANDATORY_QUESTION(9,"removeMandatory",8) {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			JSONObject json = DisplayLogicUtil.getActionJson((DisplayLogicContext)facilioContext.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT), REMOVE_MANDATORY_QUESTION, null);
			DisplayLogicUtil.addDisplyLogicJsonToResult(facilioContext,json);
		}
	},
	
	;
	
	private int val;
	private String name;
	private int inverseType;
	
	public int getVal() {
		return val;
	}
	
	public String getName() {
		return name;
	}
	public DisplayLogicActionType getInverseType() {
		return TYPE_MAP.get(inverseType);
	}

	private DisplayLogicActionType(int val,String name,int inverseType) {
		this.val = val;
		this.name = name;
		this.inverseType = inverseType;
	}
	
	abstract public void performAction(FacilioContext facilioContext) throws Exception;
	
	
	private static final Map<Integer, DisplayLogicActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());


	private static Map<Integer, DisplayLogicActionType> initTypeMap() {
		Map<Integer, DisplayLogicActionType> typeMap = new HashMap<>();
		for (DisplayLogicActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	
	public static DisplayLogicActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}
}
