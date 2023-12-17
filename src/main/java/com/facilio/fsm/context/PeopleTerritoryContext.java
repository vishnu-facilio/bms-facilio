package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PeopleTerritoryContext extends V3Context {
    private V3PeopleContext left;
    private TerritoryContext right;
}
