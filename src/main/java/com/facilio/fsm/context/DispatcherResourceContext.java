package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.labour.LabourCraftAndSkillContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter@Setter
public class DispatcherResourceContext {
    private V3PeopleContext people;
    private List<LabourCraftAndSkillContext> craftAndSkill;
    private List<Map<String, Object>> shifts;
}
