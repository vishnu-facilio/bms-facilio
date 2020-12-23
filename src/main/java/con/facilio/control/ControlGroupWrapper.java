package con.facilio.control;

import java.util.List;
import java.util.Map;

public class ControlGroupWrapper {

	
	private ControlGroupContext controlGroupContext;
	private List<ControlGroupAssetCategory> controlGroupAssetCategories;
	private Map<String,List<ControlGroupAssetContext>> controlGroupAssetMap;
	private Map<String,List<ControlGroupFieldContext>> controlGroupFieldMap;
	
	public ControlGroupContext getControlGroupContext() {
		return controlGroupContext;
	}
	public void setControlGroupContext(ControlGroupContext controlGroupContext) {
		this.controlGroupContext = controlGroupContext;
	}
	public List<ControlGroupAssetCategory> getControlGroupAssetCategories() {
		return controlGroupAssetCategories;
	}
	public void setControlGroupAssetCategories(List<ControlGroupAssetCategory> controlGroupAssetCategories) {
		this.controlGroupAssetCategories = controlGroupAssetCategories;
	}
	public Map<String, List<ControlGroupAssetContext>> getControlGroupAssetMap() {
		return controlGroupAssetMap;
	}
	public void setControlGroupAssetMap(Map<String, List<ControlGroupAssetContext>> controlGroupAssetMap) {
		this.controlGroupAssetMap = controlGroupAssetMap;
	}
	public Map<String, List<ControlGroupFieldContext>> getControlGroupFieldMap() {
		return controlGroupFieldMap;
	}
	public void setControlGroupFieldMap(Map<String, List<ControlGroupFieldContext>> controlGroupFieldMap) {
		this.controlGroupFieldMap = controlGroupFieldMap;
	}
}
