package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.v3.context.V3Context;

public class V3AssetDepartmentContext extends V3Context
{

    private static final long serialVersionUID = 1L;

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
