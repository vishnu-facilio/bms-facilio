package con.facilio.control;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.v3.context.V3Context;

public class ControlGroupAssetContext extends V3Context{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	ControlGroupContext controlGroup;
	ControlGroupAssetCategory controlGroupAssetCategory;
	ResourceContext asset;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}
	public ControlGroupAssetCategory getControlGroupAssetCategory() {
		return controlGroupAssetCategory;
	}
	public void setControlGroupAssetCategory(ControlGroupAssetCategory controlGroupAssetCategory) {
		this.controlGroupAssetCategory = controlGroupAssetCategory;
	}
	public ResourceContext getAsset() {
		return asset;
	}
	public void setAsset(ResourceContext asset) {
		this.asset = asset;
	}
	
	List<ControlGroupFieldContext> controlFields;

	public List<ControlGroupFieldContext> getControlFields() {
		return controlFields;
	}
	public void setControlFields(List<ControlGroupFieldContext> controlFields) {
		this.controlFields = controlFields;
	}
	public void addField(ControlGroupFieldContext field) {
		this.controlFields = this.controlFields == null ? new ArrayList<ControlGroupFieldContext>() : this.controlFields; 
		
		this.controlFields.add(field);
	}
}
