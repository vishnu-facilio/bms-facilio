package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.v3.context.V3Context;

public class V3AssetTypeContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private Boolean movable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMovable() {
        return movable;
    }

    public void setMovable(Boolean movable) {
        this.movable = movable;
    }
}
