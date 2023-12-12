package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3BinContext extends V3Context {
    private String name;
    private V3ItemContext item;
    private V3ToolContext tool;
    private Long quantity;
    private Boolean isVirtualBin;
}
