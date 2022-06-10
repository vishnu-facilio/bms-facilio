package com.facilio.relation.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationDataContext extends V3Context {

    private ModuleBaseWithCustomFields left;
    private ModuleBaseWithCustomFields right;

}
