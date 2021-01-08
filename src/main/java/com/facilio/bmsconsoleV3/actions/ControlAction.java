package com.facilio.bmsconsoleV3.actions;

import java.util.List;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.v3.V3Action;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupRoutineContext;
import con.facilio.control.ControlGroupSection;
import con.facilio.control.ControlScheduleContext;
import con.facilio.control.ControlScheduleExceptionContext;
import lombok.Getter;
import lombok.Setter;

public class ControlAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlGroupSection.Section_Type type;
	
	
	public String getCategoryForType() throws Exception {
		
		List<AssetCategoryContext> categories = type.getAssetCategoryList();
		setData("categories", categories);
		return SUCCESS;
	}


	public int getType() {
		if(type != null) {
			type.getIntVal();
		}
		return -1;
	}

	public void setType(int type) {
		this.type = ControlGroupSection.Section_Type.getAllOptions().get(type);
	}
}
