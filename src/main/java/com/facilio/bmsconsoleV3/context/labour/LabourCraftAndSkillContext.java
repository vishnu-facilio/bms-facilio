package com.facilio.bmsconsoleV3.context.labour;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LabourCraftAndSkillContext extends V3Context{

	private Double rate;
	private CraftContext craft;
	private SkillsContext skill;
	private LabourContextV3 labour;
	private Boolean isDefault;

	public boolean isDefault() {
		if(isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}
}
