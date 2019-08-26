package com.facilio.bmsconsole.templates;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ControlActionTemplate extends Template {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private ActionType actionType;
    
    long controlActionGroupId;

    public long getControlActionGroupId() {
		return controlActionGroupId;
	}
	public void setControlActionGroupId(long controlActionGroupId) {
		this.controlActionGroupId = controlActionGroupId;
	}
	
	public int getActionType() {
    	if(actionType != null) {
    		return actionType.getIntVal();
    	}
		return -1;
	}
	public void setActionType(int actionType) {
		if(actionType > 0) {
			this.actionType = ActionType.getAllTypes().get(actionType);
		}
	}

	private long assetCategory = -1;
    public long getAssetCategory() {
        return assetCategory;
    }
    public void setAssetCategory(long assetCategory) {
        this.assetCategory = assetCategory;
    }

    private String metric;
    public String getMetric() {
        return metric;
    }
    public void setMetric(String metric) {
        this.metric = metric;
    }

    private String resource;
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    private String val;
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public int getType() {
        return Type.CONTROL_ACTION.getIntVal();
    }
    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Type getTypeEnum() {
        return Type.CONTROL_ACTION;
    }

    @Override
    public JSONObject getOriginalTemplate() throws Exception {
        JSONObject json = new JSONObject();
        json.put("metric", metric);
        json.put("resource", resource);
        json.put("val", val);
        json.put("actionType", getActionType());
        json.put("controlActionGroupId", controlActionGroupId);

        return json;
    }
    
    
    public enum ActionType {
		
		POINT(1, "Point"),
		GROUP(2, "Group"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private ActionType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, ActionType> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, ActionType> initTypeMap() {
			Map<Integer, ActionType> typeMap = new HashMap<>();

			for (ActionType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, ActionType> getAllTypes() {
			return optionMap;
		}
	}
}
