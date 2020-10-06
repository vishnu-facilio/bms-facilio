package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class WebtabWebgroupContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long webTabId = -1;

    public long getWebTabId() {
        return webTabId;
    }

    public void setWebTabId(long webTabId) {
        this.webTabId = webTabId;
    }

    private  long webTabGroupId = -1;

    public long getWebTabGroupId() {
        return webTabGroupId;
    }

    public void setWebTabGroupId(long webTabGroupId) {
        this.webTabGroupId = webTabGroupId;
    }

    private int order = -1;
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

}
