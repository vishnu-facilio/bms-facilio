package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

public class V3DepartmentContext extends V3Context{

    private static final long serialVersionUID = 1L;

    private String name;
    private String color;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
