package com.facilio.bmsconsole.floorplan;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class FloorPlanViewContext extends ModuleBaseWithCustomFields {
	
	private long floorPlanId;
	
	public long getFloorPlanId() {
		return floorPlanId;
	}
	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	
	private String viewMode;
	
	public String getViewMode() {
		return viewMode;
	}
	public FloorPlanMode getViewModeEnum() {
		if (this.viewMode != null) {
			return FloorPlanMode.getFloorPlanMode(viewMode);
		}
		return null;
	}
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	public void setViewMode(FloorPlanMode viewMode) {
		this.viewMode = viewMode.getName();
	}
	
	private ScriptMode scriptMode = ScriptMode.DEFAULT_SCRIPT;
	
	public void setScriptMode(ScriptMode scriptMode) {
		this.scriptMode = scriptMode;
	}
	
	public ScriptMode getScriptMode() {
		return this.scriptMode;
	}
	
	public void setScriptModeInt(int scriptModeInt) {
		this.scriptMode = scriptModeInt == 1 ? ScriptMode.DEFAULT_SCRIPT : ScriptMode.CUSTOM_SCRIPT;
	}
	
	public int getScriptModeInt() {
		return this.scriptMode.getValue();
	}
	
	private Long customScriptId;
	
	public Long getCustomScriptId() {
		return this.customScriptId;
	}
	
	public void setCustomScriptId(Long customScriptId) {
		this.customScriptId = customScriptId;
	}
	
	private String customScript;
	
	public void setCustomScript(String customScript) {
		this.customScript = customScript;
	}
	
	public String getCustomScript() {
		return this.customScript;
	}

	private boolean isDefault;
	
	public boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	private JSONObject viewParams;
	
	public JSONObject getViewParams() {
		return viewParams;
	}
	public void setViewParams(JSONObject viewParams) {
		this.viewParams = viewParams;
	}
	public String getViewParamsJSON() {
		if (viewParams != null) {
			return viewParams.toJSONString();
		}
		return null;
	}
	public void setViewParamsJSON(String viewParams) throws Exception {
		if (viewParams != null) {
			this.viewParams = (JSONObject) new JSONParser().parse(viewParams);
		}
	}
	
	private JSONObject viewState;
	
	public JSONObject getViewState() {
		return viewState;
	}
	public void setViewState(JSONObject viewState) {
		this.viewState = viewState;
	}
	public String getViewStateJSON() {
		if (viewState != null) {
			return viewState.toJSONString();
		}
		return null;
	}
	public void setViewStateJSON(String viewState) throws Exception {
		if (viewState != null) {
			this.viewState = (JSONObject) new JSONParser().parse(viewState);
		}
	}
	
	public static enum ScriptMode {
		DEFAULT_SCRIPT(1),
		CUSTOM_SCRIPT(2);
		
		private int value;
		
		ScriptMode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}
