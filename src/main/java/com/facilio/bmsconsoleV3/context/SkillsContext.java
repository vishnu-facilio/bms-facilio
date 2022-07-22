package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SkillsContext extends V3Context{

	private String name;
	private String description;
	private Double standardRate;
	private CraftContext parentId;
	private Integer skillLevelRank;
}
