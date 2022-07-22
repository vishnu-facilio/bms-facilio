package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CraftContext extends V3Context{

	private String name;
	private String description;
	private Double standardRate;
	private List<SkillsContext> skills;
}
